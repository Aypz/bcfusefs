package com.bitcasa.javalib.exception;

import com.bitcasa.javalib.dao.BitcasaError;

public class BitcasaClientException extends BitcasaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BitcasaClientException(BitcasaError error) {
		super(error);
		// TODO Auto-generated constructor stub
	}

	public BitcasaClientException(String message) {
		super(message);
	}
	
	public BitcasaClientException(Exception e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	public BitcasaClientException(int errorCode, String message) {
		super(errorCode, message);
		// TODO Auto-generated constructor stub
	}

}
