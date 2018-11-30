package com.chailease.tw.app.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chailease.tw.app.android.utils.LogUtility;
import com.chailease.tw.app.android.utils.NetworkStateKeeper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 *
 */
public abstract class AbstractBaseActivity extends AppCompatActivity implements NetworkStateKeeper.NetworkStateChangeListener {

	static public final String LOG_LIFECYCLE  =   "LIFE_CYCLE";

	protected boolean dependNetwork = true;
	protected NetworkStateKeeper mNetworkKeeper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtility.debug(this, "onCreate", " -- begin");
		super.onCreate(savedInstanceState);
		setContentView(savedInstanceState);
		loadArguments(savedInstanceState);
		bindView();
		onCreate();
		LogUtility.debug(this, "onCreate", " -- end");
	}
	protected void bindView() {
		try {
			// the package reference of ButterKnife moving to project, removing the library butterknife-7.0.1.jar from libs
			Class butterKnife = Class.forName("butterknife.ButterKnife");
			butterKnife.getMethod("bind", Activity.class).invoke(null, this);
		} catch (ClassNotFoundException|NoSuchMethodException|InvocationTargetException|IllegalAccessException e) {
			throw new IllegalStateException("ButterKnife not found! need include this package for APP!", e);
		}
	}
	protected void onCreate() {
		/* 初始化資料,包含從其他Activity傳來的Bundle資料 ,Preference資枓 */
		initData();
		/* 設置必要的系統服務元件如: Services、BroadcastReceiver */
		setSystemServices();
		/* 設置View元件對應的linstener事件,讓UI可以與用戶產生互動 */
		setLinstener();
		setListener();
		mNetworkKeeper = new NetworkStateKeeper();
		mNetworkKeeper.isOnline(this);
	}
	abstract protected void setContentView(Bundle savedInstanceState);

	@Override
	protected void onStart() {
		LogUtility.debug(this, "onStart", " -- begin");
        /* 查尋資料庫或是ContentProvider,取得所需資料 */
		queryDataBase();
		/* 設定各式Adapter元件,將UI與資料做整合程現 */
		setAdapter();

		if (dependNetwork)
			NetworkStateKeeper.registerListener(this);
		super.onStart();
		LogUtility.debug(this, "onStart", " -- end");
	}
	@Override
	protected void onResume() {
		/* 依據初始化得到的資料,若有些View元件需要重新繪制或填上預設資料,則在此處理 */
		renderUI();
		//  Activity show on top than check the network state
		super.onResume();
	}
	@Override
	protected void onPause() {
		//  Activity hide than remove from network state keeper
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// 處理於onStop中回收的資料,在此處需要重新還原
		restoreObject();
		super.onRestart();
	}

	@Override
	protected void onStop() {
		/* 做必要的資料儲存,如存放在SharePreference或是SQL DataBase */
		saveData();
		/* 釋放DataBase相關的物件,如Cursor */
		releaseObject();
		if (dependNetwork)
			NetworkStateKeeper.unregisterListener(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	/**
	 *  activated when onCreate
	 *   初始化資料,包含從其他Activity傳來的Bundle資料 ,Preference資枓
	 */
	abstract protected void initData();
	/**
	 *  activated when onCreate
	 *  設置必要的系統服務元件如: Services、BroadcastReceiver
	 */
	abstract protected void setSystemServices();
	/**
	 *  activated when onCreate
	 *  設置View元件對應的linstener事件,讓UI可以與用戶產生互動
	 */
	@Deprecated
	abstract protected void setLinstener();

	abstract protected void setListener();
	/**
	 *  activated when onStart()
	 *  查尋資料庫或是ContentProvider,取得所需資料
	 */
	abstract protected void queryDataBase();
	/**
	 *  activated when onStart()
	 *  設定各式Adapter元件,將UI與資料做整合程現
	 */
	abstract protected void setAdapter();

	/**
	 *  activated when onResume()
	 *  依據初始化得到的資料,若有些View元件需要重新繪制或填上預設資料,則在此處理
	 */
	abstract protected void renderUI();

	/**
	 *  activated when onRestart()
	 *   處理於onStop中回收的資料,在此處需要重新還原
	 */
	abstract protected void restoreObject();

	/**
	 *  activated when onStop()
	 *  做必要的資料儲存,如存放在SharePreference或是SQL DataBase
	 */
	abstract protected void saveData();
	/**
	 *  activated when onStop()
	 *  釋放DataBase相關的物件,如Cursor
	 */
	abstract protected void releaseObject();

	protected void loadArguments(Bundle savedInstanceState) {}

	protected String getStringArgValue(Bundle savedInstanceState, String argName) {
		return getStringArgValue(savedInstanceState, argName, null);
	}
	protected String getStringArgValue(Bundle savedInstanceState, String argName, String def) {
		if (null != savedInstanceState && savedInstanceState.containsKey(argName)) {
			return savedInstanceState.getString(argName, def);
		}
		return null;
	}
	protected String[] getStrArrayArgValue(Bundle savedInstanceState, String argName) {
		if (null != savedInstanceState && savedInstanceState.containsKey(argName)) {
			return savedInstanceState.getStringArray(argName);
		}
		return null;
	}
	protected ArrayList<String> getStrArrayListArgValue(Bundle savedInstanceState, String argName) {
		if (null != savedInstanceState && savedInstanceState.containsKey(argName)) {
			return savedInstanceState.getStringArrayList(argName);
		}
		return null;
	}
	protected int getIntArgValue(Bundle savedInstanceState, String argName) {
		if (null != savedInstanceState && savedInstanceState.containsKey(argName)) {
			return savedInstanceState.getInt(argName);
		}
		return -1;
	}
	protected int[] getIntArrayArgValue(Bundle savedInstanceState, String argName) {
		if (null != savedInstanceState && savedInstanceState.containsKey(argName)) {
			return savedInstanceState.getIntArray(argName);
		}
		return null;
	}
	protected ArrayList<Integer> getIntArrayListArgValue(Bundle savedInstanceState, String argName) {
		if (null != savedInstanceState && savedInstanceState.containsKey(argName)) {
			return savedInstanceState.getIntegerArrayList(argName);
		}
		return null;
	}

	/*************************************************************************
	 * 網路狀態檢查
	 *************************************************************************/
	protected boolean isNetworkAvailable() {
		return mNetworkKeeper.isOnline();
	}
	@Override
	public void notifyAvailable() {
		//  TODO:   Good for work, if the screen already be block than release screen
	}
	@Override
	public void notifyUnavailable() {
		//  TODO:   No Good for work, block screen and show message for user to wait or exit APP
	}

}