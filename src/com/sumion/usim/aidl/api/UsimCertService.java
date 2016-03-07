package com.sumion.usim.aidl.api;

import java.net.HttpURLConnection;
import java.security.cert.CertificateEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.security.cert.X509Certificate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.sumion.usim.aidl.UsimCertError;
import com.sumion.usim.aidl.UsimCertMgr;
import com.sumion.usim.aidl.UsimCertificate;
import com.sumion.usim.aidl.UsimTokenInfo;
import com.sumion.usim.util.AppClient;
import com.sumion.usim.util.GlobalError;
import com.sumion.usim.util.SumionMessage;
import com.sumion.usim.util.Utils;

import android.telephony.TelephonyManager;

/**
 * 스마트 USIM 서비스 제공 API class
 * @author hyunboklee
 */
public class UsimCertService {
	////////////////////////////// 인증서 발급/갱신 CA index ///////////////////////////////////////
	public static final int IDX_CA_YESSIGN		= 1;
	public static final int IDX_CA_CROSSCERT	= 2;
	public static final int IDX_CA_SIGNKOREA	= 3;
	public static final int IDX_CA_INITECH		= 4;
	public static final int IDX_CA_SIGNGATE		= 5;

	////////////////////////////// 서비스 연결 처리 결과 값 /////////////////////////////////////////
	/** 연결 성공 */
	public static final int SERVICE_CONNECT_REQUEST_OK		= 0;
	/** 이미 연결되어 있는 경우 */
	public static final int SERVICE_ALREADY_CONNECTED		= 1;
	/** 연결 실패 */
	public static final int SERVICE_CONNECT_REQUEST_FAIL	= 2;

	////////////////////////////// 결과 처리 handler 구분 값 ///////////////////////////////////////
	/** USIM 내 인증서 조회 결과 처리 */
	private static final int USIM_RESULT_CERT			= 0;
	/** USIM 내 인증서 개수 조회 결과 처리 */
	private static final int USIM_RESULT_CERT_CNT		= 1;
	/** USIM 내 현재 저장 가능한 인증서 개수 조회 결과 처리 */
	private static final int USIM_RESULT_FREE_CNT		= 2;
	/** USIM 내 인증서 목록 조회 결과 처리 */
	private static final int USIM_RESULT_CERT_LIST		= 3;
	/** SD card 내 인증서 목록 조회 결과 처리 */
	private static final int USIM_RESULT_SD_CERT_LIST	= 4;
	/** USIM 내 인증서로 원문을 전자서명 (PKCS#1 서명) 결과 처리 */
	private static final int USIM_RESULT_SIGN			= 5;
	/** USIM 내 인증서로 원문을 전자서명 (PKCS#7 서명) 결과 처리 */
	private static final int USIM_RESULT_SEVEN_SIGN		= 6;
	/** PKCS#7 서명 데이터에 특정 속성 추가 결과 처리 */
	private static final int USIM_RESULT_ADD_ATTR		= 7;
	/** 인증서의 개인키 R값 조회 결과 처리 */
	private static final int USIM_RESULT_VID_RANDOM		= 8;
	/** 토큰 정보 조회(여유 공간 및 USIM Serial(ICCID) 조회) 결과 처리 */
	private static final int USIM_RESULT_TOKEN_INFO		= 9;
	/** USIM 내 인증서 발급 결과 처리 */
	private static final int USIM_RESULT_ISSUE			= 10;
	/** USIM 내 인증서 갱신 결과 처리 */
	private static final int USIM_RESULT_UPDATE			= 11;
	/** 인증서 저장 결과 처리 */
	private static final int USIM_RESULT_SAVE			= 12;
	/** USIM 내 인증서 삭제 결과 처리 */
	private static final int USIM_RESULT_DEL			= 13;
	/** 서비스 가입 여부 조회 결과 처리 */
	private static final int USIM_RESULT_JOIN			= 14;

	/** 서비스 사용 가능 시점(bind 후 부가서비스 조회 결과가 OK인 시점) 처리 리스너 */
	public interface OnUsimServiceAvailable {
		/**
		 * 서비스 사용 가능 시점(bind 후 부가서비스 조회 결과가 OK인 시점) 처리
		 */
		public void onUsimServiceAvailable();
	}

	////////////////////////////// 결과 처리 Listener ///////////////////////////////////////
	/** USIM 내 인증서 조회 결과 처리 리스너 */
	public interface OnGetCertResult {
		/**
		 * USIM 내 인증서 조회 결과 처리
		 * @param usimCertificate - 인증서 정보 객체
		 */
		public void onGetCertResult(UsimCertificate usimCertificate);
	}

	/** USIM 내 인증서 개수 조회 결과 처리 리스너 */
	public interface OnCertCntResult {
		/**
		 * USIM 내 인증서 개수 조회 결과 처리
		 * @param nCnt - 인증서 개수
		 */
		public void onCertCntResult(int nCnt);
	}

	/** USIM 내 현재 저장 가능한 인증서 개수 조회 결과 처리 리스너 */
	public interface OnFreeCntResult {
		/**
		 * USIM 내 현재 저장 가능한 인증서 개수 조회 결과 처리
		 * @param nCnt - 저장 가능 개수
		 */
		public void onFreeCntResult(int nCnt);
	}

	/** USIM/SD card 내 인증서 목록 조회 결과 처리 리스너 */
	public interface OnCertListResult {
		/**
		 * USIM/SD card 내 인증서 목록 조회 결과 처리
		 * @param certList - 인증서 목록
		 */
		public void onCertListResult(ArrayList<UsimCertificate> certList);
	}

	/** USIM 내 인증서로 원문을 전자서명 (PKCS#1 서명) 결과 처리 리스너 */
	public interface OnSignResult {
		/**
		 * USIM 내 인증서로 원문을 전자서명 (PKCS#1 서명) 결과 처리
		 * @param signResultData - PKCS#1 서명 데이터
		 */
		public void onSignResult(byte[] signResultData);
	}

	/** USIM 내 인증서로 원문을 전자서명 (PKCS#7 서명) 결과 처리 리스너 */
	public interface OnSevenSignResult {
		/**
		 * USIM 내 인증서로 원문을 전자서명 (PKCS#7 서명) 결과 처리
		 * @param signResultData - PKCS#7 서명 데이터 반환
		 */
		public void onSevenSignResult(byte[] signResultData);
	}

	/** PKCS#7 서명 데이터에 특정 속성 추가 결과 처리 리스너 */
	public interface OnAddAttrResult {
		/**
		 * PKCS#7 서명 데이터에 특정 속성 추가 결과 처리
		 * @param signResultData - 속성 추가된 PKCS#7 서명 데이터
		 */
		public void onAddAttrResult(byte[] signResultData);
	}

	/** 인증서의 개인키 R값 조회 결과 처리 리스너 */
	public interface OnVIDRandomResult {
		/**
		 * 인증서의 개인키 R값 조회 결과 처리
		 * @param randomData - 조회된 R 값
		 */
		public void onVIDRandomResult(byte[] randomData);
	}

	/** 토큰 정보 조회(여유 공간 및 USIM Serial(ICCID) 조회) 결과 처리 리스너 */
	public interface OnTokenInfoResult {
		/**
		 * 토큰 정보 조회(여유 공간 및 USIM Serial(ICCID) 조회) 결과 처리
		 * @param usimTokenInfo - 토큰 정보
		 */
		public void onTokenInfoResult(UsimTokenInfo usimTokenInfo);
	}

	/** USIM 내 인증서 발급 결과 처리 리스너 */
	public interface OnIssueResult {
		/**
		 * USIM 내 인증서 발급 결과 처리
		 * @param bSuccess - 발급 성공 여부
		 */
		public void onIssueResult(boolean bSuccess);
	}

	/** USIM 내 인증서 갱신 결과 처리 리스너 */
	public interface OnUpdateResult {
		/**
		 * USIM 내 인증서 갱신 결과 처리
		 * @param bSuccess - 갱신 성공 여부
		 */
		public void onUpdateResult(boolean bSuccess);
	}

	/** 인증서 저장 결과 처리 리스너 */
	public interface OnSaveResult {
		/**
		 * 인증서 저장 결과 처리
		 * @param bSuccess - 저장 성공 여부
		 */
		public void onSaveResult(boolean bSuccess);
	}

	/** USIM 내 인증서 삭제 결과 처리 리스너 */
	public interface OnDelResult {
		/**
		 * USIM 내 인증서 삭제 결과 처리
		 * @param bSuccess - 삭제 성공 여부
		 */
		public void onDelResult(boolean bSuccess);
	}

	/** 서비스 가입 여부 조회 결과 처리 리스너 */
	public interface OnCheckJoinResult {
		/**
		 * 서비스 가입 여부 조회 결과 처리
		 * @param strResultCode - 결과 값(GlobalError.result 참고)
		 */
		public void onCheckJoinResult(String strResultCode);
	}

	/** 서비스 사용 가능 시점(bind 후 부가서비스 조회 결과가 OK인 시점) 처리 리스너 */
	private OnUsimServiceAvailable m_availableListener;

	/** 현재 수행 중인 background 처리에 등록된 결과 처리 리스너 */
	private Object m_resultListener;

	/** Context */
	private Context m_context;
	/** 결과 처리 Handler */
	private Handler m_handler;
	/** 서비스 연결/해제 결과 처리 listener */
	private UsimServiceConnection m_connection;
	/** 서비스 연결 요청 요청 수행 판단 flag - bind/unbind 를 직접 호출 할 경우 설정 */
	private boolean m_bConnectRequest;
	/** 서비스 명 */
	private static final String SERVICE_NAME = "com.sumion.usim.intent.CERT_SERVICE";
	/** 스마트 USIM 서비스 패키지 명 */
	private static final String PACKAGE_NAME = "com.sumion.usim";
	/** SEIO Agent 패키지 명 */
	private static final String PACKAGE_NAME_SEIO = "com.skp.seio";
	/** KT Agent 패키지 명*/
	private static final String PACKAGE_NAME_OLLEH = "com.kt.ollehusimmanager";
	/** LGT TSM Proxy 패키지 명*/
	public static final String LGT_PKG_NAME = "com.lguplus.tsmproxy";		
	/** 서비스 제공 stub */
	private UsimCertMgr m_usimCertMgr;
	/** 처리 결과 error code */
	private String m_strErrCode;
	/** 처리 결과 error message */
	private String m_strErrMsg;

	public String certPath = null;
	public String privKeyPath = null;
	
	/** 가입여부 체크 결과 변수 */
	private int m_iSubscriberCheck = -1;
	
	/** 통합 API 처리 진행 상태 */
	private boolean m_bProgressStatus = false;
	
	/* 인증서 필터링 정보*/
	/*
String strOID, String strSerialNumber, String strSubjectDN, String strIssuerDN, boolean bExpired 
	 */
	private String m_strOID = null;
	private String m_strSerialNumber = null;
	private String m_strSubjectDN = null;
	private String m_strIssuerDN = null;
	private boolean m_bExpired = false;
	
//	private byte[] mPKCS7Sign;
//	private byte[] mPKCS7SignAdd;
//	private byte[] mRandomValue;
//
//	private String mSignTime;
	
//	private static final String LOGIN_URL = "https://demo.initech.com:8343/initech/mobilianNet/login.jsp";

	/**
	 * 생성자
	 * @param context - Context
	 */
	public UsimCertService(Context context) {
		m_context = context;
		m_handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				// 처리 중 연결 해제 등으로 초기화 된 경우 무시
				if(m_resultListener == null) {
					return;
				}

				switch(msg.what) {
					/* USIM 내 인증서 조회 결과 처리 */
					case USIM_RESULT_CERT: {
						OnGetCertResult resultListener = (OnGetCertResult)m_resultListener;
						m_resultListener = null;
						resultListener.onGetCertResult((UsimCertificate) msg.obj);
						break;
					}
					/* USIM 내 인증서 개수 조회 결과 처리 */
					case USIM_RESULT_CERT_CNT: {
						OnCertCntResult resultListener = (OnCertCntResult)m_resultListener;
						m_resultListener = null;
						resultListener.onCertCntResult((Integer) msg.obj);
						break;
					}
					/* USIM 내 현재 저장 가능한 인증서 개수 조회 결과 처리 */
					case USIM_RESULT_FREE_CNT: {
						OnFreeCntResult resultListener = (OnFreeCntResult)m_resultListener;
						m_resultListener = null;
						resultListener.onFreeCntResult((Integer) msg.obj);
						break;
					}
					/* USIM/SD card 내 인증서 목록 조회 결과 처리 */
					case USIM_RESULT_CERT_LIST:
					case USIM_RESULT_SD_CERT_LIST: {
						OnCertListResult resultListener = (OnCertListResult)m_resultListener;
						m_resultListener = null;
						resultListener.onCertListResult((ArrayList<UsimCertificate>) msg.obj);
						break;
					}
					/* USIM 내 인증서로 원문을 전자서명 (PKCS#1 서명) 결과 처리 */
					case USIM_RESULT_SIGN: {
						OnSignResult resultListener = (OnSignResult)m_resultListener;
						m_resultListener = null;
						resultListener.onSignResult((byte[]) msg.obj);
						break;
					}
					/* USIM 내 인증서로 원문을 전자서명 (PKCS#7 서명) 결과 처리 */
					case USIM_RESULT_SEVEN_SIGN: {
						OnSevenSignResult resultListener = (OnSevenSignResult)m_resultListener;
						m_resultListener = null;
						resultListener.onSevenSignResult((byte[]) msg.obj);
						break;
					}
					/* PKCS#7 서명 데이터에 특정 속성 추가 결과 처리 */
					case USIM_RESULT_ADD_ATTR: {
						OnAddAttrResult resultListener = (OnAddAttrResult)m_resultListener;
						m_resultListener = null;
						resultListener.onAddAttrResult((byte[]) msg.obj);
						break;
					}
					/* 인증서의 개인키 R값 조회 결과 처리 */
					case USIM_RESULT_VID_RANDOM: {
						OnVIDRandomResult resultListener = (OnVIDRandomResult)m_resultListener;
						m_resultListener = null;
						resultListener.onVIDRandomResult((byte[]) msg.obj);
						break;
					}
					/* 토큰 정보 조회(여유 공간 및 USIM Serial(ICCID) 조회) 결과 처리 */
					case USIM_RESULT_TOKEN_INFO: {
						OnTokenInfoResult resultListener = (OnTokenInfoResult)m_resultListener;
						m_resultListener = null;
						resultListener.onTokenInfoResult((UsimTokenInfo) msg.obj);
						break;
					}
					/* USIM 내 인증서 발급 결과 처리 */
					case USIM_RESULT_ISSUE: {
						OnIssueResult resultListener = (OnIssueResult)m_resultListener;
						m_resultListener = null;
						resultListener.onIssueResult((Boolean) msg.obj);
						break;
					}
					/* USIM 내 인증서 갱신 결과 처리 */
					case USIM_RESULT_UPDATE: {
						OnUpdateResult resultListener = (OnUpdateResult)m_resultListener;
						m_resultListener = null;
						resultListener.onUpdateResult((Boolean) msg.obj);
						break;
					}
					/* 인증서 저장 결과 처리 */
					case USIM_RESULT_SAVE: {
						OnSaveResult resultListener = (OnSaveResult)m_resultListener;
						m_resultListener = null;
						resultListener.onSaveResult((Boolean) msg.obj);
						break;
					}
					/* USIM 내 인증서 삭제 결과 처리 */
					case USIM_RESULT_DEL: {
						OnDelResult resultListener = (OnDelResult)m_resultListener;
						m_resultListener = null;
						resultListener.onDelResult((Boolean) msg.obj);
						break;
					}
					/* 서비스 가입 여부 조회 결과 처리 */
					case USIM_RESULT_JOIN: {
						OnCheckJoinResult resultListener = (OnCheckJoinResult)m_resultListener;
						m_resultListener = null;
						resultListener.onCheckJoinResult((String) msg.obj);
						break;
					}
				}
			}
		};
	}

	/**
	 * USIM 인증 서비스 연결 요청
	 * @param conn - 서비스 연결/해제 처리 listener
	 * @return int - 결과 코드<br>
	 * 연결 성공 : SERVICE_CONNECT_OK<br>
	 * 이미 연결되어 있는 경우 : SERVICE_CONNECTED<br>
	 * 연결 실패 : SERVICE_CONNECT_FAIL
	 */
	public int bind(UsimServiceConnection conn) {
		Log.d("UsimCertService", "bind m_bConnectRequest[" + m_bConnectRequest + "]");
		if (m_bConnectRequest) {
			return SERVICE_ALREADY_CONNECTED;
		}

		if(m_context.bindService(new Intent(SERVICE_NAME), conn, Context.BIND_AUTO_CREATE)) {
			m_connection = conn;
			m_bConnectRequest = true;
			return SERVICE_CONNECT_REQUEST_OK;
		}

		setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);

		return SERVICE_CONNECT_REQUEST_FAIL;
	}

	/**
	 * USIM 인증 서비스 연결 해제 요청
	 */
	public void unbind() {
		Log.d("UsimCertService", "unbind m_usimCertMgr[" + m_usimCertMgr + "]");
		if (m_usimCertMgr == null) {
			return;
		}

		m_bConnectRequest = false;
		clearStub();
		m_context.unbindService(m_connection);
	}

	/**
	 * 연결 해제 상태에서 resume 될 경우 재 연결 처리를 위해 Activity의 onResume에서 호출
	 */
	public void onResume() {
		if(m_connection != null) {
			if(!m_bConnectRequest) {  // 한번 연결 시도 된 상태에서 해제 시도 후 resume 된 상태
				bind(m_connection);
			}
		}
	}

	/**
	 * Service connection 반환
	 * @return UsimServiceConnection
	 */
	public UsimServiceConnection getUsimServiceConnection() {
		return m_connection;
	}

	/**
	 * 서비스 사용 가능 시점(bind 후 부가서비스 조회 결과가 OK인 시점) 처리 리스너 등록<br>
	 * 더이상 사용하지 않을 경우 null로 clear 해 주어야 함
	 * @param availableListener
	 */
	public void setOnAvailableListener(OnUsimServiceAvailable availableListener) {
		m_availableListener = availableListener;
	}

	/**
	 * 서비스 사용 가능 시점에서 등록된 초기화 처리 수행
	 */
	public void doInitJob() {
		if(m_availableListener != null) {
			m_availableListener.onUsimServiceAvailable();
		}
	}
	
	/**
	 * 서비스 stub 등록
	 * @param usimCertMgr
	 */
	public void setStub(UsimCertMgr usimCertMgr) {
		m_usimCertMgr = usimCertMgr;
		m_resultListener = null;
	}

	/**
	 * 서비스 stub 해제
	 * @param usimCertMgr
	 */
	public void clearStub() {
		m_usimCertMgr = null;
		m_resultListener = null;
	}

	/**
	 * 처리 결과 코드 반환
	 * @return String - 처리 결과 코드
	 */
	public String getErrorCode() {
		return m_strErrCode;
	}

	/**
	 * 처리 결과 메시지 반환
	 * @return String - 처리 결과 메시지
	 */
	public String getErrorMessage() {
		return m_strErrMsg;
	}

	/**
	 * 처리 결과 에러 메시지 설정
	 * @param strErrCode - error code
	 * @param strErrMsg - error message
	 */
	private void setErrorMessage(String strErrCode, String strErrMsg) {
		m_strErrCode = strErrCode;
		m_strErrMsg = strErrMsg;
	}

	/**
	 * 처리 결과 에러 메시지 설정
	 * @param error - UsimCertError 결과 객체
	 */
	private void setErrorMessage(UsimCertError error) {
		m_strErrCode = error.getErrorCode();
		m_strErrMsg = error.getErrorMessage();
	}

	/**
	 * 서비스 사용 가능 상태 여부 반환
	 * @return boolean - 서비스 사용 가능 여부
	 */
	public boolean isReady() {
		if(m_bConnectRequest && m_usimCertMgr != null) {
			if(m_resultListener != null) {
				setErrorMessage(GlobalError.code.IN_USE, GlobalError.msg.IN_USE);
				return false;
			}
			else {
				if(m_bProgressStatus == true) {
					setErrorMessage(GlobalError.code.IN_USE, GlobalError.msg.IN_USE);
					return false;
				} else {
					setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
					return true;
				}
			}
		}
		else {
			setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
			return false;
		}
	}

	/**
	 * 스마트 USIM 서비스 앱 설치 여부 확인
	 * @return boolean - 스마트 USIM 서비스 앱 설치 여부
	 */
	public boolean isInstalled() {
		PackageManager pm = m_context.getPackageManager();
		
		try {
			pm.getPackageInfo(PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
		}
		catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 스마트 USIM 서비스 앱 설치
	 */
	public void installSmartUsimApplication() {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE_NAME));  
		m_context.startActivity(intent);
	}

	/**
	 * SEIO Agent 설치
	 */
	public void installSEIOAgent() {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE_NAME_SEIO));
		m_context.startActivity(intent);
	}
	
	/**
	 * 통신사별  Agent 설치
	 */
	public void installTelecomAgent() {
		String MCC_MNC_SKT = "45005";
		String MCC_MNC_KT = "45008";
		String MCC_MNC_LGT = "45006";
		TelephonyManager telephonyManager = (TelephonyManager)m_context.getSystemService(m_context.TELEPHONY_SERVICE);
		String strMCCMNC = telephonyManager.getSimOperator();
		if(MCC_MNC_SKT.equals(strMCCMNC)) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE_NAME_SEIO));
			m_context.startActivity(intent);
		} else if(MCC_MNC_KT.equals(strMCCMNC)) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE_NAME_OLLEH));
			m_context.startActivity(intent);
		} else if(MCC_MNC_LGT.equals(strMCCMNC)) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + LGT_PKG_NAME));
			m_context.startActivity(intent);
		} /*
		else {
			//SKT, KT 이외의 통신사는 추가 적용 필요.
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE_NAME_SEIO));
			m_context.startActivity(intent);
		}*/
	}	
	
	/*
	 * USIM 공인인증서 저장 서비스 가입여부 확인 및 가입 CP 파악, 해당 CP 앱 호출
	 * return value : 1 - sumion 가입자, 2- 라온 가입자, 3- 드림시큐리티 가입자, 0 - 미 가입자, 
	 */
	public int checkSmartCert() {
		String MCC_MNC_SKT = "45005";
		String MCC_MNC_KT = "45008";
		String MCC_MNC_LGT = "45006";
		//1. 핸드폰 번호, 통신사 정보 획득
		TelephonyManager telephonyManager = (TelephonyManager)m_context.getSystemService(m_context.TELEPHONY_SERVICE);
		String strMCCMNC = telephonyManager.getSimOperator();
		String phoneOperator = "";
		if(MCC_MNC_SKT.equals(strMCCMNC)) {
			phoneOperator = "SKT";
		} else if(MCC_MNC_KT.equals(strMCCMNC)) {
			phoneOperator = "KT";
		} else if(MCC_MNC_LGT.equals(strMCCMNC)) {
			phoneOperator = "LGU";
		} else {
			phoneOperator = "UNKNOWN";
		}
		telephonyManager = (TelephonyManager)m_context.getSystemService("phone");
		String phoneNumber = telephonyManager.getLine1Number();
		if(phoneNumber != null && phoneNumber.length()> 0) {
			phoneNumber = phoneNumber.replace("+82", "0");
		} else {
			phoneNumber = "010" + telephonyManager.getDeviceId().substring(0, 8);
		}
		String phoneInfo = phoneNumber + phoneOperator;
		//2. 중계서버로의 가입여부 확인
		AppClient httpClient = new AppClient(m_context, phoneInfo);
		SumionMessage msg = httpClient.sendSyncRequest("100", null);
		m_iSubscriberCheck = isRegister(msg, m_context.getPackageName());
/*
			LogUtil.d(TAG, "서비스 가입여부 조회 시작..");
			Message msg = m_httpClient.sendSyncRequest(GlobalConst.A100, Utils.getRegistrationId(getApplicationContext()));
			LogUtil.d(TAG, "isRegister info : msg = ["+msg+"], package_name = ["+strPkgName+"], android.os.Build.model = ["+Build.MODEL+"]");
			m_bRegister = isRegister(msg, strPkgName);
			//m_bRegister = true;
			if(m_bRegister) {
				// 토큰 상태 확인(동기)
				long flag = getTokenFlag(true);
				if(flag >= 0) {
					checkTokenFlag(flag);
					m_bCheakDone = true;
					isServiceReady();
				}
			}
 		
 */
		/*나중에 고민하자
		//2-1 기 가입자.
		//2-1-1 수미온 가입자
		//2-1-1-1 에이전트 및 스마트 USIM 인증 앱 설치 확인 후 실행 또는 설치 화면 전환
		//2-1-2 타CP 가입자
		//2-1-2-1 에이전트 및 CP 앱 설치 확인 후 실행 또는 설치 화면 전환
		//2-2 미 가입자
		//2-2-1 가입 안내(SKT, KT, LGU+ 처리 고민)
		*/
		return m_iSubscriberCheck;
	}

	/**
	 * 부가서비스 가입 여부 조회 결과 처리
	 * @param message - 조회 결과 message
	 * @param strPkgName - 사용 package 명
	 * @return boolean - true(가입)/false(미가입 또는 error)
	 */
	private int isRegister(SumionMessage message, String strPkgName) {
		Log.d("UsimCertService", "isRegister function start");
		//boolean bRegister = false;
		int iResult = -1;

		if(message == null) {
			//m_usimCertError.setError(GlobalError.code.SYSTEM, GlobalError.code.SYSTEM);
			//return false;
			Log.e("UsimCertService", "isRegister function received message is null");
			return iResult;
		}

		if(message.getResponseCode() == HttpURLConnection.HTTP_OK && message.getErrorCode().equals(GlobalError.code.NORMAL)) {
			boolean bRegPkg = false;
			String[] arrStrRegPkgName = message.getBody().split("\\|");
			Log.d("UsimCertService", "is Register message body = ["+message.getBody()+"]");
			for(String strRegPkgName : arrStrRegPkgName) {
				Log.d("UsimCertService", "isRegister Registered PackageName(strRegPkgName) = ["+strRegPkgName+"], current package name = ["+strPkgName+"]");
				if(strRegPkgName.equals(strPkgName)) {
					bRegPkg = true;
					break;
				}
			}

			if(bRegPkg) {
				if(message.getCmd().equals("100")) {
					//m_usimCertError.setError(GlobalError.code.NORMAL, GlobalError.code.NORMAL);
					//bRegister = true;
					Log.d("UsimCertService", "isRegister function result is sumion subscriber");
					iResult = 1;
				}
			}
			else {
				//m_usimCertError.setError(GlobalError.code.PACKAGE_PRIVILEGE, GlobalError.msg.PACKAGE_PRIVILEGE);
				Log.e("UsimCertService", "isRegister function package name is not found in registered package name list");
				iResult = -2;
			}
		}
		else {
			Log.e("UsimCertService", "--ERROR--ERROR---------------ERROR--ERROR--");
			Log.e("UsimCertService", "message2.getErrorCode : " + message.getErrorCode()); 
			Log.e("UsimCertService", "message2.getBody() : " + message.getBody());
			Log.e("UsimCertService", "--ERROR--ERROR---------------ERROR--ERROR--");
			
			//bRegister = false;
			if(message.getErrorCode().equals(GlobalError.code.JOIN_NOT)) {
				//m_usimCertError.setError(GlobalError.code.JOIN_NOT, GlobalError.msg.JOIN_NOT);
				if(message.getBody().contains("USIM_0001")) {
					//라온
					iResult = 5;
				} else if(message.getBody().contains("USIM_0002")) {
					//드림
					iResult = 6;
				} else {
					//수미온
					iResult = 0;
				}
				
			} else if(message.getErrorCode().equals(GlobalError.code.JOIN_OTHER_CP)) {
				if(message.getBody().contains("스마트인증(유심)")) {
					Log.d("UsimCertService", "isRegister function result is raon subscriber");
					iResult = 2;
				} else if(message.getBody().contains("스마트인증(공인)")) {
					Log.d("UsimCertService", "isRegister function result is dream subscriber");
					iResult = 3;
				} else {
					Log.d("UsimCertService", "isRegister function result is the others subscriber");
					iResult = 4;
				}
			} else {
				//m_usimCertError.setError(message.getErrorCode(), message.getBody());
				Log.e("UsimCertService", "isRegister function received unknown result = ["+message.getErrorCode()+"], message =["+message.getBody()+"]");
				iResult = -3;
			}
		}

		return iResult;
	}
	
	
	
	/**
	 * 스마트 USIM 서비스 앱 실행
	 */
	public void launchApplication() {
		// App을 사용하기 위해서는 bind를 해제 해야 함(Resume 시 bind 수행 필요)
		unbind();

		PackageManager pm = m_context.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(PACKAGE_NAME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		m_context.startActivity(intent);
	}

	/**
	 * 서비스 API 호출
	 * @param nResultCode - 처리 결과 코드
	 * @param arg - 요청 argument
	 * @throws RemoteException 
	 */
	private Object request(final int nResultCode, final Argument arg) throws RemoteException {
		Object result = null;

		switch(nResultCode) {
		/* USIM 내 인증서 조회 처리 */
		case USIM_RESULT_CERT:
			result = m_usimCertMgr.getUsimCert(arg.m_nVal1);
			break;
		/* USIM 내 인증서 개수 조회 처리 */
		case USIM_RESULT_CERT_CNT:
			result = m_usimCertMgr.getUsimCertCnt();
			break;
		/* USIM 내 현재 저장 가능한 인증서 개수 조회 처리 */
		case USIM_RESULT_FREE_CNT:
			result = m_usimCertMgr.getFreeCertCnt();
			break;
		/* USIM 내 인증서 목록 조회 처리 */
		case USIM_RESULT_CERT_LIST:
			if(arg == null) {  // 필터 사용 안함
				result = (ArrayList<UsimCertificate>) m_usimCertMgr.getUsimCertList();
			}
			else {
				result = (ArrayList<UsimCertificate>) m_usimCertMgr.getFilteredUsimCertList(arg.m_strVal1, arg.m_strVal2, arg.m_strVal3, arg.m_bVal1);
			}
			break;
		/* SD card 내 인증서 목록 조회 처리 */
		case USIM_RESULT_SD_CERT_LIST:
			result = (ArrayList<UsimCertificate>) m_usimCertMgr.getSDCardCertList();
			break;
		/* USIM 내 인증서로 원문을 전자서명 (PKCS#1 서명) 처리 */
		case USIM_RESULT_SIGN:
			result = m_usimCertMgr.getUsimSign(arg.m_arrByte1, arg.m_nVal1, arg.m_arrByte2, arg.m_strVal1);
			break;
		/* USIM 내 인증서로 원문을 전자서명 (PKCS#7 서명) 처리 */
		case USIM_RESULT_SEVEN_SIGN:
			result = m_usimCertMgr.getUsimSevenSign(arg.m_arrByte1, arg.m_nVal1, arg.m_arrByte2, arg.m_strVal1);
			break;
		/* PKCS#7 서명 데이터에 특정 속성 추가 처리 */
		case USIM_RESULT_ADD_ATTR:
			result = m_usimCertMgr.addUnauthAttr(arg.m_arrByte1, arg.m_strVal1, arg.m_arrByte2);
			break;
		/* 인증서의 개인키 R값 조회 처리 */
		case USIM_RESULT_VID_RANDOM:
			result = m_usimCertMgr.getVIDRandom(arg.m_nVal1, arg.m_arrByte1);
			break;
		/* 토큰 정보 조회(여유 공간 및 USIM Serial(ICCID) 조회) 처리 */
		case USIM_RESULT_TOKEN_INFO:
			result = m_usimCertMgr.getTokenInfo();
			break;
		/* USIM 내 인증서 발급 처리 */
		case USIM_RESULT_ISSUE:
			result = m_usimCertMgr.issueUsimCert(arg.m_nVal1, arg.m_strVal1, arg.m_strVal2, arg.m_arrByte1);
			break;
		/* USIM 내 인증서 갱신 처리 */
		case USIM_RESULT_UPDATE:
			result = m_usimCertMgr.updateUsimCert(arg.m_nVal1, arg.m_nVal2, arg.m_arrByte1);
			break;
		/* 인증서 저장 처리 */
		case USIM_RESULT_SAVE:
			result = m_usimCertMgr.saveUsimCert(arg.m_strVal1, arg.m_strVal2, arg.m_arrByte1, arg.m_arrByte2);
			break;
		/* USIM 내 인증서 삭제 처리 */
		case USIM_RESULT_DEL:
			result = m_usimCertMgr.deleteUsimCert(arg.m_nVal1, arg.m_arrByte1);
			break;
		/* 서비스 가입 여부 조회 처리 */
		case USIM_RESULT_JOIN:
			result = m_usimCertMgr.checkJoin(arg.m_strVal1);
			break;
		}

		return result;
	}

	/**
	 * Background 작업 수행
	 * @param nResultCode - 처리 결과 코드
	 * @param arg - 요청 argument
	 * @param resultListener - 결과 처리 리스너
	 */
	private void runBackground(final int nResultCode, final Argument arg, Object resultListener) {
		m_resultListener = resultListener;

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Object result = null;
				try {
					result = request(nResultCode, arg);
					setErrorMessage(m_usimCertMgr.getErrorMessage());
				}
				catch (RemoteException e) {
					e.printStackTrace();
					setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
				}

				Message msg = m_handler.obtainMessage(nResultCode, result);
				m_handler.sendMessage(msg);
			}
		}).start();
	}
	
	/*
	 * 부가서비스 가입 여부 및 앱 연동 가능 상태 조회
	 */
	public int checkAppStatus() {
		int result = -100;
		String status_result = "";
		if(!isReady()) {
			result = -10;
		} else {
			m_bProgressStatus = true;
			//m_usimCertMgr.checkJoin(arg.m_strVal1);
			try {
				status_result = m_usimCertMgr.checkJoin(m_context.getPackageName());
			} catch(RemoteException e) {
				setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
				status_result = GlobalError.code.SERVICE_CONNECT;
			}
			//-88 - 앱 연결 실패, -99 - 상태조회 실패, -10 - 사용불가, -2 - 통신사 에이전트 미설치, -1 - 미가입, 0 - 보통, 1 - 비밀번호 초기화 상태, 2 - 앱 Major 업데이트, 3 - 앱 마이너 업데이트, 
			if(GlobalError.code.NORMAL.equals(status_result)) {
				setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
				result = 0;
			} else if(GlobalError.code.JOIN_NOT.equals(status_result)) {
				setErrorMessage(GlobalError.code.JOIN_NOT, GlobalError.msg.JOIN_NOT);
				result = -10;
			} else if(GlobalError.code.TOKEN_STATUS.equals(status_result)) {
				setErrorMessage(GlobalError.code.TOKEN_STATUS, GlobalError.msg.TOKEN_STATUS);
				result = 1;
			} else if(GlobalError.code.APP_MAJOR_UPDATE.equals(status_result)) {
				setErrorMessage(GlobalError.code.APP_MAJOR_UPDATE, GlobalError.msg.APP_MAJOR_UPDATE);
				result = 2;
			} else if(GlobalError.code.APP_MINOR_UPDATE.equals(status_result)) {
				setErrorMessage(GlobalError.code.APP_MINOR_UPDATE, GlobalError.msg.APP_MINOR_UPDATE);
				result = 3;
			} else if(GlobalError.code.AGENT_NOT_INSTALLED.equals(status_result)) {
				setErrorMessage(GlobalError.code.AGENT_NOT_INSTALLED, GlobalError.msg.AGENT_NOT_INSTALLED);
				result = -2;
			} else if(GlobalError.code.SERVICE_CONNECT.equals(status_result)) {
				setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
				result = -88;
			}else {
				setErrorMessage(getErrorCode(), getErrorMessage());
				result = -99;
			}
			m_bProgressStatus = false;
		}
		return result;
	}
	
	public String readTokenInfo() {
		String strCCID = null;
		if(isReady()) {
			/*
			try {
				//UsimTokenInfo tokenInfo = null;
				//strCCID = Utils.toHexString(m_usimCertMgr.getTokenInfo().getUismSerialNumber());
				
			} catch(RemoteException e) {
				setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
			}*/
			strCCID = Utils.getUSIMSerialNumber(m_context);
		} 
		return strCCID;
	}	
	
	public int getUsimCertCnt() {
		int result = 0;
		m_strOID = null;
		m_strSerialNumber = null;
		m_strSubjectDN = null;
		m_strIssuerDN = null;
		m_bExpired = false;		
		if(isReady()) {
			try {
				result = m_usimCertMgr.getUsimCertCnt();
			} catch(RemoteException e) {
				setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
			}
		}
		return result;
	}
	
	public int getFilterCertCnt(String strOID, String strSerialNumber, String strSubjectDN, String strIssuerDN, boolean bExpired) {
		int result = 0;
		m_strOID = strOID;
		m_strSerialNumber = strSerialNumber;
		m_strSubjectDN = strSubjectDN;
		m_strIssuerDN = strIssuerDN;
		m_bExpired = bExpired;
		if(isReady()) {
			try {
				List<UsimCertificate> certlist = null;
				if(strOID != null && strOID.length() > 0) {
					certlist = m_usimCertMgr.getOIDFilteredUsimCertList(strOID, bExpired);
				} else {
					certlist = m_usimCertMgr.getFilteredUsimCertList(strSubjectDN, strIssuerDN, strSerialNumber, bExpired);
				}
				if(certlist != null) {
					result = certlist.size();
				}
			} catch(RemoteException e) {
				setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
			}
		}
		return result;
	}
	
	public byte[] getCertificate(int idx) {
		byte [] result = null;
		if(isReady()) {
			try {			
				
				//List<UsimCertificate> certlist = m_usimCertMgr.getUsimCertList();
				List<UsimCertificate> certlist = null;
				if(m_strOID != null && m_strOID.length() > 0) {
					certlist = m_usimCertMgr.getOIDFilteredUsimCertList(m_strOID, m_bExpired);
				} else {
					if(m_strSubjectDN == null && m_strIssuerDN == null && m_strSerialNumber == null) {
						certlist = m_usimCertMgr.getUsimCertList();
					} else {
						certlist = m_usimCertMgr.getFilteredUsimCertList(m_strSubjectDN, m_strIssuerDN, m_strSerialNumber, m_bExpired);
					}
				}				
				if(certlist != null) {
					try {
						for(int i = 0; i < certlist.size();i++) {
							//if(certlist.get(i).getCertIdx() == idx) {
							if(idx == i) {
								result = certlist.get(i).getCert().getEncoded();
								break;
							}
						}
						if(result == null) {
							setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_CERTLIST);
						} else {
							setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
						}
					} catch (CertificateEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						setErrorMessage(GlobalError.code.CERT_FAIL, "인증서 Encoded 실패");
					}
				} else {
					setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_CERTLIST);
				}
			} catch(RemoteException e) {
				setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
			}
		}
		return result;
	}
	
	public String getSerialNumber(int idx) {
		String result = null;
		if(isReady()) {
			try {
				//List<UsimCertificate> certlist = m_usimCertMgr.getUsimCertList();
				List<UsimCertificate> certlist = null;
				if(m_strOID != null && m_strOID.length() > 0) {
					certlist = m_usimCertMgr.getOIDFilteredUsimCertList(m_strOID, m_bExpired);
				} else {
					if(m_strSubjectDN == null && m_strIssuerDN == null && m_strSerialNumber == null) {
						certlist = m_usimCertMgr.getUsimCertList();
					} else {
						certlist = m_usimCertMgr.getFilteredUsimCertList(m_strSubjectDN, m_strIssuerDN, m_strSerialNumber, m_bExpired);
					}
				}			
				
				if(certlist != null) {
					for(int i = 0; i < certlist.size();i++) {
						//if(certlist.get(i).getCertIdx() == idx) {
						if(idx == i) {
							result = String.valueOf(certlist.get(i).getCert().getSerialNumber());
							break;
						}
					}
					if(result == null) {
						setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_RESULT);
					} else {
						setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
					}
				} else {
					setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_CERTLIST);
				}
			} catch(RemoteException e) {
				setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
			}
		}
		return result;
	}
	
	public String getIssuerDN(int idx) {
		String result = null;
		if(isReady()) {
			try {
				//List<UsimCertificate> certlist = m_usimCertMgr.getUsimCertList();
				List<UsimCertificate> certlist = null;
				if(m_strOID != null && m_strOID.length() > 0) {
					certlist = m_usimCertMgr.getOIDFilteredUsimCertList(m_strOID, m_bExpired);
				} else {
					if(m_strSubjectDN == null && m_strIssuerDN == null && m_strSerialNumber == null) {
						certlist = m_usimCertMgr.getUsimCertList();
					} else {
						certlist = m_usimCertMgr.getFilteredUsimCertList(m_strSubjectDN, m_strIssuerDN, m_strSerialNumber, m_bExpired);
					}
				}			
				
				if(certlist != null) {
					for(int i = 0; i < certlist.size();i++) {
						//if(certlist.get(i).getCertIdx() == idx) {
						if(idx == i) {
							result = certlist.get(i).getCert().getIssuerDN().getName();
							break;
						}
					}
					if(result == null) {
						setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_RESULT);
					} else {
						setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
					}
				} else {
					setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_CERTLIST);
				}
			} catch(RemoteException e) {
				setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
			}
		}
		return result;
	}
	
	public String getValidFrom(int idx) {
		String result = null;
		if(isReady()) {
			try {
				//List<UsimCertificate> certlist = m_usimCertMgr.getUsimCertList();
				List<UsimCertificate> certlist = null;
				if(m_strOID != null && m_strOID.length() > 0) {				
					certlist = m_usimCertMgr.getOIDFilteredUsimCertList(m_strOID, m_bExpired);
				} else {
					if(m_strSubjectDN == null && m_strIssuerDN == null && m_strSerialNumber == null) {
						certlist = m_usimCertMgr.getUsimCertList();						
					} else {
						certlist = m_usimCertMgr.getFilteredUsimCertList(m_strSubjectDN, m_strIssuerDN, m_strSerialNumber, m_bExpired);
					}
				}			
				
				if(certlist != null) {
					for(int i = 0; i < certlist.size();i++) {
						//if(certlist.get(i).getCertIdx() == idx) {
						if(idx == i) {
							DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
							result = date.format(certlist.get(i).getCert().getNotBefore());
							break;
						}
					}
					if(result == null) {
						setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_RESULT);
					} else {
						setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
					}
				} else {
					setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_CERTLIST);
				}
			} catch(RemoteException e) {
				setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
			}
		}
		return result;
		
	}
	
	public String getValidTo(int idx) {
		String result = null;
		if(isReady()) {
			try {
				//List<UsimCertificate> certlist = m_usimCertMgr.getUsimCertList();
				List<UsimCertificate> certlist = null;
				if(m_strOID != null && m_strOID.length() > 0) {
					certlist = m_usimCertMgr.getOIDFilteredUsimCertList(m_strOID, m_bExpired);
				} else {
					if(m_strSubjectDN == null && m_strIssuerDN == null && m_strSerialNumber == null) {
						certlist = m_usimCertMgr.getUsimCertList();
					} else {
						certlist = m_usimCertMgr.getFilteredUsimCertList(m_strSubjectDN, m_strIssuerDN, m_strSerialNumber, m_bExpired);
					}
				}							
				
				if(certlist != null) {
					for(int i = 0; i < certlist.size();i++) {
						//if(certlist.get(i).getCertIdx() == idx) {
						if(idx == i) {
							DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
							result = date.format(certlist.get(i).getCert().getNotAfter());
							break;
						}
					}
					if(result == null) {
						setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_RESULT);
					} else {
						setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
					}
				} else {
					setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_CERTLIST);
				}
			} catch(RemoteException e) {
				setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
			}
		}
		return result;
	}
	
	public String getSubjectDN(int idx) {
		String result = null;
		if(isReady()) {
			try {
				//List<UsimCertificate> certlist = m_usimCertMgr.getUsimCertList();
				List<UsimCertificate> certlist = null;
				if(m_strOID != null && m_strOID.length() > 0) {
					certlist = m_usimCertMgr.getOIDFilteredUsimCertList(m_strOID, m_bExpired);
				} else {
					if(m_strSubjectDN == null && m_strIssuerDN == null && m_strSerialNumber == null) {
						certlist = m_usimCertMgr.getUsimCertList();
					} else {
						certlist = m_usimCertMgr.getFilteredUsimCertList(m_strSubjectDN, m_strIssuerDN, m_strSerialNumber, m_bExpired);
					}
				}			
				
				if(certlist != null) {
					for(int i = 0; i < certlist.size();i++) {
						//if(certlist.get(i).getCertIdx() == idx) {
						if(idx == i) {
							result = certlist.get(i).getCert().getSubjectDN().getName();
							Log.d("UsimCertServiceAIDL", "cert result subjectdn = ["+result+"]");
							break;
						}
					}
					if(result == null) {
						setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_RESULT);
					} else {
						setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
					}
				} else {
					setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_CERTLIST);
				}
			} catch(RemoteException e) {
				setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
			}		
		}
		return result;		
	}
	
	public String getPolicy(int idx, boolean name) { 
		String result = null;
		if(isReady()) {
			try {
				//List<UsimCertificate> certlist = m_usimCertMgr.getUsimCertList();
				List<UsimCertificate> certlist = null;
				if(m_strOID != null && m_strOID.length() > 0) {
					certlist = m_usimCertMgr.getOIDFilteredUsimCertList(m_strOID, m_bExpired);
				} else {
					if(m_strSubjectDN == null && m_strIssuerDN == null && m_strSerialNumber == null) {
						certlist = m_usimCertMgr.getUsimCertList();
					} else {
						certlist = m_usimCertMgr.getFilteredUsimCertList(m_strSubjectDN, m_strIssuerDN, m_strSerialNumber, m_bExpired);
					}
				}							
				
				if(certlist != null) {
					for(int i = 0; i < certlist.size();i++) {
						//if(certlist.get(i).getCertIdx() == idx) {
						if(idx == i) {
							//수정 필요.
							//result = certlist.get(i).getCert().getSubjectDN().getName();
							result = certlist.get(i).getOID();
							//certlist.get(i).getCert().
							break;
						}
					}
					if(result == null) {
						setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_RESULT);
					} else {
						setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
					}
				} else {
					setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_CERTLIST);
				}
			} catch(RemoteException e) {
				setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
			}
		}
		return result;		
		
	}
	
	//int index, byte[] pin, int signType,	byte[] tobeSignData, String signTime
	public byte [] getUsimSign(int type, int index, byte[] pin, byte [] plainData, String strTime) {
		byte [] result = null;
		try {
			List<UsimCertificate> certlist = null;
			if(m_strOID != null && m_strOID.length() > 0) {
				certlist = m_usimCertMgr.getOIDFilteredUsimCertList(m_strOID, m_bExpired);
			} else {
				if(m_strSubjectDN == null && m_strIssuerDN == null && m_strSerialNumber == null) {
					certlist = m_usimCertMgr.getUsimCertList();
				} else {
					certlist = m_usimCertMgr.getFilteredUsimCertList(m_strSubjectDN, m_strIssuerDN, m_strSerialNumber, m_bExpired);
				}
			}							
			
			int cert_idx = -1;
			if(certlist != null) {
				for(int i = 0; i < certlist.size();i++) {
					//if(certlist.get(i).getCertIdx() == idx) {
					if(index == i) {
						cert_idx = certlist.get(i).getCertIdx();
						break;
					}
				}
			}
			
			if(cert_idx >= 0) {
			
				switch(type) {
				case 1: //P7 Sign(not included time)
					result = m_usimCertMgr.getUsimSevenSign(plainData, cert_idx, pin, null);
					break;
				case 2: //P7 Sign(include time)
					result = m_usimCertMgr.getUsimSevenSign(plainData, cert_idx, pin, strTime);
					break;
				case 3: //P7 Koscom (not include time)
					result = m_usimCertMgr.getUsimSevenSign(plainData, cert_idx, pin, null);
					break;
				case 4: //P1 Sign
					result = m_usimCertMgr.getUsimSign(plainData, cert_idx, pin, strTime);
					break;
				case 5: //P1 Koscom
					result = m_usimCertMgr.getUsimSign(plainData, cert_idx, pin, strTime);
					break;
				case 6: //P7 Koscom(not include time)
					result = m_usimCertMgr.getUsimSign(plainData, cert_idx, pin, null);
					break;
				default: //P7 Sign(include time)
					result = m_usimCertMgr.getUsimSevenSign(plainData, cert_idx, pin, strTime);
					break;
				}
				setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
			} else {
				setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_CERTLIST);
			}
			
		} catch(RemoteException e) {
			setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
		}
		return result;
	}
	
	public byte [] addUnauthAttr(byte[] signedData, String strOid, byte[] oidVal) {
		byte[] result = null;
		try {
			result = m_usimCertMgr.addUnauthAttr(signedData, strOid, oidVal);
			setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
		} catch(RemoteException e) {
			setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
		}
		return result;
	}
	
	public byte[] getVIDRandom(int index, byte[] pin){
		byte[] result = null;
		try {
			List<UsimCertificate> certlist = null;
			if(m_strOID != null && m_strOID.length() > 0) {
				certlist = m_usimCertMgr.getOIDFilteredUsimCertList(m_strOID, m_bExpired);
			} else {
				if(m_strSubjectDN == null && m_strIssuerDN == null && m_strSerialNumber == null) {
					certlist = m_usimCertMgr.getUsimCertList();
				} else {
					certlist = m_usimCertMgr.getFilteredUsimCertList(m_strSubjectDN, m_strIssuerDN, m_strSerialNumber, m_bExpired);
				}
			}							
			
			int cert_idx = -1;
			if(certlist != null) {
				for(int i = 0; i < certlist.size();i++) {
					//if(certlist.get(i).getCertIdx() == idx) {
					if(index == i) {
						cert_idx = certlist.get(i).getCertIdx();
						break;
					}
				}
			}
			if(cert_idx >= 0) {
				result = m_usimCertMgr.getVIDRandom(cert_idx, pin);
				setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
			} else {
				setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_CERTLIST);
			}
			//setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
		} catch(RemoteException e) {
			setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
		}
		return result;
	}
	
	public boolean getVerifyVID(int index, byte[] pin, byte[] ssn) {
		boolean result = false;
		try {
			List<UsimCertificate> certlist = null;
			if(m_strOID != null && m_strOID.length() > 0) {
				certlist = m_usimCertMgr.getOIDFilteredUsimCertList(m_strOID, m_bExpired);
			} else {
				if(m_strSubjectDN == null && m_strIssuerDN == null && m_strSerialNumber == null) {
					certlist = m_usimCertMgr.getUsimCertList();
				} else {
					certlist = m_usimCertMgr.getFilteredUsimCertList(m_strSubjectDN, m_strIssuerDN, m_strSerialNumber, m_bExpired);
				}
			}							
			
			int cert_idx = -1;
			if(certlist != null) {
				for(int i = 0; i < certlist.size();i++) {
					//if(certlist.get(i).getCertIdx() == idx) {
					if(index == i) {
						cert_idx = certlist.get(i).getCertIdx();
						break;
					}
				}
			}			
			
			if(cert_idx >= 0) {
				result = m_usimCertMgr.getVerifyVID(cert_idx, pin, ssn);
				setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
			} else {
				setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_CERTLIST);
			}			
			//result = m_usimCertMgr.getVerifyVID(index, ssn);
			setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
		} catch(RemoteException e) {
			setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
		}
		return result;
	}
	
	public boolean writeUsimCert(byte[] pin, byte[] cert, byte[] prikey, byte[] passwd) {
		boolean result = false;
		try {
			result = m_usimCertMgr.writeUsimCert(pin, cert, prikey, passwd);
			setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
		} catch(RemoteException e) {
			setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
		}
		return result;
	}
	
	public boolean delUsimCert(int index, byte[] pin) {
		boolean result = false;
		try {
			List<UsimCertificate> certlist = null;
			if(m_strOID != null && m_strOID.length() > 0) {
				certlist = m_usimCertMgr.getOIDFilteredUsimCertList(m_strOID, m_bExpired);
			} else {
				if(m_strSubjectDN == null && m_strIssuerDN == null && m_strSerialNumber == null) {
					certlist = m_usimCertMgr.getUsimCertList();
				} else {
					certlist = m_usimCertMgr.getFilteredUsimCertList(m_strSubjectDN, m_strIssuerDN, m_strSerialNumber, m_bExpired);
				}
			}							
			
			int cert_idx = -1;
			if(certlist != null) {
				for(int i = 0; i < certlist.size();i++) {
					//if(certlist.get(i).getCertIdx() == idx) {
					if(index == i) {
						cert_idx = certlist.get(i).getCertIdx();
						break;
					}
				}
			}	

			if(cert_idx >= 0) {
				result = m_usimCertMgr.deleteUsimCert(cert_idx, pin);
				setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
			} else {
				setErrorMessage(GlobalError.code.NO_RESULT, GlobalError.msg.NO_CERTLIST);
			}					
			//result = m_usimCertMgr.deleteUsimCert(index, pin);
			//setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);
		} catch(RemoteException e) {
			setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
		}
		return result;
	}
	
	public boolean checkPIN(byte[] pin) {
		boolean result = false;
		try {
			result = m_usimCertMgr.getCheckPIN(pin);
			setErrorMessage(GlobalError.code.NORMAL, GlobalError.msg.NORMAL);			
		} catch(RemoteException e) {
			setErrorMessage(GlobalError.code.SERVICE_CONNECT, GlobalError.msg.SERVICE_CONNECT);
		}
		return result;
	}
	/**
	 * 부가서비스 가입 여부 및 연동 가능 상태 조회 요청
	 * @param listener - 결과 처리 리스너
	 */
	public void checkJoin(OnCheckJoinResult listener) {
		if(!isReady()) {
			listener.onCheckJoinResult(getErrorCode());
			return;
		}

		Argument arg = new Argument();
		arg.m_strVal1 = m_context.getPackageName();

		runBackground(USIM_RESULT_JOIN, arg, listener);
	}

	/**
	 * USIM 내 인증서 개수 조회
	 * @param listener - 결과 처리 리스너
	 */
	public void getUsimCertCnt(OnCertCntResult listener) {
		if(!isReady()) {
			listener.onCertCntResult(-1);
			return;
		}

		runBackground(USIM_RESULT_CERT_CNT, null, listener);
	}

	/**
	 * USIM 내 현재 저장 가능한 인증서 개수 조회
	 * @param listener - 결과 처리 리스너
	 */
	public void getFreeCertCnt(OnFreeCntResult listener) {
		if(!isReady()) {
			listener.onFreeCntResult(-1);
			return;
		}

		runBackground(USIM_RESULT_FREE_CNT, null, listener);
	}

	/**
	 * USIM 내 인증서 조회
	 * @param nIdx - 조회할 인덱스
	 * @param listener - 결과 처리 리스너
	 */
	public void getUsimCert(int nIdx, OnGetCertResult listener) {
		if(!isReady()) {
			listener.onGetCertResult(null);
			return;
		}

		Argument arg = new Argument();
		arg.m_nVal1 = nIdx;

		runBackground(USIM_RESULT_CERT, arg, listener);
	}

	/**
	 * USIM 내 인증서 목록 조회
	 * @param listener - 결과 처리 리스너
	 */
	public void getUsimCertList(OnCertListResult listener)  {
		if(!isReady()) {
			listener.onCertListResult(null);
			return;
		}

		runBackground(USIM_RESULT_CERT_LIST, null, listener);
	}

	/**
	 * USIM 내 인증서 목록 조회(필터 적용)
	 * @param strSubjectDN - Subject DN
	 * @param strIssuerDN - Issuer DN
	 * @param strSerialNo - Serial Number
	 * @param bShowExpired - 만료된 인증서 포함 여부
	 * @param listener - 결과 처리 리스너
	 */
	public void getFilteredUsimCertList(String strSubjectDN, String strIssuerDN, String strSerialNo, boolean bShowExpired, OnCertListResult listener)  {
		if(!isReady()) {
			listener.onCertListResult(null);
			return;
		}

		Argument arg = new Argument();
		arg.m_strVal1 = strSubjectDN;
		arg.m_strVal2 = strIssuerDN;
		arg.m_strVal3 = strSerialNo;
		arg.m_bVal1 = bShowExpired;

		runBackground(USIM_RESULT_CERT_LIST, arg, listener);
	}

	/**
	 * SD card 내 인증서 목록 조회
	 * @param listener - 결과 처리 리스너
	 */
	public void getSDCardCertList(OnCertListResult listener)  {
		if(!isReady()) {
			listener.onCertListResult(null);
			return;
		}

		runBackground(USIM_RESULT_SD_CERT_LIST, null, listener);
	}

	/**
	 * USIM 내 인증서로 원문을 전자서명 (PKCS#1 서명)
	 * @param plainData - 서명할 원문
	 * @param nIdx - 선택 인증서 index
	 * @param passwd - 스마트 인증 비밀번호
	 * @param strTime - 서명 시간
	 * @param listener - 결과 처리 리스너
	 */
	public void getUsimSign(byte[] plainData, int nIdx, byte[] passwd, String strTime, OnSignResult listener) {
		if (!isReady()) {
			listener.onSignResult(null);
			return;
		}

		Argument arg = new Argument();
		arg.m_arrByte1 = plainData;
		arg.m_nVal1 = nIdx;
		arg.m_arrByte2 = passwd;
		arg.m_strVal1 = strTime;

		runBackground(USIM_RESULT_SIGN, arg, listener);
	}

	/**
	 * USIM 내 인증서로 원문을 전자서명 (PKCS#7 서명)
	 * @param plainData - 서명할 원문
	 * @param nIdx - 선택 인증서 index
	 * @param passwd - 스마트 인증 비밀번호
	 * @param strTime - 서명 시간
	 * @param listener - 결과 처리 리스너
	 */
	public void getUsimSevenSign(byte[] plainData, int nIdx, byte[] passwd, String strTime, OnSevenSignResult listener) {
		if (!isReady()) {
			listener.onSevenSignResult(null);
			return;
		}

		Argument arg = new Argument();
		arg.m_arrByte1 = plainData;
		arg.m_nVal1 = nIdx;
		arg.m_arrByte2 = passwd;
		arg.m_strVal1 = strTime;

		runBackground(USIM_RESULT_SEVEN_SIGN, arg, listener);
	}

	/**
	 * PKCS#7 서명 데이터에 특정 속성 추가
	 * @param signedData - PKCS#7 서명 데이터
	 * @param strOid - PKCS#7에 추가할 OID
	 * @param oidVal - PKCS#7에 추가할 OID 정보
	 * @param listener - 결과 처리 리스너
	 */
	public void addUnauthAttr(byte[] signedData, String strOid, byte[] oidVal, OnAddAttrResult listener) {
		if (!isReady()) {
			listener.onAddAttrResult(null);
			return;
		}

		Argument arg = new Argument();
		arg.m_arrByte1 = signedData;
		arg.m_strVal1 = strOid;
		arg.m_arrByte2 = oidVal;

		runBackground(USIM_RESULT_ADD_ATTR, arg, listener);
	}

	/**
	 * 인증서의 개인키 R값 조회
	 * @param nIdx - 선택 인증서 index
	 * @param passwd - 스마트 인증 비밀번호
	 * @param listener - 결과 처리 리스너
	 */
	public void getVIDRandom(int nIdx, byte[] passwd, OnVIDRandomResult listener) {
		if (!isReady()) {
			listener.onVIDRandomResult(null);
			return;
		}

		Argument arg = new Argument();
		arg.m_nVal1 = nIdx;
		arg.m_arrByte1 = passwd;

		runBackground(USIM_RESULT_VID_RANDOM, arg, listener);
	}
	
	/**
	 * 토큰 정보 조회(여유 공간 및 USIM Serial(ICCID) 조회)
	 * @param listener - 결과 처리 리스너
	 */
	public void getTokenInfo(OnTokenInfoResult listener) {
		if (!isReady()) {
			listener.onTokenInfoResult(null);
			return;
		}

		runBackground(USIM_RESULT_TOKEN_INFO, null, listener);
	}

	/**
	 * USIM 내 인증서 발급
	 * @param nCa - 발급 요청 CA index
	 * @param strRefNum - 참조번호
	 * @param strAuthCode - 인가코드
	 * @param passwd - 스마트 인증 비밀번호
	 * @param listener - 결과 처리 리스너
	 */
	public void issueUsimCert(int nCa, String strRefNum, String strAuthCode, byte[] passwd, OnIssueResult listener) {
		if (!isReady()) {
			listener.onIssueResult(false);
			return;
		}

		Argument arg = new Argument();
		arg.m_nVal1 = nCa;
		arg.m_strVal1 = strRefNum;
		arg.m_strVal2 = strAuthCode;
		arg.m_arrByte1 = passwd;

		runBackground(USIM_RESULT_ISSUE, arg, listener);
	}

	/**
	 * USIM 내 인증서 갱신
	 * @param nIdx - 선택 인증서 index
	 * @param nCa - 발급 요청 CA index
	 * @param passwd - 스마트 인증 비밀번호
	 * @param listener - 결과 처리 리스너
	 */
	public void updateUsimCert(int nIdx, int nCa, byte[] passwd, OnUpdateResult listener) {
		if (!isReady()) {
			listener.onUpdateResult(false);
			return;
		}

		Argument arg = new Argument();
		arg.m_nVal1 = nIdx;
		arg.m_nVal2 = nCa;
		arg.m_arrByte1 = passwd;

		runBackground(USIM_RESULT_UPDATE, arg, listener);
	}

	/**
	 * SD card 의 인증서를 USIM 에 저장
	 * @param strCertPath - SD card 의 인증서 경로
	 * @param strPrivPath - SD card 의 개인키 경로
	 * @param certPasswd - SD card 의 인증서 비밀번호
	 * @param passwd - 스마트 인증 비밀번호
	 * @param listener - 결과 처리 리스너
	 */
	public void saveUsimCert(String strCertPath, String strPrivPath, byte[] certPasswd, byte[] passwd, OnSaveResult listener) {
		if (!isReady()) {
			listener.onSaveResult(false);
			return;
		}

		Argument arg = new Argument();
		arg.m_strVal1 = strCertPath;
		arg.m_strVal2 = strPrivPath;
		arg.m_arrByte1 = certPasswd;
		arg.m_arrByte2 = passwd;

		runBackground(USIM_RESULT_SAVE, arg, listener);
	}

	/**
	 * USIM 내 인증서 삭제
	 * @param nIdx - 선택 인증서 index
	 * @param passwd - 스마트 인증 비밀번호
	 * @param listener - 결과 처리 리스너
	 */
	public void deleteUsimCert(int nIdx, byte[] passwd, OnDelResult listener) {
		if (!isReady()) {
			listener.onDelResult(false);
			return;
		}

		Argument arg = new Argument();
		arg.m_nVal1 = nIdx;
		arg.m_arrByte1 = passwd;

		runBackground(USIM_RESULT_DEL, arg, listener);
	}

	/**
	 * Service 연동 인자 class
	 * @author hyunboklee
	 */
	class Argument {
		int m_nVal1;
		int m_nVal2;
		String m_strVal1;
		String m_strVal2;
		String m_strVal3;
		byte[] m_arrByte1;
		byte[] m_arrByte2;
		boolean m_bVal1;
	}

//	public byte[] setUnauthData(){
//			
//			INISAFESign sign = new INISAFESign();
//		try{
//			mRandomValue = sign.getCipherAttribute(mRandomValue, mSignTime, 1);
//			if(mPKCS7Sign != null) {
//			
//				mPKCS7SignAdd = m_usimCertMgr.addUnauthAttr(mPKCS7Sign, sign.getInitechRandomOID(), mRandomValue);
//				if(mPKCS7SignAdd == null){
//					UsimCertError usimCertError = m_usimCertMgr.getErrorMessage();
//					m_strErrCode = usimCertError.getErrorCode();
//					m_strErrMsg =  usimCertError.getErrorMessage();
//					Log.e("TEST", "mPKCS7SingAdd.getErrorCode() : " + usimCertError.getErrorCode());
//					Log.e("TEST", "mPKCS7SingAdd.getErrorMessage() : " + usimCertError.getErrorMessage());
//					return null;
//				}
//				else
//				{
//					Log.e("TEST", "mPKCS7SingAdd : " + new String(mPKCS7Sign));
//				}
//			}else{
//				return null;
//			}
//			new SignVerifyTask().execute();
//			
//			
//		} catch (RemoteException e2) {
//			e2.printStackTrace();
//		}
//	
//		return mPKCS7SignAdd;
//	}
//
//	class SignVerifyTask extends AsyncTask<String, Integer, Integer> {
//		
//		ServerConnector timeHttps = null;
//		String resTime = null;
//		NetServerConnector loginHttps = null;
//		NetResult netRes = null;
//		String res = null;
//		
//		@Override
//		protected Integer doInBackground(String... args) {
//			
//			try {
//				byte[] signData = Base64.encode(mPKCS7SignAdd, Base64.DEFAULT);
//				loginHttps = new NetServerConnector(LOGIN_URL);
//				loginHttps.setParameter("JuminNO", "1234");
//				loginHttps.setParameter("PKCS7SignedData", new String(signData));
//				netRes = loginHttps.getNetResponse();
//		    	res = netRes.getFullResMessage();
//		    	Log.e("TEST", "Login test result : " + res);
//		    	
//		    	
//		    	
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//				res = e.toString();
//			} catch (ProtocolException e) {
//				e.printStackTrace();
//				res = e.toString();
//			} catch (Exception e) {
//				e.printStackTrace();
//				res = e.toString();
//			}
//	    	
//	    	return -1; 
//		}   
//		
//		@Override
//		protected void onPostExecute(Integer result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//		}
//
//		public String result(){
//			
//			return res;
//		}
//	}
}
