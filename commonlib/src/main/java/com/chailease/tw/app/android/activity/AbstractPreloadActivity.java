package com.chailease.tw.app.android.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.chailease.tw.app.android.commonlib.R;
import com.chailease.tw.app.android.core.IBaseApplication;
import com.chailease.tw.app.android.data.FuncItem;
import com.chailease.tw.app.android.data.UserProfile;
import com.chailease.tw.app.android.utils.LogUtility;
import com.chailease.tw.app.android.utils.Logger;
import com.chailease.tw.app.android.utils.PermissionChecker;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 *  LAUNCHER Activity
 *  Task: APP 初始化載入頁
 *      判斷登入帳號、
 *      取得功能權限、
 *      選單資料更新檢查、
 *      開啟登入頁
 */
public abstract class AbstractPreloadActivity extends BaseFullScreenActivity {

	protected String strVerCode;
	protected String strVerNum;

	protected Logger mLog = new Logger(this.getClass(), "");
	protected IBaseApplication mApp;
	protected UserProfile mUser;
	protected final int PROGRESS_MAX = 100;
	protected final int PROGRESS_START = 1;

	/*************************************************************************
	 * Active at onCreate
	 * 	初始化資料,包含從其他Activity傳來的Bundle資料 ,Preference資枓
	 * 	initData();
	 * 	設置必要的系統服務元件如: Services、BroadcastReceiver
	 * 	setSystemServices();
	 * 	設置View元件對應的linstener事件,讓UI可以與用戶產生互動
	 * 	setLinstener();
	 *************************************************************************/
	@Override
	protected void initData() {
		super.initData();
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
			strVerCode = String.valueOf(pInfo.versionCode);
			strVerNum = pInfo.versionName;
			mApp = (IBaseApplication) getApplication();
			if (null != mApp.getUserProfile()) {
				mUser = mApp.getUserProfile();
			}
		} catch (PackageManager.NameNotFoundException e) {
			mLog.e("initData", "load package info failure " + e.getMessage(), e);
			LogUtility.error(this, "initData", "load package info failure " + e.getMessage(), e);
		}
	}
	@Override
	protected void setViewWithDelayHideTouchListener() {
	}
	/*************************************************************************
	 *  Active at onStart
	 *      查尋資料庫或是ContentProvider,取得所需資料
	 *      queryDataBase();
	 *      設定各式Adapter元件,將UI與資料做整合程現
	 *      setAdapter();
	 *************************************************************************/
	@Override
	protected void setAdapter() {
	}

	/*************************************************************************
	 *  Active at onResume
	 *      依據初始化得到的資料,若有些View元件需要重新繪制或填上預設資料,則在此處理
	 *      renderUI();
	 **************************************************************************/
	@Override
	protected void renderUI() {
		//  先檢查 Dangerous Permission 是否已取得, 若無需要求提供
		String[] dps = getDangerousPermission4Check();
		boolean[] ps = null;
		if (dps!=null)
			ps = PermissionChecker.checkPermissions(this, dps);
		ArrayList<String> needRequestPermission = new ArrayList<String>();
		for (int i=0; i<dps.length; i++) {
			boolean p = ps[i];
			if (!p) needRequestPermission.add(dps[i]);
		}
		if (needRequestPermission.size()>0) {
			//  TODO: maybe request permission first
		}

		goalChecking();
	}
	protected abstract String[] getDangerousPermission4Check();
	protected abstract void goalChecking();

	/*************************************************************************
	 *  Active at onRestart
	 *      處理於 onRestart 中回收的資料,在此處需要重新還原
	 *      restoreObject();
	 **************************************************************************/
	@Override
	protected void restoreObject() {
	}

	/*************************************************************************
	 *  Active at onStop
	 *      做必要的資料儲存,如存放在SharePreference或是SQL DataBase
	 *      saveData();
	 *      釋放DataBase相關的物件,如Cursor
	 *      releaseObject();
	 **************************************************************************/
	@Override
	protected void saveData() {
	}
	@Override
	protected void releaseObject() {
	}

	@Override
	public void onBackPressed() {
		// if onBackPressed at this Activity, that mean close this APP, interrupt loading process?
//		DialogUtils.genAlertDialogBuilder(this, DialogConstants.DIALOG_TYPE.CONFIRM,
//				getString(R.string.dialog_title_exit), getString(R.string.dialog_confirm_msg_exit, getString(R.string.app_name)),
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialogInterface, int i) {
//						endOfAppp();
//						mApp.closeAllActivity();
//					}
//				}, BUTTON_STRID_DEFAULT, null, BUTTON_STRID_NONEED, null, BUTTON_STRID_NONEED).show();
		confirmEndOfAppByOnBackPressed();
	}
	protected abstract void confirmEndOfAppByOnBackPressed();

	/*************************************************************************
	 *  商業邏輯
	 **************************************************************************/
	/**
	 * 透過 API 取得使用者功能權限清單
	 * 如果可取得清單, 則可進入首頁 - true
	 * 無法取得清單, 則提示訊息後關閉APP -false
	 */
	protected boolean loadUserFuncs(UserProfile usr) {
		if (null == usr) return false;
		return true;
	}
	protected void finishUserPrivFuncs(FuncItem[] funcs) {
		if (null == funcs || funcs.length==0) {
			mApp.setEnabledFunc(new FuncItem[0]);
			return;
		}
		ArrayList<FuncItem> mainFuncs = new ArrayList<FuncItem>();
		FuncItem mainFunc = null;
		ArrayList<FuncItem> subFuncs = null;
		for (FuncItem func : funcs) {
			if (null == mainFunc || mainFunc.getFuncId()!=func.getParentId()) {
				//  another main-func
				if (null != subFuncs) {
					// setup main func
					mainFunc.setSubFuncs(subFuncs!=null ? subFuncs.toArray(new FuncItem[subFuncs.size()]) : null);
					mainFuncs.add(mainFunc);
				}
				mainFunc = func;
				subFuncs = new ArrayList<FuncItem>();
			} else {
				subFuncs.add(func);
			}
		}
		mainFunc.setSubFuncs(subFuncs!=null ? subFuncs.toArray(new FuncItem[subFuncs.size()]) : null);
		mainFuncs.add(mainFunc);
		mApp.setEnabledFunc(mainFuncs.toArray(new FuncItem[mainFuncs.size()]));
	}

	/**
	 * 關閉 APP
	 */
	protected void endOfAppp() {
		// TODO: APP 關閉時要確定停止APP, 可以關閉APP的 Activity 有2, MainActivity 及 HomeActivity or Logout 功能;
	}
	/**
	 *  依據 Intent 內容開啟對應 Activity
	 *  支援 一般開啟, 推播開啟
	 */
	protected void readyToGoGoal() {
		if (null!=mUser) {
			FuncItem func = null;
			Intent req = getIntent();
			if (null != req) {
				//  支援推播訊息開啟或是APP被喚醒
				int funcId = req.getIntExtra(IBaseApplication.INTENT_EXTRA_FUNC_ID, 0);
				if (0 == funcId && !StringUtils.isBlank(req.getStringExtra(IBaseApplication.INTENT_EXTRA_FUNC_ID))) {
					funcId = Integer.parseInt(req.getStringExtra(IBaseApplication.INTENT_EXTRA_FUNC_ID));
				}
				func = mApp.getFuncItem(funcId);
			}
			if (null != func && null != func.getActivity()) {
				Intent intent = new Intent(this, func.getActivity());
				intent.putExtras(req);
				startActivity(intent);
			} else {
				/*
					無轉向功能, 轉入預設頁
				 */
				Intent intent = getDefaultActivityIntent(); //new Intent(this, AlbumListActivity.class); //  HomeActivity
				startActivity(intent);
			}
		} else {
			Intent intent = getLoginActivityIntent();   //new Intent(this, LoginActivity.class); // LoginActivity
			startActivity(intent);
		}
		finish();   //  close this Activity, do not put it into stack
	}
	abstract protected Intent getDefaultActivityIntent();
	abstract protected Intent getLoginActivityIntent();

}