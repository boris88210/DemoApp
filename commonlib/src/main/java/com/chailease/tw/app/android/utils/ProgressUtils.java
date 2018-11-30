package com.chailease.tw.app.android.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressUtils {

	public static String formatInPercentage(int progress, int maxProgress) {
		float percent = progress/maxProgress;		
		return String.valueOf(percent*100) + "%";
	}

	public static ProgressDialog startProgressDialog(Context context, int hint_id, String message) {
		ProgressDialog pDialog = genProgressDialog(context, hint_id, message);
	    pDialog.show();
	    return pDialog;
	}
	public static ProgressDialog startProgressDialog(Context context, boolean cancelable, int hint_id, String message) {
		ProgressDialog pDialog = genProgressDialog(context, hint_id, message);
		pDialog.setCancelable(cancelable);
		pDialog.show();
		return pDialog;
	}

	public static ProgressDialog genProgressDialog(Context context, int hint_id, String message) {
		ProgressDialog pDialog = new ProgressDialog(context);
		String msg = null;
		if (null!=message)
			msg = message;
		else if (hint_id==0)
			msg = "loading...";
		else msg = context.getString(hint_id);
		pDialog.setMessage(msg);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.setCanceledOnTouchOutside(false);
		return pDialog;
	}
}