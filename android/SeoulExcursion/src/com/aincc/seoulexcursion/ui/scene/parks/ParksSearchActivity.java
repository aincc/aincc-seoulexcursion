package com.aincc.seoulexcursion.ui.scene.parks;

import java.util.ArrayList;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aincc.cache.ImageLoader;
import com.aincc.cache.ImageWorker.ImageWorkerAdapter;
import com.aincc.ui.common.annotation.InjectView;
import com.aincc.network.common.BaseTrans;
import com.aincc.network.common.BaseTransEx;
import com.aincc.network.http.HttpParam;
import com.aincc.ui.widget.list.section.AmazingListView;
import com.aincc.util.PreferencesUtil;
import com.aincc.seoulexcursion.App;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.SeoulBaseActivity;
import com.aincc.seoulexcursion.ui.control.SectionAdapter;
import com.aincc.seoulexcursion.ui.widget.Navibar;
import com.aincc.seoulexcursion.ui.widget.Navisheet.OptionSelected;
import com.aincc.seoulexcursion.ui.widget.SearchBar;
import com.aincc.seoulexcursion.ui.widget.SearchBar.SearchRequest;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.seoulexcursion.util.SeoulFont;
import com.aincc.seoulopenapi.OpenAPI;
import com.aincc.seoulopenapi.ServiceExecutor;
import com.aincc.seoulopenapi.model.ParkInfo;
import com.aincc.seoulopenapi.network.OpenBase;
import com.aincc.seoulopenapi.openapi.park.OpenParkInfo;

/**
 * 
 * <h3><b>ParksSearchActivity</b></h3></br>
 * 
 * 공원목록 표시 <br>
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class ParksSearchActivity extends SeoulBaseActivity implements OptionSelected
{
	/**
	 * <pre>
	 * 공원정보 
	 * 세부화면에서 지도에 각 공원의 위치를 표시하기 위하여 static 으로 관리
	 * </pre>
	 */
	private static List<Pair<String, List<ParkInfo>>> listdata = new ArrayList<Pair<String, List<ParkInfo>>>();

	/**
	 * 상단 네비게이션 바
	 */
	@InjectView
	private Navibar navibar;

	/**
	 * 공원 리스트뷰
	 */
	@InjectView
	private AmazingListView listview;

	/**
	 * 공원 어댑터
	 */
	private SearchParksAdapter<ParkInfo> adapter;

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
	 * 이미지 다운로더
	 */
	private ImageLoader imageLoader;

	/**
	 * 이미지 어댑터
	 */
	private ParkImageAdapter imageAdapter;

	/**
	 * 검색리스너
	 */
	private SearchRequest searchListener = new SearchRequest()
	{

		@Override
		public void OnSearchRequest(String keyword)
		{
			Toast.makeText(context, "Search : " + keyword, Toast.LENGTH_SHORT).show();
			clearData();
			searchName = keyword;
			requestSearch(keyword, Constants.INIT_START_INDEX, Constants.FETCH_COUNT);
		}

		@Override
		public void OnSearchCancel()
		{
			Toast.makeText(context, "Search canceled", Toast.LENGTH_SHORT).show();
		}
	};

	/**
	 * 현재 조회된 공원정보 가져오기
	 * 
	 * @since 1.0.0
	 * @return 공원목록
	 */
	public static List<Pair<String, List<ParkInfo>>> getParks()
	{
		return listdata;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parks_search);

		mappingViews(this);
		initializeUI();
		SearchBar.show(context, searchListener);
	}

	@Override
	protected void initializeUI()
	{
		super.initializeUI();

		if (null == imageAdapter)
		{
			imageAdapter = new ParkImageAdapter();
		}

		// 검색은 1개의 섹션만 관리한다.
		if (0 == listdata.size())
		{
			listdata.add(new Pair<String, List<ParkInfo>>("Search", new ArrayList<ParkInfo>()));
		}

		if (null == adapter)
		{
			adapter = new SearchParksAdapter<ParkInfo>();
			adapter.setList(listdata);
			listview.setAdapter(adapter);
		}

		listview.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.cell_header_parks, listview, false));
		listview.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				if (isLoading() || isNoMoreData.get() || listdata.isEmpty() || 0 == totalItemCount)
				{
					return;
				}

				// 마지막 항목이 표시 중이면
				if ((firstVisibleItem + visibleItemCount) == totalItemCount)
				{
					requestSearch(searchName, listdata.get(0).second.size() + 1, listdata.get(0).second.size() + Constants.FETCH_COUNT);
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
				SearchBar.show(context, searchListener);
			}
		});
		navibar.setActionFuncIcon(drawable(R.drawable.ic_action_search));

		if (null == options)
		{
			options = new ArrayList<Pair<String, List<String>>>();
			options.add(new Pair<String, List<String>>(string(R.string.search_addr), null));
			options.add(new Pair<String, List<String>>(string(R.string.search_park), null));
			optionSelected1 = PreferencesUtil.getInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PARKOPT3, Constants.OPTION_START_INDEX);
			optionSelected2 = Constants.OPTION_INVALID_INDEX;
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

		PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PARKOPT3, optionSelected1);

		switch (optionSelected1)
		{
		case Constants.SEARCH_BY_ADDR:
			searchType = optionSelected1;
			break;
		case Constants.SEARCH_BY_PARK:
			searchType = optionSelected1;
			break;
		}

		clearData();
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

		imageLoader = App.getImageLoader(imageAdapter);

		// 2012.09.01 aincc : 화면갱신처리
		refreshData();
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
		if (0 == listdata.size())
		{
			listdata.add(new Pair<String, List<ParkInfo>>("Search", new ArrayList<ParkInfo>()));
		}
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
	 * <h3><b>SearchParksAdapter</b></h3></br>
	 * 
	 * 공원정보 어댑터
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	class SearchParksAdapter<T> extends SectionAdapter<T>
	{

		@SuppressWarnings("unchecked")
		@Override
		public View getAmazingView(final int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			ViewHolder viewHolder = null;
			if (null == convertView)
			{
				view = getLayoutInflater().inflate(R.layout.cell_parks, null);

				viewHolder = new ViewHolder();
				viewHolder.cellSelector = (LinearLayout) view.findViewById(R.id.cellSelector);
				viewHolder.loading = (ProgressBar) view.findViewById(R.id.loading);
				viewHolder.parkImg = (ImageView) view.findViewById(R.id.image);
				viewHolder.P_PARK = (TextView) view.findViewById(R.id.P_PARK);
				viewHolder.P_ZONE = (TextView) view.findViewById(R.id.P_ZONE);
				viewHolder.P_ADMINTEL = (TextView) view.findViewById(R.id.P_ADMINTEL);

				viewHolder.P_PARK.setTypeface(SeoulFont.getInstance().getSeoulHangang());
				viewHolder.P_ZONE.setTypeface(SeoulFont.getInstance().getSeoulHangang());
				viewHolder.P_ADMINTEL.setTypeface(SeoulFont.getInstance().getSeoulHangang());

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
					Intent intent = new Intent(context, ParksDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable(Constants.EXTRA_KEY_PARK_INFO, (ParkInfo) getItem(position));
					bundle.putInt(Constants.EXTRA_KEY_PARK_DETAIL_FROM, 1);
					intent.putExtras(bundle);
					startActivity(intent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			});

			ParkInfo item = (ParkInfo) getItem(position);
			imageLoader.loadImage(item.P_IMG, viewHolder.parkImg);

			viewHolder.P_PARK.setText(item.P_PARK);
			viewHolder.P_ZONE.setText(item.P_ZONE);
			viewHolder.P_ADMINTEL.setText(item.P_ADMINTEL);

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
			ProgressBar loading;
			ImageView parkImg;
			TextView P_PARK;
			TextView P_ZONE;
			TextView P_ADMINTEL;
		}
	}

	/**
	 * 
	 * <h3><b>ParkImageAdapter</b></h3></br>
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	class ParkImageAdapter extends ImageWorkerAdapter
	{

		@Override
		public Object getItem(int num)
		{
			if (null != listdata)
			{
				int c = 0;
				for (int ii = 0; ii < listdata.size(); ii++)
				{
					if (num >= c && num < c + listdata.get(ii).second.size())
					{
						return listdata.get(ii).second.get(num - c).P_IMG;
					}
					c += listdata.get(ii).second.size();
				}
			}
			return null;
		}

		@Override
		public int getSize()
		{
			if (null != listdata)
			{
				int res = 0;
				for (int ii = 0; ii < listdata.size(); ii++)
				{
					res += listdata.get(ii).second.size();
				}
				return res;
			}
			return 0;
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

	/**
	 * 검색유형
	 */
	private int searchType = Constants.SEARCH_BY_ADDR;

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
		case PARK_INFO_BY_PARKNAME:
		case PARK_INFO_BY_ADDRESS:
			handleOpenParkInfo(tr);
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
	 * 공원정보 검색 요청
	 * 
	 * @since 1.0.0
	 * @param keyword
	 * @param start
	 * @param end
	 */
	private void requestSearch(String keyword, int start, int end)
	{
		showLoading();
		requestAccCount.incrementAndGet();
		switch (searchType)
		{
		case Constants.SEARCH_BY_ADDR:
			ServiceExecutor.getInstance().getParkInfo(OpenAPI.PARK_INFO_BY_ADDRESS.name(), listener, start, end, OpenAPI.PARK_INFO_BY_ADDRESS, keyword);
			break;
		case Constants.SEARCH_BY_PARK:
			ServiceExecutor.getInstance().getParkInfo(OpenAPI.PARK_INFO_BY_PARKNAME.name(), listener, start, end, OpenAPI.PARK_INFO_BY_PARKNAME, keyword);
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
	 * 공원정보 처리
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenParkInfo(BaseTrans tr)
	{
		OpenParkInfo item = (OpenParkInfo) tr;
		if (!item.infos.isEmpty())
		{
			listdata.get(0).second.addAll(item.infos);
			if (Constants.FETCH_COUNT > item.infos.size())
			{
				isNoMoreData.set(true);
			}
			refreshData();
		}
		else
		{
			isNoMoreData.set(true);
			Toast.makeText(context, string(R.string.empty_data), Toast.LENGTH_SHORT).show();
		}
	}
}
