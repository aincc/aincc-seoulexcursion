package com.aincc.seoulexcursion.util;

import android.util.Log;

/**
 * 
 * <h3><b>Logger</b></h3></br>
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class Logger
{
	static boolean isDebug = false;
	private static final String TAG = "Logger";

	// 라이브러리는 release package 가 안되..
	// private static final boolean isDebug = BuildConfig.DEBUG;

	private Logger()
	{
	}

	/**
	 * 디버깅 모드 설정
	 * 
	 * @since 1.0.0
	 * @param isDebug
	 */
	public static void setDebug(boolean isDebug)
	{
		Logger.isDebug = isDebug;
	}

	/**
	 * Send a {@link #VERBOSE} log message.
	 * 
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int v(String msg)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.v(TAG, msg);
	}

	/**
	 * Send a {@link #VERBOSE} log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int v1(String userTag, String msg)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.v(userTag, msg);
	}

	/**
	 * Send a {@link #VERBOSE} log message and log the exception.
	 * 
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int v(String msg, Throwable tr)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.v(TAG, msg, tr);
	}

	/**
	 * Send a {@link #VERBOSE} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int v1(String userTag, String msg, Throwable tr)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.v(userTag, msg, tr);
	}

	/**
	 * Send a {@link #DEBUG} log message.
	 * 
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int d(String msg)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.d(TAG, msg);
	}

	/**
	 * Send a {@link #DEBUG} log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int d1(String userTag, String msg)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.d(userTag, msg);
	}

	/**
	 * Send a {@link #DEBUG} log message and log the exception.
	 * 
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int d(String msg, Throwable tr)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.d(TAG, msg, tr);
	}

	/**
	 * Send a {@link #DEBUG} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int d1(String userTag, String msg, Throwable tr)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.d(userTag, msg, tr);
	}

	/**
	 * Send an {@link #INFO} log message.
	 * 
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int i(String msg)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.i(TAG, msg);
	}

	/**
	 * Send an {@link #INFO} log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int i1(String userTag, String msg)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.i(userTag, msg);
	}

	/**
	 * Send a {@link #INFO} log message and log the exception.
	 * 
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int i(String msg, Throwable tr)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.i(TAG, msg, tr);
	}

	/**
	 * Send a {@link #INFO} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int i1(String userTag, String msg, Throwable tr)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.i(userTag, msg, tr);
	}

	/**
	 * Send a {@link #WARN} log message.
	 * 
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int w(String msg)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.w(TAG, msg);
	}

	/**
	 * Send a {@link #WARN} log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int w1(String userTag, String msg)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.w(TAG, msg);
	}

	/**
	 * Send a {@link #WARN} log message and log the exception.
	 * 
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int w(String msg, Throwable tr)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.w(TAG, msg, tr);
	}

	/**
	 * Send a {@link #WARN} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int w1(String userTag, String msg, Throwable tr)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.w(userTag, msg, tr);
	}

	/**
	 * Send a {@link #WARN} log message and log the exception.
	 * 
	 * @param tr
	 *            An exception to log
	 */
	public static int w(Throwable tr)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.w(TAG, tr);
	}

	/**
	 * Send a {@link #WARN} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param tr
	 *            An exception to log
	 */
	public static int w1(String userTag, Throwable tr)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.w(userTag, tr);
	}

	/**
	 * Send an {@link #ERROR} log message.
	 * 
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int e(String msg)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.e(TAG, msg);
	}

	/**
	 * Send an {@link #ERROR} log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int e1(String userTag, String msg)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.e(userTag, msg);
	}

	/**
	 * Send a {@link #ERROR} log message and log the exception.
	 * 
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int e(String msg, Throwable tr)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.e(TAG, msg, tr);
	}

	/**
	 * Send a {@link #ERROR} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int e1(String userTag, String msg, Throwable tr)
	{
		if (!isDebug)
		{
			return -1;
		}
		return Log.e(userTag, msg, tr);
	}

	/**
	 * Handy function to get a loggable stack trace from a Throwable
	 * 
	 * @param tr
	 *            An exception to log
	 */
	public static String getStackTraceString(Throwable tr)
	{
		if (!isDebug)
		{
			return "";
		}
		return Log.getStackTraceString(tr);
	}

	public static String getClassMethMsgString(Object obj, String meth, String message)
	{

		if (!isDebug)
		{
			return "";
		}
		return new StringBuilder(obj.getClass().getName()).append(":\n").append(meth).append(":\n").append(message).append("\n").toString();
	}
}
