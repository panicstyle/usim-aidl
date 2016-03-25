package com.sumion.usim.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.os.Build;
//import android.preference.Preference;
import android.util.Log;

public class AppClient {
	
	private Context mContext;
	/** 동기 처리 종료를 대기하기 위한 flag */
	private boolean m_bWait = false;

	/** 동기 처리를 위한 message */
	private SumionMessage m_syncMsg;
	/**
	 * 유저 정보(전화번호 11자리 + 통신사 3자리)
	 * ex : “01099991111SKT”, “01077772222KT “, “01066663333LGU”
	 */
	private String mUserInfo;	
	
	private static final String mServerURL = "https://relay.mobileusim.com/usim/app/";
	//private static final String mServerURL = "https://dev.mobileusim.com/usim/app/";
	//private static final String mServerURL = "http://192.168.40.15:8080/usim/app/";
	/** HTTP 통신 time out */
	public static int HTTP_TIME_OUT_MS	= 60000;	
	
	public AppClient(Context context, String userInfo) {
		LogUtil.d("AppClient", "AppClient function start = ["+userInfo+"]");
		mContext = context;
		mUserInfo = userInfo;
		/*
		mCurrMessage = new Message();
		
		mUserInfo = Utils.getUserNo(mContext);
		//mUserInfo = "01037400336SKT";
		mUSIMSerial = Utils.getUSIMSerialNumber(mContext, true);
		if (GlobalConst.logging) Toast.makeText(context, "USER INFO : " + mUserInfo, Toast.LENGTH_SHORT).show();
		*/
	}	
	
	/**
	 * HTTP 요청 - 동기처리
	 * @param msg - 요청 메시지 객체(개별 메시지 사용)
	 * @param strCmd - command
	 * @param strBody - 요청 body
	 */
	public SumionMessage sendSyncRequest(String strCmd, String strBody) {
		LogUtil.d("AppClient", "sendSyncRequest function start");
		if(m_bWait) {  // Waiting status
			Log.d("AppClient", "sendSyncRequest function m_bWait is true");
			return null;
		}

		m_syncMsg = new SumionMessage();
		getMessage(strCmd, m_syncMsg, strBody);
		LogUtil.d("AppClient", "getMessage : strCmd = ["+strCmd +"], m_syncMsg = ["+m_syncMsg.getBody()+"], strBody = ["+strBody+"]");
		m_bWait = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				doHttpCmd(m_syncMsg.getCmd(), m_syncMsg);
				m_bWait = false;
			}
		}).start();

		while (m_bWait) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				m_syncMsg = null;
				m_bWait = false;
			}
		}
		LogUtil.d("AppClient", "sendSyncRequest function start, m_syncMsg = ["+m_syncMsg.getBody()+"]");
		return m_syncMsg;
	}	

	/**
	 * HTTP command 처리
	 * @param strCmd - command
	 */
	private void doHttpCmd(String strCmd, SumionMessage msg) {
		
		//2015.07.07
		//dspark
		//관리자 접속 서버 변경 기능 추가 관련  
		//start
		/*
		Preference pref = new Preference(mContext);
		String server_ip = pref.getValue("server_ip","dev1.mobileusim.com");
		if(!server_ip.equals("dev1.mobileusim.com"))
			GlobalConst.SERVER_IP = server_ip;
		
		if (GlobalConst.SERVER_IP.equals("test.mobileusim.com")) {
			GlobalConst.SERVER_URL = "https://" + GlobalConst.SERVER_IP + "/usim/app/";
		} else if (GlobalConst.SERVER_IP.equals("dev.mobileusim.com")) {
			GlobalConst.SERVER_URL = "https://" + GlobalConst.SERVER_IP + "/usim/app/";
		} else {
			if(!GlobalConst.SERVER_IP.equals("relay.mobileusim.com")) {
				GlobalConst.SERVER_URL = GlobalConst.SERVER_IP + "/usim/app/";
			} else {
				GlobalConst.SERVER_URL = "https://" + GlobalConst.SERVER_IP + "/usim/app/";
			}
		}	
		
		LogUtil.d(TAG, "Read preference result : server ip = ["+server_ip+"]");
		//end		
		
		LogUtil.d(TAG, "<<<<<<-----CMD " + strCmd + " Http Connect Start-----");
		LogUtil.d(TAG, "Connect serverURL : " + GlobalConst.SERVER_URL + strCmd);
		*/
		try {
			
			HttpURLConnection con = (HttpURLConnection) new URL(mServerURL + strCmd).openConnection();
			con.setConnectTimeout(HTTP_TIME_OUT_MS);
			con.setReadTimeout(HTTP_TIME_OUT_MS);
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Cache-Control", "no-cache");
			con.setRequestProperty("Pragma", "no-cache");
			con.setRequestProperty("Connection", "keep-alive");
			con.setRequestProperty("Content-type", "charset=utf-8");

			Iterator iterator = msg.getHeader().entrySet().iterator();

			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				if(entry.getValue() != null) {
					con.setRequestProperty(entry.getKey().toString(), entry.getValue().toString());
					LogUtil.d("AppClient", "INPUT Parm Key: " + entry.getKey() + ", Value: " + entry.getValue());
				}
			}

			if (msg.getBody() != null) {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
				bw.write(msg.getBody());
				bw.flush();
				bw.close();
				//LogUtil.d(TAG, "INPUT Body() : " + msg.getBody());
			}

			msg.clearMessage();
			msg.setResponseCode(con.getResponseCode());

			StringBuffer response = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			msg.setBody(response.toString());
			in.close();
			//LogUtil.d(TAG, "OUTPUT Body : " + msg.getBody());

			Map<String, List<String>> map = con.getHeaderFields();
			Iterator key = map.entrySet().iterator();
			while (key.hasNext()) {
				Map.Entry entry = (Map.Entry) key.next();
				String strKey = (String)entry.getKey();
				if (strKey != null) {
					//LogUtil.d(TAG, "OUTPUT Parm key : " + strKey + "[" + con.getHeaderField(strKey) + "]");
					msg.getHeader().put(strKey, con.getHeaderField(strKey));
					if (strKey.equals("Set-Cookie"))
						setCookie(con, msg.getCmd());
					else if (strKey.equals("ERR_CODE"))
						msg.setErrorCode(con.getHeaderField(strKey));
					else if(strKey != null && strKey.equals("STANDBY_CMD"))
						msg.setCmd(con.getHeaderField(strKey));
					else if(strKey != null && strKey.endsWith("CONNECT_ID")) {
						msg.setExtraInfo(con.getHeaderField(strKey));
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//msg.setBody(mContext.getResources().getString(R.string.popup_body_error_16));
			//msg.setErrorCode("999");
		}

		//LogUtil.d(TAG, "-----CMD " + strCmd + " Http Connect End----->>>>>>");
		//LogUtil.d(TAG, "");
	}
	
	
	private void setCookie(HttpURLConnection con, String strCmd) {
		/*
		String cookieStr = con.getHeaderField("Set-Cookie");
		System.out.println("cookieStr : " + cookieStr);
		if(cookieStr != null) {
			String[] cookieArr = cookieStr.split(";");
			for(String cookie : cookieArr) {
				if(cookie.startsWith("JSESSIONID")) {
					GlobalConst.SET_COOKIE = cookie;
				}
			}
		}*/
	}
	
	
	private void getMessage(String cmd, SumionMessage msg, String body) {
		//LogUtil.w(TAG, "getMessage cmd : " + cmd);
		/*if(cmd.equals(GlobalConst.A102))
			getA102S(msg, body);
		else if(cmd.equals(GlobalConst.A090))
			getA090S(msg, body);
		else if(cmd.equals(GlobalConst.A091))
			getA091S(msg, body);
		else*/ if(cmd.equals("100"))
			getA100S(msg, body);
		/*else if(cmd.equals(GlobalConst.A101))
			getA101S(msg, body);
		else if(cmd.equals(GlobalConst.A103))
			getA103S(msg, body);
		else if(cmd.equals(GlobalConst.A108))
			getA108S(msg, body);
		else if(cmd.equals(GlobalConst.A109))
			getA109S(msg, body);
		else if(cmd.equals(GlobalConst.A110))
			getA110S(msg, body);
		else if(cmd.equals(GlobalConst.A120))
			getA120S(msg, body);
		else if(cmd.equals(GlobalConst.A210))
			getA210S(msg, body);
		else if(cmd.equals(GlobalConst.A310))
			getA310S(msg, body);
		else if(cmd.equals(GlobalConst.A320))
			getA320S(msg, body);
		else if(cmd.equals(GlobalConst.A410))
			getA410S(msg, body);
		else if(cmd.equals(GlobalConst.A420))
			getA420S(msg, body);
		else if(cmd.equals(GlobalConst.A430))
			getA430S(msg, body);
		else if(cmd.equals(GlobalConst.A440))
			getA440S(msg, body);
		else if(cmd.equals(GlobalConst.A800))
			getA800S(msg, body);
		else if(cmd.equals(GlobalConst.C_Initialize))
			getC_Initialize(msg, body);
		else if(cmd.equals(GlobalConst.C_Finalize))
			getC_Finalize(msg, body);
		else if(cmd.equals(GlobalConst.C_OpenSession))
			getC_OpenSession(msg, body);
		else if(cmd.equals(GlobalConst.C_CloseSession))
			getC_CloseSession(msg, body);
		else if(cmd.equals(GlobalConst.C_CloseAllSessions))
			getC_CloseAllSessions(msg, body);
		else if(cmd.equals(GlobalConst.C_Login))
			getC_Login(msg, body);
		else if(cmd.equals(GlobalConst.C_Logout))
			getC_Logout(msg, body);
		else if(cmd.equals(GlobalConst.C_CreateObject))
			getC_CreateObject(msg, body);
		else if(cmd.equals(GlobalConst.C_DestroyObject))
			getC_DestroyObject(msg, body);
		else if(cmd.equals(GlobalConst.C_GetAttributeValue))
			getC_GetAttributeValue(msg, body);
		else if(cmd.equals(GlobalConst.C_SetAttributeValue))
			getC_SetAttributeValue(msg, body);
		else if(cmd.equals(GlobalConst.C_FindObjectsInit))
			getC_FindObjectsInit(msg, body);
		else if(cmd.equals(GlobalConst.C_Decrypt))
			getC_Decrypt(msg, body);
		else if(cmd.equals(GlobalConst.C_Sign))
			getC_Sign(msg, body);
		else if(cmd.equals(GlobalConst.C_GenerateKeyPair))
			getC_GenerateKeyPair(msg, body);
		else if(cmd.equals(GlobalConst.C_UnWrapKey))
			getC_UnWrapKey(msg, body);
		else if(cmd.equals(GlobalConst.C_GenerateRandom))
			getC_GenerateRandom(msg, body);
		*/
		if(msg != null) {
			msg.setCmd(cmd);
		}
	}	

	private void getA100S(SumionMessage msg, String body) {
		msg.clearMessage();
		msg.getHeader().put("Cookie", null);
		//msg.getHeader().put("CONNECT_ID", "site.skip.domain");
		msg.getHeader().put("USER_INFO", mUserInfo);
		//msg.getHeader().put("USER_INFO", "01027123769SKT");
		msg.getHeader().put("PKG_NAME", mContext.getPackageName());
		msg.setBody("|" + Build.MODEL);

		//msg.setBody(Utils.getRegistrationId(this.mContext) + "|IM-A890S"/* + Build.MODEL*/);
	}	
	
}
