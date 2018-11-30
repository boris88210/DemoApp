package com.chailease.tw.app.android.db;

/**
 *
 */
public interface DataBean {

	void migration(int oldVersion, int newVersion);

}