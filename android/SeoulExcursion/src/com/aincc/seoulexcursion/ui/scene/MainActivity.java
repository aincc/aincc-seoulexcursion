package com.aincc.seoulexcursion.ui.scene;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

import com.aincc.lib.common.annotation.InjectView;
import com.aincc.lib.network.common.BaseTrans;
import com.aincc.lib.network.common.BaseTransEx;
import com.aincc.lib.network.http.HttpParam;
import com.aincc.lib.ui.control.ExitBackChecker;
import com.aincc.lib.ui.widget.page.APagerAdapter.OnPagerClickListener;
import com.aincc.lib.ui.widget.page.PageIndicator;
import com.aincc.lib.ui.widget.page.PageIndicator.OnPageClickListener;
import com.aincc.lib.util.PreferencesUtil;
import com.aincc.seoulexcursion.App;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.SeoulBaseActivity;
import com.aincc.seoulexcursion.ui.control.MainPagerAdapter;
import com.aincc.seoulexcursion.ui.control.MainPagerAdapter.MainPageItem;
import com.aincc.seoulexcursion.ui.scene.assets.AssetsActivity;
import com.aincc.seoulexcursion.ui.scene.parks.ParksActivity;
import com.aincc.seoulexcursion.ui.scene.plays.PlaysActivity;
import com.aincc.seoulexcursion.ui.widget.Navibar;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.seoulexcursion.util.SeoulFont;
import com.aincc.seoulopenapi.OpenAPI;
import com.aincc.seoulopenapi.ServiceExecutor;
import com.aincc.seoulopenapi.model.CodeInfo;
import com.aincc.seoulopenapi.network.OpenBase;
import com.aincc.seoulopenapi.openapi.culture.OpenAssetsCodeInfo;
import com.aincc.seoulopenapi.openapi.culture.OpenFacilCodeInfo;
import com.aincc.seoulopenapi.openapi.culture.OpenPlayCodeInfo;

/**
 * 
 * <h3><b>MainActivity</b></h3></br>
 * 
 * 메인 화면 <br>
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class MainActivity extends SeoulBaseActivity
{
	/**
	 * 상단 네비게이션 바
	 */
	@InjectView
	private Navibar navibar;

	/**
	 * 대메뉴 페이져
	 */
	@InjectView
	private ViewPager pager;

	/**
	 * 대메뉴 문구
	 */
	@InjectView
	private TextView title;

	/**
	 * 대메뉴 페이지 인디게이터
	 */
	@InjectView
	private PageIndicator pageIndicator;

	/**
	 * 대메뉴 페이저 어댑터
	 */
	private MainPagerAdapter adapter;

	/**
	 * 대메뉴 페이지 항목
	 */
	private List<MainPageItem> pages;

	/**
	 * 핸들러
	 */
	private LauncherHandler handler;

	/**
	 * 종료처리
	 */
	private ExitBackChecker exitChecker;

	/**
	 * 요청누적계수
	 */
	private AtomicInteger requestAccCount = new AtomicInteger(0);

	/**
	 * 문화재 분류코드 이터레이터
	 */
	private Iterator<CodeInfo> assetsIterator;
	private CodeInfo currentAssetsCode = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 세부화면 실행 핸들러
		handler = new LauncherHandler(this);

		// 종료체크 설정
		exitChecker = new ExitBackChecker(this, handler, 2000);

		mappingViews(this);
		initializeUI();
	}

	@Override
	protected void initializeUI()
	{
		super.initializeUI();

		setCurrentMenu(0);
		title.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		initializePager();
		initializeCodeInfo();
	}

	/**
	 * 상단 네비게이션 바 초기화
	 * 
	 * @since 1.0.0
	 */
	protected void initializeNavibar()
	{
		super.initializeNavibar();
		navibar.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		navibar.setActionBackVisible(View.INVISIBLE);
		navibar.setActionFuncVisible(View.INVISIBLE);
		navibar.setTitle(string(R.string.title_activity_main));
	}

	/**
	 * 대메뉴 페이저 초기화
	 * 
	 * @since 1.0.0
	 */
	private void initializePager()
	{
		// 대메뉴 항목 설정
		pages = new ArrayList<MainPagerAdapter.MainPageItem>();
		pages.add(new MainPageItem(R.layout.main_page, R.id.presentImageView, R.drawable.seoul_park));
		pages.add(new MainPageItem(R.layout.main_page, R.id.presentImageView, R.drawable.seoul_play));
		pages.add(new MainPageItem(R.layout.main_page, R.id.presentImageView, R.drawable.seoul_assets));

		// 페이저 어댑터 설정
		adapter = new MainPagerAdapter(this, pages);
		adapter.setOnPagerClickListener(new OnPagerClickListener()
		{

			@Override
			public void onPagerClicked(int position)
			{
				Message msg = handler.obtainMessage(position);
				handler.sendMessage(msg);
			}
		});

		// 페이저 설정
		pager.setAdapter(adapter);
		pager.setOverScrollMode(View.OVER_SCROLL_NEVER);
		pager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int position)
			{
				pageIndicator.setPage(position);
				setCurrentMenu(position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{
			}
		});

		// 페이지 인디게이터 설정
		pageIndicator.setColor(0xFFFF711C, 0x80808080);
		pageIndicator.setMaxPage(pages.size());
		pageIndicator.setOnPageClickListener(new OnPageClickListener()
		{

			@Override
			public void onPageClick(PageIndicator v, int page)
			{
				pager.setCurrentItem(page, true);
				setCurrentMenu(page);
			}
		});
	}

	/**
	 * 코드정보 초기화 요청
	 * 
	 * @since 1.0.0
	 */
	private void initializeCodeInfo()
	{
		requestThemeCategory();
		requestSubjectCategory();
		requestAssetsCategory();
		requestPlayCategory();
	}

	/**
	 * 현재 메뉴 문구 설정
	 * 
	 * @since 1.0.0
	 * @param position
	 */
	private void setCurrentMenu(int position)
	{
		switch (position)
		{
		case MOVE_TO_PARKS:
			title.setText(string(R.string.main_menu_park));
			break;
		case MOVE_TO_PLAY:
			title.setText(string(R.string.main_menu_play));
			break;
		case MOVE_TO_ASSETS:
			title.setText(string(R.string.main_menu_assets));
			break;
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

		// 종료체크 옵저버 등록
		exitChecker.registerObserver();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		ServiceExecutor.getInstance().cancelAll();
		// 종료체크 옵저버 해제
		exitChecker.unregisterObserver();
	}

	@Override
	public void onBackPressed()
	{
		// 백버튼 처리
		exitChecker.backPressed();
	}

	/**
	 * 로딩 표시 (초기화 과정이므로, 취소 불가)
	 * 
	 * @since 1.0.0
	 */
	private void showLoading()
	{
		startProgress(string(R.string.loading_init), false, null, Constants.COLOR_BLACK);
	}

	/**
	 * 문화시설 테마분류 요청
	 * 
	 * @since 1.0.0
	 */
	private void requestThemeCategory()
	{
		if (0 != App.facilThemeCode.size())
		{
			return;
		}
		showLoading();
		requestAccCount.incrementAndGet();
		ServiceExecutor.getInstance().getFacilCodeInfo(OpenAPI.CULTURE_FACIL_CATEGORY_BY_THEME.name(), listener, OpenAPI.CULTURE_FACIL_CATEGORY_BY_THEME, Constants.INIT_START_INDEX, Constants.FETCH_COUNT);
	}

	/**
	 * 문화시설 주제분류 요청
	 * 
	 * @since 1.0.0
	 */
	private void requestSubjectCategory()
	{
		if (0 != App.facilSubjectCode.size())
		{
			return;
		}
		showLoading();
		requestAccCount.incrementAndGet();
		ServiceExecutor.getInstance().getFacilCodeInfo(OpenAPI.CULTURE_FACIL_CATEGORY_BY_SUBJ.name(), listener, OpenAPI.CULTURE_FACIL_CATEGORY_BY_SUBJ, Constants.INIT_START_INDEX, Constants.FETCH_COUNT);
	}

	/**
	 * 문화재 분류코드 요청
	 * 
	 * @since 1.0.0
	 */
	private void requestAssetsCategory()
	{
		if (0 != App.assetsCode.size())
		{
			return;
		}
		showLoading();
		requestAccCount.incrementAndGet();
		ServiceExecutor.getInstance().getAssetsCodeInfo(OpenAPI.CULTURE_ASSETS_CATEGORY.name(), listener, OpenAPI.CULTURE_ASSETS_CATEGORY, null, Constants.INIT_START_INDEX, Constants.FETCH_COUNT);
	}

	/**
	 * 문화재 세부분류코드 요청
	 * 
	 * @since 1.0.0
	 */
	private void requestAssetsDetailCategory(String upperCode)
	{
		// 요청구분을 OpenAPI 타입으로 구분되므로, 순차적으로 처리한다.
		showLoading();
		requestAccCount.incrementAndGet();
		ServiceExecutor.getInstance().getAssetsCodeInfo(OpenAPI.CULTURE_ASSETS_DETAIL_CATEGORY.name(), listener, OpenAPI.CULTURE_ASSETS_DETAIL_CATEGORY, upperCode, Constants.INIT_START_INDEX, Constants.FETCH_COUNT);
	}

	/**
	 * 공연행사 주제분류코드 요청
	 * 
	 * @since 1.0.0
	 */
	private void requestPlayCategory()
	{
		if (0 != App.playCode.size())
		{
			return;
		}
		showLoading();
		requestAccCount.incrementAndGet();
		ServiceExecutor.getInstance().getPlayCodeInfo(OpenAPI.CULTURE_PLAY_CATEGORY.name(), listener, OpenAPI.CULTURE_PLAY_CATEGORY, Constants.INIT_START_INDEX, Constants.FETCH_COUNT);
	}

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
		case CULTURE_FACIL_CATEGORY_BY_THEME:
			handleOpenFacilCategoryTheme(tr);
			break;
		case CULTURE_FACIL_CATEGORY_BY_SUBJ:
			handleOpenFacilCategorySubject(tr);
			break;
		case CULTURE_ASSETS_CATEGORY:
			handleOpenAssetsCategory(tr);
			break;
		case CULTURE_PLAY_CATEGORY:
			handleOpenPlayCategory(tr);
			break;
		case CULTURE_ASSETS_DETAIL_CATEGORY:
			handleOpenAssetsDetailCategory(tr);
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
	}

	/**
	 * 문화시설 테마분류 목록 검색
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenFacilCategoryTheme(BaseTrans tr)
	{
		OpenFacilCodeInfo item = (OpenFacilCodeInfo) tr;
		if (!item.infos.isEmpty())
		{
			StringBuilder sb = new StringBuilder();
			for (CodeInfo code : item.infos)
			{
				sb.append(code.CODE).append("|").append(code.CODENAME).append("|");
			}
			PreferencesUtil.setString(this, Constants.PREFS_CODE_FILE, Constants.PREFS_KEY_FACILITY_THEME, sb.toString());
			App.setFacilThemeCode(sb.toString());
		}
	}

	/**
	 * 문화시설 주제분류 목록 검색
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenFacilCategorySubject(BaseTrans tr)
	{
		OpenFacilCodeInfo item = (OpenFacilCodeInfo) tr;
		if (!item.infos.isEmpty())
		{
			StringBuilder sb = new StringBuilder();
			for (CodeInfo code : item.infos)
			{
				sb.append(code.CODE).append("|").append(code.CODENAME).append("|");
			}
			PreferencesUtil.setString(this, Constants.PREFS_CODE_FILE, Constants.PREFS_KEY_FACILITY_SUBJECT, sb.toString());
			App.setFacilSubjectCode(sb.toString());
		}
	}

	/**
	 * 문화재 분류 목록 검색
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenAssetsCategory(BaseTrans tr)
	{
		OpenAssetsCodeInfo item = (OpenAssetsCodeInfo) tr;
		if (!item.infos.isEmpty())
		{
			StringBuilder sb = new StringBuilder();
			for (CodeInfo code : item.infos)
			{
				sb.append(code.CODE).append("|").append(code.CODENAME).append("|");
			}
			PreferencesUtil.setString(this, Constants.PREFS_CODE_FILE, Constants.PREFS_KEY_ASSETS, sb.toString());
			App.setAssetsCode(sb.toString());

			assetsIterator = App.assetsCode.iterator();
			if (assetsIterator.hasNext())
			{
				currentAssetsCode = assetsIterator.next();
				requestAssetsDetailCategory(currentAssetsCode.CODE);
			}
		}
	}

	/**
	 * 문화재 세부분류 목록 검색
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenAssetsDetailCategory(BaseTrans tr)
	{
		OpenAssetsCodeInfo item = (OpenAssetsCodeInfo) tr;
		if (!item.infos.isEmpty())
		{
			StringBuilder sb = new StringBuilder();
			for (CodeInfo code : item.infos)
			{
				sb.append(code.CODE).append("|").append(code.CODENAME).append("|");
			}
			PreferencesUtil.setString(this, Constants.PREFS_CODE_FILE, Constants.PREFS_KEY_ASSETS_DETAIL + currentAssetsCode.CODE, sb.toString());
			App.setAssetsDetailCode(currentAssetsCode, sb.toString());
		}

		// 다음 문화재 메인분류에 대한 세부분류 요청
		if (assetsIterator.hasNext())
		{
			currentAssetsCode = assetsIterator.next();
			requestAssetsDetailCategory(currentAssetsCode.CODE);
		}
	}

	/**
	 * 공연행사 주제분류 목록 검색
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenPlayCategory(BaseTrans tr)
	{
		OpenPlayCodeInfo item = (OpenPlayCodeInfo) tr;
		if (!item.infos.isEmpty())
		{
			StringBuilder sb = new StringBuilder();
			for (CodeInfo code : item.infos)
			{
				sb.append(code.CODE).append("|").append(code.CODENAME).append("|");
			}
			PreferencesUtil.setString(this, Constants.PREFS_CODE_FILE, Constants.PREFS_KEY_PLAY, sb.toString());
			App.setPlayCode(sb.toString());
		}
	}

	private static final String LOG = "Seoul";
	private static final int MOVE_TO_PARKS = 0;
	private static final int MOVE_TO_PLAY = 1;
	private static final int MOVE_TO_ASSETS = 2;

	private static final int MOVE_TO_UPDATE = 999;

	/**
	 * 공원정보 조회 액티비티 호출
	 * 
	 * @since 1.0.0
	 */
	private void moveToParks()
	{
		Intent intent = new Intent(this, ParksActivity.class);
		startActivity(intent);
	}

	/**
	 * 공연/문화시설 조회 액티비티 호출
	 * 
	 * @since 1.0.0
	 */
	private void moveToPlays()
	{
		Intent intent = new Intent(this, PlaysActivity.class);
		startActivity(intent);
	}

	/**
	 * 문화재정보 조회 액티비티 호출
	 * 
	 * @since 1.0.0
	 */
	private void moveToAssets()
	{
		Intent intent = new Intent(this, AssetsActivity.class);
		startActivity(intent);
	}

	// TODO: 버전체크 가능하도록 해야겠어..근데 어떻게?
	// 확인해보고 되면 팝업 띄워서 업데이트 연결하도록 해봅시다.
	/**
	 * 마켓 연결
	 * 
	 * @since 1.0.0
	 */
	private void moveToUpdateMarket()
	{
		String uri = "market://details?id=com.aincc.seoulexcursion";
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(intent);
	}

	/**
	 * 메시지 처리
	 * 
	 * @since 1.0.0
	 * @param msg
	 */
	public void handleMessage(Message msg)
	{
		switch (msg.what)
		{
		case MOVE_TO_PARKS:
			moveToParks();
			break;
		case MOVE_TO_PLAY:
			moveToPlays();
			break;
		case MOVE_TO_ASSETS:
			moveToAssets();
			break;
		case MOVE_TO_UPDATE:
			moveToUpdateMarket();
		default:
			break;
		}
	}

	/**
	 * 
	 * <h3><b>LauncherHandler</b></h3></br>
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	static class LauncherHandler extends Handler
	{
		private final WeakReference<MainActivity> activity;

		LauncherHandler(MainActivity activity)
		{
			this.activity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			MainActivity activity = this.activity.get();
			activity.handleMessage(msg);
		}
	}
}
