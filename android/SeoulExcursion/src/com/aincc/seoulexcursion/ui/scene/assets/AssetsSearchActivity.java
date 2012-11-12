package com.aincc.seoulexcursion.ui.scene.assets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aincc.ui.common.annotation.InjectView;
import com.aincc.network.common.BaseTrans;
import com.aincc.network.common.BaseTransEx;
import com.aincc.network.http.HttpParam;
import com.aincc.ui.widget.list.section.AmazingListView;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.seoulexcursion.App;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.SeoulBaseActivity;
import com.aincc.seoulexcursion.ui.control.OneSectionAdapter;
import com.aincc.seoulexcursion.ui.widget.Navibar;
import com.aincc.seoulexcursion.ui.widget.SearchBar;
import com.aincc.seoulexcursion.ui.widget.SearchBar.SearchRequest;
import com.aincc.seoulexcursion.util.SeoulFont;
import com.aincc.seoulopenapi.LangCode;
import com.aincc.seoulopenapi.OpenAPI;
import com.aincc.seoulopenapi.ServiceExecutor;
import com.aincc.seoulopenapi.model.AssetsSimpleInfo;
import com.aincc.seoulopenapi.network.OpenBase;
import com.aincc.seoulopenapi.openapi.culture.OpenAssetsSearchBy;

/**
 * 
 * <h3><b>AssetsSearchActivity</b></h3></br>
 * 
 * 문화재목록 표시 <br>
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class AssetsSearchActivity extends SeoulBaseActivity
{
	private List<AssetsSimpleInfo> listdata = new ArrayList<AssetsSimpleInfo>();

	/**
	 * 상단 네비게이션 바
	 */
	@InjectView
	private Navibar navibar;

	/**
	 * 문화재 리스트뷰
	 */
	@InjectView
	private AmazingListView listview;

	/**
	 * 문화재 어댑터
	 */
	private AssetsAdapter<AssetsSimpleInfo> adapter;

	/**
	 * 검색시 사용
	 */
	private String searchName = "";

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assets_search);

		mappingViews(this);
		initializeUI();
		SearchBar.show(context, searchListener);
	}

	@Override
	protected void initializeUI()
	{
		super.initializeUI();

		if (null == adapter)
		{
			adapter = new AssetsAdapter<AssetsSimpleInfo>();
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
					requestSearch(searchName, listdata.size() + 1, listdata.size() + Constants.FETCH_COUNT);
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

		navibar.setTitle(string(R.string.search_asset));
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
	 * 문화재 분류 코드명 가져오기
	 * 
	 * @since 1.0.0
	 * @param code1
	 * @param code2
	 * @return
	 */
	private String getAssetsCode(String code1, String code2)
	{
		if (null != code2 && 0 < code2.length())
		{
			return App.assetsMap.get(code1).first + " > " + App.assetsMap.get(code1).second.get(code2);
		}
		return App.assetsMap.get(code1).first;
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
	 * <h3><b>AssetsAdapter</b></h3></br>
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	class AssetsAdapter<T> extends OneSectionAdapter<T>
	{
		@SuppressWarnings("unchecked")
		@Override
		public View getAmazingView(final int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			ViewHolder viewHolder = null;
			if (null == convertView)
			{
				view = getLayoutInflater().inflate(R.layout.cell_assets, null);

				viewHolder = new ViewHolder();
				viewHolder.cellSelector = (LinearLayout) view.findViewById(R.id.cellSelector);
				viewHolder.CULTASSTK = (TextView) view.findViewById(R.id.CULTASSTK);
				viewHolder.CULTASSTH = (TextView) view.findViewById(R.id.CULTASSTH);
				viewHolder.CLSSCODE = (TextView) view.findViewById(R.id.CLSSCODE);

				viewHolder.CULTASSTK.setTypeface(SeoulFont.getInstance().getSeoulHangang());
				viewHolder.CLSSCODE.setTypeface(SeoulFont.getInstance().getSeoulHangang());

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
					Intent intent = new Intent(context, AssetsDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable(Constants.EXTRA_KEY_ASSETS_SIMPLE_INFO, (AssetsSimpleInfo) getItem(position));
					bundle.putInt(Constants.EXTRA_KEY_ASSETS_LANGUAGE, 0);
					intent.putExtras(bundle);
					startActivity(intent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			});

			AssetsSimpleInfo item = (AssetsSimpleInfo) getItem(position);

			viewHolder.CULTASSTK.setText(item.CULTASSTK);
			if (null != item.CULTASSTH && 0 < item.CULTASSTH.length())
			{
				viewHolder.CULTASSTH.setText("(" + item.CULTASSTH + ")");
			}
			else
			{
				viewHolder.CULTASSTH.setText("");
			}
			viewHolder.CLSSCODE.setText(string(R.string.assets_clss) + " " + getAssetsCode(item.CLSSCODE1, item.CLSSCODE2));
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
			TextView CULTASSTK;
			TextView CULTASSTH;
			TextView CLSSCODE;
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
		case CULTURE_ASSETS_SEARCH_BY_NAME:
			handleOpenAssetsInfoSearchBy(tr);
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
	 * 문화재정보 검색 요청
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
		ServiceExecutor.getInstance().getAssetsSearchBy(OpenAPI.CULTURE_ASSETS_SEARCH_BY_NAME.name(), listener, OpenAPI.CULTURE_ASSETS_SEARCH_BY_NAME, LangCode.KOREAN, keyword, null, start, end);

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
	 * 문화재정보 처리
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenAssetsInfoSearchBy(BaseTrans tr)
	{
		OpenAssetsSearchBy item = (OpenAssetsSearchBy) tr;
		if (!item.infos.isEmpty())
		{
			if (Constants.FETCH_COUNT > item.infos.size())
			{
				isNoMoreData.set(true);
			}
			listdata.addAll(item.infos);
			refreshData();
		}
		else
		{
			isNoMoreData.set(true);
			Toast.makeText(context, string(R.string.empty_data), Toast.LENGTH_SHORT).show();
		}
	}
}
