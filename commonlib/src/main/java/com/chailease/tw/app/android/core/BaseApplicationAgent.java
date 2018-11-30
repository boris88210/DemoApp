package com.chailease.tw.app.android.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.chailease.tw.app.android.data.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;

import static com.chailease.tw.app.android.constants.SystemConstants.PUSH_KEY_EMPTY;
import static com.chailease.tw.app.android.core.IBaseApplication.PREF_USR_PRFL_DEPTCOMP;
import static com.chailease.tw.app.android.core.IBaseApplication.PREF_USR_PRFL_DEPTCOMP_NAME;
import static com.chailease.tw.app.android.core.IBaseApplication.PREF_USR_PRFL_DEPT_CD;
import static com.chailease.tw.app.android.core.IBaseApplication.PREF_USR_PRFL_DEPT_ID;
import static com.chailease.tw.app.android.core.IBaseApplication.PREF_USR_PRFL_DEPT_LVL;
import static com.chailease.tw.app.android.core.IBaseApplication.PREF_USR_PRFL_DEPT_NAME;
import static com.chailease.tw.app.android.core.IBaseApplication.PREF_USR_PRFL_EMAIL;
import static com.chailease.tw.app.android.core.IBaseApplication.PREF_USR_PRFL_IS_DEPTHD;
import static com.chailease.tw.app.android.core.IBaseApplication.PREF_USR_PRFL_USR_ID;
import static com.chailease.tw.app.android.core.IBaseApplication.PREF_USR_PRFL_USR_NAME;
import static com.chailease.tw.app.android.core.IBaseApplication.PREF_USR_PRFL_VERIFY_CODE;
import static com.chailease.tw.app.android.core.IBaseApplication.PREF_USR_PRFL_WORK_TYPE;

/**
 *
 */
class BaseApplicationAgent {

	static void closeAllActivity(Context context, LinkedList<Activity> activityList) {
		synchronized (context) {
			if (null != activityList) {
				for (Activity activity : activityList) {
					//fininsh()將Activity推向後台，移出了Activity推疊，資源並沒有被
					//釋放，不會觸發onDestory()，但按Back鍵也會回到原Activity
					if (null != activity)
						activity.finish();
				}
			}
		}
	}
	static void logout(Context context) {
		PrivatePreferenceManager prfMgr = PrivatePreferenceManager.getInstance(context, APPInfo.getAppInfo(context).getPREF_CRYPT_KEY());
		prfMgr.clear(context);
	}

	static UserProfile getUserProfile(Context context) {
		PrivatePreferenceManager prfMgr = PrivatePreferenceManager.getInstance(context, APPInfo.getAppInfo(context).getPREF_CRYPT_KEY());
		UserProfile usr = new UserProfile();
		usr.setUserId(prfMgr.getPreference(PREF_USR_PRFL_USR_ID, context));
		usr.setVerifyCode(prfMgr.getPreference(PREF_USR_PRFL_VERIFY_CODE, context));
		if (!StringUtils.isBlank(usr.getUserId()) && !StringUtils.isBlank(usr.getVerifyCode())) {
			usr.setUserName(prfMgr.getPreference(PREF_USR_PRFL_USR_NAME, context));
			usr.setDeptId(Integer.parseInt(prfMgr.getPreference(PREF_USR_PRFL_DEPT_ID, context)));
			usr.setDeptName(prfMgr.getPreference(PREF_USR_PRFL_DEPT_NAME, context));
			usr.setDeptLvl(Integer.parseInt(prfMgr.getPreference(PREF_USR_PRFL_DEPT_LVL,context)));
			usr.setHD("Y".equals(prfMgr.getPreference(PREF_USR_PRFL_IS_DEPTHD, context)));
			usr.setDeptCompId(Integer.parseInt(prfMgr.getPreference(PREF_USR_PRFL_DEPTCOMP, context)));
			usr.setEmail(prfMgr.getPreference(PREF_USR_PRFL_EMAIL, context));
			usr.setVerifyCode(prfMgr.getPreference(PREF_USR_PRFL_VERIFY_CODE, context));
			usr.setDeptCompName(prfMgr.getPreference(PREF_USR_PRFL_DEPTCOMP_NAME, context));
			usr.setDeptCd(prfMgr.getPreference(PREF_USR_PRFL_DEPT_CD, context));
			usr.setWorkType(Integer.parseInt(prfMgr.getPreference(PREF_USR_PRFL_WORK_TYPE, context)));
		} else return null;
		return usr;
	}
	static UserProfile updateUserProfile(Context context, UserProfile usr) {
		if (null != usr && !StringUtils.isBlank(usr.getUserId())) {
			HashMap<String, String> dataSet = new HashMap<String, String>();
			dataSet.put(PREF_USR_PRFL_USR_ID, usr.getUserId());
			dataSet.put(PREF_USR_PRFL_USR_NAME, usr.getUserName());
			dataSet.put(PREF_USR_PRFL_DEPT_ID, String.valueOf(usr.getDeptId()));
			dataSet.put(PREF_USR_PRFL_DEPT_NAME, usr.getDeptName());
			dataSet.put(PREF_USR_PRFL_DEPT_LVL, String.valueOf(usr.getDeptLvl()));
			dataSet.put(PREF_USR_PRFL_IS_DEPTHD, usr.isHD()?"Y":"N");
			dataSet.put(PREF_USR_PRFL_DEPTCOMP, String.valueOf(usr.getDeptCompId()));
			dataSet.put(PREF_USR_PRFL_EMAIL, usr.getEmail());
			dataSet.put(PREF_USR_PRFL_VERIFY_CODE, usr.getVerifyCode());
			dataSet.put(PREF_USR_PRFL_DEPTCOMP_NAME, usr.getDeptCompName());
			dataSet.put(PREF_USR_PRFL_DEPT_CD, usr.getDeptCd());
			dataSet.put(PREF_USR_PRFL_WORK_TYPE, String.valueOf(usr.getWorkType()));
			PrivatePreferenceManager prfMgr = PrivatePreferenceManager.getInstance(context, APPInfo.getAppInfo(context).getPREF_CRYPT_KEY());
			prfMgr.savePreference(dataSet, context);
			return usr;
		}
		return null;
	}

	static String getAPP_PUSH_KEY() {
		Class fcmInstanceIdClass = null;
		try {
			fcmInstanceIdClass = Class.forName("com.google.firebase.iid.FirebaseInstanceId");
		} catch (ClassNotFoundException e) {
			return PUSH_KEY_EMPTY;
		}
		try {
			Object FirebaseInstanceId = fcmInstanceIdClass.getMethod("getInstance").invoke(null);
		    String token = (String) fcmInstanceIdClass.getMethod("getToken").invoke(FirebaseInstanceId);
			return (StringUtils.isNoneBlank(token))? token : PUSH_KEY_EMPTY;
		} catch (IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
			return PUSH_KEY_EMPTY;
		}
	}

	static void setPrefValue(Context context, String key, String value) {
		PrivatePreferenceManager prfMgr = PrivatePreferenceManager.getInstance(context, APPInfo.getAppInfo(context).getPREF_CRYPT_KEY());
		prfMgr.savePreference(key, value, context);
	}

	static String getPrefValue(Context context, String key) {
		PrivatePreferenceManager prfMgr = PrivatePreferenceManager.getInstance(context, APPInfo.getAppInfo(context).getPREF_CRYPT_KEY());
		return prfMgr.getPreference(key, null, context);
	}

}