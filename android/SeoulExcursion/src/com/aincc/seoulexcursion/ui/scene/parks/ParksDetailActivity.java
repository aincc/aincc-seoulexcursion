package com.aincc.seoulexcursion.ui.scene.parks;

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
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.util.Linkify;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.aincc.cache.ImageLoader;
import com.aincc.cache.ImageWorker.ImageWorkerAdapter;
import com.aincc.ui.common.annotation.InjectView;
import com.aincc.network.common.BaseTrans;
import com.aincc.network.common.BaseTransEx;
import com.aincc.network.http.HttpParam;
import com.aincc.ui.anim.Animationz;
import com.aincc.util.PreferencesUtil;
import com.aincc.util.Utils;
import com.aincc.seoulexcursion.App;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.SeoulMapBaseActivity;
import com.aincc.seoulexcursion.ui.widget.MapOverlayPopup;
import com.aincc.seoulexcursion.ui.widget.Navibar;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.seoulexcursion.util.SeoulFont;
import com.aincc.seoulopenapi.OpenAPI;
import com.aincc.seoulopenapi.ServiceExecutor;
import com.aincc.seoulopenapi.model.ParkInfo;
import com.aincc.seoulopenapi.model.ParkProgramInfo;
import com.aincc.seoulopenapi.model.TotalCount;
import com.aincc.seoulopenapi.network.OpenBase;
import com.aincc.seoulopenapi.openapi.park.OpenParkProgramInfo;
import com.aincc.seoulopenapi.openapi.park.OpenParkProgramTotalCount;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * 
 * <h3><b>ParksDetailActivity</b></h3></br>
 * 
 * 공원 상세정보 표시<br>
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class ParksDetailActivity extends SeoulMapBaseActivity
{
	/**
	 * 초기 줌 레벨
	 */
	private static final int INIT_ZOOM_LEVEL = 16;

	/**
	 * 공원위치 마커키
	 */
	private static final String KEY_PARK = "park";

	/**
	 * 상단 네비게이션 바
	 */
	@InjectView
	private Navibar navibar;

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
	 * 본문 레이아웃
	 */
	@InjectView
	private RelativeLayout content_layout;

	/**
	 * 맵 레이아웃
	 */
	@InjectView(id = R.id.map_layout_ref)
	private RelativeLayout mapLayout;

	/**
	 * 맵뷰
	 */
	@InjectView
	private MapView map;

	/**
	 * 줌컨트롤
	 */
	@InjectView
	private ZoomControls zoom;

	/**
	 * 내위치 표시 버튼
	 */
	@InjectView
	private ImageButton mylocate;

	/**
	 * 현재공원위치 표시 버튼
	 */
	@InjectView
	private ImageButton parklocate;

	/**
	 * 공원정보 레이아웃
	 */
	@InjectView(id = R.id.parkinfo_layout_ref)
	private RelativeLayout parkinfoLayout;

	/**
	 * 공원주소
	 */
	@InjectView
	private TextView P_ADDR;

	/**
	 * 공원관리부서
	 */
	@InjectView
	private TextView P_DIVISION;

	/**
	 * 공원연락처
	 */
	@InjectView
	private TextView P_ADMINTEL;

	/**
	 * 공원설명
	 */
	@InjectView
	private TextView P_LIST_CONTENT;

	/**
	 * 공원이미지
	 */
	@InjectView(id = R.id.image)
	private ImageView parkImg;

	/**
	 * 프로그램목록 조회
	 */
	@InjectView
	private ImageButton programlist;

	/**
	 * 프로그램목록 배지
	 */
	@InjectView
	private TextView programbadge;

	/**
	 * 관리부서 전화걸기
	 */
	@InjectView
	private ImageButton callphone;

	/**
	 * 닫기
	 */
	@InjectView
	private ImageButton close;

	/**
	 * 이미지 다운로더
	 */
	private ImageLoader imageLoader;

	/**
	 * 이미지 어댑터
	 */
	private ImageWorkerAdapter imageAdapter;

	/**
	 * 공원정보
	 */
	private ParkInfo info = null;

	/**
	 * 현재 표시모드 상태
	 */
	private boolean isMapViewing = false;

	/**
	 * 팝업 뷰 (맵)
	 */
	private MapOverlayPopup mapPopup = null;

	/**
	 * 위치정보접근 허용여부
	 */
	private boolean isEnableAccessLocation = false;

	/**
	 * 위치 서비스 가능 여부
	 */
	private boolean isEnableLocationService = false;

	/**
	 * 현재 위치
	 */
	private Location currentLocation = null;

	/**
	 * 내위치 오버레이
	 */
	private MyLocationOverlay myLocationOverlay;

	/**
	 * 핸들러
	 */
	private Handler handler = null;

	/**
	 * 0 : ParksActivity, 1 : ParksSearchActivity
	 */
	private int fromActivity = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Logger.d1(LOG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parks_detail);

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
				info = bundle.getParcelable(Constants.EXTRA_KEY_PARK_INFO);
				fromActivity = bundle.getInt(Constants.EXTRA_KEY_PARK_DETAIL_FROM);
			}
		}
	}

	@Override
	protected void initializeUI()
	{
		super.initializeUI();

		content_layout.setVisibility(View.GONE);
		handler = new Handler();
		parkImg.setScaleType(ScaleType.FIT_CENTER);
		if (null == imageAdapter)
		{
			imageAdapter = new ImageWorkerAdapter()
			{

				@Override
				public Object getItem(int num)
				{
					return null != info ? info.P_IMG : null;
				}

				@Override
				public int getSize()
				{
					return null != info ? 1 : 0;
				}
			};
		}

		// 팝업 뷰
		if (null == mapPopup)
		{
			mapPopup = new MapOverlayPopup(this);
			mapPopup.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					map.removeView(mapPopup);

					ParkInfo parkInfo = (ParkInfo) mapPopup.getValue();
					if (null != parkInfo)
					{
						changeParkInfo(parkInfo);
					}
				}
			});
		}

		mediaToggle.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (null == info)
				{
					Toast.makeText(context, string(R.string.empty_media), Toast.LENGTH_SHORT).show();
					return;
				}

				if (View.GONE == mediaLayout.getVisibility())
				{
					if (Utils.isTrimEmpty(info.P_IMG))
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

		mylocate.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				runOnUiThread(refreshToCurrentLocation);
			}
		});

		parklocate.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				runOnUiThread(refreshToCurrentPark);
			}
		});

		programlist.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (0 != programTotalCount.get())
				{
					requestProgramInfo(Constants.INIT_START_INDEX, programTotalCount.get(), info.P_IDX);
				}
				else
				{
					Toast.makeText(context, string(R.string.empty_park_program), Toast.LENGTH_SHORT).show();
				}
			}
		});

		callphone.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (null != info && !Utils.isTrimEmpty(info.P_ADMINTEL))
				{
					// 전화 다이얼러 호출
					Intent callIntent = new Intent(Intent.ACTION_DIAL);
					callIntent.setData(Uri.parse("tel:" + info.P_ADMINTEL));
					startActivity(callIntent);
				}
				else
				{
					Toast.makeText(context, string(R.string.empty_callphone), Toast.LENGTH_SHORT).show();
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

		P_ADDR.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		P_DIVISION.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		P_ADMINTEL.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		P_LIST_CONTENT.setTypeface(SeoulFont.getInstance().getSeoulHangang());

		P_ADMINTEL.setAutoLinkMask(Linkify.PHONE_NUMBERS);

		// 맵초기화
		initializeMap();
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

		// 지도 버튼
		navibar.setActionFunc(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				flipContentLayout();
			}
		});
		navibar.setActionFuncIcon(drawable(R.drawable.location_searching));

		// 제목 설정
		navibar.setTitle(null != info ? info.P_PARK : "");
	}

	/**
	 * 맵초기화
	 * 
	 * @since 1.0.0
	 */
	private void initializeMap()
	{
		zoom.setOnZoomInClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				map.getController().zoomIn();
			}
		});
		zoom.setOnZoomOutClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				map.getController().zoomOut();
			}
		});
		map.setSatellite(false);
		map.displayZoomControls(true);
		map.getController().setZoom(INIT_ZOOM_LEVEL);
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

		showAlertAskEnableAccessLocation();

		if (null != info)
		{
			if (!isMapViewing)
			{
				// 정보표시
				refreshData();
			}
			else
			{
				// 내위치를 표시할거면..
				// showAlertAskEnableAccessLocation();

				if (null != myLocationOverlay)
				{
					myLocationOverlay.enableMyLocation();
					myLocationOverlay.enableCompass();
				}

				// 현재 공원을 표시.
				runOnUiThread(refreshToCurrentPark);
			}
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		ServiceExecutor.getInstance().cancelAll();
		if (null != myLocationOverlay)
		{
			myLocationOverlay.disableMyLocation();
			myLocationOverlay.disableCompass();
		}
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
	// 이하 Map 연동
	// TODO: GPS 상태체크 후 설정유도
	// ///////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 내용 레이아웃 뒤집기
	 * 
	 * @since 1.0.0
	 */
	private void flipContentLayout()
	{
		// 전환 후 버튼이미지 변경
		if (View.GONE == parkinfoLayout.getVisibility())
		{
			// 지도->정보로 변경되므로 맵전환 버튼으로 변경
			navibar.setActionFuncIcon(drawable(R.drawable.location_searching));
			isMapViewing = false;
		}
		else
		{
			// 정보->지도로 변경되므로 정보전환 버튼으로 변경
			navibar.setActionFuncIcon(drawable(R.drawable.view_as_info));
			isMapViewing = true;

			// 현재 공원을 표시.
			runOnUiThread(refreshToCurrentPark);
		}

		// 뷰 애니메이션 효과 적용 및 전환
		Animationz.performFlip(content_layout, parkinfoLayout, mapLayout, mapLayout.getWidth() / 2, mapLayout.getHeight() / 2);

	}

	/**
	 * 공원정보 변경 (지도에서 팝업을 선택하는 경우)
	 * 
	 * @since 1.0.0
	 * @param info
	 */
	private void changeParkInfo(ParkInfo info)
	{
		this.info = info;

		programTotalCount.set(0);

		flipContentLayout();
		refreshData();
	}

	/**
	 * 공원정보 표시하기
	 * 
	 * @since 1.0.0
	 */
	private void displayParkInfo()
	{
		content_layout.setVisibility(View.VISIBLE);
		navibar.setTitle(info.P_PARK);
		P_ADDR.setText(string(R.string.park_addr) + " " + info.P_ADDR);
		P_DIVISION.setText(string(R.string.park_division) + " " + info.P_DIVISION);
		P_ADMINTEL.setText(string(R.string.park_phone) + " " + info.P_ADMINTEL);
		P_LIST_CONTENT.setText(" " + info.P_LIST_CONTENT);
		imageLoader.loadImage(info.P_IMG, parkImg);
		programbadge.setText(String.valueOf(programTotalCount.get()));

		if (0 == programTotalCount.get())
		{
			handler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					requestTotalCount(info.P_IDX);
				}
			}, 500);
		}
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

	// /**
	// * 데이터 초기화 후 갱신
	// *
	// * @since 1.0.0
	// */
	// private void clearData()
	// {
	// refreshData();
	// }

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
				displayParkInfo();
			}
		});
	}

	@Override
	protected void onTapOverlayItem(int index, GeoPoint geopoint, List<OverlayItem> overlays)
	{
		if (KEY_PARK.equals(overlays.get(index).getSnippet()))
		{
			// 다른 팝업 오버레이 삭제 및 현재 마커 팝업 오버레이 추가
			MapView.LayoutParams param = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, geopoint, 0, -45, MapView.LayoutParams.BOTTOM_CENTER);
			map.removeView(mapPopup);
			mapPopup.setText(overlays.get(index).getTitle());

			String name = overlays.get(index).getTitle();
			List<Pair<String, List<ParkInfo>>> listdata = (0 == fromActivity) ? ParksActivity.getParks() : ParksSearchActivity.getParks();
			if (null != listdata)
			{
				for (Pair<String, List<ParkInfo>> pair : listdata)
				{
					for (ParkInfo park : pair.second)
					{
						if (name.equals(park.P_PARK))
						{
							mapPopup.setValue(park);
							break;
						}
					}
				}
			}
			map.addView(mapPopup, param);
			map.getController().animateTo(geopoint);
			map.invalidate();
		}
	}

	/**
	 * 내위치 정보 접근 승인요청 팝업
	 * 
	 * @since 1.0.0
	 */
	private void showAlertAskEnableAccessLocation()
	{
		isEnableAccessLocation = PreferencesUtil.getBoolean(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_ACCESS_LOCATION, false);

		// 위치정보접근을 허용한 경우 현재위치표시를 하도록 한다.
		if (isEnableAccessLocation)
		{
			if (null == myLocationOverlay)
			{
				myLocationOverlay = new MyLocationOverlay(context, map);
				myLocationOverlay.enableMyLocation();
				myLocationOverlay.enableCompass();
			}

			// runOnUiThread(refreshToCurrentLocation);
			return;
		}

		// 위치정보접근 허용여부 팝업 표시하기.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(string(R.string.alert_msg_access_location));
		builder.setPositiveButton(string(R.string.alert_btn_confirm), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				isEnableAccessLocation = true;
				PreferencesUtil.setBoolean(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_ACCESS_LOCATION, isEnableAccessLocation);

				if (null == myLocationOverlay)
				{
					myLocationOverlay = new MyLocationOverlay(context, map);
					myLocationOverlay.enableMyLocation();
					myLocationOverlay.enableCompass();
				}

				// runOnUiThread(refreshToCurrentLocation);
			}
		}).setNegativeButton(string(R.string.alert_btn_cancel), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				isEnableAccessLocation = false;
				PreferencesUtil.setBoolean(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_ACCESS_LOCATION, isEnableAccessLocation);
			}
		}).create().show();
	}

	/**
	 * 위치기반 서비스 설정 승인요청 팝업
	 * 
	 * @since 1.0.0
	 */
	private void showAlertAskLocationService()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(string(R.string.alert_msg_location_service));
		builder.setPositiveButton(string(R.string.alert_btn_confirm), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		}).setNegativeButton(string(R.string.alert_btn_cancel), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			}
		}).create().show();
	}

	/**
	 * 현재위치로 맵뷰 갱신하기
	 */
	private Runnable refreshToCurrentLocation = new Runnable()
	{
		public void run()
		{
			isEnableLocationService = checkProvider();
			if (!isEnableLocationService)
			{
				showAlertAskLocationService();
			}
			else
			{
				if (isEnableAccessLocation)
				{
					refreshMap();

					if (null != myLocationOverlay.getMyLocation())
					{
						Logger.d1(LOG, "move to myLocation by using myLocationOverlay");
						// map.getController().animateTo(myLocationOverlay.getMyLocation());
						// map.invalidate();
						myLocationOverlay.runOnFirstFix(new Runnable()
						{
							@Override
							public void run()
							{
								map.getController().animateTo(myLocationOverlay.getMyLocation());
								map.invalidate();
							}
						});
					}
					else
					{
						currentLocation = getLastLocation(bestProvider);
						if (null != currentLocation)
						{
							Logger.d1(LOG, "move to myLocation by using getLastLocation");
							map.getController().animateTo(getGeoPoint(currentLocation));
							map.invalidate();
						}
					}
				}
				else
				{
					showAlertAskEnableAccessLocation();
				}
			}
		}
	};

	/**
	 * 현재공원으로 맵뷰 갱신하기
	 */
	private Runnable refreshToCurrentPark = new Runnable()
	{
		public void run()
		{
			isEnableLocationService = checkProvider();
			if (!isEnableLocationService)
			{
				showAlertAskLocationService();
			}
			else
			{
				Double lat = Double.parseDouble(info.LATITUDE) * 1E6;
				Double lng = Double.parseDouble(info.LONGITUDE) * 1E6;
				GeoPoint parkpoint = new GeoPoint(lat.intValue(), lng.intValue());
				refreshMap();

				MapView.LayoutParams param = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, parkpoint, 0, -45, MapView.LayoutParams.BOTTOM_CENTER);
				map.removeView(mapPopup);
				mapPopup.setText(info.P_PARK);
				map.addView(mapPopup, param);

				map.getController().animateTo(parkpoint);
				map.invalidate();
			}
		}
	};

	/**
	 * 맵 갱신
	 * 
	 * @since 1.0.0
	 */
	private void refreshMap()
	{
		// GeoPoint geoPoint = null;
		Drawable marker = null;
		// LocationItemizedOverlay overlayIam = null;
		// OverlayItem overlayItem = null;

		if (null == map)
		{
			return;
		}

		removeOverlay();
		marker = drawable(R.drawable.location_pin3);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());

		// // 입력된 현재 위치정보의 GeoPoint 를 생성한다.
		// if (null != mylocation)
		// {
		// geoPoint = getGeoPoint(mylocation);
		// overlayIam = new LocationItemizedOverlay(marker);
		// overlayIam.populateImmediate();
		// overlayItem = new OverlayItem(geoPoint, KEY_MY, KEY_MY);
		// overlayIam.addOverlay(overlayItem);
		// map.getOverlays().add(overlayIam);
		// }

		// 주변공원 정보를 가져온다.
		LocationItemizedOverlay overlayParks = getParks();
		map.getOverlays().add(overlayParks);

		if (null != myLocationOverlay)
		{
			map.getOverlays().add(myLocationOverlay);
		}

		map.invalidate();
	}

	/**
	 * 공원 오버레이 마커 생성하기
	 * 
	 * @since 1.0.0
	 * @return LocationItemizedOverlay
	 */
	private LocationItemizedOverlay getParks()
	{
		Drawable marker = null;
		marker = drawable(R.drawable.location_pin3);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());

		LocationItemizedOverlay overlayParks = new LocationItemizedOverlay(marker);
		overlayParks.populateImmediate();

		List<Pair<String, List<ParkInfo>>> listdata = (0 == fromActivity) ? ParksActivity.getParks() : ParksSearchActivity.getParks();
		if (null != listdata)
		{
			// 공원목록 정보 등록
			for (Pair<String, List<ParkInfo>> pair : listdata)
			{
				for (ParkInfo park : pair.second)
				{
					Double lat = Double.parseDouble(park.LATITUDE) * 1E6;
					Double lng = Double.parseDouble(park.LONGITUDE) * 1E6;
					GeoPoint parkpoint = new GeoPoint(lat.intValue(), lng.intValue());

					OverlayItem overlayItem = new OverlayItem(parkpoint, park.P_PARK, KEY_PARK);
					overlayParks.addOverlay(overlayItem);
				}
			}
		}
		else
		{
			// 공원목록이 없는 경우 현재 공원정보 등록
			Double lat = Double.parseDouble(info.LATITUDE) * 1E6;
			Double lng = Double.parseDouble(info.LONGITUDE) * 1E6;
			GeoPoint parkpoint = new GeoPoint(lat.intValue(), lng.intValue());

			OverlayItem overlayItem = new OverlayItem(parkpoint, info.P_PARK, KEY_PARK);
			overlayParks.addOverlay(overlayItem);
		}
		return overlayParks;
	}

	/**
	 * 오버레이 마커 삭제
	 * 
	 * @since 1.0.0
	 */
	private void removeOverlay()
	{
		if (null != map)
		{
			List<Overlay> overlays = map.getOverlays();
			if (0 < overlays.size())
			{
				overlays.clear();
			}
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// 이하 OpenAPI 연동
	// ///////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 공원 프로그램 전체 개수
	 */
	private AtomicInteger programTotalCount = new AtomicInteger(0);

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
		case PARK_PROGRAM_TOTAL_COUNT_BY_PARKID:
			handleOpenProgramInfoTotalCount(tr);
			break;
		case PARK_PROGRAM_INFO_BY_PARKID:
			handleOpenProgramInfo(tr);
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
	 * 공원 프로그램 전체개수 요청
	 * 
	 * @since 1.0.0
	 */
	private void requestTotalCount(String parkid)
	{
		// 개수 요청은 로딩표시하지 않음.
		// showLoading();
		requestAccCount.incrementAndGet();
		ServiceExecutor.getInstance().getParkProgramTotalCount(OpenAPI.PARK_PROGRAM_TOTAL_COUNT_BY_PARKID.name(), listener, OpenAPI.PARK_PROGRAM_TOTAL_COUNT_BY_PARKID, parkid);
	}

	/**
	 * 공원프로그램 정보 조회 요청
	 * 
	 * @since 1.0.0
	 * @param start
	 * @param end
	 * @param parkid
	 */
	private void requestProgramInfo(int start, int end, String parkid)
	{
		showLoading();
		requestAccCount.incrementAndGet();
		ServiceExecutor.getInstance().getParkProgramInfo(OpenAPI.PARK_PROGRAM_INFO_BY_PARKID.name(), listener, start, end, OpenAPI.PARK_PROGRAM_INFO_BY_PARKID, parkid);
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
	 * 공원 프로그램 전체개수 정보 처리
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenProgramInfoTotalCount(BaseTrans tr)
	{
		OpenParkProgramTotalCount item = (OpenParkProgramTotalCount) tr;
		if (!item.infos.isEmpty())
		{
			Iterator<TotalCount> it = item.infos.iterator();
			if (it.hasNext())
			{
				programTotalCount.set(Integer.parseInt(it.next().TOTALCNT));
				Logger.d1(LOG, "handleOpenProgramInfoTotalCount: totalcount = " + programTotalCount.get());
				programbadge.setText(String.valueOf(programTotalCount.get()));
			}
		}
	}

	/**
	 * 공원프로그램 정보 처리
	 * 
	 * @since 1.0.0
	 * @param tr
	 */
	private void handleOpenProgramInfo(BaseTrans tr)
	{
		OpenParkProgramInfo item = (OpenParkProgramInfo) tr;
		if (!item.infos.isEmpty())
		{
			Iterator<ParkProgramInfo> it = item.infos.iterator();
			while (it.hasNext())
			{
				Logger.d1(LOG, it.next().toString());
			}
			Intent intent = new Intent(context, ParksProgramActivity.class);

			Bundle bundle = new Bundle();
			bundle.putParcelable(Constants.EXTRA_KEY_PARK_INFO, info);
			bundle.putParcelableArrayList(Constants.EXTRA_KEY_PARK_PROGRAM_LIST, (ArrayList<? extends Parcelable>) item.infos);
			intent.putExtras(bundle);
			startActivity(intent);
			overridePendingTransition(R.anim.push_up_in, android.R.anim.fade_out);
		}
		else
		{
			Toast.makeText(context, string(R.string.empty_park_program), Toast.LENGTH_SHORT).show();
		}
	}

	// /**
	// * 데이터 초기화 후 갱신
	// *
	// * @since 1.0.0
	// */
	// private void clearData()
	// {
	// refreshData();
	// }

}
