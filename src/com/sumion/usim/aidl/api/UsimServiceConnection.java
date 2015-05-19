package com.sumion.usim.aidl.api;

import com.sumion.usim.aidl.UsimCertMgr;
import com.sumion.usim.aidl.UsimCertMgr.Stub;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * 스마트 USIM 서비스 연결/해제 처리 listener 구현 class
 * @author hyunboklee
 */
public class UsimServiceConnection implements ServiceConnection {
	/** 스마트 USIM 서비스 제공 API 객체 */
	private UsimCertService m_usimCertService;

	/**
	 * 생성자
	 * @param usimCertService - 스마트 USIM 서비스 제공 API 객체
	 */
	public UsimServiceConnection(UsimCertService usimCertService) {
		m_usimCertService = usimCertService;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		if(m_usimCertService != null) {
			m_usimCertService.clearStub();
		}
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		if(m_usimCertService != null) {
			m_usimCertService.setStub(UsimCertMgr.Stub.asInterface(service));
		}
	}
}
