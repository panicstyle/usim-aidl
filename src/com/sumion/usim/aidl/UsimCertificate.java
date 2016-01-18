package com.sumion.usim.aidl;

import java.security.cert.X509Certificate;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 스마트 USIM 인증서 정보 class
 */
public class UsimCertificate implements Parcelable {
	/** X.509 인증서 */
	private X509Certificate m_x509Certificate;
	/** USIM 인증서 index */
	private int m_nCertIdx;
	/** SD card 에 저장된 인증서 경로 */
	private String m_strCertPath;
	/** SD card 에 저장된 개인키 경로 */
	private String m_strPrivPath;
	
	private String m_strOID;
	private String m_strType;
	private String m_strIssuer;
	
	public UsimCertificate() {
	}

	public UsimCertificate(Parcel In) {
		readFromParcel(In);
	}

	public UsimCertificate(X509Certificate x509, int nIdx) {
		m_x509Certificate = x509;
		m_nCertIdx = nIdx;
	}

	public UsimCertificate(X509Certificate x509, String strCertPath, String strPrivPath) {
		m_x509Certificate = x509;
		m_strCertPath = strCertPath;
		m_strPrivPath = strPrivPath;
	}

	public UsimCertificate(X509Certificate x509, int nIdx, String oid, String type) {
		m_x509Certificate = x509;
		m_nCertIdx = nIdx;
		m_strOID = oid;
		m_strType = type;
	}
	
	/**
	 * X.509 인증서 반환
	 * @return X509Certificate - X.509 인증서
	 */
	public X509Certificate getCert() {
		return m_x509Certificate;
	}

	/**
	 * USIM 인증서 index 반환
	 * @return int - USIM 인증서 index
	 */
	public int getCertIdx() {
		return m_nCertIdx;
	}

	/**
	 * SD card 에 저장된 인증서 경로 반환
	 * @return String - SD card 에 저장된 인증서 경로
	 */
	public String getCertPath() {
		return m_strCertPath;
	}

	/**
	 * SD card 에 저장된 개인키 경로 반환
	 * @return String - SD card 에 저장된 개인키 경로
	 */
	public String getPrivPath() {
		return m_strPrivPath;
	}

	public String getOID() {
		return m_strOID;
	}
	
	public String getType() {
		return m_strType;
	}
	
	public String getIssuer() {
		return m_strIssuer;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(m_x509Certificate);
		dest.writeInt(m_nCertIdx);
		dest.writeString(m_strCertPath);
		dest.writeString(m_strPrivPath);
		dest.writeString(m_strOID);
		dest.writeString(m_strType);
		dest.writeString(m_strIssuer);
	}
	
	public void readFromParcel(Parcel in) {
		m_x509Certificate = (X509Certificate) in.readSerializable();
		m_nCertIdx = in.readInt();
		m_strCertPath = in.readString();
		m_strPrivPath = in.readString();
		m_strOID = in.readString();
		m_strType = in.readString();
		m_strIssuer = in.readString();
	}

	public static final Parcelable.Creator<UsimCertificate> CREATOR = new Parcelable.Creator<UsimCertificate>() {
		@Override
		public UsimCertificate createFromParcel(Parcel source) {
			return new UsimCertificate(source);
		}

		@Override
		public UsimCertificate[] newArray(int size) {
			return new UsimCertificate[size];
		}
	};
}
