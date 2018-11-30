package com.chailease.tw.app.android.data;

import android.content.Intent;

import java.util.ArrayList;

public class FuncItem {

	private String name;
	private Class activity;
	private int funcId;
	private int parentId;
	private int count;
	private FuncItem[] subFuncs;
	private Intent intent;
	private boolean enabled = false;

	public FuncItem(String name, int funcId, Class activity) {
		this.name = name;
		this.funcId = funcId;
		this.activity = activity;
	}
	public FuncItem(String name, int funcId, Class activity, Intent intent) {
		this(name, funcId, activity);
		this.intent = intent;
	}
	public FuncItem(String name, int funcId, Class activity, FuncItem[] subFuncs) {
		this(name, funcId, activity, null, subFuncs);
	}
	public FuncItem(String name, int funcId, Class activity, Intent intent, FuncItem[] subFuncs) {
		this(name, funcId, activity, intent);
		this.subFuncs = subFuncs;
		if (subFuncs!=null && subFuncs.length>0)
			for(FuncItem sub : subFuncs)
				sub.setParentId(getFuncId());
	}

	public Class getActivity() {
		return activity;
	}

	public void setActivity(Class activity) {
		this.activity = activity;
	}

	public int getFuncId() {
		return funcId;
	}

	public void setFuncId(int funcId) {
		this.funcId = funcId;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public FuncItem[] getSubFuncs() {
		return subFuncs;
	}
	public FuncItem[] getSubFuncs(boolean enableOnly) {
		if (enableOnly && null!=subFuncs && subFuncs.length>0) {
			ArrayList<FuncItem> enables = new ArrayList<FuncItem>();
			for (FuncItem s : subFuncs)
				if (s.enabled) enables.add(s);
			return enables.toArray(new FuncItem[enables.size()]);
		}
		return subFuncs;
	}

	public void setSubFuncs(FuncItem[] subFuncs) {
		this.subFuncs = subFuncs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Intent getIntent() {
		return intent;
	}
	public void setIntent(Intent intent) {
		if (null == intent) return;
		if (null != this.intent) this.intent.putExtras(intent);
		else this.intent = intent;
	}

	static abstract public class FuncIntent extends Intent {
		public FuncIntent() {
			super();
			initExtra();
		}
		abstract public void initExtra();
	}

	public boolean isEnabled() {
		return enabled && activity!=null;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

}