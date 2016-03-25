package com.sumion.usim.util;

import android.util.Log;

public class LogConsole
{
	public LogConsole(){}
	
	protected void logv(String tag, String msg){
		Log.v(tag, msg);
	}
	
	protected void logd(String tag, String msg){
		Log.d(tag, msg);
	}
	
	protected void logi(String tag, String msg){
		Log.i(tag, msg);
	}
	
	protected void logw(String tag, String msg){
		Log.w(tag, msg);
	}
	
	protected void loge(String tag, String msg){
		Log.e(tag, msg);
	}
	
	protected void loge(String tag, String msg, Throwable tr){
		Log.e(tag, msg, tr);
	}
	
//	protected void init(){
//		super.init();
//	}
//	
//	public void destroy(){
//		super.destroy();
//	}
}