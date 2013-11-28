package net.coldbird.bcfusefs.driver.cache.file;

import com.bitcasa.javalib.dao.BitcasaFile;

public class BitcasaFileCacheItem {

	/**
	 * Constructor
	 * @param file Bitcasa File
	 */
	public BitcasaFileCacheItem(BitcasaFile file) {
		// Save Bitcasa File Information
		this.file = file;
	}
	
	// Bitcasa File
	public BitcasaFile file = null;
	
	// Cached Byte Chunk
	public byte[] cache = null;
	public long cachePosition = 0;

}
