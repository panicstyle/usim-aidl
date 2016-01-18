package com.sumion.usim.util;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Utils {
    public static String toHexString(byte buf[]){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            sb.append(Integer.toHexString(0x0100 + (buf[i] & 0x00FF)).substring(1));
        }
        return sb.toString();
    }

	public static String getUSIMSerialNumber(Context context) {
		TelephonyManager telephonyManager;
		telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = telephonyManager.getSimSerialNumber();
		return simSerialNumber;
	}
    
}
