package com.aincc.seoulexcursion.ui.scene.plays;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aincc.lib.cache.ImageLoader;
import com.aincc.lib.cache.ImageWorker.ImageWorkerAdapter;
import com.aincc.lib.common.annotation.InjectView;
import com.aincc.lib.network.common.BaseTrans;
import com.aincc.lib.network.common.BaseTransEx;
import com.aincc.lib.network.http.HttpParam;
import com.aincc.lib.ui.anim.Animationz;
import com.aincc.lib.util.URLImageParser;
import com.aincc.lib.util.Utils;
import com.aincc.seoulexcursion.App;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.SeoulBaseActivity;
import com.aincc.seoulexcursion.ui.widget.Navibar;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.seoulexcursion.util.SeoulFont;
import com.aincc.seoulopenapi.OpenAPI;
import com.aincc.seoulopenapi.ServiceExecutor;
import com.aincc.seoulopenapi.model.PlayInfo;
import com.aincc.seoulopenapi.model.PlaySimpleInfo;
import com.aincc.seoulopenapi.network.OpenBase;
import com.aincc.seoulopenapi.openapi.culture.OpenPlayDetailInfo;

/**
 * 
 * <h3><b>PlaysDetailActivity</b></h3></br>
 * 
 * 공연 세부정보 표시 <br>
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class PlaysDetailActivity extends SeoulBaseActivity
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
	 * 미디어 보기 토클
	 */
	@InjectView
	private ImageButton mediaToggle;

	/**
	 * 메인 이미지
	 */
	@InjectView
	private ImageView image;

	/**
	 * 문의전화
	 */
	@InjectView
	private ImageButton callphone;

	/**
	 * 원문링크 연결
	 */
	@InjectView
	private ImageButton orglink;

	/**
	 * 닫기
	 */
	@InjectView
	private ImageButton close;

	/**
	 * 공연정보
	 */
	private PlaySimpleInfo info;
	private List<PlayInfo> playsInfo = new ArrayList<PlayInfo>();

	/**
	 * 이미지 다운로더
	 */
	private ImageLoader imageLoader;

	/**
	 * 이미지 어댑터
	 */
	private ImageWorkerAdapter imageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Logger.d1(LOG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plays_detail);

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
				info = bundle.getParcelable(Constants.EXTRA_KEY_PLAYS_SIMPLE_INFO);
			}
		}
	}

	@Override
	protected void initializeUI()
	{
		super.initializeUI();

		content_layout.setVisibility(View.GONE);
		image.setScaleType(ScaleType.FIT_CENTER);
		if (null == imageAdapter)
		{
			imageAdapter = new ImageWorkerAdapter()
			{

				@Override
				public Object getItem(int num)
				{
					if (0 < playsInfo.size())
					{
						return playsInfo.get(0).MAIN_IMG;
					}
					return null;
				}

				@Override
				public int getSize()
				{
					if (0 < playsInfo.size())
					{
						return 1;
					}
					return 0;
				}
			};
		}

		mediaToggle.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (View.GONE == mediaLayout.getVisibility())
				{
					if (0 == playsInfo.size())
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

		callphone.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (0 < playsInfo.size())
				{
					PlayInfo detailInfo = playsInfo.get(0);

					if (!Utils.isTrimEmpty(detailInfo.INQUIRY))
					{
						// 전화 다이얼러 호출
						Intent intent = new Intent(Intent.ACTION_DIAL);
						intent.setData(Uri.parse("tel:" + detailInfo.INQUIRY));
						startActivity(intent);
					}
					else
					{
						Toast.makeText(context, string(R.string.empty_callphone), Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(context, string(R.string.empty_callphone), Toast.LENGTH_SHORT).show();
				}

			}
		});

		orglink.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (0 < playsInfo.size())
				{
					PlayInfo detailInfo = playsInfo.get(0);

					if (!Utils.isTrimEmpty(detailInfo.ORG_LINK))
					{
						// 웹페이지 표시
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(detailInfo.ORG_LINK));
						startActivity(intent);
					}
					else
					{
						Toast.makeText(context, string(R.string.empty_org_link), Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(context, string(R.string.empty_org_link), Toast.LENGTH_SHORT).show();
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

		PROGRAM.setAutoLinkMask(Linkify.WEB_URLS);
		CONTENTS.setAutoLinkMask(Linkify.WEB_URLS);
		ETC_DESC.setAutoLinkMask(Linkify.WEB_URLS);
		HOMEPAGE.setAutoLinkMask(Linkify.WEB_URLS);
		INQUIRY.setAutoLinkMask(Linkify.PHONE_NUMBERS);
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
		navibar.setActionFuncVisible(View.INVISIBLE);

		// 제목 설정
		navibar.setTitle(null != info ? info.TITLE : "");
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
		TITLE.setTypeface(tf);
		DATE.setTypeface(tf);
		PLACE.setTypeface(tf);
		TIME.setTypeface(tf);
		ORG_LINK.setTypeface(tf);
		HOMEPAGE.setTypeface(tf);
		USE_TRGT.setTypeface(tf);
		USE_FEE.setTypeface(tf);
		SPONSOR.setTypeface(tf);
		INQUIRY.setTypeface(tf);
		SUPPORT.setTypeface(tf);
		RGSTDATE.setTypeface(tf);
		MDFYDATE.setTypeface(tf);
		AGELIMIT.setTypeface(tf);
		TICKET.setTypeface(tf);
		PLAYER.setTypeface(tf);
		PROGRAM.setTypeface(tf);
		CONTENTS.setTypeface(tf);
		ETC_DESC.setTypeface(tf);
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

		imageLoader = App.getImageLoader(imageAdapter);

		if (null != info && 0 < playsInfo.size())
		{
			// 정보표시
			refreshData();
		}

		if (0 == playsInfo.size())
		{
			// 상세정보 요청
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
	private void displayPlaysInfo()
	{
		if (0 == playsInfo.size())
		{
			return;
		}

		content_layout.setVisibility(View.VISIBLE);
		PlayInfo detailInfo = playsInfo.get(0);

		navibar.setTitle(detailInfo.TITLE);

		viewInfo(TITLE, detailInfo.TITLE, string(R.string.play_title) + " ");
		viewInfo(AGELIMIT, detailInfo.AGELIMIT, string(R.string.play_agelimit) + " ");
		viewInfo(DATE, detailInfo.STRTDATE + " ~ " + detailInfo.END_DATE, string(R.string.play_date) + " ");
		viewInfo(HOMEPAGE, detailInfo.HOMEPAGE, string(R.string.play_homepage) + " ");
		viewInfo(INQUIRY, detailInfo.INQUIRY, string(R.string.play_inquiry) + " ");
		viewInfo(MDFYDATE, detailInfo.MDFYDATE, string(R.string.play_mdfydate) + " ");
		viewInfo(ORG_LINK, "", string(R.string.play_org_link) + " "); // 표시하지 않고, 웹버튼 선택시 표시
		viewInfo(PLACE, detailInfo.PLACE, string(R.string.play_place) + " ");
		viewInfo(USE_TRGT, detailInfo.USE_TRGT, string(R.string.play_use_trgt) + " ");
		viewInfo(USE_FEE, detailInfo.USE_FEE, string(R.string.play_use_fee) + " ");
		viewInfo(PLAYER, detailInfo.PLAYER, string(R.string.play_player) + " ");
		viewInfo(RGSTDATE, detailInfo.RGSTDATE, string(R.string.play_rgstdate) + " ");
		viewInfo(SPONSOR, detailInfo.SPONSOR, string(R.string.play_sponsor) + " ");
		viewInfo(SUPPORT, detailInfo.SUPPORT, string(R.string.play_support) + " ");
		viewInfo(TICKET, detailInfo.TICKET, string(R.string.play_ticket) + " ");
		viewInfo(TIME, detailInfo.TIME, string(R.string.play_time) + " ");

		if (detailInfo.HTML_USE.equals("1"))
		{
			PROGRAM.setText(Html.fromHtml(StringUtils.trim(detailInfo.PROGRAM), new URLImageParser(PROGRAM, this), null));
			CONTENTS.setText(Html.fromHtml(StringUtils.trim(detailInfo.CONTENTS), new URLImageParser(CONTENTS, this), null));
			ETC_DESC.setText(Html.fromHtml(StringUtils.trim(string(R.string.play_etc_desc) + " " + detailInfo.ETC_DESC), new URLImageParser(ETC_DESC, this), null));
		}
		else
		{
			viewInfo(PROGRAM, detailInfo.PROGRAM, "");
			viewInfo(CONTENTS, detailInfo.CONTENTS, "");
			viewInfo(ETC_DESC, detailInfo.ETC_DESC, string(R.string.play_etc_desc) + " ");
		}

		imageLoader.loadImage(detailInfo.MAIN_IMG, image);
	}

	/**
	 * 데이터 초기화 후 갱신
	 * 
	 * @since 1.0.0
	 */
	private void clearData()
	{
		playsInfo.clear();
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
				displayPlaysInfo();
			}
		});
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
		case CULTURE_PLAY_DETAIL_INFO:
			handleOpenInfo(tr);
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
		requestInfo(1, 5, info.CULTCODE);
	}

	/**
	 * 문화재정보 조회 요청
	 * 
	 * @since 1.0.0
	 * @param start
	 * @param end
	 * @param cultcode
	 */
	private void requestInfo(int start, int end, String cultcode)
	{
		showLoading();
		requestAccCount.incrementAndGet();
		ServiceExecutor.getInstance().getPlayDetailInfo(OpenAPI.CULTURE_PLAY_DETAIL_INFO.name(), listener, OpenAPI.CULTURE_PLAY_DETAIL_INFO, cultcode, start, end);
	}

	/**
	 * 문화재정보 처리
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenInfo(BaseTrans tr)
	{
		OpenPlayDetailInfo item = (OpenPlayDetailInfo) tr;
		if (!item.infos.isEmpty())
		{
			playsInfo.addAll(item.infos);
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
	 * 제목
	 */
	@InjectView
	private TextView TITLE;

	/**
	 * 일정
	 */
	@InjectView
	private TextView DATE;

	/**
	 * 장소
	 */
	@InjectView
	private TextView PLACE;

	/**
	 * 시간
	 */
	@InjectView
	private TextView TIME;

	/**
	 * 원문링크
	 */
	@InjectView
	private TextView ORG_LINK;

	/**
	 * 홈페이지
	 */
	@InjectView
	private TextView HOMEPAGE;

	/**
	 * 대상
	 */
	@InjectView
	private TextView USE_TRGT;

	/**
	 * 이용요금
	 */
	@InjectView
	private TextView USE_FEE;

	/**
	 * 스폰서
	 */
	@InjectView
	private TextView SPONSOR;

	/**
	 * 문의
	 */
	@InjectView
	private TextView INQUIRY;

	/**
	 * 주관 및 후원
	 */
	@InjectView
	private TextView SUPPORT;

	/**
	 * 정보제공일자
	 */
	@InjectView
	private TextView RGSTDATE;

	/**
	 * 정보수정일자
	 */
	@InjectView
	private TextView MDFYDATE;

	/**
	 * 연령대
	 */
	@InjectView
	private TextView AGELIMIT;

	/**
	 * 티켓
	 */
	@InjectView
	private TextView TICKET;

	/**
	 * 출연자정보
	 */
	@InjectView
	private TextView PLAYER;

	/**
	 * 프로그램 소개
	 */
	@InjectView
	private TextView PROGRAM;

	/**
	 * 본문
	 */
	@InjectView
	private TextView CONTENTS;

	/**
	 * 기타내용
	 */
	@InjectView
	private TextView ETC_DESC;
}
