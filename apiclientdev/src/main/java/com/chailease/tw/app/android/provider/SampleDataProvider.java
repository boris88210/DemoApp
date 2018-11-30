package com.chailease.tw.app.android.provider;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.chailease.tw.app.android.utils.Cachable;
import com.chailease.tw.app.android.utils.LogUtility;

import java.io.IOException;

import static com.chailease.tw.app.android.apiclient.ApiClientFactory.LOG_TAG;

/**
 *
 */
public class SampleDataProvider implements Cachable {

	private static SampleDataProvider instance;

	JSONAssetProvider mDataProvider;

	public static synchronized SampleDataProvider getInstance() {
		if (null == instance) {
			instance = new SampleDataProvider();
			instance.mDataProvider = new JSONAssetProvider();
			Log.d(LOG_TAG, "checkout IndustryProvider:>>" + instance.mDataProvider
					+ "\n size:>> " + instance.mDataProvider);
		}
		return instance;
	}
	protected String sampleRoot() {
		return "sample_json/";
	}

	public <T> T getSampleInstance(Activity activity, String source, Class<T> type) {
		return getSampleInstance(activity.getApplicationContext(), source, type);
	}
	public <T> T getSampleInstance(Context context, String source, Class<T> type) {
		if (!source.startsWith(sampleRoot()))
			source = sampleRoot() + source;
		try {
			return mDataProvider.getContent(context, source, type);
		} catch (IOException e) {
			Log.e(LOG_TAG, "get data from source " + source + " failure! ", e);
			LogUtility.error(this, "getSampleInstance", "failure of source " + source, e);
			return null;
		}
	}

	@Override
	public void refresh() {
		mDataProvider.refresh();
	}

	@Override
	public void refresh(String key) {
		key = sampleRoot() + key;
		mDataProvider.refresh(key);
	}
}