package com.bitcasa.javalib.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BitcasaError {
	
	public static final String TAG_CODE		= "code";
	public static final String TAG_MESSAGE	= "message";
	
	@JsonProperty(TAG_CODE)
	private int mCode;
	@JsonProperty(TAG_MESSAGE)
	private String mMessage;
	
	public int getCode() {
		return mCode;
	}
	
	public void setCode(int code) {
		this.mCode = code;
	}
	
	public String getMessage() {
		return mMessage;
	}
	
	public void setMessage(String message) {
		this.mMessage = message;
	}

	@Override
	public String toString() {
		return "BitcasaError [mCode=" + mCode + ", mMessage=" + mMessage + "]";
	}
}
