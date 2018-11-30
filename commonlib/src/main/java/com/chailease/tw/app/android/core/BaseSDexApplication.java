package com.chailease.tw.app.android.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.chailease.tw.app.android.data.FuncItem;
import com.chailease.tw.app.android.data.UserProfile;
import com.chailease.tw.app.android.utils.LogUtility;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *      String mAPI_HOST, mUPLOAD_HOST, mSECU_HOST 在專案中得定
 */
public class BaseSDexApplication extends Application implements IBaseApplication {

	private static BaseSDexApplication instance;

	protected String mAPP_PUSH_KEY;

	protected FuncItem[] userFuncs;
	protected FuncItem[] funcs;
	protected int mNotifyCount;
	protected UserProfile mUsrPrfl = null;
	protected LinkedList<Activity> mActivityList;

	public static BaseSDexApplication getInstance(Context context) {
		if (null == instance) {
			instance = (BaseSDexApplication) context.getApplicationContext();
		}
		return instance;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		this.registerActivityLifecycleCallbacks(this);
		init();
	}

	public synchronized void init() {
		if (null == mActivityList) {
			mActivityList = new LinkedList<Activity>();
		}
	}
	//用來關掉所有被管理的Activity的方法
	public void closeAllActivity() {
		closeAllActivity(true);
	}
	public void closeAllActivity(boolean endApp) {
		if (!endApp) {
			Activity last = this.mActivityList.removeLast();
			BaseApplicationAgent.closeAllActivity(this, mActivityList);
			this.mActivityList.add(last);
		} else {
			BaseApplicationAgent.closeAllActivity(this, mActivityList);
			System.exit(0);
		}
	}
	public UserProfile getUserProfile() {
		if (null == mUsrPrfl) {
			mUsrPrfl = BaseApplicationAgent.getUserProfile(this);
		}
		return mUsrPrfl;
	}
	public void updateUserProfile(UserProfile usr) {
		UserProfile nUsr = BaseApplicationAgent.updateUserProfile(this, usr);
		if (null != nUsr) {
			mUsrPrfl = nUsr;
		}
	}

	public void logout() {
		BaseApplicationAgent.logout(this);
		mUsrPrfl = null;
		userFuncs = null;
	}

	@Override
	public FuncItem[] getUserFuncs() {
		return getUserFuncs(true);
	}
	@Override
	public FuncItem[] getUserFuncs(boolean enabledOnly) {
		if (enabledOnly)
			return userFuncs;
		else return funcs;
	}
	@Override
	public FuncItem getFuncItem(int funcId) {
		return null;
	}

	@Override
	public int getNotifyCount() { return mNotifyCount; }

	@Override
	public void setEnabledFunc(int... funcIds) {
		if (null != funcIds) {
			ArrayList<FuncItem> fs = new ArrayList<FuncItem>();
			for (int funcId : funcIds) {
				FuncItem func = getFuncItem(funcId);
				if (null != func.getSubFuncs())
					for (FuncItem sf : func.getSubFuncs()) sf.setEnabled(false);
				fs.add(func);
			}
			userFuncs = fs.toArray(new FuncItem[fs.size()]);
		} else {
			userFuncs = new FuncItem[0];
		}
	}
	@Override
	public void setEnabledFunc(FuncItem... funcs) {
		if (null != funcs && funcs.length>0) {
			ArrayList<FuncItem> fs = new ArrayList<FuncItem>();
			for (FuncItem ckdFunc : funcs) {
				FuncItem func = getFuncItem(ckdFunc.getFuncId());
				if (null != func) {
					func.setEnabled(true);
					if (null != func.getSubFuncs()) {   //  has sub func
						for (FuncItem sf : func.getSubFuncs()) {
							sf.setEnabled(false);   //  by default disable sub func until find privilege
							//Log.d(LOG_TAG, "sub func " + sf.getName() + " " + sf.getFuncId() + " as disable ");
							for (FuncItem ckdSf : ckdFunc.getSubFuncs()) {
								//Log.d(LOG_TAG, "ckd-func > " + ckdSf.getName() + " " + ckdSf.getFuncId() + " " + ckdSf.isEnabled());
								if (ckdSf.getFuncId() == sf.getFuncId()) {  //  find privilege turn on sub function
									sf.setEnabled(true);
									//Log.d(LOG_TAG, " sub func " + sf.getName() + " enable > " + sf.isEnabled());
									break;
								}
							}
						}
					}
					fs.add(func);
				} else {
					LogUtility.warn(this, "setEnabledFunc", "not supported function " + ckdFunc.getFuncId());
				}
			}
			userFuncs = fs.toArray(new FuncItem[fs.size()]);
		} else {
			userFuncs = new FuncItem[0];
		}
	}
	@Override
	public void enabledSubFunc(int parentId, int... subs) {
		if (parentId==0 || null==subs || subs.length==0) return;
		for (FuncItem func : userFuncs) {
			if (func.getFuncId() == parentId) {
				for(FuncItem sub : func.getSubFuncs()) {
					for (int subid : subs) {
						if (sub.getFuncId() == subid) sub.setEnabled(true);
					}
				}
			}
		}
	}
	public FuncItem getUserFunc(int funcId) {
		return findFunc(funcId, userFuncs);
	}
	protected FuncItem findFunc(int funcId, FuncItem... funcs) {
		if (funcs!=null && funcs.length>0)
			for (FuncItem func : funcs)
				if (func.getFuncId() == funcId) return func;
				else if (func.getSubFuncs()!=null && func.getSubFuncs().length>0) {
					FuncItem found = null;
					if((found = findFunc(funcId, func.getSubFuncs())) !=null)
						return found;
				}
		return null;
	}

	protected String getResourceString(int resc) {
		return getResources().getString(resc);
	}

	@Override
	public String getAPP_PUSH_KEY() {
		if (StringUtils.isBlank(mAPP_PUSH_KEY)) {
			mAPP_PUSH_KEY = BaseApplicationAgent.getAPP_PUSH_KEY();
		}
		return mAPP_PUSH_KEY;
	}

	@Override
	public void setPrefValue(String key, String value) {
		BaseApplicationAgent.setPrefValue(this, key, value);
	}

	@Override
	public String getPrefValue(String key) {
		return BaseApplicationAgent.getPrefValue(this, key);
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle bundle) {
		init();
		mActivityList.add(activity);
	}

	@Override
	public void onActivityStarted(Activity activity) {}

	@Override
	public void onActivityResumed(Activity activity) {}

	@Override
	public void onActivityPaused(Activity activity) {}

	@Override
	public void onActivityStopped(Activity activity) {}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

	@Override
	public void onActivityDestroyed(Activity activity) {
		synchronized (this) {
			mActivityList.remove(activity);
		}
	}

}