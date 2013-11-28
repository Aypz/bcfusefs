package com.bitcasa.javalib.http;

import java.io.OutputStream;

import com.bitcasa.javalib.dao.BitcasaFile;

public class Downloader {

	private BitcasaFile mFile;
	private String mDestination;
	private OutputStream mTarget;
	private ProgressTracker mProgressTracker;
	
	public Downloader(BitcasaFile file, String destination) {
		mFile = file;
		mDestination = destination;
	}
	
	public Downloader(BitcasaFile file, OutputStream os) {
		mFile = file;
		mTarget = os;
	}
	
	public Downloader(BitcasaFile file) {
		mFile = file;
	}
	
	public BitcasaFile getBitcasaFile() {
		return mFile;
	}
	
	public String getDestination() {
		return mDestination;
	}
	
	public OutputStream getTargetOutputStream() {
		return mTarget;
	}
	
	public void setProgressTracker(ProgressTracker pt) {
		if (pt != null)
			mProgressTracker = pt;
	}
	
	public ProgressTracker getProgressTracker() {
		return mProgressTracker;
	}
}
