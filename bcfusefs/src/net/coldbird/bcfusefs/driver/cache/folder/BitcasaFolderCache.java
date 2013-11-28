package net.coldbird.bcfusefs.driver.cache.folder;

import java.util.ArrayList;
import java.util.List;

import com.bitcasa.javalib.dao.BitcasaItem;

public class BitcasaFolderCache {

	// Internal Folder Structure Cache
	private final List<BitcasaFolderCacheItem> cache = new ArrayList<BitcasaFolderCacheItem>();

	// Folder Structure Cache Timeout (in nanoseconds)
	private long cacheTimeout = Long.MAX_VALUE;

	/**
	 * Constructor
	 * @param cacheTimeout Folder Structure Cache Timeout (in nanoseconds)
	 */
	public BitcasaFolderCache(final long cacheTimeout) {
		// Save Timeout
		this.cacheTimeout = cacheTimeout;
	}

	/**
	 * Add Folder to Cache
	 * @param item Folder
	 */
	public void add(final BitcasaFolderCacheItem item) {
		// Cache Duplicate Variable
		BitcasaFolderCacheItem existingCache = null;

		// Iterate Cache Content
		for (final BitcasaFolderCacheItem cacheItem : cache) {
			// Found in Cache already
			if (cacheItem.path.equals(item.path)) {
				// Save Reference for Deletion
				existingCache = cacheItem;
			}
		}

		// Remove Duplicate
		if (existingCache != null) {
			cache.remove(existingCache);
		}

		// Set Timestamp
		item.timeStamp = System.nanoTime();

		// Add Folder Content to Cache
		cache.add(item);
	}

	/**
	 * Get Folder from Cache
	 * @param path Path Folder Path
	 * @return Folder Contents (or null if not cached)
	 */
	public List<BitcasaItem> get(final String path) {
		// Outdated Cache
		BitcasaFolderCacheItem outdatedCache = null;

		// Iterate Cache Content
		for (final BitcasaFolderCacheItem cacheItem : cache) {
			// Found in Cache already
			if (cacheItem.path.equals(path)) {
				// Still valid
				if ((System.nanoTime() - cacheItem.timeStamp) < cacheTimeout) {
					// Return Folder Content
					return cacheItem.content;
				}

				// Outdated
				else {
					// Save Reference for Deletion
					outdatedCache = cacheItem;

					// Stop Search
					break;
				}
			}
		}

		// Delete outdated Data
		if (outdatedCache != null) {
			cache.remove(outdatedCache);
		}

		// Not cached
		return null;
	}

}
