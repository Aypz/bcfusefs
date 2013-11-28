package com.bitcasa.javalib.dao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BitcasaResult {
	
	public static final String TAG_ITEMS		= "items";
	public static final String TAG_DELETED		= "deleted";
	
	@JsonProperty(TAG_ITEMS)
	private List<BitcasaItem> mItems;
	@JsonProperty(TAG_DELETED)
	private BitcasaDeleted mDeleted;
	
	public List<BitcasaItem> getItems() {
		return mItems;
	}
	
	public void setItems(List<BitcasaItem> items) {
		mItems = items;
	}
	
	public BitcasaDeleted getDeletedInfo() {
		return mDeleted;
	}

	@Override
	public String toString() {
		return "BitcasaResult [mItems=" + mItems + ", mDeleted=" + mDeleted
				+ "]";
	}
}
