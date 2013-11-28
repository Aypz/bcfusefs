package com.bitcasa.javalib.exception;

import com.bitcasa.javalib.dao.BitcasaError;

public class BitcasaAccountException extends BitcasaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BitcasaAccountException(BitcasaError error) {
		super(error);
		// TODO Auto-generated constructor stub
	}

	public BitcasaAccountException(Exception e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	public BitcasaAccountException(int errorCode, String message) {
		super(errorCode, message);
		// TODO Auto-generated constructor stub
	}
}
