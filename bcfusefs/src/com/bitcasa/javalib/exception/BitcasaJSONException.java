package com.bitcasa.javalib.exception;

import com.bitcasa.javalib.dao.BitcasaError;

public class BitcasaJSONException extends BitcasaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BitcasaJSONException(BitcasaError error) {
		super(error);
	}

	public BitcasaJSONException(Exception e) {
		super(e);
	}

	public BitcasaJSONException(String message, Exception e) {
		super(message, e);
	}
	
	public BitcasaJSONException(int errorCode, String message) {
		super(errorCode, message);
	}
}
