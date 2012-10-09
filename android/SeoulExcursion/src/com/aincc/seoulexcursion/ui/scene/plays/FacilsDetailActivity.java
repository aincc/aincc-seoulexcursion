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
import com.aincc.seoulopenapi.model.FacilInfo;
import com.aincc.seoulopenapi.model.FacilSimpleInfo;
import com.aincc.seoulopenapi.model.FacilTrafficInfo;
import com.aincc.seoulopenapi.network.OpenBase;
import com.aincc.seoulopenapi.openapi.culture.OpenFacilDetailInfo;
import com.aincc.seoulopenapi.openapi.culture.OpenFacilTrafficInfo;

/**
 * 
 * <h3><b>FacilDetailActivity</b></h3></br>
 * 
 * 문화시설 세부정보 표시
 * <p>
 * 
 * version 1.3.0 : 교통정보 표시 기능 추가.
 * 
 * @author aincc@barusoft.com
 * @version 1.3.0
 * @since 1.0.0
 */
public class FacilsDetailActivity extends SeoulBaseActivity
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
	 * 홈페이지 연결
	 */
	@InjectView
	private ImageButton org_link;

	/**
	 * 교통정보
	 * 
	 * @since 1.3.0
	 */
	@InjectView
	private ImageButton traffic;

	/**
	 * 닫기
	 */
	@InjectView
	private ImageButton close;

	/**
	 * 시설정보
	 */
	private FacilSimpleInfo info;

	/**
	 * 시설정보
	 */
	private List<FacilInfo> facilsInfo = new ArrayList<FacilInfo>();

	/**
	 * 교통정보
	 * 
	 * @since 1.3.0
	 */
	private List<FacilTrafficInfo> trafficInfo = new ArrayList<FacilTrafficInfo>();

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
		setContentView(R.layout.activity_facils_detail);

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
				info = bundle.getParcelable(Constants.EXTRA_KEY_FACILS_SIMPLE_INFO);
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
					if (0 < facilsInfo.size())
					{
						return facilsInfo.get(0).MAIN_IMG;
					}
					return null;
				}

				@Override
				public int getSize()
				{
					if (0 < facilsInfo.size())
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
					if (0 == facilsInfo.size())
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
				if (0 < facilsInfo.size())
				{
					FacilInfo detailInfo = facilsInfo.get(0);
					if (!Utils.isTrimEmpty(detailInfo.PHNE))
					{
						// 전화 다이얼러 호출
						Intent intent = new Intent(Intent.ACTION_DIAL);
						intent.setData(Uri.parse("tel:" + detailInfo.PHNE));
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

		org_link.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (0 < facilsInfo.size())
				{
					FacilInfo detailInfo = facilsInfo.get(0);

					if (!Utils.isTrimEmpty(detailInfo.HOMEPAGE))
					{
						String url = "";
						if (!detailInfo.HOMEPAGE.startsWith("http://"))
						{
							url = "http://" + detailInfo.HOMEPAGE;
						}
						else
						{
							url = detailInfo.HOMEPAGE;
						}

						// 웹페이지 표시
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						startActivity(intent);
					}
					else
					{
						Toast.makeText(context, string(R.string.empty_homepage), Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(context, string(R.string.empty_homepage), Toast.LENGTH_SHORT).show();
				}

			}
		});

		/**
		 * 교통정보 버튼 선택시 처리
		 * 
		 * @since 1.3.0
		 */
		traffic.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (0 < trafficInfo.size())
				{
					TrafficInfoPopup.show(context, trafficInfo);
				}
				else
				{
					Toast.makeText(context, string(R.string.empty_traffic), Toast.LENGTH_SHORT).show();
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

		HOMEPAGE.setAutoLinkMask(Linkify.WEB_URLS);
		ETC_DESC.setAutoLinkMask(Linkify.WEB_URLS);
		FAC_DESC.setAutoLinkMask(Linkify.WEB_URLS);
		PHNE.setAutoLinkMask(Linkify.PHONE_NUMBERS);
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
		navibar.setTitle(null != info ? info.FAC_NAME : "");
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
		FAC_NAME.setTypeface(tf);
		ADDR.setTypeface(tf);
		PHNE.setTypeface(tf);
		FAX.setTypeface(tf);
		HOMEPAGE.setTypeface(tf);
		OPENHOUR.setTypeface(tf);
		ENTR_FEE.setTypeface(tf);
		CLOSEDAY.setTypeface(tf);
		OPEN_DAY.setTypeface(tf);
		SEAT_CNT.setTypeface(tf);
		ETC_DESC.setTypeface(tf);
		FAC_DESC.setTypeface(tf);
		ENTRFREE.setTypeface(tf);
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

		if (null != info && 0 < facilsInfo.size())
		{
			// 정보표시
			refreshData();
		}

		if (0 == facilsInfo.size())
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
	private void displayFacilsInfo()
	{
		if (0 == facilsInfo.size())
		{
			return;
		}

		content_layout.setVisibility(View.VISIBLE);
		FacilInfo detailInfo = facilsInfo.get(0);

		navibar.setTitle(detailInfo.FAC_NAME);

		viewInfo(FAC_NAME, detailInfo.FAC_NAME, string(R.string.facil_name) + " ");
		viewInfo(ADDR, detailInfo.ADDR, string(R.string.facil_addr) + " ");
		viewInfo(PHNE, detailInfo.PHNE, string(R.string.facil_phne) + " ");
		viewInfo(FAX, detailInfo.FAX, string(R.string.facil_fax) + " ");
		viewInfo(HOMEPAGE, detailInfo.HOMEPAGE, string(R.string.facil_homepage) + " ");
		viewInfo(OPENHOUR, detailInfo.OPENHOUR, string(R.string.facil_openhour) + " ");
		viewInfo(ENTR_FEE, detailInfo.ENTR_FEE, string(R.string.facil_entr_fee) + " ");
		viewInfo(CLOSEDAY, detailInfo.CLOSEDAY, string(R.string.facil_closeday) + " ");
		viewInfo(OPEN_DAY, detailInfo.OPEN_DAY, string(R.string.facil_open_day) + " ");
		viewInfo(SEAT_CNT, detailInfo.SEAT_CNT, string(R.string.facil_seat_cnt) + " ");

		ETC_DESC.setText(Html.fromHtml(StringUtils.trim(string(R.string.facil_etc_desc) + " " + detailInfo.ETC_DESC), new URLImageParser(ETC_DESC, this), null));
		FAC_DESC.setText(Html.fromHtml(StringUtils.trim(string(R.string.facil_fac_desc) + " " + detailInfo.FAC_DESC), new URLImageParser(FAC_DESC, this), null));
		// viewInfo(ETC_DESC, detailInfo.ETC_DESC, string(R.string.facil_etc_desc) + " ");
		// viewInfo(FAC_DESC, detailInfo.FAC_DESC, string(R.string.facil_fac_desc) + " ");

		if (detailInfo.ENTRFREE.equals("0"))
		{
			viewInfo(ENTRFREE, string(R.string.free), string(R.string.facil_entrfree) + " ");
		}
		else
		{
			viewInfo(ENTRFREE, string(R.string.nonfree), string(R.string.facil_entrfree) + " ");
		}

		imageLoader.loadImage(detailInfo.MAIN_IMG, image);
	}

	/**
	 * 데이터 초기화 후 갱신
	 * 
	 * @since 1.3.0
	 */
	private void clearData()
	{
		facilsInfo.clear();
		trafficInfo.clear();
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
				displayFacilsInfo();
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
		case CULTURE_FACIL_DETAIL_INFO:
			handleOpenInfo(tr);
			break;
		case CULTURE_FACIL_TRAFFIC_INFO:
			handleOpenTrafficInfo(tr);
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
		requestInfo(1, 5, info.FAC_CODE);
		requestTrafficInfo(1, 5, info.FAC_CODE);
	}

	/**
	 * 시설정보 조회 요청
	 * 
	 * @since 1.0.0
	 * @param start
	 * @param end
	 * @param faccode
	 */
	private void requestInfo(int start, int end, String faccode)
	{
		showLoading();
		requestAccCount.incrementAndGet();
		ServiceExecutor.getInstance().getFacilDetailInfo(OpenAPI.CULTURE_FACIL_DETAIL_INFO.name(), listener, OpenAPI.CULTURE_FACIL_DETAIL_INFO, faccode, start, end);
	}

	/**
	 * 교통정보 요청
	 * 
	 * @since 1.0.0
	 * @param start
	 * @param end
	 * @param faccode
	 */
	private void requestTrafficInfo(int start, int end, String faccode)
	{
		showLoading();
		requestAccCount.incrementAndGet();
		ServiceExecutor.getInstance().getFacilTrafficInfo(OpenAPI.CULTURE_FACIL_TRAFFIC_INFO.name(), listener, OpenAPI.CULTURE_FACIL_TRAFFIC_INFO, faccode, start, end);
	}

	/**
	 * 시설정보 처리
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenInfo(BaseTrans tr)
	{
		OpenFacilDetailInfo item = (OpenFacilDetailInfo) tr;
		if (!item.infos.isEmpty())
		{
			facilsInfo.addAll(item.infos);
			refreshData();
		}
	}

	/**
	 * 교통정보 처리
	 * 
	 * @since 1.3.0
	 * @param tr
	 */
	private void handleOpenTrafficInfo(BaseTrans tr)
	{
		OpenFacilTrafficInfo item = (OpenFacilTrafficInfo) tr;
		if (!item.infos.isEmpty())
		{
			trafficInfo.addAll(item.infos);
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
	 * 시설명
	 */
	@InjectView
	private TextView FAC_NAME;

	/**
	 * 주소
	 */
	@InjectView
	private TextView ADDR;

	/**
	 * 전화번호
	 */
	@InjectView
	private TextView PHNE;

	/**
	 * 팩스번호
	 */
	@InjectView
	private TextView FAX;

	/**
	 * 홈페이지
	 */
	@InjectView
	private TextView HOMEPAGE;

	/**
	 * 관람시간
	 */
	@InjectView
	private TextView OPENHOUR;

	/**
	 * 관람료
	 */
	@InjectView
	private TextView ENTR_FEE;

	/**
	 * 휴관일
	 */
	@InjectView
	private TextView CLOSEDAY;

	/**
	 * 개관일자
	 */
	@InjectView
	private TextView OPEN_DAY;

	/**
	 * 객석수
	 */
	@InjectView
	private TextView SEAT_CNT;

	/**
	 * 기타사항
	 */
	@InjectView
	private TextView ETC_DESC;

	/**
	 * 시설소개
	 */
	@InjectView
	private TextView FAC_DESC;

	/**
	 * 무료구분
	 */
	@InjectView
	private TextView ENTRFREE;
}
