package com.aincc.seoulexcursion.ui.scene.plays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aincc.lib.common.annotation.InjectView;
import com.aincc.lib.network.common.BaseTrans;
import com.aincc.lib.network.common.BaseTransEx;
import com.aincc.lib.network.http.HttpParam;
import com.aincc.lib.ui.widget.list.section.AmazingListView;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.lib.util.PreferencesUtil;
import com.aincc.seoulexcursion.App;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.SeoulBaseActivity;
import com.aincc.seoulexcursion.ui.control.OneSectionAdapter;
import com.aincc.seoulexcursion.ui.widget.Navibar;
import com.aincc.seoulexcursion.ui.widget.Navisheet.OptionSelected;
import com.aincc.seoulexcursion.util.SeoulFont;
import com.aincc.seoulopenapi.OpenAPI;
import com.aincc.seoulopenapi.ServiceExecutor;
import com.aincc.seoulopenapi.model.CodeInfo;
import com.aincc.seoulopenapi.model.FacilSimpleInfo;
import com.aincc.seoulopenapi.model.PlaySimpleInfo;
import com.aincc.seoulopenapi.network.OpenBase;
import com.aincc.seoulopenapi.openapi.culture.OpenFacilInfo;
import com.aincc.seoulopenapi.openapi.culture.OpenPlayInfo;

/**
 * 
 * <h3><b>PlaysActivity</b></h3></br>
 * 
 * 공연/문화시설 목록 표시
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class PlaysActivity extends SeoulBaseActivity implements OptionSelected
{
	private static final int MAIN_FACIL_THEME = 0;
	private static final int MAIN_FACIL_SUBJ = 1;
	private static final int MAIN_PLAY = 2;

	/**
	 * 공연/문화시설 데이터
	 */
	private List<PlaySimpleInfo> playdata = new ArrayList<PlaySimpleInfo>();
	private List<FacilSimpleInfo> facildata = new ArrayList<FacilSimpleInfo>();

	/**
	 * 상단 네비게이션 바
	 */
	@InjectView
	private Navibar navibar;

	/**
	 * 섹션명
	 */
	private String sectionName = "";

	/**
	 * 세부코드
	 */
	private String sectionCode = null;

	/**
	 * 리스트뷰
	 */
	@InjectView
	private AmazingListView listview;

	/**
	 * 어댑터
	 */
	private PlaysAdapter<?> adapter;

	/**
	 * 옵션정보
	 */
	private List<Pair<String, List<String>>> options;

	/**
	 * 옵션 1 대분류
	 */
	private int optionSelected1 = 0;

	/**
	 * 옵션 2 소분류
	 */
	private int optionSelected2 = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plays);

		mappingViews(this);
		initializeUI();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void initializeUI()
	{
		super.initializeUI();

		if (null == adapter)
		{
			adapter = new PlaysAdapter();
			adapter.setList(facildata);
			listview.setAdapter(adapter);
		}

		listview.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.cell_header_plays, listview, false));
		listview.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				if (isLoading() || isNoMoreData.get() || adapter.getList().isEmpty() || 0 == totalItemCount)
				{
					return;
				}

				// 마지막 항목이 표시 중이면
				if ((firstVisibleItem + visibleItemCount) == totalItemCount)
				{
					requestInfo(adapter.getList().size() + 1, adapter.getList().size() + Constants.FETCH_COUNT);
				}
			}
		});
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

		navibar.setActionFunc(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, PlaysSearchActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
		navibar.setActionFuncIcon(drawable(R.drawable.ic_action_search));

		// 제목 설정
		navibar.setTitle(string(R.string.title_activity_plays));

		if (null == options)
		{
			options = new ArrayList<Pair<String, List<String>>>();

			// 문화시설-테마 세부코드
			Iterator<CodeInfo> it = App.facilThemeCode.iterator();
			String[] themecodes = new String[App.facilThemeCode.size()];
			for (int ii = 0; it.hasNext(); ii++)
			{
				themecodes[ii] = it.next().CODENAME;
			}
			options.add(new Pair<String, List<String>>(string(R.string.option_main_facil_theme), Arrays.asList(themecodes)));

			// 문화시설-주제 세부코드
			it = App.facilSubjectCode.iterator();
			String[] subjcodes = new String[App.facilSubjectCode.size()];
			for (int ii = 0; it.hasNext(); ii++)
			{
				subjcodes[ii] = it.next().CODENAME;
			}
			options.add(new Pair<String, List<String>>(string(R.string.option_main_facil_subj), Arrays.asList(subjcodes)));

			// 공연 세부코드
			it = App.playCode.iterator();
			String[] playcodes = new String[App.playCode.size()];
			for (int ii = 0; it.hasNext(); ii++)
			{
				playcodes[ii] = it.next().CODENAME;
			}
			options.add(new Pair<String, List<String>>(string(R.string.option_main_play), Arrays.asList(playcodes)));
			optionSelected1 = PreferencesUtil.getInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PLAYOPT1, Constants.OPTION_START_INDEX);
			optionSelected2 = PreferencesUtil.getInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PLAYOPT2, Constants.OPTION_START_INDEX);
			navibar.setOptionData(options, optionSelected1, optionSelected2);
			navibar.setOptionListener(this);
			navibar.setOptionText(optionSelected1, optionSelected2);
		}
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
	protected void onResume()
	{
		super.onResume();

		if (adapter.getList().isEmpty())
		{
			request();
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		ServiceExecutor.getInstance().cancelAll();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	@Override
	public void OnOptionSelected(int optionSelected1, int optionSelected2)
	{
		this.optionSelected1 = optionSelected1;
		this.optionSelected2 = optionSelected2;

		PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PLAYOPT1, optionSelected1);
		PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PLAYOPT2, optionSelected2);

		request();
	}

	/**
	 * 로딩표시
	 * 
	 * @since 1.0.0
	 */
	private void showLoading()
	{
		startProgress("", true, new OnCancelListener()
		{

			@Override
			public void onCancel(DialogInterface dialog)
			{
				ServiceExecutor.getInstance().cancelAll();
				loadingAccCount.set(ATOMIC_LOADING_INIT);
			}
		}, Constants.COLOR_BLACK);
	}

	/**
	 * 데이터 초기화 후 갱신
	 * 
	 * @since 1.0.0
	 */
	private void clearData()
	{
		adapter.clear();
		isNoMoreData.set(false);
		refreshData();
	}

	/**
	 * 데이터 갱신
	 * 
	 * @since 1.0.0
	 */
	synchronized private void refreshData()
	{
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				adapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 
	 * <h3><b>PlaysAdapter</b></h3></br>
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	class PlaysAdapter<T> extends OneSectionAdapter<T>
	{
		@SuppressWarnings("unchecked")
		@Override
		public View getAmazingView(final int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			ViewHolder viewHolder = null;
			if (null == convertView)
			{
				view = getLayoutInflater().inflate(R.layout.cell_plays, null);

				viewHolder = new ViewHolder();
				viewHolder.cellSelector = (LinearLayout) view.findViewById(R.id.cellSelector);
				viewHolder.title = (TextView) view.findViewById(R.id.title);
				viewHolder.desc = (TextView) view.findViewById(R.id.desc);

				viewHolder.title.setTypeface(SeoulFont.getInstance().getSeoulHangang());
				viewHolder.desc.setTypeface(SeoulFont.getInstance().getSeoulHangang());
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
					switch (optionSelected1)
					{
					case MAIN_PLAY:
					{
						Intent intent = new Intent(context, PlaysDetailActivity.class);
						Bundle bundle = new Bundle();
						bundle.putParcelable(Constants.EXTRA_KEY_PLAYS_SIMPLE_INFO, (PlaySimpleInfo) getItem(position));
						intent.putExtras(bundle);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
						break;
					case MAIN_FACIL_THEME:
					case MAIN_FACIL_SUBJ:
					{
						Intent intent = new Intent(context, FacilsDetailActivity.class);
						Bundle bundle = new Bundle();
						bundle.putParcelable(Constants.EXTRA_KEY_FACILS_SIMPLE_INFO, (FacilSimpleInfo) getItem(position));
						intent.putExtras(bundle);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
						break;
					}
				}
			});

			switch (optionSelected1)
			{
			case MAIN_PLAY:
			{
				PlaySimpleInfo item = (PlaySimpleInfo) getItem(position);

				viewHolder.title.setText(item.TITLE);
				if (0 < item.PLACE.length())
				{
					viewHolder.desc.setText(item.PLACE + ", " + item.STRTDATE + " ~ " + item.END_DATE);
				}
				else
				{
					viewHolder.desc.setText(item.STRTDATE + " ~ " + item.END_DATE);
				}
			}
				break;
			case MAIN_FACIL_THEME:
			case MAIN_FACIL_SUBJ:
			{
				FacilSimpleInfo item = (FacilSimpleInfo) getItem(position);

				viewHolder.title.setText(item.FAC_NAME);
				viewHolder.desc.setText(item.ADDR);
			}
				break;
			}

			return view;
		}

		@Override
		public String[] getSections()
		{
			String[] res = new String[1];
			res[0] = sectionName;
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
			TextView title;
			TextView desc;
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// 이하 OpenAPI 연동
	// ///////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 요청 누적 계수
	 */
	private AtomicInteger requestAccCount = new AtomicInteger(0);

	/**
	 * 더보기 데이터 존재여부
	 */
	private AtomicBoolean isNoMoreData = new AtomicBoolean(false);

	@Override
	public void iNetEnabled()
	{
		super.iNetEnabled();
	}

	@Override
	public void iNetDisabled()
	{
	}

	@Override
	public boolean iNetListenedTransaction(BaseTrans tr)
	{
		if (0 < requestAccCount.get())
		{
			requestAccCount.decrementAndGet();
		}
		HttpParam param = (HttpParam) tr.getParam();

		if (((OpenBase) tr).isError())
		{
			handleError(tr);
			return false;
		}

		switch (OpenAPI.valueOf(param.getRequestKey()))
		{
		case CULTURE_PLAY_INFO:
			handleOpenPlayInfo(tr);
			break;
		case CULTURE_FACIL_INFO_BY_THEME:
		case CULTURE_FACIL_INFO_BY_SUBJ:
			handleOpenFacilInfo(tr);
			break;
		case UNKNOWN:
		default:
			break;
		}
		return super.iNetListenedTransaction(tr);
	}

	@Override
	public boolean iNetListenedError(BaseTransEx ex)
	{
		if (0 < requestAccCount.get())
		{
			requestAccCount.decrementAndGet();
		}

		return super.iNetListenedError(ex);
	}

	/**
	 * 최초 요청 및 옵션변경시 요청
	 * 
	 * @since 1.0.0
	 */
	private void request()
	{
		clearData();

		CodeInfo code = null;
		switch (optionSelected1)
		{
		case MAIN_PLAY:
			code = App.playCode.get(optionSelected2);
			adapter.setList(playdata);
			break;
		case MAIN_FACIL_THEME:
			code = App.facilThemeCode.get(optionSelected2);
			adapter.setList(facildata);
			break;
		case MAIN_FACIL_SUBJ:
			code = App.facilSubjectCode.get(optionSelected2);
			adapter.setList(facildata);
			break;
		}
		sectionName = code.CODENAME;
		sectionCode = code.CODE;

		requestInfo(Constants.INIT_START_INDEX, Constants.FETCH_COUNT);
	}

	/**
	 * 공연/문화시설 정보 조회 요청
	 * 
	 * @since 1.0.0
	 * @param start
	 * @param end
	 */
	private void requestInfo(int start, int end)
	{
		showLoading();
		requestAccCount.incrementAndGet();
		switch (optionSelected1)
		{
		case MAIN_PLAY:
			ServiceExecutor.getInstance().getPlayInfo(OpenAPI.CULTURE_PLAY_INFO.name(), listener, OpenAPI.CULTURE_PLAY_INFO, sectionCode, start, end);
			break;
		case MAIN_FACIL_THEME:
			ServiceExecutor.getInstance().getFacilInfo(OpenAPI.CULTURE_FACIL_INFO_BY_THEME.name(), listener, OpenAPI.CULTURE_FACIL_INFO_BY_THEME, sectionCode, start, end);
			break;
		case MAIN_FACIL_SUBJ:
			ServiceExecutor.getInstance().getFacilInfo(OpenAPI.CULTURE_FACIL_INFO_BY_SUBJ.name(), listener, OpenAPI.CULTURE_FACIL_INFO_BY_SUBJ, sectionCode, start, end);
			break;
		}
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleError(BaseTrans tr)
	{
		OpenBase item = (OpenBase) tr;
		Logger.d1(LOG, "error >> " + item.getErrorInfoType().getCode());
		Logger.d1(LOG, " " + item.getErrorInfoType().getTitle(this));
		Logger.d1(LOG, " " + item.getErrorInfoType().getMsg(this));
		Toast.makeText(context, string(R.string.failed_request), Toast.LENGTH_SHORT).show();
	}

	/**
	 * 공연정보 처리
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenPlayInfo(BaseTrans tr)
	{
		OpenPlayInfo item = (OpenPlayInfo) tr;
		if (!item.infos.isEmpty())
		{
			if (Constants.FETCH_COUNT > item.infos.size())
			{
				isNoMoreData.set(true);
			}
			playdata.addAll(item.infos);
			refreshData();
		}
		else
		{
			isNoMoreData.set(true);
			Toast.makeText(context, string(R.string.empty_data), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 문화시설정보 처리
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenFacilInfo(BaseTrans tr)
	{
		OpenFacilInfo item = (OpenFacilInfo) tr;
		if (!item.infos.isEmpty())
		{
			if (Constants.FETCH_COUNT > item.infos.size())
			{
				isNoMoreData.set(true);
			}
			facildata.addAll(item.infos);
			refreshData();
		}
		else
		{
			isNoMoreData.set(true);
			Toast.makeText(context, string(R.string.empty_data), Toast.LENGTH_SHORT).show();
		}
	}

}
