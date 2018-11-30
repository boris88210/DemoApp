package com.chailease.tw.app.android.apiclient;

import com.google.gson.JsonElement;

/**
 *
 */
public class APIResultInfoGeneral extends APIResultInfo {
	public JsonElement Data;
	public Result Result;

	public JsonElement getData() {
		return Data;
	}

	/**
	 *   "Result":
	 *      {
	 *          "ReturnCode":0,
	 *          "ReturnMsg":"xxxx",
	 *          "Alert":"xxxx",
	 *          "Count":2
	 *       }
	 */
	static public class Result {
		public int ReturnCode;
		public String ReturnMsg;
		public String Alert;
		public int Count;
	}
}