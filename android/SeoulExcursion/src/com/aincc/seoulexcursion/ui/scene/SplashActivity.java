package com.aincc.seoulexcursion.ui.scene;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.aincc.lib.common.annotation.InjectView;
import com.aincc.lib.util.Utils;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.SeoulBaseActivity;
import com.aincc.seoulexcursion.util.SeoulFont;

/**
 * 
 * <h3><b>SplashActivity</b></h3></br>
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class SplashActivity extends SeoulBaseActivity
{
	/**
	 * 제목
	 */
	@InjectView
	private TextView title;

	/**
	 * 핸들러
	 */
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case MOVE_TO_MAIN:
				moveToMain();
				break;
			default:
				break;
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mappingViews(this);
		initializeUI();
	}

	@Override
	protected void initializeUI()
	{
		super.initializeUI();
		title.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		title.setText(string(R.string.app_name) + " v" + Utils.getProgramVersion(this));
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Message msg = handler.obtainMessage(MOVE_TO_MAIN);
		handler.sendMessageDelayed(msg, 3000);
	}

	@Override
	public void onBackPressed()
	{
	}

	private static final int MOVE_TO_MAIN = 0;

	private void moveToMain()
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		finish();
	}
}
