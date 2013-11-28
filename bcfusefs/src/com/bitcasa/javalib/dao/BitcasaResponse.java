package com.bitcasa.javalib.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BitcasaResponse {

	public static final String TAG_ERROR		= "error";
	public static final String TAG_RESULT		= "result";
	
	@JsonProperty("error")
	private BitcasaError mError;
	@JsonProperty("result")
	private BitcasaResult mResult;
	
	public BitcasaError getError() {
		return mError;
	}
	
	public void setError(BitcasaError error) {
		mError = error;
	}
	
	public BitcasaResult getResult() {
		return mResult;
	}

	public void setResult(BitcasaResult result) {
		mResult = result;
	}
	
	@Override
	public String toString() {
		return "BitcasaResponse [mError=" + mError + ", mResult=" + mResult
				+ "]";
	}
}
