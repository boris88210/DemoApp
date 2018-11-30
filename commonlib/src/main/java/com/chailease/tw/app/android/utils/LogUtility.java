package com.chailease.tw.app.android.utils;

import android.app.Application;
import android.util.Log;

import com.chailease.tw.app.android.core.APPInfo;
import com.chailease.tw.app.android.core.IBaseApplication;

import org.apache.commons.lang3.StringUtils;

/********************************************************
 * 類別說明<br>
 * Log 訊息管理工具，提供一個Log的外包類以，以提供更佳的Log控制機制<br>
 ******************************************************* 
 */
public class LogUtility {

	/* Log的標籤訊息 */
	public static final String LOG_TAG = "ChaileaseGroupApp";
	/* 靜態常數,於其它程式中參照此Flag來決定是否要將Log訊息編譯進Class檔中 */
	public static final boolean isCompilerLog = false;

	/* 要輸出顯示的Debug層級 */
	public static int LOG_LEVEL = Log.DEBUG;

	private static SubLog log;

	private static IBaseApplication _App;
	private static APPInfo _AppInfo;

	public static void loadAppInfo(IBaseApplication app) {
		if (null == _App && app != null) {
			_App = app;
			_AppInfo = APPInfo.getAppInfo((Application)_App);
			if (StringUtils.isBlank(_AppInfo.getAPP_BUILD_TYPE()))
				logger(_AppInfo.getAPP_ID());
			else logger(_AppInfo.getAPP_ID()+"_"+_AppInfo.getAPP_BUILD_TYPE().toUpperCase());
		}
	}
	private static SubLog logger(String logTag) {
		if (null == log) {
			if (null != logTag && !"".equals(logTag.trim())) {
				log = new SubLog(logTag.trim().toUpperCase());
			} else {
				log = new SubLog(LOG_TAG);
			}
		}
		return log;
	}

	/**
	 * Debug訊息
	 * 
	 * @param className
	 *            類別名稱
	 * @param methodName
	 *            函式名稱
	 * @param msg
	 *            除錯訊息
	 */
	public static void d(String className ,String methodName,String msg) {
		log.d(className, methodName, msg);
	}
	public static void debug(Object logger, String hint, String msg) {
		if (logger !=null) {
			logger(LOG_TAG).d(getLoggerName(logger), hint, msg);
		} else {
			logger(LOG_TAG).d(null, hint, msg);
		}
	}

	/**
	 * Information訊息
	 * 
	 * @param className
	 *            類別名稱
	 * @param methodName
	 *            函式名稱
	 * @param msg
	 *            除錯訊息
	 */
	public static void i( String className ,String methodName,String msg) {
		log.i(className, methodName, msg);
	}
	public static void info(Object logger, String hint, String msg) {
		if (logger !=null) {
			logger(LOG_TAG).i(getLoggerName(logger), hint, msg);
		} else {
			logger(LOG_TAG).i(null, hint, msg);
		}
	}

	/**
	 * Error訊息
	 * 
	 * @param className
	 *            類別名稱
	 * @param methodName
	 *            函式名稱
	 * @param msg
	 *            除錯訊息
	 */
	public static void e(String className ,String methodName,String msg) {
		log.e(className, methodName, msg);
	}
	public static void e(String className ,String methodName,String msg, Throwable e) {
		log.e(className, methodName, msg, e);
	}
	public static void error(Object logger, String hint, String msg) {
		if (logger !=null) {
			logger(LOG_TAG).e(getLoggerName(logger), hint, msg);
		} else {
			logger(LOG_TAG).e(null, hint, msg);
		}
	}
	public static void error(Object logger, String hint, String msg, Throwable e) {
		if (logger !=null) {
			logger(LOG_TAG).e(getLoggerName(logger), hint, msg, e);
		} else {
			logger(LOG_TAG).e(null, hint, msg, e);
		}
	}
	private static String getLoggerName(Object logger) {
		if (logger instanceof Class) {
			return ((Class) logger).getSimpleName();
		} else if (logger instanceof String) {
			return logger.toString();
		} else if (logger != null) {
			return logger.getClass().getSimpleName();
		}
		return null;
	}

	/**
	 * Verbose訊息
	 * 
	 * @param className
	 *            類別名稱
	 * @param methodName
	 *            函式名稱
	 * @param msg
	 *            除錯訊息
	 */
	public static void v(String className ,String methodName,String msg) {
		log.v(className, methodName, msg);
	}
	public static void verbose(Object logger, String hint, String msg) {
		if (logger !=null) {
			logger(LOG_TAG).v(getLoggerName(logger), hint, msg);
		} else {
			logger(LOG_TAG).v(null, hint, msg);
		}
	}

	/**
	 * Warning訊息
	 * 
	 * @param className
	 *            類別名稱
	 * @param methodName
	 *            函式名稱
	 * @param msg
	 *            除錯訊息
	 */
	public static void w(String className ,String methodName,String msg) {
		log.w(className, methodName, msg);
	}
	public static void warn(Object logger, String hint, String msg) {
		if (logger !=null) {
			logger(LOG_TAG).w(getLoggerName(logger), hint, msg);
		} else {
			logger(LOG_TAG).w(null, hint, msg);
		}
	}
	public static void warn(Object logger, String hint, String msg, Throwable e) {
		if (logger !=null) {
			logger(LOG_TAG).w(getLoggerName(logger), hint, msg, e);
		} else {
			logger(LOG_TAG).w(null, hint, msg, e);
		}
	}

	public static class SubLog {

		protected String LOG_TAG;
		public SubLog(String logTag) {
			this.LOG_TAG = logTag;
		}

		public void d(String className ,String methodName,String msg) {
			if (Log.DEBUG >= LOG_LEVEL) {
				if (StringUtils.isBlank(className))
					Log.d(LOG_TAG, "[" + methodName + "]: " + msg);
				else
					Log.d(LOG_TAG, className+"["+methodName+"]: "+ msg);
			}
		}

		/**
		 * Information訊息
		 * 
		 * @param className
		 *            類別名稱
		 * @param methodName
		 *            函式名稱
		 * @param msg
		 *            除錯訊息
		 */
		public void i( String className ,String methodName,String msg) {
			if (Log.INFO >= LOG_LEVEL) {
				if (StringUtils.isBlank(className))
					Log.i(LOG_TAG, "[" + methodName + "]: " + msg);
				else
					Log.i(LOG_TAG, className+"["+methodName+"]: "+ msg);
			}
		}

		/**
		 * Error訊息
		 * 
		 * @param className
		 *            類別名稱
		 * @param methodName
		 *            函式名稱
		 * @param msg
		 *            除錯訊息
		 */
		public void e(String className ,String methodName,String msg) {
			e(className, methodName, msg, null);
		}
		public void e(String className ,String methodName,String msg, Throwable e) {
			if (Log.ERROR >= LOG_LEVEL) {
				if (StringUtils.isBlank(className))
					if (null == e) Log.e(LOG_TAG, "[" + methodName + "]: " + msg);
					else Log.e(LOG_TAG, "[" + methodName + "]: " + msg, e);
				else if (null == e) Log.e(LOG_TAG, className+"["+methodName+"]: "+ msg);
				else Log.e(LOG_TAG, className+"["+methodName+"]: "+ msg, e);
			}
		}

		/**
		 * Verbose訊息
		 * 
		 * @param className
		 *            類別名稱
		 * @param methodName
		 *            函式名稱
		 * @param msg
		 *            除錯訊息
		 */
		public void v(String className ,String methodName,String msg) {
			if (Log.VERBOSE >= LOG_LEVEL) {
				if (StringUtils.isBlank(className))
					Log.v(LOG_TAG, "[" + methodName + "]: " + msg);
				else
					Log.v(LOG_TAG, className+"["+methodName+"]: "+ msg);
			}
		}

		/**
		 * Warning訊息
		 * 
		 * @param className
		 *            類別名稱
		 * @param methodName
		 *            函式名稱
		 * @param msg
		 *            除錯訊息
		 */
		public void w(String className ,String methodName,String msg) {
			w(className, methodName, msg, null);
		}
		public void w(String className ,String methodName,String msg, Throwable e) {
			if (Log.WARN >= LOG_LEVEL) {
				if (StringUtils.isBlank(className))
					if (e == null) Log.w(LOG_TAG, "[" + methodName + "]: " + msg);
					else Log.w(LOG_TAG, "[" + methodName + "]: " + msg, e);
				else if (e == null) Log.w(LOG_TAG, className+"["+methodName+"]: "+ msg);
				else Log.w(LOG_TAG, className+"["+methodName+"]: "+ msg, e);
			}
		}

	}

}
