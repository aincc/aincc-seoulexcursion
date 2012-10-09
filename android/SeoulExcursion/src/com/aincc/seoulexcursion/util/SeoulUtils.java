package com.aincc.seoulexcursion.util;

import com.aincc.seoulopenapi.TrafficCode;

/**
 * 
 * <h3><b>SeoulUtils</b></h3></br>
 * 
 * 공통유틸
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class SeoulUtils
{
	private SeoulUtils()
	{

	}

	/**
	 * 교통코드값에 따른 교통코드정보 문자열 반환 (한글)
	 * 
	 * @since 1.0.0
	 * @param trafficCode
	 *            A, Y, G, B, R, T
	 * @return 교통정보명
	 * @since 1.3.0
	 */
	public static String getTrafficInfo(String trafficCode)
	{
		try
		{
			TrafficCode code = TrafficCode.valueOf(trafficCode);
			return code.getDesc();
		}
		catch (UnsupportedOperationException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}
}
