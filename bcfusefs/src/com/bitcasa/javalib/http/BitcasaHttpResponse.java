package com.bitcasa.javalib.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.bitcasa.javalib.exception.BitcasaException;

public class BitcasaHttpResponse {

	private int mHttpStatusCode;
	private InputStream mResponseBody;
	private Map<String, List<String>> mHeaders;
	
	public BitcasaHttpResponse(int statusCode, InputStream responseBody, Map<String, List<String>> map) {
		this.mHttpStatusCode = statusCode;
		this.mResponseBody = responseBody;
		this.mHeaders = map;
	}
	
	public int getStatusCode() {
		return mHttpStatusCode;
	}
	
	public InputStream getBody() {
		return mResponseBody;
	}
	
	public List<String> getValuesForHeader(String header) {
		return mHeaders != null ? mHeaders.get(header) : null;
	}
	
	public void finish() throws IOException {
		mResponseBody.close();
	}
	
	public static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	@Override
	public String toString() {
		return "BitcasaHttpResponse [mHttpStatusCode=" + mHttpStatusCode
				+ ", mResponseBody=" + mResponseBody + ", mHeaders=" + mHeaders
				+ "]";
	}
}
