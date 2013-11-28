package com.bitcasa.javalib.exception;

import com.bitcasa.javalib.dao.BitcasaError;

public class BitcasaFileException extends BitcasaException {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BitcasaFileException(BitcasaError error) {
		super(error);
		// TODO Auto-generated constructor stub
	}

	public BitcasaFileException(Exception e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	public BitcasaFileException(int errorCode, String message) {
		super(errorCode, message);
		// TODO Auto-generated constructor stub
	}
}
