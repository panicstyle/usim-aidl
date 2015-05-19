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
	}
	
	public void readFromParcel(Parcel in) {
		m_x509Certificate = (X509Certificate) in.readSerializable();
		m_nCertIdx = in.readInt();
		m_strCertPath = in.readString();
		m_strPrivPath = in.readString();
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
