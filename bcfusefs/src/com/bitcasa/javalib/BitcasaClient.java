package com.bitcasa.javalib;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.bitcasa.javalib.dao.BitcasaFile;
import com.bitcasa.javalib.dao.BitcasaFolder;
import com.bitcasa.javalib.dao.BitcasaItem;
import com.bitcasa.javalib.dao.BitcasaItem.Category;
import com.bitcasa.javalib.dao.BitcasaItem.SyncType;
import com.bitcasa.javalib.dao.BitcasaItem.Type;
import com.bitcasa.javalib.dao.BitcasaResponse;
import com.bitcasa.javalib.exception.BitcasaAccountException;
import com.bitcasa.javalib.exception.BitcasaAuthenticationException;
import com.bitcasa.javalib.exception.BitcasaClientException;
import com.bitcasa.javalib.exception.BitcasaException;
import com.bitcasa.javalib.exception.BitcasaFileException;
import com.bitcasa.javalib.exception.BitcasaFileSystemException;
import com.bitcasa.javalib.exception.BitcasaServerException;
import com.bitcasa.javalib.http.BitcasaHttpRequestor;
import com.bitcasa.javalib.http.BitcasaHttpRequestor.FileOperation;
import com.bitcasa.javalib.http.BitcasaHttpRequestor.PostRequest;
import com.bitcasa.javalib.http.BitcasaHttpResponse;
import com.bitcasa.javalib.http.Downloader;
import com.bitcasa.javalib.http.ProgressTracker;
import com.bitcasa.javalib.http.RESTConstants;
import com.bitcasa.javalib.http.Uploader;
import com.bitcasa.javalib.jsonparser.BitcasaJSONParser;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class BitcasaClient {
	
	private BitcasaJSONParser mJsonParser;
	private AuthenticationManager mAuthManager;
	
	// for constructing the root file system structure
	private static final String MIRRORED_FOLDER			= "Mirrored Folders";
	private static final String NO_DEVICE_NAME			= "NO DEVICE NAME";
	
	/**
	 * Initialize BitcasaClient with client id and client secret.
	 * Authenticate should then be called to acquire the URL for the user to log in with
	 * @param clientId
	 * @param clientSecret
	 */
	public BitcasaClient(String clientId, String clientSecret) {
		init();
		mAuthManager.mClientId = clientId;
		mAuthManager.mClientSecret = clientSecret;
	}
	
	/**
	 * Initialize BitcasaClient with accessToken if you already have it from
	 * previous authentication
	 * @param accessToken
	 */
	public BitcasaClient(String accessToken) {
		init();
		mAuthManager.mAccessToken = accessToken;
	}
	
	private void init() {
		mJsonParser = new BitcasaJSONParser();
		mAuthManager = new AuthenticationManager();
	}
	
	/**
	 * Returns the authentication page that asks the user to log in.  The user will then be redirected
	 * to the URL that was provided along with the authorization code in the query string
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String getAuthenticateUrl() throws IOException, URISyntaxException {
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put(RESTConstants.PARAM_CLIENT_ID, mAuthManager.mClientId);
		params.put(RESTConstants.PARAM_RSEPONSE_TYPE, "code");
		return BitcasaHttpRequestor.buildUrlWithParams(BitcasaHttpRequestor.urlBuilder(RESTConstants.METHOD_OAUTH), params);
	}
	
	/**
	 * With the provided authorization code, this will get an access token which can be used for subsequent
	 * requests
	 * @param authorizationCode
	 * @return
	 * @throws IOException
	 * @throws BitcasaException
	 */
	public String requestForAccessToken(String authorizationCode) throws IOException, BitcasaException {
		BitcasaHttpRequestor requestor = BitcasaHttpRequestor.getInstance();
		String url = BitcasaHttpRequestor.urlBuilder(RESTConstants.METHOD_ACCESS_TOKEN);
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put(RESTConstants.PARAM_SECRET, mAuthManager.mClientSecret);
		params.put(RESTConstants.PARAM_CODE, authorizationCode);
		params.put(RESTConstants.PARAM_GRANT_TYPE, "authorization_code");
		BitcasaHttpResponse httpResponse = requestor.doGet(url, params);
		final int statusCode = httpResponse.getStatusCode();
		String errorMessage = null;
		if (statusCode == 200 || statusCode == 400) {
			JsonFactory jFactory = new JsonFactory();
			JsonParser jParser = jFactory.createParser(httpResponse.getBody());
			while (jParser.nextToken() != JsonToken.END_OBJECT) {
				String fieldName = jParser.getCurrentName();
				if ("access_token".equals(fieldName)) {
					jParser.nextToken();
					mAuthManager.mAccessToken = jParser.getText();
				} else if ("error".equals(fieldName)) {
					jParser.nextToken();
					errorMessage= jParser.getText();
				}
			}
			jParser.close();
		}
		httpResponse.finish();
		if (errorMessage != null) {
			throw new BitcasaAuthenticationException(errorMessage);
		}
		return mAuthManager.mAccessToken;
	}
	
	public String getAccessToken() {
		return mAuthManager.mAccessToken;
	}
	
	/*
	 * 	 FILE AND FOLDER OPERATIONS
	 */
	private List<BitcasaItem> getItemsInPath(String path, RESTConstants.Depth depth, RESTConstants.Category category) throws IOException, BitcasaException {
		BitcasaHttpRequestor requestor = BitcasaHttpRequestor.getInstance();
		if (path == null)
			path = "/";
		String url = BitcasaHttpRequestor.urlBuilder(RESTConstants.METHOD_FOLDERS, path);
		
		// set query parameters
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put(RESTConstants.PARAM_ACCESS_TOKEN, mAuthManager.mAccessToken);
		if (depth != null) 
			params.put(RESTConstants.PARAM_DEPTH, String.valueOf(depth.ordinal()));
		if (category != null) 
			params.put(RESTConstants.PARAM_CATEGORY, category.getCategory());
		
		final BitcasaHttpResponse httpResponse = requestor.doGet(url, params);
		BitcasaResponse response = mJsonParser.parseJSONToObject(httpResponse.getBody(), BitcasaResponse.class);
		httpResponse.finish();
		List<BitcasaItem> result = null;
		if (response.getError() != null) {
			throw new BitcasaServerException(response.getError());
		} else {
			result = response.getResult().getItems();
		}
		
		return result;
	}
	
	private List<BitcasaItem> getItemsInPath(String path) throws BitcasaException, IOException {
		return getItemsInPath(path, null, null);
	}
	
	/**
	 * Request a list of content within a given folder
	 * @param folder
	 * 		To get the content at root, pass in null
	 * @return
	 * @throws BitcasaException
	 * @throws IOException
	 */
	public List<BitcasaItem> getItemsInFolder(BitcasaFolder folder) throws IOException, BitcasaException {
		validateAccessToken();
		return getItemsInFolder(folder, null, null);
	}
	
	private List<BitcasaItem> getItemsInFolder(BitcasaFolder folder, RESTConstants.Depth depth, RESTConstants.Category category) throws IOException, BitcasaException {
		List<BitcasaItem> result = null;
		// when the root path/synthetic folder are requested we need to do some magic here
		final SyncType syncType = folder == null ? null : folder.getSyncType();
		if (folder == null || syncType == SyncType.MIRRORED_FOLDER || syncType == SyncType.DEVICE) {
			List<BitcasaItem> items = getItemsInPath(null, depth, category);
			// check for infinite drive
			BitcasaFolder infiniteDrive = checkForInfiniteDrive(items);
			// build the devices in mirrored folder if necessary
			Hashtable<BitcasaFolder, List<BitcasaItem>> devices = getMirroredFolderDevices(items);
			
			// if the actual request was for one of the synthetic folder that we created
			if (syncType == SyncType.MIRRORED_FOLDER) {
				return new ArrayList<BitcasaItem>(devices.keySet());
			} else if (syncType == SyncType.DEVICE) {
				return devices.get(folder);
			} else {
				List<BitcasaItem> infiniteDriveContent = getItemsInFolder(infiniteDrive, null, null);
				items.addAll(infiniteDriveContent);
				result = items;
			}
		} else {
			result = getItemsInPath(folder.getPath(), depth, category);
		}
		return result;
	}
	
	/**
	 * Delete the given file/folder if it is editable (not mirrored)
	 * @param item
	 * @throws IOException
	 * @throws BitcasaException
	 */
	public void deleteItem(BitcasaItem item) throws IOException, BitcasaException {
		validateAccessToken();
		if (!item.isEditable()) {
			throw new BitcasaFileSystemException(2008, "Cannot Delete. Specified location is read only.");
		}
		BitcasaHttpRequestor requestor = BitcasaHttpRequestor.getInstance();
		String method = null;
		if (item instanceof BitcasaFolder) 
			method = RESTConstants.METHOD_FOLDERS;
		else
			method = RESTConstants.METHOD_FILES;
		String url = BitcasaHttpRequestor.urlBuilder(method);
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put(RESTConstants.PARAM_ACCESS_TOKEN, mAuthManager.mAccessToken);
		
		BitcasaHttpResponse httpResponse = requestor.doDelete(url, item.getPath(), params);
		BitcasaResponse response = mJsonParser.parseJSONToObject(httpResponse.getBody(), BitcasaResponse.class);
		httpResponse.finish();
		if (response.getError() != null) {
			throw new BitcasaServerException(response.getError());
		} 
	}
	
	/**
	 * Download the given file to the specify location or stream
	 * @param downloader
	 * @throws IOException
	 * @throws BitcasaException
	 */
	public void downloadFile(Downloader downloader) throws IOException, BitcasaException {
		BitcasaFile file = downloader.getBitcasaFile();
		BitcasaHttpRequestor requestor = BitcasaHttpRequestor.getInstance();
		String url = BitcasaHttpRequestor.urlBuilder(RESTConstants.METHOD_FILES, file.getId(), "/");
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put(RESTConstants.PARAM_ACCESS_TOKEN, mAuthManager.mAccessToken);
		
		final BitcasaHttpResponse httpResponse = requestor.doGet(url, params);
		if (httpResponse.getStatusCode() >= 400) {
			BitcasaResponse response = mJsonParser.parseJSONToObject(httpResponse.getBody(), BitcasaResponse.class);
			throw new BitcasaServerException(response.getError());
		} else {
			ProgressTracker pt = downloader.getProgressTracker();
			if (pt != null)
				pt.setFileSize(file.getSize());
			OutputStream os = downloader.getTargetOutputStream();
			if (os == null) {
				File newFile = new File(getFilePath(file, downloader.getDestination()));
				if (!newFile.exists()) {
					if (!newFile.getParentFile().mkdirs() && !newFile.getParentFile().exists())
						throw new BitcasaException(9007, "Appliation not authorized to perform this action");
				}
				os = new FileOutputStream(newFile);
			}
			copyInputStreamToOutputStream(httpResponse.getBody(), os, pt);
			if (pt != null) 
				pt.progressComplete(downloader.getBitcasaFile());
			if (os != null)
				os.close();
			httpResponse.finish();
		}
	}
	
	public byte[] downloadFileRange(BitcasaFile file, long offset, long size) throws IOException, BitcasaException {
		BitcasaHttpRequestor requestor = BitcasaHttpRequestor.getInstance();
		String url = BitcasaHttpRequestor.urlBuilder(RESTConstants.METHOD_FILES, file.getId(), "/");
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put(RESTConstants.PARAM_ACCESS_TOKEN, mAuthManager.mAccessToken);

		Hashtable<String, String> headers = new Hashtable<String, String>();
		headers.put("Range", "bytes=" + offset + "-" + (offset + size - 1));
		
		final BitcasaHttpResponse httpResponse = requestor.doGet(url, params, headers);
		if (httpResponse.getStatusCode() >= 400) {
			BitcasaResponse response = mJsonParser.parseJSONToObject(httpResponse.getBody(), BitcasaResponse.class);
			throw new BitcasaServerException(response.getError());
		} else {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			copyInputStreamToOutputStream(httpResponse.getBody(), os, null);
			return os.toByteArray();
		}
	}
	
	/**
	 * Upload a file to the specified folder
	 * @param uploader
	 * @return
	 * @throws BitcasaException
	 * @throws IOException
	 */
	public List<BitcasaItem> uploadFile(Uploader uploader) throws BitcasaException, IOException {
		validateAccessToken();
		// check if the destination folder is editable
		List<BitcasaItem> result = null;
		BitcasaFolder parentFolder = uploader.getParentFolder();
		if (parentFolder != null && !parentFolder.isEditable()) {
			throw new BitcasaFileException(2004, "Cannot Upload. Specified destination is read only.");
		}
		File fileToUpload = uploader.getFileToUpload();
		if (fileToUpload == null) {
			fileToUpload = new File(uploader.getFilePath());
		}
		if (fileToUpload.isDirectory()) {
			throw new BitcasaClientException("Sorry, we can only upload file right now");
		}
		String destinationPath = parentFolder.getPath();
		// the second and third condition shouldn't really occur
		// we have to request 
		if (parentFolder == null || destinationPath == null || parentFolder.getPath().equals("/")) {
			BitcasaFolder infiniteDrive = getInfiniteDrive();
			// for legacy account that may not have infinite drive
			if (infiniteDrive == null) {
				throw new BitcasaFileSystemException(2004, "Cannot Upload. Specified destination is read only.");
			}
			destinationPath = infiniteDrive.getPath();
		}
		
		ProgressTracker pt = uploader.getProgressTracker();
		if (pt != null) {
			pt.setFileSize(fileToUpload.length());
		}
		if (fileToUpload.isDirectory()) {
			
		}
		if (fileToUpload.exists() && fileToUpload.canRead()) {
			PostRequest upload = getUploadRequest(destinationPath);
			FileInputStream fis = new FileInputStream(fileToUpload);
			DataOutputStream dos = new DataOutputStream(upload.getOutputStream());
			dos.writeBytes(BitcasaHttpRequestor.TWO_HYPHENS + BitcasaHttpRequestor.BOUNDARY + BitcasaHttpRequestor.CRLF);
			dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileToUpload.getName() + "\"" + BitcasaHttpRequestor.CRLF);
			dos.writeBytes(BitcasaHttpRequestor.CRLF);
			copyInputStreamToOutputStream(fis, dos, pt);
			dos.writeBytes(BitcasaHttpRequestor.CRLF);
			dos.writeBytes(BitcasaHttpRequestor.TWO_HYPHENS + BitcasaHttpRequestor.BOUNDARY + BitcasaHttpRequestor.TWO_HYPHENS + BitcasaHttpRequestor.CRLF);
			dos.flush();
			
			// close stream
			dos.close();
			fis.close();
			
			BitcasaHttpResponse httpResponse = upload.getPostResponse();
			BitcasaResponse response = mJsonParser.parseJSONToObject(httpResponse.getBody(), BitcasaResponse.class);
			httpResponse.finish();
			if (response.getError() != null) {
				throw new BitcasaException(response.getError());
			} else {
				result = response.getResult().getItems();
			}
		} else {
			throw new BitcasaClientException("Unable to read file: " + fileToUpload.getName());
		}
		return result;
	}
	
	private PostRequest getUploadRequest(String destinationPath) throws IOException {
		BitcasaHttpRequestor requestor = BitcasaHttpRequestor.getInstance();
		String url = BitcasaHttpRequestor.urlBuilder(RESTConstants.METHOD_FILES, destinationPath);
		// prep params and headers
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put(RESTConstants.PARAM_ACCESS_TOKEN, mAuthManager.mAccessToken);
		Hashtable<String, String> headers = new Hashtable<String, String>();
		headers.put("Connection", "Keep-Alive");
		headers.put("Cache-Control", "no-cache");
		headers.put("Content-Type", "multipart/form-data;boundary=" + BitcasaHttpRequestor.BOUNDARY);
		// make the request
		return requestor.startPost(url, params, headers);
	}
	
	/**
	 * Create a new folder within the specified folder 
	 * @param parentFolder
	 * @param folderName
	 * @return
	 * @throws IOException
	 * @throws BitcasaException
	 */
	public List<BitcasaItem> createFolder(BitcasaFolder parentFolder, String folderName) throws IOException, BitcasaException {
		validateAccessToken();
		if (parentFolder != null && !parentFolder.isEditable()) {
			throw new BitcasaFileSystemException(2009, "Cannot create folder. Specified location is read only.");
		}
		validateItemName(folderName, Type.FOLDER);
		BitcasaHttpRequestor requestor = BitcasaHttpRequestor.getInstance();
		String parentPath = parentFolder == null ? null : parentFolder.getPath();
		String url = BitcasaHttpRequestor.urlBuilder(RESTConstants.METHOD_FOLDERS, parentPath);
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put(RESTConstants.PARAM_ACCESS_TOKEN, mAuthManager.mAccessToken);
		
		PostRequest request = requestor.startPost(url, params, null);
		OutputStreamWriter osw = new OutputStreamWriter(request.getOutputStream());
		osw.write(RESTConstants.PARAM_FOLDER_NAME + "=" + URLEncoder.encode(folderName, BitcasaHttpRequestor.ENCODING));
		osw.flush();
		osw.close();
		
        BitcasaHttpResponse httpResponse = request.getPostResponse();
		BitcasaResponse response = mJsonParser.parseJSONToObject(httpResponse.getBody(), BitcasaResponse.class);
		httpResponse.finish();
		if (response.getError() != null) {
			throw new BitcasaServerException(response.getError());
		} else {
			return response.getResult().getItems();
		}
	}
	
	private List<BitcasaItem> manipulateItem(FileOperation operation, BitcasaItem originalItem, BitcasaItem destinationFolder, String newFileName) throws IOException, BitcasaException {
		// validation
		validateAccessToken();
		if (newFileName != null) {
			validateItemName(newFileName, originalItem.getType());
		}
		// validation ends
		
		BitcasaHttpRequestor requestor = BitcasaHttpRequestor.getInstance();
		String method = RESTConstants.METHOD_FILES;
		if (originalItem instanceof BitcasaFolder)
			method = RESTConstants.METHOD_FOLDERS;
		String url = BitcasaHttpRequestor.urlBuilder(method);
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put(RESTConstants.PARAM_OPERATION, operation.toString());
		params.put(RESTConstants.PARAM_ACCESS_TOKEN, mAuthManager.mAccessToken);
		
		PostRequest request = requestor.startPost(url, params, null);
		Hashtable<String, String> body = new Hashtable<String, String>();
		body.put(RESTConstants.PARAM_FROM, originalItem.getPath());
		if (destinationFolder != null)
			body.put(RESTConstants.PARAM_TO, destinationFolder.getPath());
		if (operation == FileOperation.RENAME)
			body.put(RESTConstants.PARAM_FILENAME, newFileName);
		OutputStreamWriter osw = new OutputStreamWriter(request.getOutputStream());
		osw.write(BitcasaHttpRequestor.encodeParams(body));
		osw.flush();
		osw.close();
		
        BitcasaHttpResponse httpResponse = request.getPostResponse();
		BitcasaResponse response = mJsonParser.parseJSONToObject(httpResponse.getBody(), BitcasaResponse.class);
		httpResponse.finish();
		if (response.getError() != null) {
			throw new BitcasaServerException(response.getError());
		} else {
			return response.getResult().getItems();
		}
	}
	
	/**
	 * Move the specified item to the destination (must be a folder).
	 * @param originalItem
	 * 		Can be a file or a folder
	 * @param destinationFolder
	 * 		If the destination folder is null, then the item will be moved to the infinite folder
	 * @return
	 * @throws IOException
	 * @throws BitcasaException 
	 */
	public List<BitcasaItem> moveItem(BitcasaItem originalItem, BitcasaFolder destinationFolder) throws IOException, BitcasaException {
		return manipulateItem(FileOperation.MOVE, originalItem, destinationFolder, null);
	}
	
	/**
	 * Rename the specified item to the new provide name.
	 * @param originalItem
	 * 		Can be a file or a folder
	 * @param newName
	 * 		New name must adhere to the naming restriction
	 * @return
	 * @throws IOException
	 * @throws BitcasaException 
	 */
	public List<BitcasaItem> renameItem(BitcasaItem originalItem, String newName) throws IOException, BitcasaException {
		return manipulateItem(FileOperation.RENAME, originalItem, null, newName);
	}
	
	/**
	 * Move the specified item to the destination (must be a folder).
	 * @param originalItem
	 * 		Can be a file or a folder
	 * @param destinationFolder
	 * 		If the destination folder is null, then the item will be moved to the infinite folder
	 * @return
	 * @throws IOException
	 * @throws BitcasaException 
	 */
	public List<BitcasaItem> copyItem(BitcasaItem originalItem, BitcasaFolder destinationFolder) throws IOException, BitcasaException {
		validateAccessToken();
		// check both paramters are not null
		return manipulateItem(FileOperation.COPY, originalItem, destinationFolder, null);
	}
	
	private void validateAccessToken() throws BitcasaAccountException {
		if (mAuthManager.mAccessToken == null)
			throw new BitcasaAccountException(1023, "Access token cannot be fonud");
	}
	
	private void validateItemName(String name, Type type) throws BitcasaFileSystemException {
		if (name == null || name.length() > 64 || name.startsWith("*") || name.matches(".*[<>:\"/\\|?*].*")) {
			if (type == Type.FILE) {
				throw new BitcasaFileSystemException(2030, "File name is invalide");
			} else { 
				throw new BitcasaFileSystemException(2031, "Folder name is invalid");
			}
		} 
	}
	
	private String getFilePath(BitcasaFile file, String destinationFolder) {
		StringBuilder sb = new StringBuilder(destinationFolder);
		if (sb.charAt(sb.length() - 1) != '/') {
			sb.append("/");
		}
		sb.append(file.getName());
		return sb.toString();
	}
	
	private BitcasaFolder checkForInfiniteDrive(List<BitcasaItem> items) {
		BitcasaFolder infiniteDrive = null;
		for (BitcasaItem item : items) {
			if (((BitcasaFolder)item).getSyncType() == SyncType.INFINITE_DRIVE) {
				infiniteDrive = (BitcasaFolder)item;
				break;
			}
		}
		if (infiniteDrive != null)
			items.remove(infiniteDrive);
		return infiniteDrive;
	}
	
	private BitcasaFolder getInfiniteDrive() throws BitcasaException, IOException {
		BitcasaFolder infiniteDrive = null;
		List<BitcasaItem> items = getItemsInPath(null, null, null);
		for (BitcasaItem item : items) {
			if (((BitcasaFolder)item).getSyncType() == SyncType.INFINITE_DRIVE) {
				infiniteDrive = (BitcasaFolder)item;
				break;
			}
		}
		return infiniteDrive;
	}
	
	private Hashtable<BitcasaFolder, List<BitcasaItem>>getMirroredFolderDevices(List<BitcasaItem> items) {
		Hashtable<BitcasaFolder, List<BitcasaItem>> result = null;
		boolean shouldAddMirroredFolder = false;
		Iterator<BitcasaItem> iterator = items.iterator();
		while(iterator.hasNext()) {
			BitcasaItem item = iterator.next();
			SyncType syncType = ((BitcasaFolder)item).getSyncType();
			if (syncType == SyncType.BACKUP || syncType == SyncType.SYNC) {
				// first time create the table and add the mirrored folder
				if (result == null) {
					result = new Hashtable<BitcasaFolder, List<BitcasaItem>>();
					shouldAddMirroredFolder = true;
				}
				
				// create a synthetic folder for the device
				BitcasaFolder device = new BitcasaFolder();
				device.setCategory(Category.FOLDERS);
				device.setSyncType(SyncType.DEVICE);
				device.setMirrored(true);
				device.setType(Type.FOLDER);
				final String deviceName = item.getOriginDevice();
				device.setName(deviceName == null ? NO_DEVICE_NAME : deviceName);

				// check if we already have a list of folders for this device
				List<BitcasaItem> deviceFolders = result.get(device);
				if (deviceFolders == null)
					deviceFolders = new ArrayList<BitcasaItem>();
				deviceFolders.add(item);
				result.put(device, deviceFolders);
				iterator.remove();
			}
		}
		
		if (shouldAddMirroredFolder) {
			BitcasaFolder mirroredFolder = new BitcasaFolder();
			mirroredFolder.setCategory(Category.FOLDERS);
			mirroredFolder.setSyncType(SyncType.MIRRORED_FOLDER);
			mirroredFolder.setName(MIRRORED_FOLDER);
			mirroredFolder.setType(Type.FOLDER);
			mirroredFolder.setMirrored(true);
			items.add(mirroredFolder);
		}
		
		return result;
	}
	
	private void copyInputStreamToOutputStream(InputStream is, OutputStream os, ProgressTracker pt) throws IOException {
		int bufferSize = 4096;
		long totalWritten = 0;
		byte[] buf = new byte[bufferSize];
		int len;
		while ((len = is.read(buf)) > 0) {
			os.write(buf, 0, len);
			if (pt != null) {
				totalWritten += len;
				pt.progressUpdate((int) (totalWritten * 100 / pt.getFileSize()));
			}
		}
		
	}
	
	public static class AuthenticationManager {
		String mClientId;
		String mClientSecret;
		String mAccessToken;
	}
}
