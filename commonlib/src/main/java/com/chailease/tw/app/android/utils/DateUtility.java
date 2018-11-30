package com.chailease.tw.app.android.utils;

import java.text.SimpleDateFormat;

/**
 *
 */
public class DateUtility {

	public static SimpleDateFormat DATE_FORMAT_DISPLAY = new SimpleDateFormat("yyyy/MM/dd");
	public static SimpleDateFormat DATE_FORMAT_VALUE = new SimpleDateFormat("yyyyMMdd");

	public static String todayString() {
		return DATE_FORMAT_DISPLAY.format(System.currentTimeMillis());
	}
	public static String todayValue() {
		return DATE_FORMAT_VALUE.format(System.currentTimeMillis());
	}
}