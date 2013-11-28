package com.bitcasa.javalib.http;

import java.io.File;

import com.bitcasa.javalib.dao.BitcasaFolder;
import com.bitcasa.javalib.http.BitcasaHttpRequestor.FileExistsOperation;

public class Uploader {
	
	private BitcasaFolder mParentFolder;
	private String mFilePath;
	private File mFile;
	private ProgressTracker mProgressTracker;
	private FileExistsOperation mFileExists = FileExistsOperation.RENAME;
	
	public Uploader(BitcasaFolder parentFolder, String filePath) {
		mParentFolder = parentFolder;
		mFilePath = filePath;
	}
	
	public Uploader(BitcasaFolder parentFolder, File fileToUpload) {
		mParentFolder = parentFolder;
		mFile = fileToUpload;
	}
	
	public BitcasaFolder getParentFolder() {
		return mParentFolder;
	}
	
	public String getFilePath() {
		return mFilePath;
	}
	
	public File getFileToUpload() {
		return mFile;
	}
	
	public void setProgressTracker(ProgressTracker pt) {
		if (pt != null)
			mProgressTracker = pt;
	}
	
	public ProgressTracker getProgressTracker() {
		return mProgressTracker;
	}
	
	public void ifFileExistsDo(FileExistsOperation operation) {
		mFileExists = operation;
	}
	
	/**
	 * Gets the action that server will take if a file with the same name
	 * already exists in the provided folder.
	 * @return
	 */
	public FileExistsOperation getFileExistsOperation() {
		return mFileExists;
	}
}
