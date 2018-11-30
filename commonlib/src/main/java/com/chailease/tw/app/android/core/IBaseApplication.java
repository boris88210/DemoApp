package com.chailease.tw.app.android.core;

import android.app.Application;

import com.chailease.tw.app.android.data.FuncItem;
import com.chailease.tw.app.android.data.UserProfile;


/**
 *
 */
public interface IBaseApplication extends Application.ActivityLifecycleCallbacks {

	String INTENT_EXTRA_FUNC_ID         =   "FUNC_ID";
	String INTENT_EXTRA_MSG_COUNT     =   "intent.extra.count.Notify";

	String PREF_USR_PRFL_USR_ID         =   "USR_PRFL.USRID";
	String PREF_USR_PRFL_USR_NAME     =   "USR_PRFL.USRNAME";
	String PREF_USR_PRFL_DEPT_ID       =   "USR_PRFL.DEPTID";
	String PREF_USR_PRFL_DEPT_NAME     =   "USR_PRFL.DEPTNAME";
	String PREF_USR_PRFL_DEPT_LVL     =   "USR_PRFL.DEPTLVL";
	String PREF_USR_PRFL_IS_DEPTHD     =   "USR_PRFL.DEPTHD";
	String PREF_USR_PRFL_DEPTCOMP     =   "USR_PRFL.DEPTCOMP";
	String PREF_USR_PRFL_EMAIL         =   "USR_PRFL.EMAIL";
	String PREF_USR_PRFL_VERIFY_CODE     =   "USR_PRFL.VERIFY_CODE";
	String PREF_USR_PRFL_DEPTCOMP_NAME     =   "USR_PRFL.DEPTCOMP_NAME";
	String PREF_USR_PRFL_DEPT_CD       =   "USR_PRFL.DEPT_CD";
	String PREF_USR_PRFL_WORK_TYPE       =   "USR_PRFL.WORK_TYPE";

	//用來關掉所有被管理的Activity的方法
	void closeAllActivity();
	void closeAllActivity(boolean endApp);
	UserProfile getUserProfile();
	void updateUserProfile(UserProfile usr);

	void logout();

	FuncItem[] getUserFuncs();
	FuncItem[] getUserFuncs(boolean enabledOnly);
	FuncItem getFuncItem(int funcId);
	void setEnabledFunc(int... funcIds);
	void setEnabledFunc(FuncItem... funcs);
	void enabledSubFunc(int parentId, int... subs);

	int getNotifyCount();

	String getAPP_PUSH_KEY();

	void setPrefValue(String key, String value);
	String getPrefValue(String key);

}