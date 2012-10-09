package com.aincc.seoulexcursion.ui.scene.plays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

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
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.SeoulBaseActivity;
import com.aincc.seoulexcursion.ui.control.OneSectionAdapter;
import com.aincc.seoulexcursion.ui.widget.Navibar;
import com.aincc.seoulexcursion.ui.widget.Navisheet.OptionSelected;
import com.aincc.seoulexcursion.ui.widget.SearchBar;
import com.aincc.seoulexcursion.ui.widget.SearchBar.SearchRequest;
import com.aincc.seoulexcursion.util.SeoulFont;
import com.aincc.seoulopenapi.OpenAPI;
import com.aincc.seoulopenapi.ServiceExecutor;
import com.aincc.seoulopenapi.model.FacilSimpleInfo;
import com.aincc.seoulopenapi.model.PlaySimpleInfo;
import com.aincc.seoulopenapi.network.OpenBase;
import com.aincc.seoulopenapi.openapi.culture.OpenFacilSearchBy;
import com.aincc.seoulopenapi.openapi.culture.OpenPlaySearchBy;

/**
 * 
 * <h3><b>PlaysSearchActivity</b></h3></br>
 * 
 * 공연/문화시설목록 표시 <br>
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class PlaysSearchActivity extends SeoulBaseActivity implements OptionSelected
{
	private static final int MAIN_FACIL = 0;
	private static final int SUB_FACIL_NAME = 0;
	private static final int SUB_FACIL_LOC = 1;
	private static final int MAIN_PLAY = 1;
	private static final int SUB_PLAY_NAME = 0;
	private static final int SUB_PLAY_LOC = 1;

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
	 * 공연/문화시설 리스트뷰
	 */
	@InjectView
	private AmazingListView listview;

	/**
	 * 어댑터
	 */
	private PlaysAdapter<?> adapter;

	/**
	 * 검색시 사용
	 */
	private String searchName = "";

	/**
	 * 옵션정보
	 */
	private List<Pair<String, List<String>>> options;

	/**
	 * 옵션 1 검색유형
	 */
	private int optionSelected1 = 0;
	private int optionSelected2 = 0;

	/**
	 * 검색리스너
	 */
	private SearchRequest searchListener = new SearchRequest()
	{

		@Override
		public void OnSearchRequest(String keyword)
		{
			// 공백제거
			keyword = StringUtils.replace(keyword, " ", "");
			Toast.makeText(context, "Search : " + keyword, Toast.LENGTH_SHORT).show();
			clearData();
			searchName = keyword;
			request();
		}

		@Override
		public void OnSearchCancel()
		{
			Toast.makeText(context, "Search canceled", Toast.LENGTH_SHORT).show();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plays_search);

		mappingViews(this);
		initializeUI();
		SearchBar.show(context, searchListener);
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
					requestInfo(searchName, adapter.getList().size() + 1, adapter.getList().size() + Constants.FETCH_COUNT);
				}
			}
		});

		// 옵션설정에 따른 어댑터 데이터 리스트 변경
		clearData();

		switch (optionSelected1)
		{
		case MAIN_PLAY:
			adapter.setList(playdata);
			break;
		case MAIN_FACIL:
			adapter.setList(facildata);
			break;
		}
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
				SearchBar.show(context, searchListener);
			}
		});
		navibar.setActionFuncIcon(drawable(R.drawable.ic_action_search));

		// 제목 설정
		navibar.setTitle(string(R.string.title_activity_plays));

		if (null == options)
		{
			options = new ArrayList<Pair<String, List<String>>>();
			options.add(new Pair<String, List<String>>(string(R.string.option_main_facil), Arrays.asList(new String[]
			{ string(R.string.option_sub_name), string(R.string.option_sub_loc) })));
			options.add(new Pair<String, List<String>>(string(R.string.option_main_play), Arrays.asList(new String[]
			{ string(R.string.option_sub_name), string(R.string.option_sub_loc) })));
			optionSelected1 = PreferencesUtil.getInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PLAYOPT3, Constants.OPTION_START_INDEX);
			optionSelected2 = PreferencesUtil.getInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PLAYOPT4, Constants.OPTION_START_INDEX);
			navibar.setOptionData(options, optionSelected1, optionSelected2);
			navibar.setOptionListener(this);
			navibar.setOptionText(optionSelected1, optionSelected2);
		}
	}

	@Override
	public void OnOptionSelected(int optionSelected1, int optionSelected2)
	{
		this.optionSelected1 = optionSelected1;
		this.optionSelected2 = optionSelected2;

		PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PLAYOPT3, optionSelected1);
		PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PLAYOPT4, optionSelected2);

		// 옵션설정에 따른 어댑터 데이터 리스트 변경
		clearData();

		switch (optionSelected1)
		{
		case MAIN_PLAY:
			adapter.setList(playdata);
			break;
		case MAIN_FACIL:
			adapter.setList(facildata);
			break;
		}
	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
		ServiceExecutor.getInstance().cancelAll();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		initializeUI();
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
					case MAIN_FACIL:
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
			case MAIN_FACIL:
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
			res[0] = searchName;
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
		case CULTURE_PLAY_SEARCH_BY_LOC:
		case CULTURE_PLAY_SEARCH_BY_NAME:
			handleOpenPlayInfo(tr);
			break;
		case CULTURE_FACIL_SEARCH_BY_ADDR:
		case CULTURE_FACIL_SEARCH_BY_NAME:
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
		// 옵션설정에 따른 어댑터 데이터 리스트 변경
		clearData();

		switch (optionSelected1)
		{
		case MAIN_PLAY:
			adapter.setList(playdata);
			break;
		case MAIN_FACIL:
			adapter.setList(facildata);
			break;
		}

		requestInfo(searchName, Constants.INIT_START_INDEX, Constants.FETCH_COUNT);
	}

	/**
	 * 공연/문화시설 정보 조회 요청
	 * 
	 * @since 1.0.0
	 * @param keyword
	 * @param start
	 * @param end
	 */
	private void requestInfo(String keyword, int start, int end)
	{
		showLoading();
		requestAccCount.incrementAndGet();
		switch (optionSelected1)
		{
		case MAIN_PLAY:
			switch (optionSelected2)
			{
			case SUB_PLAY_NAME:
				ServiceExecutor.getInstance().getPlaySearchBy(OpenAPI.CULTURE_PLAY_SEARCH_BY_NAME.name(), listener, OpenAPI.CULTURE_PLAY_SEARCH_BY_NAME, keyword, null, null, start, end);
				break;
			case SUB_PLAY_LOC:
				ServiceExecutor.getInstance().getPlaySearchBy(OpenAPI.CULTURE_PLAY_SEARCH_BY_LOC.name(), listener, OpenAPI.CULTURE_PLAY_SEARCH_BY_LOC, keyword, null, null, start, end);
				break;
			}

			break;
		case MAIN_FACIL:
			switch (optionSelected2)
			{
			case SUB_FACIL_NAME:
				ServiceExecutor.getInstance().getFacilSearchBy(OpenAPI.CULTURE_FACIL_SEARCH_BY_NAME.name(), listener, OpenAPI.CULTURE_FACIL_SEARCH_BY_NAME, keyword, start, end);
				break;
			case SUB_FACIL_LOC:
				ServiceExecutor.getInstance().getFacilSearchBy(OpenAPI.CULTURE_FACIL_SEARCH_BY_ADDR.name(), listener, OpenAPI.CULTURE_FACIL_SEARCH_BY_ADDR, keyword, start, end);
				break;
			}
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
		OpenPlaySearchBy item = (OpenPlaySearchBy) tr;
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
		OpenFacilSearchBy item = (OpenFacilSearchBy) tr;
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
