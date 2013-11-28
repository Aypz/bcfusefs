package com.bitcasa.javalib.exception;

import com.bitcasa.javalib.dao.BitcasaError;

public class BitcasaFileSystemException extends BitcasaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BitcasaFileSystemException(BitcasaError error) {
		super(error);
		// TODO Auto-generated constructor stub
	}

	public BitcasaFileSystemException(Exception e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	public BitcasaFileSystemException(int errorCode, String message) {
		super(errorCode, message);
		// TODO Auto-generated constructor stub
	}
}
