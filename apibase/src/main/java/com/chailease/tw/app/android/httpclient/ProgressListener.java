package com.chailease.tw.app.android.httpclient;

public interface ProgressListener {

	public static final int PROGRESS_FAIL	=	-99;

	/**
	 * To get the max progressing value
	 * @return
	 */
	public int getMaxProgress();

	/**
	 * to get current progressing value
	 * @return
	 */
	public int getProgress();

	/**
	 * to set current progressing value
	 * @param progress
	 */
	public void setProgress(int progress);

	/**
	 * if current progressing value equals max progressing value then return true or setDone already be called by true
	 * some how may be failure when the progressing value equals  #PROGRESS_FAIL
	 * @return
	 */
	public boolean isDone();

	/**
	 * to set the progressing already done.
	 * @param done
	 */
	public void setDone(boolean done);

	/**
	 *  when the progressing value has be changed and push for notify
	 */
	public void pushProgress();

}