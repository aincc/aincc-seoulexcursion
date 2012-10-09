package com.aincc.seoulexcursion.ui;

import java.util.regex.Pattern;

import android.graphics.Color;

import com.aincc.seoulopenapi.LangCode;

/**
 * 
 * <h3><b>Constants</b></h3></br>
 * 
 * 공통 상수 정의
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class Constants
{
	/**
	 * 디스크 캐쉬 경로명
	 */
	public static final String CACHE_DIR = "SeoulExcursion";

	/**
	 * 페이징 기본 개수
	 */
	public static final int FETCH_COUNT = 40;

	/**
	 * 시작 인덱스
	 */
	public static final int INIT_START_INDEX = 1;

	/**
	 * 공원 주소 검색
	 */
	public static final int SEARCH_BY_ADDR = 0;

	/**
	 * 공원 이름 검색
	 */
	public static final int SEARCH_BY_PARK = 1;

	/**
	 * 옵션 초기인덱스
	 */
	public static final int OPTION_INVALID_INDEX = -1;

	/**
	 * 옵션 시작인덱스
	 */
	public static final int OPTION_START_INDEX = 0;

	/**
	 * 상세페이지 미디어 표시 애니메이션 딜레이
	 */
	public static final int MEDIA_ANIM_DELAY = 300;

	/**
	 * Extra Key 공원정보
	 */
	public static final String EXTRA_KEY_PARK_INFO = "ParkInfo";

	/**
	 * Extra Key 공원상세페이지로 진입하기 이전 액티비티 구분 (0: ParksActivity, 1: ParksSearchActivity)
	 */
	public static final String EXTRA_KEY_PARK_DETAIL_FROM = "fromActivity";

	/**
	 * Extra Key 공원프로그램정보
	 */
	public static final String EXTRA_KEY_PARK_PROGRAM_LIST = "ProgramList";

	/**
	 * Extra Key 문화재 정보
	 */
	public static final String EXTRA_KEY_ASSETS_SIMPLE_INFO = "AssetsSimpleInfo";

	/**
	 * Extra Key 문화재 설명 언어 인덱스
	 */
	public static final String EXTRA_KEY_ASSETS_LANGUAGE = "AssetsLanguage";

	/**
	 * Extra Key 공연 정보
	 */
	public static final String EXTRA_KEY_PLAYS_SIMPLE_INFO = "PlaySimpleInfo";

	/**
	 * Extra Key 문화시설 정보
	 */
	public static final String EXTRA_KEY_FACILS_SIMPLE_INFO = "FacilSimpleInfo";

	/**
	 * 코드 프리퍼런스 파일명
	 */
	public static final String PREFS_CODE_FILE = "SeoulCodeFile";

	/**
	 * 문화시설 테마코드
	 */
	public static final String PREFS_KEY_FACILITY_THEME = "FacilityThemeCode";

	/**
	 * 문화시설 주제코드
	 */
	public static final String PREFS_KEY_FACILITY_SUBJECT = "FacilitySubjectCode";

	/**
	 * 문화재 분류코드
	 */
	public static final String PREFS_KEY_ASSETS = "AssetsCode";

	/**
	 * 문화재 세부분류코드 접두어 (AssetsDetailCode + code)
	 */
	public static final String PREFS_KEY_ASSETS_DETAIL = "AssetsDetailCode";

	/**
	 * 공연행사 주제분류코드
	 */
	public static final String PREFS_KEY_PLAY = "PlayCode";

	/**
	 * 코드구분 패턴 : Preference 에 저장된 코드데이터 구분패턴
	 */
	public static final Pattern PATTERN_CODE = Pattern.compile("[^\\|]*\\|[^\\|]*\\|");

	/**
	 * 설정 프리퍼런스 파일명
	 */
	public static final String PREFS_SETTING_FILE = "SeoulSetting";

	/**
	 * 최초 실행 여부
	 */
	public static final String PREFS_KEY_FIRST_EXCUTE = "first_execute";

	/**
	 * 현재위치 정보 접근허용 여부
	 */
	public static final String PREFS_KEY_ACCESS_LOCATION = "access_location";

	/**
	 * 디스크 캐시 사용 여부
	 */
	public static final String PREFS_KEY_DISKCACHE = "disk_cache";

	/**
	 * 마지막 선택 공원옵션1
	 */
	public static final String PREFS_KEY_LAST_PARKOPT1 = "last_parkopt1";

	/**
	 * 마지막 선택 공원옵션2
	 */
	public static final String PREFS_KEY_LAST_PARKOPT2 = "last_parkopt2";

	/**
	 * 마지막 선택 공원검색옵션
	 */
	public static final String PREFS_KEY_LAST_PARKOPT3 = "last_parkopt3";

	/**
	 * 마지막 선택 공연옵션1
	 */
	public static final String PREFS_KEY_LAST_PLAYOPT1 = "last_playopt1";

	/**
	 * 마지막 선택 공연옵션2
	 */
	public static final String PREFS_KEY_LAST_PLAYOPT2 = "last_playopt2";

	/**
	 * 마지막 선택 공연검색옵션1
	 */
	public static final String PREFS_KEY_LAST_PLAYOPT3 = "last_playopt3";

	/**
	 * 마지막 선택 공연검색옵션2
	 */
	public static final String PREFS_KEY_LAST_PLAYOPT4 = "last_playopt4";

	/**
	 * 마지막 선택 문화재옵션1
	 */
	public static final String PREFS_KEY_LAST_ASSETOPT1 = "last_assetopt1";

	/**
	 * 마지막 선택 문화재옵션2
	 */
	public static final String PREFS_KEY_LAST_ASSETOPT2 = "last_assetopt2";

	public static final int COLOR_BLACK = Color.BLACK;
	public static final int COLOR_WHITE = Color.WHITE;

	/**
	 * 언어
	 */
	public static final String[] LANGUAGES =
	{ LangCode.KOREAN.getDesc(), LangCode.ENGLISH.getDesc(), LangCode.JAPANESE.getDesc(), LangCode.CHINAB.getDesc(), LangCode.CHINAG.getDesc() };

	/**
	 * 언어코드
	 */
	public static final String[] LANGUAGES_CODE =
	{ LangCode.KOREAN.getCode(), LangCode.ENGLISH.getCode(), LangCode.JAPANESE.getCode(), LangCode.CHINAB.getCode(), LangCode.CHINAG.getCode() };
}
