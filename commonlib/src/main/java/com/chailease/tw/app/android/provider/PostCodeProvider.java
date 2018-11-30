package com.chailease.tw.app.android.provider;

import android.app.Activity;

import com.chailease.tw.app.android.data.PostCode;
import com.chailease.tw.app.android.utils.Cachable;
import com.chailease.tw.app.android.utils.LogUtility;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import static com.chailease.tw.app.android.constants.SystemConstants.LOG_TAG_COMMONLIB;


/**
 *
 */
public class PostCodeProvider implements Cachable {

	static PostCodeProvider instance;
	static final String DATA_SOURCE = "postcode.json";

	PostCode mPostCode;

	private PostCodeProvider() {}

	public static synchronized PostCodeProvider getInstance(Activity activity) {
		if (null == instance) {
			JSONFileDataProvider dataProvider = new JSONAssetProvider();
			try {
				instance = new PostCodeProvider();
				instance.mPostCode = new PostCode();
				instance.mPostCode.CITYS = dataProvider.getContent(activity, DATA_SOURCE, PostCode.CITY[].class);
			} catch (IOException ioe) {
				LogUtility.error(PostCodeProvider.class, "getInstance", "initial PostCodeProvider failure " + ioe.getMessage(), ioe);
				instance = null;
			} finally {
				dataProvider.refresh(DATA_SOURCE);
				dataProvider.refresh();
				dataProvider = null;
			}
		}
		return instance;
	}

	public PostCode getPostCode() {return mPostCode; }
	public PostCode.CITY[] getCitys() {
		return mPostCode.CITYS;
	}
	public PostCode.REGION[] getRegions(int cityId) {
		for (PostCode.CITY city : mPostCode.CITYS) {
			if (cityId == city.CITYID)
				return city.REGION;
		}
		return null;
	}

	public PostCode.REGION getPostCode(int postCode) {
		for (PostCode.CITY city : mPostCode.CITYS) {
			for (PostCode.REGION region : city.REGION) {
				if (postCode == region.POSTCODE)
					return region;
			}
		}
		return null;
	}
	public PostCode.REGION getPostCode(String cityName, String regionName) {
		for (PostCode.CITY city : mPostCode.CITYS) {
			if (StringUtils.isEmpty(cityName) || cityName.equals(city.CITYNAME))
				for (PostCode.REGION region : city.REGION) {
					if (regionName.equals(region.REGIONNAME))
						return region;
				}
		}
		return null;
	}

	@Override
	public void refresh() {
		if (null != instance) {
			synchronized (instance) {
				if (null != instance) {
					instance.mPostCode = null;
					instance = null;
				}
			}
		}
	}

	@Override
	public void refresh(String key) {}
}