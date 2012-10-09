package com.aincc.seoulexcursion.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.aincc.lib.common.MapBaseActivity;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.scene.setting.SettingActivity;

/**
 * 
 * <h3><b>SeoulBaseActivity</b></h3></br>
 * 
 * 기본 액티비티
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
abstract public class SeoulMapBaseActivity extends MapBaseActivity
{
	protected static final String LOG = "Seoul";
	protected Context context;

	// GUI 위젯은 하위 클래스에서 선언한다.

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// 옵션메뉴설정
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// 옵션메뉴동작 설정
		switch (item.getItemId())
		{
		case R.id.menu_settings:
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void initializeUI()
	{
		super.initializeUI();

		// 상단 공통 네비게이션바 초기화
		initializeNavibar();
	}

	/**
	 * 상단 네비게이션 바 초기화
	 * 
	 * @since 1.0.0
	 */
	protected void initializeNavibar()
	{
		// 초기화를 할 Navibar 객체는 하위 클래스에서 선언한다.
	}

	/**
	 * 리소스 문자열 가져오기
	 * 
	 * @since 1.0.0
	 * @param id
	 * @return String
	 */
	protected String string(int id)
	{
		return getResources().getString(id);
	}

	/**
	 * 리소스 문자열 배열 가져오기
	 * 
	 * @since 1.0.0
	 * @param id
	 * @return String[]
	 */
	protected String[] stringArray(int id)
	{
		return getResources().getStringArray(id);
	}

	/**
	 * 리소스 Drawable 가져오기
	 * 
	 * @since 1.0.0
	 * @param id
	 * @return Drawable
	 */
	protected Drawable drawable(int id)
	{
		return getResources().getDrawable(id);
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}

	@Override
	protected void onLocationLost()
	{
		Logger.e("onLocationLost");
	}

	@Override
	protected void onLocationChanged(Location location)
	{
		Logger.e("onLocationChanged " + location.getProvider() + " (" + location.getLatitude() + " , " + location.getLongitude() + ")");
	}

	@Override
	protected void onStatusChanged(String provider, int status, Bundle extras)
	{
		switch (status)
		{
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			Logger.d("onStatusChanged " + provider + " : TEMPORARILY_UNAVAILABLE");
			break;
		case LocationProvider.OUT_OF_SERVICE:
			Logger.d("onStatusChanged " + provider + " : OUT_OF_SERVICE");
			break;
		case LocationProvider.AVAILABLE:
			Logger.d("onStatusChanged " + provider + " : AVAILABLE");
			break;
		}
	}

	@Override
	protected void onGpsStatusChanged(int event)
	{
		switch (event)
		{
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			Logger.d("onGpsStatusChanged GPS_EVENT_FIRST_FIX");

			break;
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			Logger.d("onGpsStatusChanged GPS_EVENT_SATELLITE_STATUS");
			break;
		case GpsStatus.GPS_EVENT_STARTED:
			Logger.d("onGpsStatusChanged GPS_EVENT_STARTED");
			break;
		case GpsStatus.GPS_EVENT_STOPPED:
			Logger.d("onGpsStatusChanged GPS_EVENT_STOPPED");
			break;
		}

		// public static final int GPS_EVENT_STARTED
		// Event sent when the GPS system has started.
		// Constant Value: 1 (0x00000001)
		// public static final int GPS_EVENT_STOPPED
		// Event sent when the GPS system has stopped.
		// Constant Value: 2 (0x00000002)
		// public static final int GPS_EVENT_FIRST_FIX
		// Event sent when the GPS system has received its first fix since starting. Call getTimeToFirstFix() to find the time from start to first fix.
		// Constant Value: 3 (0x00000003)
		// public static final int GPS_EVENT_SATELLITE_STATUS
		// Event sent periodically to report GPS satellite status. Call getSatellites() to retrieve the status for each satellite.
		// Constant Value: 4 (0x00000004)
	}
}
