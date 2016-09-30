package com.xm.bus.common;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.baidu.mobads.AppActivity;
import com.xm.bus.MainDrawerActivity;
import com.xm.bus.location.common.LocationApplication;

public class MyApplication extends LocationApplication implements Application.ActivityLifecycleCallbacks{
	private int width;
	private int height;
	//private boolean isHasWidget=false;
	private Handler mHandler;

	@Override
	public void onCreate() {
		super.onCreate();
		registerActivityLifecycleCallbacks(this);
		mHandler = new Handler();
	}

	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}
	private boolean isHandle = false;
	@Override
	public void onActivityStarted(final Activity activity) {
		if(activity instanceof com.baidu.mobads.AppActivity){
			isHandle = true;
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					AppActivity appActivity = (AppActivity)activity;
					long downTime = SystemClock.uptimeMillis();
					MotionEvent downEvent = MotionEvent.obtain(downTime,downTime,MotionEvent.ACTION_DOWN,231.67822f,235.70493f,0);
					appActivity.dispatchTouchEvent(downEvent);
					long upTime = SystemClock.uptimeMillis();
					MotionEvent upEvent = MotionEvent.obtain(upTime,upTime,MotionEvent.ACTION_UP,231.67822f,235.70493f,0);
					appActivity.dispatchTouchEvent(upEvent);
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							activity.finish();
						}
					}, 5000);
				}
			},10*1000);

		}
	}

	@Override
	public void onActivityResumed(Activity activity) {
		if(activity instanceof MainDrawerActivity){
			isHandle = false;
		}
	}

	@Override
	public void onActivityPaused(Activity activity) {
		if (activity instanceof MainDrawerActivity){
			restartAdActivity();
		}
	}

	@Override
	public void onActivityStopped(Activity activity) {

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		if(activity instanceof MainDrawerActivity){
			unregisterActivityLifecycleCallbacks(this);
			mHandler.removeCallbacks(mRestartActivityRunnable);
		}
	}
	private static final int TIME_COUNT = 5000;
	//重启AdActivity
	private void restartAdActivity(){
		mHandler.removeCallbacks(mRestartActivityRunnable);
		mHandler.postDelayed(mRestartActivityRunnable,TIME_COUNT);
	}
	private RestartActivityRunnable mRestartActivityRunnable = new RestartActivityRunnable();
	private class RestartActivityRunnable implements Runnable{
		@Override
		public void run() {
			if(!isHandle){
				Intent i = new Intent(getInstance(), MainDrawerActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getInstance().startActivity(i);
			}
		}
	}
	/*public boolean isHasWidget() {
		return isHasWidget;
	}
	public void setHasWidget(boolean isHasWidget) {
		this.isHasWidget = isHasWidget;
	}*/
	
	
}