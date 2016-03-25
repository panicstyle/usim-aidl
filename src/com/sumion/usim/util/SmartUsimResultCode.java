package com.sumion.usim.util;

public class SmartUsimResultCode {
	
	// 에러코드
	public static final int RESULT_OK						= 0;	// 에러없음
	
	// 기본 결과
	public static final int RESULT_IS_MEMBER				= 1001;	// isMember()를 호출해서 서비스밴더를 확인해야 한다.
	public static final int RESULT_BIND_FAIL				= 1002; // Bind 실패
	public static final int RESULT_JOIN_FAIL				= 1003; // Join 실패
	public static final int RESULT_IMPOSSIBLE_SERVICE		= 1004; // isServiceValid()를 호출해야 합니다.
	
	// 가입자 결과
	public static final int RESULT_USER_NO_MEMBER			= 2001;	// 미 가입자
	public static final int RESULT_USER_USIM_MEMBER			= 2002; // USIM 서비스 가입자 (Dream or Raon)
	public static final int RESULT_USER_SIMILAR_MEMBER		= 2003; // 유사 서비스 가입자 (Ubikey 등..)
	public static final int RESULT_USER_IMPOSSIBLE_MEMBER	= 2004; // 서비스 불가 사용자 (위의 상황을 제외한 나머지)
	public static final int RESULT_USER_NOT_TELECOM_MEMBER	= 2005; // 통신사 가입자가 아님
	public static final int RESULT_USER_SERVICE_STATUS_STOP	= 2006; // 일시정지 단말기 상태
	public static final int RESULT_USER_NOT_SUPPORTED_MEMBER= 2007; // 선불폰 사용자
	public static final int RESULT_USER_NOT_OPEN_USIM		= 2008; // 개통되지 않은 단말기
	public static final int RESULT_USER_NOT_SUPPORTED_DEVICE= 2009; // 지원하지 않는 단말기
	public static final int RESULT_USER_NOT_SUPPORTED_USIM	= 2010; // 지원하지 않는 USIM
	
	// 서비스 사용 할 수 있는 상태 확인
	public static final int RESULT_APP_NOT_SUPPORT			= 3001; // 허가되지 않은 앱의 접근
	public static final int RESULT_APP_REQUIED_UPDATE		= 3002; // 필수 업데이트
		
	// 통신 오류
	public static final int RESULT_NETWORK_CONNECT			= 4001;	// 서버 커넥션 접근 오류
	public static final int RESULT_NETWORK_ERROR			= 4002; // 서버와 통신 중 접근 오류
		
	// 스마트인증 및 인증서 비밀번호 결과
	public static final int RESULT_PW_PIN_INCORRECT			= 5001; // 스마트인증 비밀번호가 틀림
	public static final int RESULT_PW_PIN_LOCK				= 5002; // 스마트인증 비밀번호가 10번 틀리고 초기화 될 때
	public static final int RESULT_PW_PIN_INIT_STATE		= 5003; // 스마트인증 비밀번호 설정이 필요
	public static final int RESULT_PW_PIN_INVALID_LENGTH	= 5004; // 스마트인증 비밀번호 길이 정보 틀림
	public static final int RESULT_PW_CERT_INCORRECT		= 5005; // 인증서 비밀번호 틀림
	public static final int RESULT_SAME_CERT_EXIST			= 5006; // 동일한 인증서 존재

	// 지원하지 않는 서비스 종류
	public static final int RESULT_NOT_EXIST_VID_RANDOM		= 6001; // 개인키에 VID R값이 존재하지 않음.
	public static final int RESULT_NOT_EXIST_FILTER_CERT	= 6002; // 검색된 인증서가 없음
	public static final int RESULT_NOT_ENOUGH_CERT_STORAGE	= 6003; // 저장공간이 없음.
	public static final int RESULT_NOT_SUPPORTED_ROOTING	= 6004; // 루팅 단말기에 지원하지 않음.
	
	// API의 개별 오류 코드
	public static final int RESULT_ISPOSSIBLESERVICE_FAIL	= 7001; // isPossibleService()
	public static final int RESULT_JOINSERVICE_FAIL			= 7002; // JoinService()
	public static final int RESULT_GETTOKENINFO_FAIL		= 7003; // getTokenInfo()
	public static final int RESULT_SETFILTER_FAIL			= 7004; // SetFilter()
	public static final int RESULT_GET_CERT_COUNT_FAIL		= 7005; // getCertCount()
	public static final int RESULT_GETCERTIFICATE_FAIL		= 7006; // getCertificate()
	public static final int RESULT_GET_ATTRIBUTES_FAIL		= 7007; // getAttribute()
	public static final int RESULT_SIGN_FAIL				= 7008; // sign()
	public static final int RESULT_ADD_UNSIGNED_ATTRIBUTE_SIGNEDDDATA_FAIL = 7009; // addUnsignedAttributeSignedData()
	public static final int RESULT_GET_VID_RANDOM_FAIL		= 7010; // getVidRandom()
	public static final int RESULT_VERIFY_VID_FAIL			= 7011; // verifyVID()
	public static final int RESULT_WRITE_CERT_USIM_FAIL		= 7012; // writeCertUsim()
	public static final int RESULT_DELETE_CERT_USIM_FAIL	= 7013; // deleteCertUsim()
	public static final int RESULT_LOGIN_FAIL				= 7014; // login
	public static final int RESULT_LOGOUT_FAIL				= 7015; // logout
	public static final int RESULT_CHANGEPIN_FAIL			= 7016; // changePIN()
	public static final int RESULT_INITPIN_FAIL				= 7017; // initPIN()
	
	
	public static final int RESULT_INVALID_PARAM			= 9001; // 전달인자값이 잘못 되었을 경우
	public static final int RESULT_CODE_INSTALL_APP_FAIL	= 9002; // 앱 설치 실패
	public static final int RESULT_REMOTE_EXCEPTION			= 9003; // 바인드가 풀렸을 경우
	public static final int RESULT_CORE_INIT_FAIL			= 9004; // Core Fail
	
	public static final String CONTACT_VENDER_RAON = "RAON";
	public static final String CONTACT_VENDER_DREAM = "DREAM";
	public static final String CONTACT_VENDER_SUMION = "SUMION";
	public static final String CONTACT_RAON = "1644-5128";
	public static final String CONTACT_DREAM = "1688-0124";
	public static final String CONTACT_SUMION = "1670-6623";
	
	//------------------------------------개발 중---------------------------------
	//getTokenInfo
	
	private int mResultCode = RESULT_OK;
	private int mResultCodeVendor = 0;
	
	
	protected void setResultCode( int resultCode, int resultCodeVendor ) {
		mResultCode = resultCode;
		mResultCodeVendor = resultCodeVendor;
	}
	
	public int getResultCode() {
		return mResultCode;
	}
	
	public int getResultCodeVendor() {
		return mResultCodeVendor;
	}
}
