package com.sumion.usim.util;


/**
 * Error code/문구 정의 class
 * @author hyunboklee
 */
public final class GlobalError {
	/**
	 * Error code
	 */
	public static final class code {
		/** 정상적인 상태 */
		public static final String NORMAL = "000";
		/** 사용자가 취소 했을 경우 */
		public static final String CANCEL = "A00";
		/** 사용자가 인증번호 입력횟수를 모두 초과했을 경우 */
		public static final String CERT_FAIL = "A01";
		/** 스마트인증 비밀번호를 10번 다 틀려서 애플릿이 초기화 되었을 경우 */
		public static final String PASS_INITED = "A02";
		/** USIM 연결에 실패하였을 경우 */
		public static final String USIM_CONNECT_FAIL = "A03";
		/** USIM 처리 중 PKCS#11 함수 호출 시 발생한 오류 */
		public static final String USIM_PKCS11 = "A04";
		/** 인증서 리스트가 없을 경우 */
		public static final String NO_CERTLIST = "A05";
		/** USIM 처리 중 발생한 기타 오류 */
		public static final String USIM_ETC = "A06";
		/** USIM 처리 중 발생한 알 수 없는 오류. 하단에 에러 원인 표시 */
		public static final String USIM_UNKNOWN = "A07";
		/** 스마트인증 비밀번호 오류 */
		public static final String WRONG_PASS = "002";
		/** 토큰이 잠긴 상태 또는 초기화된 경우 */
		public static final String TOKEN_STATUS = "003";
		/** 개인키 비밀번호 오류 */
		public static final String WRONG_PRIV_PASS = "004";
		/** 동일한 인증서가 이미 저장되어 있는 경우 */
		public static final String ALREADY_EXIST = "005";
		/** 서비스 미가입자 */
		public static final String JOIN_NOT = "010";
		/** 타 CP 서비스 가입자 */
		public static final String JOIN_OTHER_CP = "011";
		/** 해당 통신사 미가입자(LGU+ or KT) */
		public static final String JOIN_OTHER_NET = "012";
		/** 부가서비스 가입 실패 */
		public static final String JOIN_FAIL = "013";
		/** 지원하지 않는 통신사(LGU+ or KT) */
		public static final String JOIN_NOT_SERVICE_NET = "015";
		/** 지원하지 않는 단말 */
		public static final String JOIN_NOT_SERVICE_PHONE = "016";
		/** 서버에서 보낼 데이터 없음 */
		public static final String A102R_030 = "030";
		/** A102S를 일정시간이상 대기하여 앱 종료하기 위한 에러 */
		public static final String A102R_TIMEOUT = "031";
		/** SEIO Agent 미설치 */
		public static final String AGENT_NOT_INSTALLED = "080";
		/** Applet 상태 오류 */
		public static final String APPLET_ERROR = "081";
		/** 미등록 앱 */
		public static final String APP_UNREGISTER = "091";
		/** 앱 변조 탐지 */
		public static final String APP_FALSIFY = "092";
		/** 새로운 앱을 설치해야 됨(마이너 버전)(취소 가능) */
		public static final String APP_MINOR_UPDATE = "093";
		/** 루팅 탐지 */
		public static final String PHONE_ROOTING = "094";
		/** 새로운 앱을 무조전 설치해야 됨(메이저 버전) */
		public static final String APP_MAJOR_UPDATE = "095";
		/** 위변조 확인 불가 */
		public static final String APP_UNABLE_CHECK = "096";
		/** 중복 작업 방지 오류(AIDL이 이미 연결된 경우 또는 App 실행 중인 경우) */
		public static final String DUPLICATE = "097";
		/** 사용 권한이 없는 package */
		public static final String PACKAGE_PRIVILEGE = "099";
		/** AIDL에서 부가서비스 조회를 하지 않았을 경우 */
		public static final String AIDL_JOIN = "100";
		/** 각종 조회 결과값이 없을 경우 */
		public static final String NO_RESULT = "101";
		/** 서비스 연결 오류 */
		public static final String SERVICE_CONNECT = "901";
		/** 선행 작업 진행 중인 경우 */
		public static final String IN_USE = "902";
		/** 시스템 오류 */
		public static final String SYSTEM = "950";
	}

	/**
	 * Error message
	 */
	public static final class msg {
		/** 정상적인 상태 */
		public static final String NORMAL = "정상 처리되었습니다.";
		/** 사용자가 취소 했을 경우 */
		public static final String CANCEL = "사용자가 취소했습니다.";
		/** 사용자가 인증번호 입력횟수를 모두 초과했을 경우 */
		public static final String CERT_FAIL = "인증번호 재시도 횟수를 초과하였습니다.";
		/** 스마트인증 비밀번호를 10번 다 틀려서 애플릿이 초기화 되었을 경우 */
		public static final String PASS_INITED = "스마트인증 비밀번호 오류 횟수가 초과되어 Applet이 초기화 되었습니다. 앱을 다시 실행해 주세요.";
		/** USIM 연결에 실패하였을 경우 */
		public static final String USIM_CONNECT_FAIL = "USIM 연결에 실패하였습니다.\n에러코드 : ";
		/** USIM 처리 중 PKCS#11 함수 호출 시 발생한 오류 */
		public static final String USIM_PKCS11 = "USIM에서 에러가 발생했습니다.\n에러코드 : ";
		/** 인증서 리스트가 없을 경우 */
		public static final String NO_CERTLIST = "USIM에 저장된 인증서가 없습니다.";
		/** USIM 처리 중 발생한 기타 오류 */
		public static final String USIM_ETC = "USIM에서 에러가 발생했습니다.\n에러코드 : ";
		/** USIM 처리 중 발생한 알 수 없는 오류. 하단에 에러 원인 표시 */
		public static final String USIM_UNKNOWN = "USIM에서 알 수 없는 에러가 발생했습니다.";
		/** 스마트인증 비밀번호 오류 */
		public static final String WRONG_PASS = "스마트인증 비밀번호 오류 횟수가 %d회 남았습니다.";
		/** 토큰이 잠긴 상태 또는 초기화된 경우 */
		public static final String TOKEN_STATUS = "스마트인증 비밀번호 설정이 필요합니다. 스마트 USIM 인증 App을 실행해 주세요.";
		/** 개인키 비밀번호 오류 */
		public static final String WRONG_PRIV_PASS = "공인인증서 비밀번호가 맞지 않습니다.";
		/** 동일한 인증서가 이미 저장되어 있는 경우 */
		public static final String ALREADY_EXIST = "동일한 인증서가 이미 저장되어 있습니다.";
		/** 서비스 미가입자 */
		public static final String JOIN_NOT = "통신사 부가서비스에 가입하지 않은 고객입니다.";
		/** 타 CP 서비스 가입자 */
		public static final String JOIN_OTHER_CP = "스마트인증(유심) 부가서비스에 가입된 고객입니다.";
		/** 해당 통신사 미가입자(LGU+ or KT) */
		public static final String JOIN_OTHER_NET = "SKT에 가입되지 않은 고객입니다.";
		/** 부가서비스 가입 실패 */
		public static final String JOIN_FAIL = "SKT 부가서비스 가입에 실패했습니다.";
		/** 지원하지 않는 통신사(LGU+ or KT) */
		public static final String JOIN_NOT_SERVICE_NET = "서비스를 지원하지 않는 통신사입니다.";
		/** 지원하지 않는 단말 */
		public static final String JOIN_NOT_SERVICE_PHONE = "지원하지 않는 단말입니다.";
//		/** 서버에서 보낼 데이터 없음 */
//		public static final String A102R_030 = "";
		/** A102S를 일정시간이상 대기하여 앱 종료하기 위한 에러 */
		public static final String A102R_TIMEOUT = "대기시간이 초과되었습니다. 종료하시겠습니까?";
		/** SEIO Agent 미설치 */
		public static final String AGENT_NOT_INSTALLED = "SEIO Agent가 설치되지 않았습니다. 설치 후 사용해 주세요.";
		/** Applet 상태 오류 */
		public static final String APPLET_ERROR = "USIM Applet 확인 오류가 발생되었습니다.";
		/** 미등록 앱 */
		public static final String APP_UNREGISTER = "미등록된 앱입니다. 등록된 앱을 설치한 후 사용해주세요.";
		/** 새로운 앱을 설치해야 됨(마이너 버전)(취소 가능) */
		public static final String APP_MINOR_UPDATE = "스마트폰의 앱의 새로운 버전이 나왔습니다. 새로운 버전을 설치 후 사용하시겠습니까?";
		/** 루팅 탐지 */
		public static final String PHONE_ROOTING = "스마트폰에서 루팅이 탐지되어 사용할 수 없습니다.";
		/** 새로운 앱을 무조전 설치해야 됨(메이저 버전) */
		public static final String APP_MAJOR_UPDATE = "스마트폰의 앱을 새로운 버전으로 설치한 후 사용해주세요.";
		/** 위변조 확인 불가 */
		public static final String APP_UNABLE_CHECK = "위변조를 확인할 수 없어 사용할 수 없습니다.";
		/** 중복 작업 방지 오류(AIDL이 이미 연결된 경우 또는 App 실행 중인 경우) */
		public static final String DUPLICATE = "외부모듈에서 서비스를 이미 사용 중 이거나 앱이 실행 중 입니다. 종료 후 재 실행해 주세요.";
		/** 사용 권한이 없는 package */
		public static final String PACKAGE_PRIVILEGE = "서비스 사용 권한이 없습니다.";
		/** AIDL에서 부가서비스 조회를 하지 않았을 경우 */
		public static final String AIDL_JOIN = "부가서비스 조회를 해주세요.";
		/** 각종 조회 결과값이 없을 경우 */
		public static final String NO_RESULT = "조회 결과가 없습니다.";
		/** 서비스 연결 오류 */
		public static final String SERVICE_CONNECT	= "서비스 연결에 문제가 발생되었습니다.";
		/** 선행 작업 진행 중인 경우 */
		public static final String IN_USE = "서비스가 이미 사용 중 입니다.";
		/** 시스템 오류 */
		public static final String SYSTEM = "시스템 오류가 발생되었습니다.";
	}
}
