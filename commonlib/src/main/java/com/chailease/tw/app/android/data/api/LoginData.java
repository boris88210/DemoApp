package com.chailease.tw.app.android.data.api;

import com.google.gson.JsonElement;

/**
 *
 */
public class LoginData {

	public class RSP_CNT {
		public String Alert;
		public String STATUS;
		public String ALERT_MSG;
		public String ERROR_MSG;
		public LOGIN_DATA OUTPUT;
	}
	public class LOGIN_DATA {
		public String MEMBER_ID;
		public String CUSTNAME;
		public String APPROVE_POLICY;
		public String VERIFY_CODE;
		public String IS_NEWAPP;
		public String APP_URL;
		public String USR_EMAIL;
		public String DEPT_LVL;
		public String DEPT_ID;
		public String DEPT_CD;
		public String DEPT_NME;
		public String DEPT_HD_USR_ID;
		public String DEPT_COMP_ID;
		public String COMP_SHRT_NME;
		public String MVPN;
		public USR_PRFL[] USR_PRFL;
		public JsonElement LOGIN_OUTPUT_JSON;
	}
	public class USR_PRFL {
		public String USR_ID;
		public String USR_NME;
		public String USR_EMAIL;
		public String MAIN_DUTY;
		public String DEPT_COMP_ID;
		public String COMP_SHRT_NME;
		public String DEPT_LVL;
		public String DEPT_ID;
		public String DEPT_CD;
		public String DEPT_NME;
		public String DEPT_HD_USR_ID;
		public String WORKTYP_ID;
	}

}