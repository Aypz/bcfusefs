package com.bitcasa.javalib.exception;

import com.bitcasa.javalib.dao.BitcasaError;

public class BitcasaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BitcasaError mError;
	
	public BitcasaException(Exception e) {
		super(e);
	}
	
	public BitcasaException(String message) {
		super(message);
	}
	
	public BitcasaException(String message, Exception e) {
		super(message, e);
	}
	
	public BitcasaException(BitcasaError error) {
		super("Error code " + error.getCode() + ". " + error.getMessage());
		mError = error;
	}
	
	public BitcasaException(int errorCode, String message) {
		super("Error code " + errorCode + ". " + message);
		mError = new BitcasaError();
		mError.setCode(errorCode);
		mError.setMessage(message);
	}
	
	public int getErrorCode() {
		return mError != null ? mError.getCode() : -1;
	}
}
