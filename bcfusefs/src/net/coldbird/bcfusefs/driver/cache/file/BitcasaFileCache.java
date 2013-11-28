package net.coldbird.bcfusefs.driver.cache.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bitcasa.javalib.BitcasaClient;
import com.bitcasa.javalib.dao.BitcasaFile;
import com.bitcasa.javalib.exception.BitcasaException;

public class BitcasaFileCache {

	// Internal File Cache
	private final List<BitcasaFileCacheItem> cache = new ArrayList<BitcasaFileCacheItem>();
	
	// Bitcasa Client
	private BitcasaClient client = null;
	
	/**
	 * Constructor
	 * @param client Bitcasa Client
	 */
	public BitcasaFileCache(final BitcasaClient client) {
		// Save Bitcasa Client
		this.client = client;
	}
	
	/**
	 * Reads a Bitcasa File using a Pre-Buffer algorithm to smooth out load-spikes (useful for Movie-Watching)
	 * @param item Bitcasa File
	 * @param offset File Offset
	 * @param size Chunk Length
	 * @return Chunk
	 * @throws IOException when stream breaks
	 * @throws BitcasaException when API limitations strike
	 */
	public byte[] read(BitcasaFile item, long offset, long size) throws IOException, BitcasaException {
		// TODO Implement a caching algorithm that makes sense...
		
		// Download Data from Bitcasa
		byte[] cache = client.downloadFileRange(item, offset, size);
		
		// Return Data
		return cache;
	}
	
}
