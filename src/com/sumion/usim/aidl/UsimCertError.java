package com.sumion.usim.aidl;

import android.os.Parcel;
import android.os.Parcelable;

import com.sumion.usim.util.GlobalError;

/**
 * 스마트 USIM 서비스 처리 에러 코드/메시지 class
 */
public class UsimCertError extends Exception implements Parcelable {

	private static final long serialVersionUID 		= 1L;

	/** 에러 코드 */
	private String mErrorCode;
	/** 에러 메시지 */
	private String mErrorMessage;

	public UsimCertError(){
		clear();
	}

	public UsimCertError(Parcel In) {
		readFromParcel(In);
	}

	/**
	 * 에러 코드/메시지 정상 값으로 초기화
	 */
	public void clear() {
		mErrorCode = GlobalError.code.NORMAL;
		mErrorMessage = GlobalError.msg.NORMAL;
	}

	/**
	 * 에러 코드/메시지 설정
	 * @param strErrCode - 에러 코드
	 * @param strErrMsg - 에러 메시지
	 */
	public void setError(String strErrCode, String strErrMsg) {
		mErrorCode = strErrCode;
		mErrorMessage = strErrMsg;
	}

	/**
	 * 에러 코드 반환
	 * @return String - 에러 코드
	 */
	public String getErrorCode() {
		return mErrorCode;
	}

	/**
	 * 에러 메시지 반환
	 * @return String - 에러 메시지
	 */
	public String getErrorMessage() {
		return mErrorMessage;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mErrorCode);
		dest.writeString(mErrorMessage);
	}
	
	public void readFromParcel(Parcel in){
		mErrorCode = in.readString();
		mErrorMessage = in.readString();
	}
	
	public static final Parcelable.Creator<UsimCertError> CREATOR = new Parcelable.Creator<UsimCertError>(){
		@Override
		public UsimCertError createFromParcel(Parcel source) {
			return new UsimCertError(source);
		}

		@Override
		public UsimCertError[] newArray(int size) {
			return new UsimCertError[size];
		}
	};
}