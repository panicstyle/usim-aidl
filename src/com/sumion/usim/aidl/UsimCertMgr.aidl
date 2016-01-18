package com.sumion.usim.aidl;
import com.sumion.usim.aidl.UsimCertificate;
import com.sumion.usim.aidl.UsimTokenInfo;
import com.sumion.usim.aidl.UsimCertError;

interface UsimCertMgr {
	/**
	 * USIM 내 인증서 조회
	 * @param nIdx - 조회할 인덱스
	 * @return UsimCertificate - 인증서 정보 객체
	 * @throws RemoteException
	 */
	UsimCertificate getUsimCert(int nIdx);

	/**
	 * USIM 내 인증서 개수 조회
	 * @return int - 인증서 개수
	 * @throws RemoteException
	 */
	int getUsimCertCnt();

	/**
	 * USIM 내 현재 저장 가능한 인증서 개수 조회
	 * @return int - 저장 가능 개수
	 * @throws RemoteException
	 */
	int getFreeCertCnt();

	/**
	 * USIM 내 인증서 목록 조회
	 * @return List<UsimCertificate> - 인증서 목록
	 * @throws RemoteException
	 */
	List<UsimCertificate> getUsimCertList();

	/**
	 * USIM 내 인증서 목록 조회(필터 적용)
	 * @param strOID - OID
	 * @param strIssuerDN - Issuer DN
	 * @param strSerialNo - Serial Number
	 * @param bShowExpired - 만료된 인증서 포함 여부
	 * @return List<UsimCertificate> - 필터된 인증서 목록
	 * @throws RemoteException
	 */
	List<UsimCertificate> getOIDFilteredUsimCertList(String strOID, boolean bShowExpired);

	/**
	 * USIM 내 인증서 목록 조회(필터 적용)
	 * @param strSubjectDN - Subject DN
	 * @param strIssuerDN - Issuer DN
	 * @param strSerialNo - Serial Number
	 * @param bShowExpired - 만료된 인증서 포함 여부
	 * @return List<UsimCertificate> - 필터된 인증서 목록
	 * @throws RemoteException
	 */
	List<UsimCertificate> getFilteredUsimCertList(String strSubjectDN, String strIssuerDN, String strSerialNo, boolean bShowExpired);

	/**
	 * SD card 내 인증서 목록 조회
	 * @return List<UsimCertificate> - 인증서 목록
	 * @throws RemoteException
	 */
	List<UsimCertificate> getSDCardCertList();

	/**
	 * USIM 내 인증서로 원문을 전자서명 (PKCS#1 서명)
	 * @param plainData - 서명할 원문
	 * @param nIdx - 선택 인증서 index
	 * @param passwd - 스마트 인증 비밀번호
	 * @param strTime - 서명 시간
	 * @return byte[] - PKCS#1 서명 데이터 반환
	 * @throws RemoteException
	 */
	byte[] getUsimSign(in byte[] plainData, int nIdx, in byte[] passwd, String strTime);

	/**
	 * USIM 내 인증서로 원문을 전자서명 (PKCS#7 서명)
	 * @param plainData - 서명할 원문
	 * @param nIdx - 선택 인증서 index
	 * @param passwd - 스마트 인증 비밀번호
	 * @param strTime - 서명 시간
	 * @return byte[] - PKCS#7 서명 데이터 반환
	 * @throws RemoteException
	 */
	byte[] getUsimSevenSign(in byte[] plainData, int nIdx, in byte[] passwd, String strTime);

	/**
	 * PKCS#7 서명 데이터에 특정 속성 추가
	 * @param signedData - PKCS#7 서명 데이터
	 * @param strOid - PKCS#7에 추가할 OID
	 * @param oidVal - PKCS#7에 추가할 OID 정보
	 * @return byte[] - 속성 추가된 PKCS#7 서명 데이터
	 * @throws RemoteException
	 */
	byte[] addUnauthAttr(in byte[] signedData, String strOid, in byte[] oidVal);

	/**
	 * 인증서의 개인키 R값 조회
	 * @param nIdx - 선택 인증서 index
	 * @param passwd - 스마트 인증 비밀번호
	 * @return byte[] - 조회된 R 값
	 * @throws RemoteException
	 */
	byte[] getVIDRandom(int nIdx, in byte[] passwd);

	/**
	 * 인증서의 본인확인
	 * @param nIdx - 선택 인증서 index
	 * @param ssn - 주민등록번호
	 * @return boolean - 결과
	 * @throws RemoteException
	 */
	boolean getVerifyVID(int nIdx, in byte[] pin, in byte[] ssn);


	/**
	 * 토큰 정보 조회(여유 공간 및 USIM Serial(ICCID) 조회)
	 * @return UsimTokenInfo - 토큰 정보
	 */
	UsimTokenInfo getTokenInfo();//토큰 정보 조회(여유 공간 및 USIM Serial(UICC) 조회)

	/**
	 * USIM 내 인증서 발급
	 * @param nCa - 발급 요청 CA index
	 * @param strRefNum - 참조번호
	 * @param strAuthCode - 인가코드
	 * @param passwd - 스마트 인증 비밀번호
	 * @return boolean - 인증서 발행 성공 여부
	 * @throws RemoteException
	 */
	boolean issueUsimCert(int nCa, String strRefNum, String strAuthCode, in byte[] passwd);

	/**
	 * USIM 내 인증서 갱신
	 * @param nIdx - 선택 인증서 index
	 * @param nCa - 갱신 요청 CA index
	 * @param passwd - 스마트 인증 비밀번호
	 * @return boolean - 인증서 발행 성공 여부
	 * @throws RemoteException
	 */
	boolean updateUsimCert(int nIdx, int nCa, in byte[] passwd);

	/**
	 * SD card 의 인증서를 USIM 에 저장
	 * @param strCertPath - SD card 의 인증서 경로
	 * @param strPrivPath - SD card 의 개인키 경로
	 * @param certPasswd - SD card 의 인증서 비밀번호
	 * @param passwd - 스마트 인증 비밀번호
	 * @return boolean - 인증서 저장 성공 여부
	 * @throws RemoteException
	 */
	boolean saveUsimCert(String strCertPath, String strPrivPath, in byte[] certPasswd, in byte[] passwd);

	/**
	 * 수신된 인증서 및 개인키를 USIM 에 저장
	 * @param pin - 스마트인증 비밀번호
	 * @param cert - 인증서 데이터
	 * @param prikey - 개인키 데이터
	 * @param passwd - 개인키 비밀번호
	 * @return boolean - 인증서 및 개인키 저장 성공 여부
	 * @throws RemoteException
	 */
	boolean writeUsimCert(in byte[] pin, in byte[] cert, in byte[] prikey, in byte[] passwd);

	/**
	 * USIM 내 인증서 삭제
	 * @param nIdx - 선택 인증서 index
	 * @param passwd - 스마트 인증 비밀번호
	 */
	boolean deleteUsimCert(int nIdx, in byte[] passwd);

	/**
	 * 스마트인증 비밀번호 체크
	 * @param passwd - 스마트 인증 비밀번호
	 * @return boolean - 체크 결과
	 * @throws RemoteException
	 */
	boolean getCheckPIN(in byte[] pin);

	/**
	 * Error 정보 획득
	 * @return UsimCertError - Error 정보
	 * @throws RemoteException
	 */
	UsimCertError getErrorMessage();

	/**
	 * 서비스 가입 여부 조회
	 * @param strPkgName - 접속 요청 package 명
	 * @return String - 결과 값<br>
	 * 000 - 서비스 가입자<br>
	 * 010 - 서비스 미가입자<br>
	 * 090 - Token 상태 오류(잠금 또는 초기화 상태)<br>
	 * 093 - Minor update 필요<br>
	 * 095 - Major update 필요<br>
	 * 097 - AIDL이 이미 연결된 경우 또는 App 실행 중인 경우<br>
	 * 099 - 사용 권한이 없는 package<br>
	 * 999 - error
	 * @throws RemoteException
	 */
	String checkJoin(String strPkgName);
}
