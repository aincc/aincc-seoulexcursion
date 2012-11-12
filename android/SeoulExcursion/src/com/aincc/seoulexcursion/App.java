package com.aincc.seoulexcursion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import android.app.Application;
import android.content.Context;
import android.util.Pair;

import com.aincc.cache.ImageLoader;
import com.aincc.cache.ImageWorker.ImageWorkerAdapter;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulopenapi.model.CodeInfo;
import com.aincc.util.PreferencesUtil;

/**
 * 
 * <h3><b>BaruChatApp</b></h3></br>
 * 
 * 공통데이터 관리 어플리케이션
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class App extends Application
{
	/**
	 * 컨텍스트
	 */
	private static Context context;

	/**
	 * 이미지 로더
	 */
	private static ImageLoader imageLoader;

	/**
	 * 문화시설 테마 코드
	 */
	public static List<CodeInfo> facilThemeCode = new ArrayList<CodeInfo>();

	/**
	 * 문화시설 주제 코드
	 */
	public static List<CodeInfo> facilSubjectCode = new ArrayList<CodeInfo>();

	/**
	 * 문화재 분류 코드
	 */
	public static List<CodeInfo> assetsCode = new ArrayList<CodeInfo>();

	/**
	 * 문화재 세부분류 코드
	 * List<Pair<문화재분류코드, List<CodeInfo>>>
	 */
	public static List<Pair<String, List<CodeInfo>>> assetsDetailCode = new ArrayList<Pair<String, List<CodeInfo>>>();

	/**
	 * 문화재 분류 맵
	 * Map<메인코드번호, Pair<메인코드명, Map<세부코드번호, 세부코드명>>>
	 */
	public static Map<String, Pair<String, Map<String, String>>> assetsMap = new HashMap<String, Pair<String, Map<String, String>>>();

	/**
	 * 공연행사 주제분류 코드
	 */
	public static List<CodeInfo> playCode = new ArrayList<CodeInfo>();

	@Override
	public void onCreate()
	{
		super.onCreate();
		// Logger.v("onCreate");

		// 어플리케이션 컨텍스트 저장
		context = this;

		// 최초 실행인 경우 설정값 초기화
		if (!PreferencesUtil.getBoolean(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_FIRST_EXCUTE))
		{
			PreferencesUtil.setBoolean(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_FIRST_EXCUTE, true);
			PreferencesUtil.setBoolean(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_DISKCACHE, true);
			PreferencesUtil.setBoolean(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_ACCESS_LOCATION, false);
			PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_ASSETOPT1, Constants.OPTION_START_INDEX);
			PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_ASSETOPT2, Constants.OPTION_START_INDEX);
			PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PARKOPT1, Constants.OPTION_START_INDEX);
			PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PARKOPT2, Constants.OPTION_START_INDEX);
			PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PARKOPT3, Constants.OPTION_START_INDEX);
			PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PLAYOPT1, Constants.OPTION_START_INDEX);
			PreferencesUtil.setInt(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_LAST_PLAYOPT2, Constants.OPTION_START_INDEX);
		}

		// Preference 에 저장된 분류 코드 정보 로딩
		setFacilThemeCode(PreferencesUtil.getString(this, Constants.PREFS_CODE_FILE, Constants.PREFS_KEY_FACILITY_THEME));
		setFacilSubjectCode(PreferencesUtil.getString(this, Constants.PREFS_CODE_FILE, Constants.PREFS_KEY_FACILITY_SUBJECT));
		setAssetsCode(PreferencesUtil.getString(this, Constants.PREFS_CODE_FILE, Constants.PREFS_KEY_ASSETS));
		Iterator<CodeInfo> it = assetsCode.iterator();
		while (it.hasNext())
		{
			CodeInfo code = it.next();
			setAssetsDetailCode(code, PreferencesUtil.getString(this, Constants.PREFS_CODE_FILE, Constants.PREFS_KEY_ASSETS_DETAIL + code.CODE));
		}
		setPlayCode(PreferencesUtil.getString(this, Constants.PREFS_CODE_FILE, Constants.PREFS_KEY_PLAY));

		// 이미지 로더 초기화
		setImageLoader(PreferencesUtil.getBoolean(context, Constants.PREFS_SETTING_FILE, Constants.PREFS_KEY_DISKCACHE));
	}

	/**
	 * 컨텍스트 정보 가져오기
	 * 
	 * @since 1.0.0
	 * @return context
	 */
	public static Context getContext()
	{
		return context;
	}

	/**
	 * 공용 이미지로더 가져오기
	 * 
	 * @since 1.0.0
	 * @param adapter
	 *            지정할 어댑터
	 * @return imageLoader
	 */
	public static ImageLoader getImageLoader(ImageWorkerAdapter adapter)
	{
		imageLoader.setAdapter(adapter);
		return imageLoader;
	}

	/**
	 * 이미지로더 디스크 캐시 설정 변경
	 * 
	 * @since 1.0.0
	 * @param enableDiskCache
	 *            디스크 캐시 사용 여부
	 */
	public static void setImageLoader(boolean enableDiskCache)
	{
		imageLoader = new ImageLoader(getContext(), null, Constants.CACHE_DIR, R.drawable.border_thumbnail, R.dimen.thumbnail_size, true, enableDiskCache, false);
		imageLoader.setImageFadeIn(true);
	}

	/**
	 * 시설 테마 코드 설정
	 * 
	 * @since 1.0.0
	 * @param data
	 */
	public static void setFacilThemeCode(String data)
	{
		if (null != data && 0 != data.length())
		{
			facilThemeCode.clear();
			Matcher matcher = Constants.PATTERN_CODE.matcher(data);
			while (matcher.find())
			{
				String group = matcher.group();
				String[] codeArray = group.split("\\|");
				CodeInfo code = new CodeInfo();
				code.CODE = codeArray[0];
				code.CODENAME = codeArray[1];
				facilThemeCode.add(code);
				// Logger.d("theme code : " + code.toString());
			}

			Collections.sort(facilThemeCode, new CodeInfo());
		}
	}

	/**
	 * 시설 주제 코드 설정
	 * 
	 * @since 1.0.0
	 * @param data
	 */
	public static void setFacilSubjectCode(String data)
	{
		if (null != data && 0 != data.length())
		{
			facilSubjectCode.clear();
			Matcher matcher = Constants.PATTERN_CODE.matcher(data);
			while (matcher.find())
			{
				String group = matcher.group();
				String[] codeArray = group.split("\\|");
				CodeInfo code = new CodeInfo();
				code.CODE = codeArray[0];
				code.CODENAME = codeArray[1];
				facilSubjectCode.add(code);
				// Logger.d("subject code : " + code.toString());
			}

			Collections.sort(facilSubjectCode, new CodeInfo());
		}
	}

	/**
	 * 문화재 분류 코드 설정
	 * 
	 * @since 1.0.0
	 * @param data
	 */
	public static void setAssetsCode(String data)
	{
		if (null != data && 0 != data.length())
		{
			assetsCode.clear();
			Matcher matcher = Constants.PATTERN_CODE.matcher(data);
			while (matcher.find())
			{
				String group = matcher.group();
				String[] codeArray = group.split("\\|");
				CodeInfo code = new CodeInfo();
				code.CODE = codeArray[0];
				code.CODENAME = codeArray[1];
				assetsCode.add(code);
				assetsMap.put(code.CODE, new Pair<String, Map<String, String>>(code.CODENAME, new HashMap<String, String>()));
				// Logger.d("assets code : " + code.toString());
			}

			Collections.sort(assetsCode, new CodeInfo());
		}
	}

	/**
	 * 문화재 세부분류 코드 설정
	 * 
	 * @since 1.0.0
	 * @param data
	 */
	public static void setAssetsDetailCode(CodeInfo assetsCode, String data)
	{
		if (null != data && 0 != data.length())
		{
			Iterator<Pair<String, List<CodeInfo>>> it = assetsDetailCode.iterator();
			int ii = -1;
			boolean exist = false;
			while (it.hasNext())
			{
				ii++;
				Pair<String, List<CodeInfo>> pair = it.next();
				if (pair.first.equalsIgnoreCase(assetsCode.CODE))
				{
					exist = true;
					break;
				}
			}

			// 최초 데이터 추가이거나 없는 데이터를 추가하는 경우
			if (-1 == ii || !exist)
			{
				assetsDetailCode.add(new Pair<String, List<CodeInfo>>(assetsCode.CODE, new ArrayList<CodeInfo>()));
				ii = assetsDetailCode.size() - 1;
			}

			assetsDetailCode.get(ii).second.clear();
			Matcher matcher = Constants.PATTERN_CODE.matcher(data);
			while (matcher.find())
			{
				String group = matcher.group();
				String[] codeArray = group.split("\\|");
				CodeInfo code = new CodeInfo();
				code.CODE = codeArray[0];
				code.CODENAME = codeArray[1];
				assetsDetailCode.get(ii).second.add(code);
				assetsMap.get(assetsCode.CODE).second.put(code.CODE, code.CODENAME);
				// Logger.d("assets [" + assetsCode.CODENAME + "] code : " + code.CODE + ", " + code.CODENAME);
			}

			Collections.sort(assetsDetailCode.get(ii).second, new CodeInfo());
		}
	}

	/**
	 * 공연코드 설정
	 * 
	 * @since 1.0.0
	 * @param data
	 */
	public static void setPlayCode(String data)
	{
		if (null != data && 0 != data.length())
		{
			playCode.clear();
			Matcher matcher = Constants.PATTERN_CODE.matcher(data);
			while (matcher.find())
			{
				String group = matcher.group();
				String[] codeArray = group.split("\\|");
				CodeInfo code = new CodeInfo();
				code.CODE = codeArray[0];
				code.CODENAME = codeArray[1];
				playCode.add(code);
				// Logger.d("play code : " + code.toString());
			}

			Collections.sort(playCode, new CodeInfo());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onLowMemory()
	 */
	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		// Logger.v("onLowMemory");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate()
	{
		super.onTerminate();
		// Logger.v("onTerminate");
	}
}
