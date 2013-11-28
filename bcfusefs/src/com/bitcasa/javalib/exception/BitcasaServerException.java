package com.bitcasa.javalib.exception;

import com.bitcasa.javalib.dao.BitcasaError;

public class BitcasaServerException extends BitcasaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BitcasaServerException(BitcasaError error) {
		super(error);
		// TODO Auto-generated constructor stub
	}

}
