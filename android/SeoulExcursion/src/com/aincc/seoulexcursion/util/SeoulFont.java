package com.aincc.seoulexcursion.util;

import android.graphics.Typeface;

import com.aincc.seoulexcursion.App;

/**
 * 
 * <h3><b>SeoulFont</b></h3></br>
 * 
 * 서울폰트 설정
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class SeoulFont
{
	/**
	 * 싱글톤 인스턴스
	 */
	private static SeoulFont instance;

	/**
	 * 서울한강체 폰트
	 */
	private static Typeface seoulHangang;

	/**
	 * private constructor
	 * 
	 * @since 1.0.0
	 */
	private SeoulFont()
	{

	}

	/**
	 * 인스턴스 가져오기
	 * 
	 * @since 1.0.0
	 * @return the SeoulFont
	 */
	public static SeoulFont getInstance()
	{
		if (null == instance)
		{
			synchronized (SeoulFont.class)
			{
				if (null == instance)
				{
					instance = new SeoulFont();
				}
			}
		}
		return instance;
	}

	/**
	 * 서울한강체 폰트 가져오기
	 * 
	 * @since 1.0.0
	 * @return SeoulHangang Typeface
	 */
	public Typeface getSeoulHangang()
	{
		if (null == seoulHangang)
		{
			synchronized (SeoulFont.class)
			{
				if (null == seoulHangang)
				{
					seoulHangang = Typeface.createFromAsset(App.getContext().getResources().getAssets(), "fonts/SeoulHangang.ttf");
				}
			}
		}
		return seoulHangang;
	}
}
