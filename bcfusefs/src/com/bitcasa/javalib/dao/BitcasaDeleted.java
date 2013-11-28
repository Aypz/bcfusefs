package com.bitcasa.javalib.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BitcasaDeleted {

	public static final String TAG_NUM_OBJECTS		= "num_objects";
	public static final String TAG_SIZE				= "size";
	
	@JsonProperty(TAG_NUM_OBJECTS)
	private int mNumerOfDeletedItems;
	@JsonProperty(TAG_SIZE)
	private long mSize;
	@Override
	public String toString() {
		return "BitcasaDeleted [mNumerOfDeletedItems=" + mNumerOfDeletedItems
				+ ", mSize=" + mSize + "]";
	}
}
