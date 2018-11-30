package com.chailease.tw.app.android.apiclient.gson;

import com.chailease.tw.app.android.apiclient.impl.FuncItemTrans;
import com.chailease.tw.app.android.json.gson.GsonTypeAdapter;
import com.google.gson.stream.JsonReader;

/**
 *
 */
public class FuncItemTransTypeAdapter extends GsonTypeAdapter<FuncItemTrans> {

	@Override
	protected Object readObject(Class tClass, String fieldName, JsonReader in) {
		return null;
	}

	@Override
	protected FuncItemTrans newTypeInstance() {
		return new FuncItemTrans();
	}
}