package com.chailease.tw.app.android.adapter;

import android.database.DataSetObservable;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.List;

/**
 * 輔助 Selection 物件, 複合性 Selector 的控制器
 * 實作類別可定義 T 作為識別關聯 Selector 的依據
 */
public abstract class SelectorContentAgent<T> {

	final protected DataSetObservable mDataSetObservable;
	final HashMap<T, Spinner> mSelectors;
	final protected T[] locked;

	public SelectorContentAgent() {
		mDataSetObservable = new DataSetObservable();
		mSelectors = new HashMap<T, Spinner>();
		this.locked = initLock();
	}

	public DataSetObservable getDataSetObservable() {
		return mDataSetObservable;
	}

	public void notifyChanged() {
		mDataSetObservable.notifyChanged();
	}

	abstract protected void register(T level, List<String> items);
	abstract protected void setSelected(int position);
	abstract protected void refresh(T level, List<String> items);
	abstract public List<String> getItemStringList(T level, int[] idxs);
	abstract protected T[] initLock();

	/**
	 * 註冊關聯 Selector Spinner
	 * @param level
	 * @param selector
	 */
	public void register(T level, Spinner selector, List<String> items) {
		mSelectors.put(level, selector);
		register(level, items);
	}

	protected void setSelected(T level, int position) {
		mSelectors.get(level).setSelection(position);
	}

}