package com.chailease.tw.app.android.apiclient.impl;

import com.chailease.tw.app.android.apiclient.gson.FuncItemTransTypeAdapter;
import com.chailease.tw.app.android.data.FuncItem;
import com.chailease.tw.app.android.json.gson.GsonTypeAdapter;
import com.google.gson.annotations.JsonAdapter;

/**
 *
 */
@JsonAdapter(FuncItemTransTypeAdapter.class)
@GsonTypeAdapter.OverwriteSuper(
		renameField = {"name", "funcId", "parentId", "count"},
		renameTo = {"FUNC_NAME", "FUNC_ID", "PARENT_ID", "ORD_BY"},
		disableSerialize = {"activity", "intent", "enabled", "subFuncs"})
public class FuncItemTrans extends FuncItem {

	public FuncItemTrans() {
		this(null, 0, null);
	}
	public FuncItemTrans(String name, int funcId, Class activity) {
		super(name, funcId, activity);
		setEnabled(true);
	}

}