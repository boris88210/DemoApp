package com.chailease.tw.app.android.utils;

import java.text.DecimalFormat;

/**
 *
 */
public class LocationUtils {

	static DecimalFormat DoubleFormat = new DecimalFormat("#######.###################");

	public static String transTudeValue(double val) {
		return DoubleFormat.format(val);
	}
}