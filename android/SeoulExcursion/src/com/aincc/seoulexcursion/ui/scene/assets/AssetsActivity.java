package com.aincc.seoulexcursion.ui.scene.assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
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

import com.aincc.ui.common.annotation.InjectView;
import com.aincc.network.common.BaseTrans;
import com.aincc.network.common.BaseTransEx;
import com.aincc.network.http.HttpParam;
import com.aincc.ui.widget.list.section.AmazingListView;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.util.PreferencesUtil;
import com.aincc.seoulexcursion.App;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.SeoulBaseActivity;
import com.aincc.seoulexcursion.ui.control.OneSectionAdapter;
import com.aincc.seoulexcursion.ui.widget.Navibar;
import com.aincc.seoulexcursion.ui.widget.Navisheet.OptionSelected;
import com.aincc.seoulexcursion.util.SeoulFont;
import com.aincc.seoulopenapi.LangCode;
import com.aincc.seoulopenapi.OpenAPI;
import com.aincc.seoulopenapi.ServiceExecutor;
import com.aincc.seoulopenapi.model.AssetsSimpleInfo;
import com.aincc.seoulopenapi.model.CodeInfo;
import com.aincc.seoulopenapi.network.OpenBase;
import com.aincc.seoulopenapi.openapi.culture.OpenAssetsInfo;
import com.aincc.seoulopenapi.openapi.culture.OpenAssetsSearchBy;

/**
 * 
 * <h3><b>AssetsActivity</b></h3></br>
 * 
 * 문화재 목록 표시
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class AssetsActivity extends SeoulBaseActivity implements OptionSelected
{
	/**
	 * 상단 네비게이션 바
	 */
	@InjectView
	private Navibar navibar;

	/**
	 * 옵션정보
	 */
	private List<Pair<String, List<String>>> options;

	/**
	 * 옵션 1 문화재대분류
	 */
	private int optionSelected1 = 0;

	/**
	 * 옵션 2 문화재세부분류
	 */
	private int optionSelected2 = 0;

	/**
	 * 섹션명
	 */
	private String sectionName = "";

	/**
	 * 섹션코드
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
	private AssetsAdapter<AssetsSimpleInfo> adapter;

	/**
	 * 리스트
	 */
	private List<AssetsSimpleInfo> listdata = new ArrayList<AssetsSimpleInfo>();

	/**
	 * 언어선택
	 */
	private int language = 0;

	/**
	 * 언어
	 */
	private LangCode langCode = LangCode.KOREAN;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Logger.d1(LOG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assets);

		mappingViews(this);
		initializeUI();
	}

	@Override
	protected void initializeUI()
	{
		super.initializeUI();

		sectionName = string(R.string.total);
		if (null == adapter)
		{
			adapter = new AssetsAdapter<AssetsSimpleInfo>();
			adapter.setList(listdata);
			listview.setAdapter(adapter);
		}

		listview.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.cell_header_assets, listview, false));
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
					requestAssetsInfo(listdata.size() + 1, listdata.size() + Constants.FETCH_COUNT, sectionCode);
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
				Intent intent = new Intent(context, AssetsSearchActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
		navibar.setActionFuncIcon(drawable(R.drawable.ic_action_search));

		// // 문화재 설명 언어 설정
		// navibar.setActionFunc(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// alertSelectLanguage();
		// }
		// });
		// navibar.setActionFuncIcon(drawable(R.drawable.navigation_lang));
		// navibar.setActionFuncVisible(View.INVISIBLE);

		// 제목 설정
		navibar.setTitle(string(R.string.main_menu_assets));

		if (null == options)
		{
			options = new ArrayList<Pair<String, List<String>>>();
			options.add(new Pair<String, List<String>>(string(R.string.total), Arrays.asList(string(R.string.assets))));

			Iterator<CodeInfo> it = App.assetsCode.iterator();
			while (it.hasNext())
			{
				CodeInfo maincode = it.next();
				List<String> subcodes = new ArrayList<String>();

				Iterator<Pair<String, List<CodeInfo>>> it2 = App.assetsDetailCode.iterator();
				while (it2.hasNext())
				{
					Pair<String, List<CodeInfo>> pair = it2.next();
					if (pair.first.equals(maincode.CODE))
					{
						List<CodeInfo> codes = pair.second;
						for (int ii = 0; ii < codes.size(); ii++)
						{
							subcodes.add(codes.get(ii).CODENAME);
						}
						break;
					}
				}
				options.add(new Pair<String, List<String>>(maincode.CODENAME, subcodes));
			}

			optionSelected1 = PreferencesUtil.getInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_ASSETOPT1, Constants.OPTION_START_INDEX);
			optionSelected2 = PreferencesUtil.getInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_ASSETOPT2, Constants.OPTION_START_INDEX);
			navibar.setOptionData(options, optionSelected1, optionSelected2);
			navibar.setOptionListener(this);
			navibar.setOptionText(optionSelected1, optionSelected2);
		}
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

		if (listdata.isEmpty())
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
	public void finish()
	{
		super.finish();
	}

	@Override
	public void OnOptionSelected(int optionSelected1, int optionSelected2)
	{
		this.optionSelected1 = optionSelected1;
		this.optionSelected2 = optionSelected2;

		PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_ASSETOPT1, optionSelected1);
		PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_ASSETOPT2, optionSelected2);

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
	 * @since 1.0.0
	 */
	@SuppressWarnings("unused")
	private void alertSelectLanguage()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert_title_language);
		builder.setNegativeButton(R.string.alert_btn_cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		builder.setSingleChoiceItems(Constants.LANGUAGES, language, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dlg, int which)
			{
				language = which;
				switch (which)
				{
				case 0:
					langCode = LangCode.KOREAN;
					break;
				case 1:
					langCode = LangCode.ENGLISH;
					break;
				case 2:
					langCode = LangCode.JAPANESE;
					break;
				case 3:
					langCode = LangCode.CHINAB;
					break;
				case 4:
					langCode = LangCode.CHINAG;
					break;
				}

				clearData();
				requestAssetsInfo(Constants.INIT_START_INDEX, Constants.FETCH_COUNT, sectionCode);
				dlg.dismiss();
			}

		});
		builder.show();
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

				switch (langCode)
				{
				case KOREAN:
				case ENGLISH:
					viewHolder.CULTASSTK.setTypeface(SeoulFont.getInstance().getSeoulHangang());
					viewHolder.CLSSCODE.setTypeface(SeoulFont.getInstance().getSeoulHangang());
					break;
				case JAPANESE:
				case CHINAB:
				case CHINAG:
				default:
					viewHolder.CULTASSTK.setTypeface(Typeface.DEFAULT);
					viewHolder.CLSSCODE.setTypeface(Typeface.DEFAULT);
					break;
				}

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
					bundle.putInt(Constants.EXTRA_KEY_ASSETS_LANGUAGE, language);
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
		case CULTURE_ASSETS_INFO:
			handleOpenAssetsInfo(tr);
			break;
		case CULTURE_ASSETS_SEARCH_BY_CODE:
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
	 * 최초 요청 및 옵션변경시 요청
	 * 
	 * @since 1.0.0
	 */
	private void request()
	{
		// 전체
		if (0 == optionSelected1)
		{
			sectionName = string(R.string.total);
			sectionCode = null;

			clearData();
			requestAssetsInfo(Constants.INIT_START_INDEX, Constants.FETCH_COUNT, sectionCode);
		}
		else if (0 < optionSelected1)
		{
			CodeInfo maincode = App.assetsCode.get(optionSelected1 - 1);
			CodeInfo subcode = null;
			Iterator<Pair<String, List<CodeInfo>>> it = App.assetsDetailCode.iterator();
			while (it.hasNext())
			{
				Pair<String, List<CodeInfo>> pair = it.next();
				if (pair.first.equals(maincode.CODE))
				{
					List<CodeInfo> codes = pair.second;
					subcode = codes.get(optionSelected2);
					break;
				}
			}
			if (null != subcode)
			{
				sectionName = maincode.CODENAME + " : " + subcode.CODENAME;
				sectionCode = subcode.CODE;
			}
			else
			{
				sectionName = maincode.CODENAME;
				sectionCode = maincode.CODE;
			}
			clearData();
			requestAssetsInfo(Constants.INIT_START_INDEX, Constants.FETCH_COUNT, sectionCode);
		}
	}

	/**
	 * 문화재정보 조회 요청
	 * 
	 * @since 1.0.0
	 * @param start
	 * @param end
	 * @param code
	 *            세부분류코드 (분류가 선택된 경우만 사용)
	 */
	private void requestAssetsInfo(int start, int end, String code)
	{
		showLoading();
		requestAccCount.incrementAndGet();
		if (null == code)
		{
			ServiceExecutor.getInstance().getAssetsInfo(OpenAPI.CULTURE_ASSETS_INFO.name(), listener, OpenAPI.CULTURE_ASSETS_INFO, langCode, null, start, end);
		}
		else
		{
			ServiceExecutor.getInstance().getAssetsSearchBy(OpenAPI.CULTURE_ASSETS_SEARCH_BY_CODE.name(), listener, OpenAPI.CULTURE_ASSETS_SEARCH_BY_CODE, langCode, null, code, start, end);

		}
	}

	/**
	 * 문화재정보 처리
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenAssetsInfo(BaseTrans tr)
	{
		OpenAssetsInfo item = (OpenAssetsInfo) tr;
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
}
