package com.development.borissu.demoapp.utils;

import android.util.Log;

/**
 * Chailease內部用log規格
 * 5個等級
 * verbose
 * debug
 * info
 * warning
 * error
 */
public class LogUtility {

    /**
     * 預設的TAG
     */
    protected final static String TAG_DEFAULT = "Chailease_Core";

    /**
     * 由AppInfo取得的APPID
     */
    protected static String TAG_APP_ID;

    /**
     * 控制是否可以印出log
     */
    protected static boolean Loggable;


    //--- Verbose ---

    public static void verbose(String msg) {
        if (isLoggable())
            verbose(getTagAppId(), msg);
    }

    public static void verbose(String tag, String msg) {
        if (isLoggable())
            Log.v(tag, buildMsg(msg));
    }

    public static void verbose(String msg, Throwable throwable) {
        if (isLoggable())
            verbose(getTagAppId(), msg, throwable);
    }

    public static void verbose(String tag, String msg, Throwable throwable) {
        if (isLoggable())
            Log.v(tag, buildMsg(msg), throwable);
    }


    //--- Debug ---

    public static void debug(String msg) {
        if (isLoggable())
            debug(getTagAppId(), msg);
    }

    public static void debug(String tag, String msg) {
        if (isLoggable())
            Log.d(tag, buildMsg(msg));
    }

    public static void debug(String msg, Throwable throwable) {
        if (isLoggable())
            debug(getTagAppId(), msg, throwable);
    }

    public static void debug(String tag, String msg, Throwable throwable) {
        if (isLoggable())
            Log.d(tag, buildMsg(msg), throwable);
    }


    //--- Info ---

    public static void info(String msg) {
        if (isLoggable())
            info(getTagAppId(), msg);
    }

    public static void info(String tag, String msg) {
        if (isLoggable())
            Log.i(tag, buildMsg(msg));
    }

    public static void info(String msg, Throwable throwable) {
        if (isLoggable())
            info(getTagAppId(), msg, throwable);
    }

    public static void info(String tag, String msg, Throwable throwable) {
        if (isLoggable())
            Log.i(tag, buildMsg(msg), throwable);
    }


    //--- Warning ---

    public static void warning(String msg) {
        if (isLoggable())
            warning(getTagAppId(), msg);
    }

    public static void warning(String tag, String msg) {
        if (isLoggable())
            Log.w(tag, buildMsg(msg));
    }

    public static void warning(String msg, Throwable throwable) {
        if (isLoggable())
            warning(getTagAppId(), msg, throwable);
    }

    public static void warning(String tag, String msg, Throwable throwable) {
        if (isLoggable())
            Log.w(tag, buildMsg(msg), throwable);
    }


    //--- Error ---

    public static void error(String msg) {
        if (isLoggable())
            error(getTagAppId(), msg);
    }

    public static void error(String tag, String msg) {
        if (isLoggable())
            Log.e(tag, buildMsg(msg));
    }

    public static void error(String msg, Throwable throwable) {
        if (isLoggable())
            error(getTagAppId(), msg, throwable);
    }

    public static void error(String tag, String msg, Throwable throwable) {
        if (isLoggable())
            Log.e(tag, buildMsg(msg), throwable);
    }

    protected static boolean isLoggable() {
        return Loggable;
    }

    public static void setLoggable(boolean isLoggable) {
        Loggable = isLoggable;
    }

    /**
     * 組成Log訊息
     * [thread, class name:line, method name] massage
     *
     * @param msg 顯示在Log的訊息
     * @return
     */
    protected static String buildMsg(String msg) {
        StringBuilder buffer = new StringBuilder();

        StackTraceElement[] stackTraceElementArray = Thread.currentThread().getStackTrace();
        final StackTraceElement stackTraceElement = stackTraceElementArray[5];//此index可能根據系統改變

        buffer.append("[thread: ");
        buffer.append(Thread.currentThread().getName());
        buffer.append(", ");
        buffer.append(stackTraceElement.getFileName());
        buffer.append(":");
        buffer.append(stackTraceElement.getLineNumber());
        buffer.append(", ");
        buffer.append(stackTraceElement.getMethodName());
        buffer.append("()] ");
        buffer.append(msg);

        return buffer.toString();
    }


    //--- Setter ---

    public static void setTagAppId(String tagAppId) {
        TAG_APP_ID = tagAppId;
    }


    //--- Getter ---

    protected static String getTagAppId() {
        return "".equals(TAG_APP_ID) ? TAG_DEFAULT : TAG_APP_ID;
    }
}
