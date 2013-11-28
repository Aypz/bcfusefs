package com.bitcasa.javalib.http;

import com.bitcasa.javalib.dao.BitcasaItem;

public abstract class ProgressTracker {
	private long mFileSize;
	
	/**
	 * @return
	 */
	public long getFileSize() {
		return mFileSize;
	}
	
	public void setFileSize(long size) {
		mFileSize = size;
	}
	
	/**
	 * This function will be called with the progress of the current
	 * operation(upload/download)
	 * @param percentage
	 */
	public abstract void progressUpdate(int percentage);
	/**
	 * This function will be called with the upload/download item
	 * when the operation is completed
	 * @param item
	 */
	public abstract void progressComplete(BitcasaItem item);
}
