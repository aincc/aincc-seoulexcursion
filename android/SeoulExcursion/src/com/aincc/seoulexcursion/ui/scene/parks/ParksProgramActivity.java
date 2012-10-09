package com.aincc.seoulexcursion.ui.scene.parks;

import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aincc.lib.common.annotation.InjectView;
import com.aincc.lib.ui.widget.list.section.AmazingListView;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.SeoulBaseActivity;
import com.aincc.seoulexcursion.ui.control.OneSectionAdapter;
import com.aincc.seoulexcursion.ui.widget.Navibar;
import com.aincc.seoulexcursion.util.SeoulFont;
import com.aincc.seoulopenapi.ServiceExecutor;
import com.aincc.seoulopenapi.model.ParkInfo;
import com.aincc.seoulopenapi.model.ParkProgramInfo;

/**
 * 
 * <h3><b>ParksProgramActivity</b></h3></br>
 * 
 * 공원 프로그램 목록 표시
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class ParksProgramActivity extends SeoulBaseActivity
{
	/**
	 * 상단 네비게이션 바
	 */
	@InjectView
	private Navibar navibar;

	/**
	 * 프로그램 리스트뷰
	 */
	@InjectView
	private AmazingListView listview;

	/**
	 * 공원 어댑터
	 */
	private SectionProgramsAdapter<ParkProgramInfo> adapter;

	/**
	 * 공원정보
	 */
	private ParkInfo info = null;

	/**
	 * 프로그램 리스트
	 */
	private List<ParkProgramInfo> listdata = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Logger.d1(LOG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_programs);

		try
		{
			getIntentData();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(context, "Exception", Toast.LENGTH_SHORT).show();
			finish();
		}
		mappingViews(this);
		initializeUI();
	}

	/**
	 * 인덴트 정보 처리
	 * 
	 * @since 1.0.0
	 */
	private void getIntentData() throws Exception
	{
		Intent data = getIntent();
		if (null != data)
		{
			Bundle bundle = data.getExtras();
			if (null != bundle)
			{
				info = bundle.getParcelable(Constants.EXTRA_KEY_PARK_INFO);
				listdata = bundle.getParcelableArrayList(Constants.EXTRA_KEY_PARK_PROGRAM_LIST);

				if (null != info)
				{
					Logger.d1(LOG, "Park : " + info.P_PARK);
				}
				if (null != listdata)
				{
					Iterator<ParkProgramInfo> it = listdata.iterator();
					while (it.hasNext())
					{
						Logger.d1(LOG, it.next().toString());
						break;
					}
				}
			}
		}
	}

	@Override
	protected void initializeUI()
	{
		super.initializeUI();

		if (null == adapter)
		{
			adapter = new SectionProgramsAdapter<ParkProgramInfo>();
			adapter.setList(listdata);
			listview.setAdapter(adapter);
		}

		listview.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.cell_header_programs, listview, false));

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

		// 제목 설정
		navibar.setTitle(null != info ? info.P_PARK : "");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		initializeUI();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (null != info && null != listdata)
		{
			// 정보표시
			displayParkInfo();
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		ServiceExecutor.getInstance().cancelAll();
	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, R.anim.push_down_out);
	}

	/**
	 * 공원정보 표시하기
	 * 
	 * @since 1.0.0
	 */
	private void displayParkInfo()
	{
		Logger.d1(LOG, "Park : " + info.P_PARK);
		navibar.setTitle(info.P_PARK);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 
	 * <h3><b>SectionProgramsAdapter</b></h3></br>
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	class SectionProgramsAdapter<T> extends OneSectionAdapter<T>
	{

		@SuppressWarnings("unchecked")
		@Override
		public View getAmazingView(final int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			ViewHolder viewHolder = null;
			if (null == convertView)
			{
				view = getLayoutInflater().inflate(R.layout.cell_programs, null);

				viewHolder = new ViewHolder();
				viewHolder.cellSelector = (LinearLayout) view.findViewById(R.id.cellSelector);
				viewHolder.P_NAME = (TextView) view.findViewById(R.id.P_NAME);
				viewHolder.P_EDUDAY = (TextView) view.findViewById(R.id.P_EDUDAY);
				viewHolder.P_EDUPERSON = (TextView) view.findViewById(R.id.P_EDUPERSON);
				viewHolder.P_EAMAX = (TextView) view.findViewById(R.id.P_EAMAX);
				viewHolder.P_NAME.setTypeface(SeoulFont.getInstance().getSeoulHangang());
				viewHolder.P_EDUDAY.setTypeface(SeoulFont.getInstance().getSeoulHangang());
				viewHolder.P_EDUPERSON.setTypeface(SeoulFont.getInstance().getSeoulHangang());
				viewHolder.P_EAMAX.setTypeface(SeoulFont.getInstance().getSeoulHangang());
				view.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.cellSelector.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					ProgramInfoPopup.show(context, (ParkProgramInfo) getItem(position));
				}
			});

			ParkProgramInfo item = (ParkProgramInfo) getItem(position);
			viewHolder.P_NAME.setText(item.P_NAME);
			viewHolder.P_EDUDAY.setText(item.P_EDUDAY_S + " ~ " + item.P_EDUDAY_E);
			viewHolder.P_EDUPERSON.setText(string(R.string.program_person) + " " + item.P_EDUPERSON);
			viewHolder.P_EAMAX.setText(string(R.string.program_max) + " " + item.P_EAMAX);
			return view;
		}

		@Override
		public String[] getSections()
		{
			String[] res = new String[1];
			res[0] = info.P_PARK;
			return res;
		}

		/**
		 * 
		 * <h3><b>ViewHolder</b></h3></br>
		 * 
		 * @author aincc@barusoft.com
		 * @version 1.0.0
		 * @since 1.0.0
		 */
		class ViewHolder
		{
			LinearLayout cellSelector;
			TextView P_NAME;
			TextView P_EDUDAY;
			TextView P_EDUPERSON;
			TextView P_EAMAX;
		}
	}
}
