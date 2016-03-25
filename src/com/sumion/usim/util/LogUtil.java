package com.sumion.usim.util;

public class LogUtil {

	static LogConsole li;
	static boolean logging = false;

	static {
		if (logging)
			li = new LogConsole();
	}

	public static void v(String tag, String msg) {
		if (logging)
			li.logv(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (logging)
			li.logi(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (logging)
			li.logw(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (logging)
			li.loge(tag, msg);
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (logging)
			li.loge(tag, msg, tr);
	}

	public static void d(String tag, String msg) {
		if (logging)
			li.logd(tag, msg);
	}

	public static void hex(String tag, byte[] bArr) {
		if (logging) {
			String msg = "";
			if(bArr != null) {
				for(int i = 0; i < bArr.length; i++) {
					msg += String.format("0x%02X ", bArr[i]);
					if((i + 1) % 8 == 0) {
						li.logd(tag, msg);
						msg = "";
					}
				}
				if(msg.length() > 0) {
					li.logd(tag, msg);
				}
			}
			else {
				li.logd(tag, "array is null!!!");
			}
		}
	}
}