package net.coldbird.bcfusefs.driver.cache.folder;

import java.util.List;

import com.bitcasa.javalib.dao.BitcasaItem;

public class BitcasaFolderCacheItem {

	// Bitcasa Folder Contents
	public List<BitcasaItem> content = null;

	// Bitcasa Folder Path
	public String path = null;

	// Cache Creation Date
	public long timeStamp = 0;

	/**
	 * Constructor
	 * @param path Folder Path
	 * @param content Folder Content
	 */
	public BitcasaFolderCacheItem(final String path, final List<BitcasaItem> content) {
		// Save Path
		this.path = path;

		// Save Content
		this.content = content;
	}

}
