package com.chailease.tw.app.android.db;

/**
 *
 */
public class PARAM {
	public TableDefinition.DATA_TYPE TYPE;
	public String VALUE;
	public int INT_VALUE;
	public double NUM_VALUE;

	public String[] VALUES = null;
	public int[] INT_VALUES = null;
	public double[] NUM_VALUES = null;

	public boolean isSingleValue() {
		return (VALUES==null && INT_VALUES==null && NUM_VALUES==null);
	}
	public int valueCount() {
		switch(TYPE) {
			case STRING:
				if (VALUES!=null) return VALUES.length;
				break;
			case INTEGER:
				if (INT_VALUES!=null) return INT_VALUES.length;
				break;
			case NUMBER:
				if (NUM_VALUES!=null) return  NUM_VALUES.length;
		}
		return 0;
	}

}