package net.coldbird.bcfusefs.driver;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import net.coldbird.bcfusefs.driver.cache.file.BitcasaFileCache;
import net.coldbird.bcfusefs.driver.cache.folder.BitcasaFolderCache;
import net.coldbird.bcfusefs.driver.cache.folder.BitcasaFolderCacheItem;
import net.fusejna.DirectoryFiller;
import net.fusejna.ErrorCodes;
import net.fusejna.StructFuseFileInfo.FileInfoWrapper;
import net.fusejna.StructStat.StatWrapper;
import net.fusejna.types.TypeMode.NodeType;
import net.fusejna.util.FuseFilesystemAdapterFull;

import com.bitcasa.javalib.BitcasaClient;
import com.bitcasa.javalib.dao.BitcasaFile;
import com.bitcasa.javalib.dao.BitcasaFolder;
import com.bitcasa.javalib.dao.BitcasaItem;
import com.bitcasa.javalib.exception.BitcasaException;

public class BitcasaFS extends FuseFilesystemAdapterFull {

	// Bitcasa API Counter Reset Timespan (in Milliseconds)
	private final long apiResetTimespan = 1000;

	// Default Bitcasa Request Timeout (in Nanoseconds)
	private long requestTimeout = Long.MAX_VALUE;

	// Bitcasa Client
	private BitcasaClient client = null;

	// Bitcasa Folder Structure Cache
	private BitcasaFolderCache folderStructureCache = null;
	
	// Bitcasa File Cache
	private BitcasaFileCache fileCache = null;
	
	/**
	 * Constructor
	 * @param client Bitcasa Client (already connected and authenticated)
	 * @param requestTimeout Request Timeout in Nanoseconds
	 * @param folderCacheTimeout Folder Structure Cache Timeout in Nanoseconds
	 */
	public BitcasaFS(final BitcasaClient client, final long requestTimeout, final long folderCacheTimeout, boolean verboseMode) {
		// Call Base Constructor
		super();

		// Enable Verbose Mode
		log(verboseMode);
		
		// Save Bitcasa Client
		this.client = client;

		// Save Request Timeout
		this.requestTimeout = requestTimeout;

		// Create Folder Structure Cache
		folderStructureCache = new BitcasaFolderCache(folderCacheTimeout);
		
		// Create File Cache
		fileCache = new BitcasaFileCache(client);
	}

	@Override
	public void destroy() {
		// TODO Shutdown Logic (cancel pending actions, etc.)
	}

	@Override
	public int getattr(final String path, final StatWrapper stat) {
		// Root Directory
		if (path.equals(File.separator)) {
			// Set Directory Status
			stat.setMode(NodeType.DIRECTORY);

			// Processed successfully
			return 0;
		}

		// Fetch File Information
		final BitcasaItem item = getItem(path);

		// File exists
		if (item != null) {
			// Set Modification Time
			stat.mtime(item.getModifiedTime().longValue());

			// Fetch Item Type
			final BitcasaItem.Type itemType = item.getType();

			// Set Item Type in Filesystem Driver (and give 777 permission)
			stat.setMode(itemType == BitcasaItem.Type.FILE ? NodeType.FILE
					: NodeType.DIRECTORY, true, true, true, true, true, true,
					true, true, true);

			// Item is a file
			if (itemType == BitcasaItem.Type.FILE) {
				// Cast as a file
				final BitcasaFile file = (BitcasaFile) item;

				// Set File Size in Filesystem Driver
				stat.size(file.getSize());
			}

			// Processed successfully
			return 0;
		}

		// File doesn't exist
		return -ErrorCodes.ENOENT();
	}

	/**
	 * Cached Bitcasa File Information Reader
	 * 
	 * @param path
	 *            File Path
	 * @return File Information (or null if not found)
	 */
	private BitcasaItem getItem(final String path) {
		// Extract Parent Folder Path from File Path
		final String parent = path.substring(0,
				path.lastIndexOf(File.separator));

		// Extract File Name from File Path
		final String name = path
				.substring(path.lastIndexOf(File.separator) + 1);

		// Get Parent Folder Contents
		final List<BitcasaItem> root = getItemsInFolder(parent);

		// Parent Folder found
		if (root != null) {
			// Iterate Files in Parent Folder
			for (final BitcasaItem item : root) {
				// Extract File Name from File
				final String itemName = item.getName();

				// File found in Parent Folder
				if (itemName.equals(name)) {
					// Return File Information
					return item;
				}
			}
		}

		// File not found
		return null;
	}

	/**
	 * Cached Bitcasa Folder Information Reader
	 * 
	 * @param path
	 *            Folder Path
	 * @param timeoutValue
	 *            Timeout Value in Milliseconds (Long.MAX_VALUE equals infinite)
	 * @return File List (or null if folder wasn't found or timeout was
	 *         exceeded)
	 */
	private List<BitcasaItem> getItemsInFolder(String path) {
		// Remove Leading Slashes
		if (path != null && path.startsWith(File.separator)) {
			path = path.substring(File.separator.length());
		}

		// Function Start Time
		final long startTime = System.nanoTime();

		// File List (filled from Cache)
		List<BitcasaItem> root = folderStructureCache.get(path);

		// File List found in Cache
		if (root != null) {
			// Return cached Copy
			return root;
		}

		// Keep retrying until we get folder contents
		while (root == null && (System.nanoTime() - startTime) < requestTimeout) {
			try {
				// Request Folder Content
				root = client.getItemsInFolder(null);
			} catch (final IOException e) {
				try {
					// Wait until API timer resets
					Thread.sleep(apiResetTimespan);
				} catch (final InterruptedException e1) {
					// This will never happen
				}
			} catch (final BitcasaException e) {
				try {
					// Wait until API timer resets
					Thread.sleep(apiResetTimespan);
				} catch (final InterruptedException e1) {
					// This will never happen
				}
			}
		}

		// Acquired Folder Contents
		if (root != null) {
			// Non-root Directory
			if (!path.isEmpty()) {
				// Split Folder Path into Segments
				// (Bitcasa can only descend one folder at a time)
				final String[] folders = path.split(File.separator);

				// Iterate Folder Segments
				for (final String folder : folders) {
					// Iterate Items in Folder
					for (final BitcasaItem item : root) {
						// Found next Folder Segment
						if (item.getName().equals(folder)) {
							// Possibly next File List Node
							List<BitcasaItem> newRoot = null;

							// Keep retrying until we get folder contents
							while (newRoot == null
									&& (System.nanoTime() - startTime) < requestTimeout) {
								try {
									// Request Folder Content
									newRoot = client
											.getItemsInFolder((BitcasaFolder) item);
								} catch (final IOException e) {
									try {
										// Wait until API timer resets
										Thread.sleep(apiResetTimespan);
									} catch (final InterruptedException e1) {
										// This will never happen
									}
								} catch (final BitcasaException e) {
									try {
										// Wait until API timer resets
										Thread.sleep(apiResetTimespan);
									} catch (final InterruptedException e1) {
										// This will never happen
									}
								}
							}

							// Timeout has been exceeded
							if (newRoot == null) {
								// Cancel Operation
								return null;
							}

							// Set new root
							root = newRoot;
						}
					}
				}
			}
		}

		// Add to Cache
		folderStructureCache.add(new BitcasaFolderCacheItem(path, root));

		// Return File List
		return root;
	}

	@Override
	public int read(final String path, final ByteBuffer buffer, final long size, final long offset, final FileInfoWrapper info) {
		// Get Bitcasa Item Information
		BitcasaItem item = getItem(path);
		
		// Bitcasa Item not found
		if (item == null) {
			// Return "File not found" Error
			return -ErrorCodes.ENOENT();
		}
		
		// Bitcasa Item is a Directory
		if (item.getType() == BitcasaItem.Type.FOLDER) {
			// Return "Is Directory" Error
			return -ErrorCodes.EISDIR();
		}
		
		// Cast as File
		BitcasaFile file = (BitcasaFile)item;
		
		// Record Start Time
		long startTime = System.nanoTime();
		
		// Create Temporary Read Buffer
		byte[] byteBuffer = null;
		
		// Keep retrying until we got the Data
		while (byteBuffer == null && (System.nanoTime() - startTime) < requestTimeout) {
			try {
				// Download File Chunk
				byteBuffer = fileCache.read(file, offset, size);
			} catch (IOException e) {
				// Channel was broken (Internet Connectivity lost?)
			} catch (BitcasaException e) {
				// Bitcasa refuses connection
			}
		}
		
		// Return Buffer
		if (byteBuffer != null) buffer.put(byteBuffer);
		
		// Return number of read Bytes
		return byteBuffer != null ? byteBuffer.length : -ErrorCodes.ETIMEDOUT();
	}

	@Override
	public int readdir(final String path, final DirectoryFiller filler) {
		// Request Folder Content
		final List<BitcasaItem> root = getItemsInFolder(path);

		// Folder wasn't found or timeout was exceeded
		if (root != null) {
			// Iterate Items in Folder
			for (final BitcasaItem item : root) {
				// Add File Name to DirectoryFiller
				filler.add(item.getName());
			}
		}

		// Processed successfully
		return 0;
	}

}
