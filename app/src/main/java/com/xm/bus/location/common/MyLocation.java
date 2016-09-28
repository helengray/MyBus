package com.xm.bus.location.common;

import android.content.Context;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import javax.security.auth.Destroyable;

public class MyLocation implements Destroyable {
	private Context context;
	private DoAfterListener doAfterListener;
	private LocationClient mLocClient = null;

	public MyLocation(Context context) {
		this.context = context;
		init();
	}

	private void init() {
		this.mLocClient = new LocationClient(
				this.context.getApplicationContext());
		this.mLocClient.setAK("pGMEW4fzUg34mQkKVegKcEY0");
		this.mLocClient.registerLocationListener(new MyLocationListener());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);//��GPS
		option.setPriority(LocationClientOption.GpsFirst);//����ʹ��GPS��λ,���׼ȷ��
		option.setCoorType("bd09ll");
		option.setAddrType("all");//���û�ȡ��ַ����ϸ��Ϣ
		option.setProdName("����ʵʱ������ѯ");
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	/**
	 * ��λ��������
	 */
	class MyLocationListener implements BDLocationListener {
		MyLocationListener() {
		}

		public void onReceiveLocation(BDLocation bDLocation) {
			onDoAfter(bDLocation);
		}

		public void onReceivePoi(BDLocation paramBDLocation) {
		}
	}
	/**
	 * ���ûص�����
	 */
	public void setDoAfterListener(DoAfterListener doAfterListener) {
		this.doAfterListener = doAfterListener;
		this.mLocClient.requestLocation();
	}
	public interface DoAfterListener {
		public abstract void onDoAfter(BDLocation bDLocation);
	}
	
	private void onDoAfter(BDLocation bDLocation) {
		if (this.doAfterListener != null){
			this.doAfterListener.onDoAfter(bDLocation);
		}
	}

	@Override
	public void destroy() {
		if (this.mLocClient != null) {
			this.mLocClient.stop();
			this.mLocClient = null;
		}
	}
	@Override
	public boolean isDestroyed() {
		return false;
	}
}