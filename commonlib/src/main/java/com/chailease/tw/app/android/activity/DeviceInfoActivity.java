package com.chailease.tw.app.android.activity;

import android.support.annotation.LayoutRes;
import android.widget.TextView;

import com.chailease.tw.app.android.commonlib.R;
import com.chailease.tw.app.android.core.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 */
public class DeviceInfoActivity extends BaseFullScreenActivity {

	private String[] lines;

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
	}
	@Override
	protected void setSystemServices() {
	}
	@Override
	protected void setListener() {
		super.setListener();
	}
	@Override
	protected @LayoutRes int getActivityLayout() {
		return R.layout.activity_device_info;
	}

	/*************************************************************************
	 *  Active at onStart
	 *      查尋資料庫或是ContentProvider,取得所需資料
	 *      queryDataBase();
	 *      設定各式Adapter元件,將UI與資料做整合程現
	 *      setAdapter();
	 *************************************************************************/
	@Override
	public void queryDataBase() {
		TextView cnt = (TextView) mContentView;
		if (null == lines) {
			String dtl = DeviceInfo.getDeviceDetail();
			try {
				JSONObject dtlJO = new JSONObject(dtl);
				lines = readJSONasStringLines(dtlJO);
			} catch (JSONException e) {
				e.printStackTrace();
				cnt.setText(e.getMessage());
			}
		}
	}
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
		TextView cnt = (TextView) mContentView;
		if (null != lines) {
			StringBuilder sb = new StringBuilder(lines.length*50);
			for (String line : lines)
				sb.append("\n").append(line);
			cnt.setText(sb.toString());
		}
	}

	/*************************************************************************
	 *  Active at onRestart
	 *      處理於onStop中回收的資料,在此處需要重新還原
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

	private String[] readJSONasStringLines(JSONObject jo) throws JSONException {
		ArrayList<String> buff = new ArrayList<String>();
		JSONArray names = jo.names();
		for (int i=0; i<names.length(); i++) {
			String name = names.get(i).toString();
			Object value = jo.get(name);
			if (value instanceof JSONObject) {
				String[] more = readJSONasStringLines((JSONObject) value);
				buff.add(name + " : {");
				Collections.addAll(buff, more);
				buff.add("}");
			} else {
				buff.add(name + " : " + value);
			}
		}
		return buff.toArray(new String[buff.size()]);
	}


}