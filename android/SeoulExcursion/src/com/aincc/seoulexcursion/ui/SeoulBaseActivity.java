package com.aincc.seoulexcursion.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.aincc.ui.common.BaseActivity;
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
abstract public class SeoulBaseActivity extends BaseActivity
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
}
