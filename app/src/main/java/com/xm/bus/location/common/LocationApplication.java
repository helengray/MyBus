package com.xm.bus.location.common;

import android.content.Context;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.xm.bus.search.common.SearchApp;

public class LocationApplication extends SearchApp {
	private static LocationApplication mInstance = null;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;

    public static final String strKey = "pGMEW4fzUg34mQkKVegKcEY0";
	
	@Override
    public void onCreate() {
	    super.onCreate();
		mInstance = this;
		initEngineManager(this);
	}
	
	public void initEngineManager(Context context) {
		/**
         * ʹ�õ�ͼsdkǰ���ȳ�ʼ��BMapManager.
         * BMapManager��ȫ�ֵģ���Ϊ���MapView���ã�����Ҫ��ͼģ�鴴��ǰ������
         * ���ڵ�ͼ��ͼģ�����ٺ����٣�ֻҪ���е�ͼģ����ʹ�ã�BMapManager�Ͳ�Ӧ������
         */
        if (mBMapManager == null) {
        	/**
             * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
             */
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(LocationApplication.getInstance().getApplicationContext(),"��ʼ������!", Toast.LENGTH_LONG).show();
        }
	}
	
	public static LocationApplication getInstance() {
		return mInstance;
	}
	
	
	// �����¼���������������ͨ�������������Ȩ��֤�����
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                /*Toast.makeText(LocationApplication.getInstance().getApplicationContext(), "���������źŲ�̫�ã�",
                    Toast.LENGTH_LONG).show();*/
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(LocationApplication.getInstance().getApplicationContext(), "������ȷ�ļ���������",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetPermissionState(int iError) {
        	//����ֵ��ʾkey��֤δͨ��
            if (iError != 0) {
                //��ȨKey����
                LocationApplication.getInstance().m_bKeyRight = false;
            }
            else{
            	LocationApplication.getInstance().m_bKeyRight = true;
            }
        }
    }
	
}