package com.development.borissu.demoapp.utilities;

import android.util.Log;

import static com.development.borissu.demoapp.constant.CoreConstant.TAG_CUST;

/**
 * 將官方Log包覆在其中，製做出屬於自己的Log格式
 */
public class LogUtility {

    public static void debug(String methodName, String msg) {
        Log.d(TAG_CUST, genCustLog(methodName, msg));

    }

    public static void error(String methodName, String msg, Throwable e) {
        Log.e(TAG_CUST, genCustLog(methodName, msg), e);
    }

    /**
     * 產生客製化的Log格式
     *
     * @param methodName 方法名稱，用來比對在StackTraceElement的元素
     * @param msg        欲Log的訊息
     * @return 客製化的log訊息
     */
    public static String genCustLog(String methodName, String msg) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
//        StackTraceElement element = elements[5];
        StackTraceElement element = null;
        for (StackTraceElement e : elements) {
            if (e.getMethodName().equals(methodName)) {
                element = e;
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(element.getMethodName());
        sb.append("(");
        sb.append(element.getFileName());
        sb.append(":");
        sb.append(element.getLineNumber());
        sb.append(")]\n");
        sb.append(msg);

        return sb.toString();
    }
}
