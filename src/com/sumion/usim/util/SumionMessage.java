package com.sumion.usim.util;

import java.util.HashMap;

public class SumionMessage {
	private HashMap<String, String> mHeader;
	private String mBody;
	private String mCmd;
	private int mResponseCode;
	private String mErrorCode;
	private String mExtraInfo;

	public SumionMessage() {
		mHeader = new HashMap<String, String>();
	}
	
	public HashMap<String, String> getHeader() {
		return mHeader;
	}
	public void setHeader(HashMap<String, String> header) {
		mHeader = header;
	}
	public String getBody() {
		return mBody;
	}
	public void setBody(String body) {
		mBody = body;
	}
	
	public String getCmd() {
		return mCmd;
	}
	public void setCmd(String cmd) {
		mCmd = cmd;
	}
	
	public int getResponseCode() {
		return mResponseCode;
	}
	public void setResponseCode(int responseCode){
		mResponseCode = responseCode;
	}
	
	public String getErrorCode() {
		return mErrorCode;
	}
	public void setErrorCode(String errorCode){
		mErrorCode = errorCode;
	}

	public String getExtraInfo() {
		return mExtraInfo;
	}

	public void setExtraInfo(String strExtraInfo) {
		mExtraInfo = strExtraInfo;
	}

	public void clearMessage(){
		mHeader = new HashMap<String, String>();
		mBody = null;
// command 설정 값은 유지  //		mCmd = null;
		mResponseCode = 0;
		mErrorCode = null;
		mExtraInfo = null;
	}

}
