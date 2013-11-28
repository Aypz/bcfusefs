package com.bitcasa.javalib.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class BitcasaHttpRequestor {

	public static final String ENCODING 		= "UTF-8";
	public static final String CRLF				= "\r\n";
	public static final String TWO_HYPHENS		= "--";
	public static final String BOUNDARY			= "----------*****----------bitcasa";
	
	/**
	 * 
	 *
	 */
	public static enum FileExistsOperation {
		/**
		 * Request will return a 409 if a file with the given name already exists at the given path
		 */
		FAIL("fail"),
		/**
		 * Existing file with the matching name will be overwritten
		 */
		OVERWRITE("overwrite"),
		/**
		 * If there a file with the given exists, the new file will get (N) appended
		 * to the file name, where N is an unused integer
		 */
		RENAME("rename");
		
		private final String mOperation;
		private FileExistsOperation(String operation) {
			mOperation = operation;
		}
		
		@Override
		public String toString() {
			return mOperation;
		}
	}
	
	public static enum FileOperation {
		COPY("copy"),
		MOVE("move"),
		RENAME("rename");
		
		private final String mOperation;
		private FileOperation(String operation) {
			mOperation = operation;
		}
		
		@Override
		public String toString() {
			return mOperation;
		}
	}
	
	private static BitcasaHttpRequestor sInstance = new BitcasaHttpRequestor();
	
	public static BitcasaHttpRequestor getInstance() {
		return sInstance;
	}
	
	public BitcasaHttpResponse doGet(String url) throws IOException {
		return doGet(url, null, null);
	}
	
	public BitcasaHttpResponse doGet(String url, Hashtable<String, String> params) throws IOException {
		return doGet(url, params, null);
	}
	
	public BitcasaHttpResponse doGet(String url, Hashtable<String, String> params, Hashtable<String, String> headers) throws IOException {
		HttpsURLConnection conn = prepRequest(url, params, headers);
        conn.setRequestMethod("GET");
        conn.connect();
        
        final int statusCode = conn.getResponseCode();
        InputStream body;
        if (statusCode >= 400) body = conn.getErrorStream();
        else body = conn.getInputStream();
        return new BitcasaHttpResponse(statusCode, body, conn.getHeaderFields());
	}
	
	public PostRequest startPost(String url, Hashtable<String, String> params, Hashtable<String, String> headers) throws IOException {
		HttpsURLConnection conn = prepRequest(url, params, headers);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(300000);
        conn.setReadTimeout(300000);
        return new PostRequest(conn);
	}
	
	public BitcasaHttpResponse doDelete(String url, String pathToFile, Hashtable<String, String> params) throws IOException {
		HttpsURLConnection conn = prepRequest(url, params, null);
        conn.setRequestMethod("DELETE");
        conn.setDoOutput(true);
        conn.connect();
        
        OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
        osw.write(RESTConstants.PARAM_PATH + "=" + pathToFile);
        osw.flush();
        
        final int statusCode = conn.getResponseCode();
        InputStream body;
        if (statusCode >= 400) body = conn.getErrorStream();
        else body = conn.getInputStream();
        return new BitcasaHttpResponse(statusCode, body, conn.getHeaderFields());
	}
	
	private HttpsURLConnection prepRequest(String url, Hashtable<String, String> params, Hashtable<String, String> headers) throws IOException {
		URL urlObject = new URL(buildUrlWithParams(url, params));
		HttpsURLConnection conn = (HttpsURLConnection)urlObject.openConnection();
		
		// add headers
		if (headers != null)
			addHeaders(conn, headers);
			
		return conn;
	}
	
	private void addHeaders(HttpURLConnection conn, Hashtable<String, String> headers) {
		Set<Entry<String, String>> set = headers.entrySet();
		for (Entry<String, String> e : set) {
			conn.addRequestProperty(e.getKey(), e.getValue());
		}
	}
	
	public static String buildUrlWithParams(String url, Hashtable<String, String> params) {

		return params == null ? url : url + "?" + encodeParams(params);
	}
	
	public static String encodeParams(Hashtable<String, String> params) {
		StringBuilder paramsBuilder = new StringBuilder();
		String holder = "";
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			paramsBuilder.append(holder);
			try {
				paramsBuilder.append(URLEncoder.encode(key, ENCODING))
					.append("=").append(URLEncoder.encode(value, ENCODING));
			} catch (UnsupportedEncodingException e) {}
			holder = "&";
		}
		return paramsBuilder.toString();
	}
	
	/**
	 * This is purely for testing ONLY!!!
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static void trustAllCerts() throws NoSuchAlgorithmException, KeyManagementException {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			}
		};
			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
	
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}
	
	public static String urlBuilder(String method) {
		return urlBuilder(method, new String[]{});
	}
	
	public static String urlBuilder(String method, String... urlSegments) {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(RESTConstants.API_V1_URL).append(method);
		for(String segment : urlSegments) {
			if (segment != null) {
				if (segment.equals("/"))
					urlBuilder.append(segment);
				else
					urlBuilder.append("/").append(segment);
			}
		}
		return urlBuilder.toString();
	}
	
	public static class PostRequest {
		
		private HttpsURLConnection mConn;
		
		public PostRequest(HttpsURLConnection conn) throws IOException {
			mConn = conn;
			mConn.connect();
		}
		
		public OutputStream getOutputStream() throws IOException {
			return mConn.getOutputStream();
		}
		
		public BitcasaHttpResponse getPostResponse() throws IOException {
	        final int statusCode = mConn.getResponseCode();
	        InputStream body;
	        if (statusCode >= 400) body = mConn.getErrorStream();
	        else body = mConn.getInputStream();
	        return new BitcasaHttpResponse(statusCode, body, mConn.getHeaderFields());
		}
		
		public void finish() {
			mConn.disconnect();
		}
	}
}
