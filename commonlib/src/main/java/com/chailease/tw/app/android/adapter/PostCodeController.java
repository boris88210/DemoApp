package com.chailease.tw.app.android.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.chailease.tw.app.android.data.PostCode;
import com.chailease.tw.app.android.provider.PostCodeProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 郵遞區號下拉選單內容控制器
 */
public abstract class PostCodeController extends ArrayAdapter<String> implements AdapterView.OnItemSelectedListener {

	public enum POST_LEVEL {
		CITY, REGION
	}

	protected Context context;
	protected POST mPost;
	protected POST_LEVEL mLevel;
	protected List<String> mItems;

	protected PostCodeController(Context context, int resource, List<String> items) {
		super(context, resource, items);
		this.context = context;
		this.mItems = items;
	}
	public PostCodeController(Context context, int resource, List<String> items, POST post, POST_LEVEL level) {
		this(context, resource, items);
		this.mPost = post;
		this.mLevel = level;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver dataSetObserver) {
		//Log.d("CLH_DEBUG", this + "::registerDataSetObserver >>" + dataSetObserver);
		mPost.getDataSetObservable().registerObserver(dataSetObserver);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
		//Log.d("CLH_DEBUG", this + "::unregisterDataSetObserver >>" + dataSetObserver);
		mPost.getDataSetObservable().unregisterObserver(dataSetObserver);
	}

	abstract public void doSelectedDetail(POST post);

	/**
	 * OnItemSelectedListener 用於取得目前被選取的項目資訊
	 * @param adapterView
	 * @param view
	 * @param position
	 * @param id
	 */
	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
		switch (mLevel) {
			case CITY:
				mPost.setIdxCity(position);
				break;
			case REGION:
				mPost.setSelected(position);
				break;
		}
		doSelectedDetail(mPost);
		mPost.notifyChanged();
	}

	/**
	 * OnItemSelectedListener 用於還原無項目被選取時需做的反應
	 * @param adapterView
	 */
	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {}

	public static class POST extends SelectorContentAgent<POST_LEVEL> {

		int idxCity;
		List<String> cities;
		List<String> regions;
		PostCode.CITY[] postCities;
		PostCode.REGION selected;
		private POST_LEVEL level;

		public POST(PostCode postCode) {
			super();
			postCities = postCode.CITYS;
			idxCity = 0;
		}
		@Override
		protected POST_LEVEL[] initLock() {
			return new POST_LEVEL[1];
		}

		@Override
		protected void register(POST_LEVEL level, List<String> items) {
			if (level == POST_LEVEL.CITY) return;
			items.clear();
			switch (level) {
				case REGION:
					regions = items; return;
				case CITY:
					cities = items; return;
			}
		}

		public PostCode.CITY[] getCities() {
			return postCities;
		}
		public PostCode.REGION[] getCurrentRegions() {
			return postCities[idxCity].REGION;
		}

		public void setIdxCity(int idxCity) {
			this.idxCity = idxCity;
			//setSelected(POST_LEVEL.CITY, idxCity);
			refresh(POST_LEVEL.REGION, regions);
			if (locked[0]==null || locked[0]==POST_LEVEL.CITY) {
				locked[0] = null;
				setSelected(0);
			}
		}
		public void setIdxCity(PostCode.REGION region) {
			for (int idx=0; idx<postCities.length; idx++) {
				if (postCities[idx].CITYID == region.CITYID) {
					setSelected(POST_LEVEL.CITY, idxCity);
					return;
				}
			}
		}
		public PostCode.REGION getSelected() {
			return this.selected;
		}

		@Override
		protected void setSelected(int position) {
			PostCode.REGION selected = getCurrentRegions()[position];
			if (locked[0]==null || selected.POSTCODE == this.selected.POSTCODE) {
				this.selected = selected;
				locked[0] = null;
			}
		}
		@Override
		protected void refresh(POST_LEVEL level, List<String> items) {
			items.clear();
			switch (level) {
				case REGION:
					for (PostCode.REGION region : getCurrentRegions())
						items.add(region.REGIONNAME);
					break;
				case CITY:
					for (PostCode.CITY city : postCities)
						items.add(city.CITYNAME);
					break;
			}
		}
		@Override
		public List<String> getItemStringList(POST_LEVEL level, int[] idxs) {
			ArrayList<String> items = new ArrayList<String>();
			switch (level) {
				case CITY:
					for (PostCode.CITY city : postCities)
						items.add(city.CITYNAME);
					return items;
				case REGION:
					for (PostCode.REGION region : postCities[idxs[0]].REGION)
						items.add(region.REGIONNAME);
					return items;
			}
			return items;
		}
		public int setRegionAsSelected(String postCode) {
			PostCode.REGION selected = PostCodeProvider.getInstance(null).getPostCode(Integer.parseInt(postCode.trim()));
			for (int i=0; i<postCities.length; i++) {
				if (postCities[i].CITYID == selected.CITYID) {
					setSelected(POST_LEVEL.CITY, i);
					for (int j=0; j<postCities[i].REGION.length; j++) {
						//Log.d("CLH_DEBUG", this + "::setRegionAsSelected >> looping region." + j + "=" + postCities[i].REGION[j].POSTCODE + " " + postCities[i].REGION[j].REGIONNAME);
						if (postCities[i].REGION[j].POSTCODE == selected.POSTCODE) {
							locked[0] = POST_LEVEL.REGION;  //  to lock level for tracing to region
							this.selected = selected;
							setSelected(POST_LEVEL.REGION, j);
							return selected.POSTCODE;
						}
					}
				}
			}
			return 0;
		}
	}
}