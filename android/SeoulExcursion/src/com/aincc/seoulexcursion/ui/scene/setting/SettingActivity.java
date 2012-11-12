package com.aincc.seoulexcursion.ui.scene.setting;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aincc.cache.BitmapDiskLruCache;
import com.aincc.ui.common.annotation.InjectView;
import com.aincc.util.PreferencesUtil;
import com.aincc.util.Utils;
import com.aincc.seoulexcursion.App;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.SeoulBaseActivity;
import com.aincc.seoulexcursion.ui.widget.Navibar;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.seoulexcursion.util.SeoulFont;

/**
 * 
 * <h3><b>SettingActivity</b></h3></br>
 * 
 * 설정 화면
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class SettingActivity extends SeoulBaseActivity implements OnClickListener, OnCheckedChangeListener
{
	/**
	 * 상단 네비게이션 바
	 */
	@InjectView
	private Navibar navibar;

	/**
	 * 캐시 삭제 버튼
	 */
	@InjectView(id = R.id.setting_delete_diskcache)
	private RelativeLayout deleteCache;

	/**
	 * 디스크 캐시 사용여부 설정
	 */
	@InjectView(id = R.id.setting_toggle_diskcache)
	private RelativeLayout toggleCache;

	/**
	 * 디스크 캐시 사용여부 설정 체크박스
	 */
	@InjectView(id = R.id.setting_toggle_diskcache_check)
	private CheckBox toggleCacheCheck;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		mappingViews(this);
		initializeUI();
	}

	@Override
	protected void initializeUI()
	{
		super.initializeUI();

		deleteCache.setOnClickListener(this);
		toggleCache.setOnClickListener(this);

		toggleCacheCheck.setChecked(PreferencesUtil.getBoolean(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_DISKCACHE));
		toggleCacheCheck.setOnCheckedChangeListener(this);

		initializeFont(SeoulFont.getInstance().getSeoulHangang());

		StringBuilder sb = new StringBuilder();
		sb.append(versionSection.getText());
		sb.append(" (").append(string(R.string.setting_current_version));
		sb.append(" : " + string(R.string.app_name) + " v");
		sb.append(Utils.getProgramVersion(context));
		sb.append(")");
		versionSection.setText(sb.toString());
	}

	@Override
	protected void initializeNavibar()
	{
		super.initializeNavibar();
		navibar.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		navibar.setActionBack(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		navibar.setActionFuncVisible(View.INVISIBLE);

		/**
		 * 버전정보 추가
		 * 
		 * @since 1.3.0
		 */
		navibar.setTitle(string(R.string.title_activity_setting) + " v" + Utils.getProgramVersion(this));
	}

	@Override
	public void finish()
	{
		super.finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		initializeUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return false;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.setting_delete_diskcache:
			alertAskDeleteCache();
			break;
		case R.id.setting_toggle_diskcache:
			boolean current = PreferencesUtil.getBoolean(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_DISKCACHE);
			current = !current;
			toggleCacheCheck.setChecked(current);
			PreferencesUtil.setBoolean(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_DISKCACHE, current);
			App.setImageLoader(current);
			break;
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean checked)
	{
		Logger.d("onCheckedChanged : " + checked);
		switch (view.getId())
		{
		case R.id.setting_toggle_diskcache_check:
			toggleCacheCheck.setChecked(checked);
			PreferencesUtil.setBoolean(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_DISKCACHE, checked);
			break;
		}
	}

	/**
	 * 디스크 캐시 삭제 팝업
	 * 
	 * @since 1.0.0
	 */
	private void alertAskDeleteCache()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(string(R.string.alert_msg_delete_diskcache));
		builder.setPositiveButton(string(R.string.alert_btn_confirm), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				(new DeleteCacheTask()).execute();
			}
		}).setNegativeButton(string(R.string.alert_btn_cancel), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			}
		}).create().show();
	}

	/**
	 * 작업 완료 팝업
	 * 
	 * @since 1.0.0
	 * @param msg
	 *            메시지 리소스 아이디
	 */
	private void alertComplete(int msg)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(string(msg));
		builder.setPositiveButton(string(R.string.alert_btn_close), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			}
		}).create().show();
	}

	/**
	 * 
	 * <h3><b>DeleteCacheTask</b></h3></br>
	 * 
	 * 디스크 캐시 삭제 처리
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	class DeleteCacheTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			startProgress("", false, null, Constants.COLOR_BLACK);
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				BitmapDiskLruCache.clearCache(getApplicationContext(), Constants.CACHE_DIR);
				BitmapDiskLruCache.clearCache(getApplicationContext(), "http");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);

			stopProgress();
			alertComplete(R.string.alert_msg_delete_diskcache_ok);
		}
	}

	@InjectView(id = R.id.setting_cache)
	private TextView cacheSection;

	@InjectView(id = R.id.setting_delete_diskcache_title)
	private TextView cacheDeleteTitle;

	@InjectView(id = R.id.setting_delete_diskcache_desc)
	private TextView cacheDeleteDesc;

	@InjectView(id = R.id.setting_toggle_diskcache_title)
	private TextView cacheToggleTitle;

	@InjectView(id = R.id.setting_toggle_diskcache_desc)
	private TextView cacheToggleDesc;

	@InjectView(id = R.id.setting_version)
	private TextView versionSection;

	@InjectView(id = R.id.setting_version_title_20120830)
	private TextView versionTitle_20120830;

	@InjectView(id = R.id.setting_version_desc_20120830)
	private TextView versionDesc_20120830;

	@InjectView(id = R.id.setting_intro)
	private TextView introSection;

	@InjectView(id = R.id.setting_intro_desc)
	private TextView introDesc;

	@InjectView(id = R.id.setting_author)
	private TextView author;

	@InjectView(id = R.id.setting_report)
	private TextView report;

	private void initializeFont(Typeface tf)
	{
		cacheSection.setTypeface(tf);
		cacheDeleteTitle.setTypeface(tf);
		cacheDeleteDesc.setTypeface(tf);
		cacheToggleTitle.setTypeface(tf);
		cacheToggleDesc.setTypeface(tf);
		versionSection.setTypeface(tf);
		versionTitle_20120830.setTypeface(tf);
		versionDesc_20120830.setTypeface(tf);
		introSection.setTypeface(tf);
		introDesc.setTypeface(tf);
		author.setTypeface(tf);
		report.setTypeface(tf);
	}
}
