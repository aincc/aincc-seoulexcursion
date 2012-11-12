package com.aincc.seoulexcursion.ui.scene.assets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aincc.ui.common.annotation.InjectView;
import com.aincc.network.common.BaseTrans;
import com.aincc.network.common.BaseTransEx;
import com.aincc.network.http.HttpParam;
import com.aincc.ui.anim.Animationz;
import com.aincc.ui.widget.page.PageIndicator;
import com.aincc.ui.widget.page.PageIndicator.OnPageClickListener;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.util.Utils;
import com.aincc.seoulexcursion.App;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.SeoulBaseActivity;
import com.aincc.seoulexcursion.ui.control.AssetsMediaPagerAdapter;
import com.aincc.seoulexcursion.ui.control.AssetsMediaPagerAdapter.MediaPageItem;
import com.aincc.seoulexcursion.ui.widget.Navibar;
import com.aincc.seoulexcursion.util.SeoulFont;
import com.aincc.seoulopenapi.LangCode;
import com.aincc.seoulopenapi.OpenAPI;
import com.aincc.seoulopenapi.ServiceExecutor;
import com.aincc.seoulopenapi.model.AssetsInfo;
import com.aincc.seoulopenapi.model.AssetsMedia;
import com.aincc.seoulopenapi.model.AssetsSimpleInfo;
import com.aincc.seoulopenapi.network.OpenBase;
import com.aincc.seoulopenapi.openapi.culture.OpenAssetsInfo;
import com.aincc.seoulopenapi.openapi.culture.OpenAssetsMedia;

/**
 * 
 * <h3><b>AssetsDetailActivity</b></h3></br>
 * 
 * 문화재 상세정보 표시<br>
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class AssetsDetailActivity extends SeoulBaseActivity
{
	/**
	 * 상단 네비게이션 바
	 */
	@InjectView
	private Navibar navibar;

	/**
	 * 컨텐츠 레이아웃
	 */
	@InjectView
	private RelativeLayout content_layout;

	/**
	 * 미디어 레이아웃
	 */
	@InjectView(id = R.id.media_layout)
	private RelativeLayout mediaLayout;

	/**
	 * 페이져
	 */
	@InjectView
	private ViewPager pager;

	/**
	 * 페이지 인디게이터
	 */
	@InjectView
	private PageIndicator pageIndicator;

	/**
	 * 페이저 어댑터
	 */
	private AssetsMediaPagerAdapter adapter;

	/**
	 * 페이지 항목
	 */
	private List<MediaPageItem> pages;

	/**
	 * 미디어 보기 토클
	 */
	@InjectView
	private ImageButton mediaToggle;

	/**
	 * 닫기
	 */
	@InjectView
	private ImageButton close;

	/**
	 * 문화재 정보
	 */
	private AssetsSimpleInfo info = null;
	private List<AssetsInfo> assetInfo = new ArrayList<AssetsInfo>();
	private List<AssetsMedia> mediaInfo = new ArrayList<AssetsMedia>();

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
		setContentView(R.layout.activity_assets_detail);

		getIntentData();
		mappingViews(this);
		initializeUI();
	}

	/**
	 * 인덴트 정보 처리
	 * 
	 * @since 1.0.0
	 */
	private void getIntentData()
	{
		Intent data = getIntent();
		if (null != data)
		{
			Bundle bundle = data.getExtras();
			if (null != bundle)
			{
				info = bundle.getParcelable(Constants.EXTRA_KEY_ASSETS_SIMPLE_INFO);
				language = bundle.getInt(Constants.EXTRA_KEY_ASSETS_LANGUAGE);

				switch (language)
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
			}
		}
	}

	@Override
	protected void initializeUI()
	{
		super.initializeUI();

		content_layout.setVisibility(View.GONE);
		mediaToggle.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (View.GONE == mediaLayout.getVisibility())
				{
					if (0 == pages.size())
					{
						Toast.makeText(context, string(R.string.empty_media), Toast.LENGTH_SHORT).show();
					}
					else
					{
						Animation anim = Animationz.translate(context, new PointF(0, -500), new PointF(0, 0), Constants.MEDIA_ANIM_DELAY, android.R.anim.decelerate_interpolator, true);
						mediaLayout.setAnimation(anim);
						mediaLayout.setVisibility(View.VISIBLE);
						mediaToggle.setImageDrawable(drawable(R.drawable.view_as_info));
					}
				}
				else
				{
					Animation anim = Animationz.translate(context, new PointF(0, 0), new PointF(0, mediaLayout.getHeight() * (-1) - 100), Constants.MEDIA_ANIM_DELAY, android.R.anim.accelerate_interpolator, true);
					mediaLayout.setAnimation(anim);
					mediaLayout.postDelayed(new Runnable()
					{

						@Override
						public void run()
						{
							mediaLayout.setVisibility(View.GONE);
						}
					}, Constants.MEDIA_ANIM_DELAY);
					mediaToggle.setImageDrawable(drawable(R.drawable.navigation_picture));
				}
			}
		});

		close.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		initializePager();
		initializeFont(SeoulFont.getInstance().getSeoulHangang());
	}

	@Override
	protected void initializeNavibar()
	{
		super.initializeNavibar();

		navibar.setActionBack(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		// 문화재 설명 언어 설정
		// navibar.setActionFunc(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// alertSelectLanguage();
		// }
		// });
		// navibar.setActionFuncIcon(drawable(R.drawable.navigation_lang));
		navibar.setActionFuncVisible(View.INVISIBLE);

		// 제목 설정
		navibar.setTitle(null != info ? info.CULTASSTK : "");
	}

	/**
	 * 대메뉴 페이저 초기화
	 * 
	 * @since 1.0.0
	 */
	private void initializePager()
	{
		// 대메뉴 항목 설정
		if (null == pages)
		{
			pages = new ArrayList<AssetsMediaPagerAdapter.MediaPageItem>();
		}

		if (null == adapter)
		{
			// 페이저 어댑터 설정
			adapter = new AssetsMediaPagerAdapter(this, pages);
		}

		// 페이저 설정
		pager.setAdapter(adapter);
		pager.setOverScrollMode(View.OVER_SCROLL_NEVER);
		pager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int position)
			{
				pageIndicator.setPage(position);
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
			}
		});
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param tf
	 */
	private void initializeFont(Typeface tf)
	{
		navibar.setTypeface(tf);

		// 폰트 지정
		CLSSCODE.setTypeface(tf);
		CULTASSTK_LABEL.setTypeface(tf);
		CULTASSTK.setTypeface(tf);
		CULTASSTH_LABEL.setTypeface(tf);
		ASSIGNNUM.setTypeface(tf);
		ASSIGNYMD.setTypeface(tf);
		PERIOD.setTypeface(tf);
		SCALE.setTypeface(tf);
		MATERIAL.setTypeface(tf);
		CLASSIFY.setTypeface(tf);
		BINDING.setTypeface(tf);
		QUANTITY.setTypeface(tf);
		SIZING.setTypeface(tf);
		OWNER.setTypeface(tf);
		LOCATION.setTypeface(tf);
		DESIGNER.setTypeface(tf);
		BUILDER.setTypeface(tf);
		HOLDER.setTypeface(tf);
		SUCCESSOR.setTypeface(tf);
		ETC_CLSS.setTypeface(tf);
		ASSIGNETC.setTypeface(tf);
		CONTENTS.setTypeface(tf);
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
					initializeFont(SeoulFont.getInstance().getSeoulHangang());
					break;
				case 1:
					langCode = LangCode.ENGLISH;
					initializeFont(SeoulFont.getInstance().getSeoulHangang());
					break;
				case 2:
					langCode = LangCode.JAPANESE;
					initializeFont(Typeface.DEFAULT);
					break;
				case 3:
					langCode = LangCode.CHINAB;
					initializeFont(Typeface.DEFAULT);
					break;
				case 4:
					langCode = LangCode.CHINAG;
					initializeFont(Typeface.DEFAULT);
					break;
				}

				clearData();
				requestAssetsInfo(Constants.INIT_START_INDEX, Constants.FETCH_COUNT, info.CODE);
				dlg.dismiss();
			}

		});
		builder.show();
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param view
	 * @param info
	 * @param header
	 */
	private void viewInfo(TextView view, String info, String header)
	{
		if (!Utils.isTrimEmpty(info))
		{
			view.setText(header + info);
		}
		else
		{
			view.setVisibility(View.GONE);
		}
	}

	/**
	 * 정보 표시하기
	 * 
	 * @since 1.0.0
	 */
	private void displayAssetsInfo()
	{
		if (0 == assetInfo.size())
		{
			return;
		}

		content_layout.setVisibility(View.VISIBLE);
		AssetsInfo detailInfo = assetInfo.get(0);

		navibar.setTitle(detailInfo.CULTASSTK);

		CLSSCODE.setText(string(R.string.assets_clss) + " " + getAssetsCode(detailInfo.CLSSCODE1, detailInfo.CLSSCODE2));

		viewInfo(CULTASSTK_LABEL, string(R.string.assets_cultasstk) + " ", "");
		viewInfo(CULTASSTK, detailInfo.CULTASSTK, "");
		viewInfo(CULTASSTH_LABEL, string(R.string.assets_cultassth) + " ", "");
		viewInfo(CULTASSTH, detailInfo.CULTASSTH, "");
		viewInfo(ASSIGNNUM, detailInfo.ASSIGNNUM, string(R.string.assets_assignnum) + " ");
		viewInfo(ASSIGNYMD, detailInfo.ASSIGNYMD, string(R.string.assets_assignymd) + " ");
		viewInfo(PERIOD, detailInfo.PERIOD, string(R.string.assets_period) + " ");
		viewInfo(SCALE, detailInfo.SCALE, string(R.string.assets_scale) + " ");
		viewInfo(MATERIAL, detailInfo.MATERIAL, string(R.string.assets_material) + " ");
		viewInfo(CLASSIFY, detailInfo.CLASSIFY, string(R.string.assets_classify) + " ");
		viewInfo(BINDING, detailInfo.BINDING, string(R.string.assets_binding) + " ");
		viewInfo(QUANTITY, detailInfo.QUANTITY, string(R.string.assets_quantity) + " ");
		viewInfo(SIZING, detailInfo.SIZING, string(R.string.assets_sizing) + " ");
		viewInfo(OWNER, detailInfo.OWNER, string(R.string.assets_owner) + " ");
		viewInfo(LOCATION, detailInfo.LOCATION, string(R.string.assets_location) + " ");
		viewInfo(DESIGNER, detailInfo.DESIGNER, string(R.string.assets_designer) + " ");
		viewInfo(BUILDER, detailInfo.BUILDER, string(R.string.assets_builder) + " ");
		viewInfo(HOLDER, detailInfo.HOLDER, string(R.string.assets_holder) + " ");
		viewInfo(SUCCESSOR, detailInfo.SUCCESSOR, string(R.string.assets_successor) + " ");
		viewInfo(ETC_CLSS, detailInfo.ETC_CLSS, string(R.string.assets_etc_clss) + " ");
		viewInfo(ASSIGNETC, detailInfo.ASSIGNETC, string(R.string.assets_assignetc) + " ");
		viewInfo(CONTENTS, detailInfo.CONTENTS, "");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		initializeUI();

		onResume();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (null != info && 0 < assetInfo.size())
		{
			// 정보표시
			refreshData();
		}

		if (0 == assetInfo.size())
		{
			// 상세정보 요청
			requestAssetsInfo(1, 5, info.CODE);
		}
		if (0 == mediaInfo.size())
		{
			// 미디어 요청
			requestAssetsMedia(1, 5, info.CODE);
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		ServiceExecutor.getInstance().cancelAll();
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		finish();
	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// 이하 OpenAPI 연동
	// ///////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 요청 누적 계수
	 */
	private AtomicInteger requestAccCount = new AtomicInteger(0);

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
		case CULTURE_ASSETS_DETAIL_INFO:
			handleOpenAssetsInfo(tr);
			break;
		case CULTURE_ASSETS_MEDIA:
			handleOpenAssetsMedia(tr);
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
	 * 문화재정보 조회 요청
	 * 
	 * @since 1.0.0
	 * @param start
	 * @param end
	 * @param code
	 */
	private void requestAssetsInfo(int start, int end, String code)
	{
		showLoading();
		requestAccCount.incrementAndGet();
		ServiceExecutor.getInstance().getAssetsInfo(OpenAPI.CULTURE_ASSETS_DETAIL_INFO.name(), listener, OpenAPI.CULTURE_ASSETS_DETAIL_INFO, langCode, code, start, end);
	}

	/**
	 * 문화재정보 조회 요청
	 * 
	 * @since 1.0.0
	 * @param start
	 * @param end
	 * @param code
	 */
	private void requestAssetsMedia(int start, int end, String code)
	{
		showLoading();
		requestAccCount.incrementAndGet();
		ServiceExecutor.getInstance().getAssetsMedia(OpenAPI.CULTURE_ASSETS_MEDIA.name(), listener, OpenAPI.CULTURE_ASSETS_MEDIA, code, start, end);
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
			assetInfo.addAll(item.infos);
			refreshData();
		}
	}

	/**
	 * 문화재정보 처리
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenAssetsMedia(BaseTrans tr)
	{
		OpenAssetsMedia item = (OpenAssetsMedia) tr;
		if (!item.infos.isEmpty())
		{
			mediaInfo.addAll(item.infos);
			Iterator<AssetsMedia> it = mediaInfo.iterator();
			while (it.hasNext())
			{
				AssetsMedia media = it.next();
				if (media.FILETYPE.equalsIgnoreCase("1"))
				{
					Logger.d1(LOG, "media(image) : " + media.FILE_IDX + ", " + media.FILEPATH + media.FILENAME);
					pages.add(new MediaPageItem(R.layout.page_assets_media, media));
				}
				else
				{
					Logger.d1(LOG, "media(video) : " + media.FILE_IDX + ", " + media.FILEPATH + media.FILENAME);
				}
			}
			pageIndicator.setMaxPage(pages.size());
			refreshData();
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
	 * 데이터 초기화 후 갱신
	 * 
	 * @since 1.0.0
	 */
	private void clearData()
	{
		assetInfo.clear();
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
				displayAssetsInfo();
				adapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 문화재 분류코드
	 */
	@InjectView
	private TextView CLSSCODE;

	@InjectView
	private TextView CULTASSTK_LABEL;

	/**
	 * 문화재명(한글)
	 */
	@InjectView
	private TextView CULTASSTK;

	@InjectView
	private TextView CULTASSTH_LABEL;

	/**
	 * 문화재명(한자)
	 */
	@InjectView
	private TextView CULTASSTH;

	/**
	 * 지정번호
	 */
	@InjectView
	private TextView ASSIGNNUM;

	/**
	 * 지정연월일
	 */
	@InjectView
	private TextView ASSIGNYMD;

	/**
	 * 시대
	 */
	@InjectView
	private TextView PERIOD;

	/**
	 * 규모_양식
	 */
	@InjectView
	private TextView SCALE;

	/**
	 * 재료
	 */
	@InjectView
	private TextView MATERIAL;

	/**
	 * 종별
	 */
	@InjectView
	private TextView CLASSIFY;

	/**
	 * 장정
	 */
	@InjectView
	private TextView BINDING;

	/**
	 * 수량
	 */
	@InjectView
	private TextView QUANTITY;

	/**
	 * 크기
	 */
	@InjectView
	private TextView SIZING;

	/**
	 * 소유자
	 */
	@InjectView
	private TextView OWNER;

	/**
	 * 소재지
	 */
	@InjectView
	private TextView LOCATION;

	/**
	 * 설계자
	 */
	@InjectView
	private TextView DESIGNER;

	/**
	 * 시공자
	 */
	@InjectView
	private TextView BUILDER;

	/**
	 * 보유자
	 */
	@InjectView
	private TextView HOLDER;

	/**
	 * 전승자
	 */
	@InjectView
	private TextView SUCCESSOR;

	/**
	 * 천연기념물분류
	 */
	@InjectView
	private TextView ETC_CLSS;

	/**
	 * 부속문화재
	 */
	@InjectView
	private TextView ASSIGNETC;

	/**
	 * 설명
	 */
	@InjectView
	private TextView CONTENTS;

}
