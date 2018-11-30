package com.chailease.tw.app.android.utils;

/**
 *
 * Sample: 
 * 	Logger log = new Logger(this.getClass());
 * 	log.v("method-name", "message");	//	for verbose message
 * 	log.d("method-name", "message");	//	for debug message
 * 	log.i("method-name", "message");	//	for information message
 * 	log.w("method-name", "message");	//	for warning message
 * 	log.e("method-name", "message", ex);	//	for error message with Exception
 */
@Deprecated
public class Logger extends LogUtility.SubLog {

	private String className;

	public Logger(Class clazz, final String APP_ID) {
		super(APP_ID);
		this.className = clazz.getName();
	}

	public void d(String methodName,String msg) {
		d(className, methodName, msg);
	}

	/**
	 * Information訊息
	 * @param methodName
	 *            函式名稱
	 * @param msg
	 *            除錯訊息
	 */
	public void i(String methodName,String msg) {
		i(className, methodName, msg);
	}

	/**
	 * Error訊息
	 * @param methodName
	 *            函式名稱
	 * @param msg
	 *            除錯訊息
	 */
	public void e(String methodName,String msg) {
		e(className, methodName, msg);
	}
	public void e(String methodName,String msg, Throwable e) {
		try {
			//ServerLog serverLog = new ServerLog();
	    	//serverLog.sendToServer(e);
		} catch (Exception e2) {
			// TODO: handle exception
		}
		e(className, methodName, msg, e);
	}

	/**
	 * Verbose訊息
	 * @param methodName
	 *            函式名稱
	 * @param msg
	 *            除錯訊息
	 */
	public void v(String methodName,String msg) {
		v(className, methodName, msg);
	}

	/**
	 * Warning訊息
	 * @param methodName
	 *            函式名稱
	 * @param msg
	 *            除錯訊息
	 */
	public void w(String methodName,String msg) {
		w(className, methodName, msg);
	}

}
