package com.bitcasa.javalib.jsonparser;

import java.io.IOException;
import java.io.InputStream;

import com.bitcasa.javalib.exception.BitcasaException;
import com.bitcasa.javalib.exception.BitcasaJSONException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class BitcasaJSONParser {

	protected final ObjectMapper sObjectMapper = new ObjectMapper();
	
	public BitcasaJSONParser() {
		sObjectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		sObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		sObjectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
	}
	
	public <T> T parseJSONToObject(InputStream in, Class<T> objectClass) throws BitcasaException {
		JsonParser jp = null;
		try {
			JsonFactory jsonFactory = new JsonFactory();
	        jp = jsonFactory.createParser(in);
	        return sObjectMapper.readValue(jp, objectClass);
		} catch (Exception e) {
			throw new BitcasaJSONException("Problem parsing json from server", e);
		} finally {
			try {
				if (jp != null) jp.close();
			} catch (IOException ioe) {
				throw new BitcasaException(ioe);
			}
		}
	}

	public <T> T parseJSONToObject(String json, Class<T> objectClass) throws BitcasaException {
		JsonParser jp = null;
		try {
			JsonFactory jsonFactory = new JsonFactory();
	        jp = jsonFactory.createParser(json);
	        return sObjectMapper.readValue(jp, objectClass);
		} catch (Exception e) {
			throw new BitcasaJSONException("Problem parsing the json from server", e);
		} finally {
			try {
				if (jp != null) jp.close();
			} catch (IOException ioe) {
				throw new BitcasaException(ioe);
			}
		}
	}
}
