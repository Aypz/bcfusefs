package com.bitcasa.javalib.http;

public class RESTConstants {

	public static final String API_V1_URL 				= "https://developer.api.bitcasa.com/v1";
	
	// methods
	public static final String METHOD_OAUTH				= "/oauth2/authenticate";
	public static final String METHOD_ACCESS_TOKEN		= "/oauth2/access_token";
	
	public static final String METHOD_FOLDERS			= "/folders";
	public static final String METHOD_FILES				= "/files";
	
	// parameters
	public static final String PARAM_SECRET				= "secret";
	public static final String PARAM_CODE				= "code";
	public static final String PARAM_CLIENT_ID			= "client_id";
	public static final String PARAM_REDIRECT			= "redirect";
	public static final String PARAM_ACCESS_TOKEN		= "access_token";
	public static final String PARAM_GRANT_TYPE			= "grant_type";
	public static final String PARAM_PATH				= "path";
	public static final String PARAM_DEPTH				= "depth";
	public static final String PARAM_LATEST				= "latest";
	public static final String PARAM_CATEGORY			= "category";
	public static final String PARAM_OPERATION			= "operation";
	public static final String PARAM_FROM				= "from";
	public static final String PARAM_TO					= "to";
	public static final String PARAM_FILENAME			= "filename";
	public static final String PARAM_EXISTS				= "exists";
	public static final String PARAM_FOLDER_NAME		= "folder_name";
	public static final String PARAM_RSEPONSE_TYPE		= "response_type";
	
	// categories
	public static enum Category {
		MUSIC_ARTISTS("music_artists"),
		MUSIC_ALBUMS("music_albums"),
		MUSIC_TRACKS("music_tracks"),
		PHOTO_ALBUMS("photo_albums"),
		PHOTOS("photos"),
		DOCUMENTS("documents"),
		VIDEOS("videos"),
		EVERYTHING("everything");
		
		private final String mCategory;
		
		private Category(String category) {
			mCategory = category;
		}
		
		public String getCategory() {
			return mCategory;
		}
		
		public static Category getEnum(String s) {
			for (Category category : Category.values()) {
				if (category.toString().equals(s)) {
					return category;
				}
			}
			return null;
		}
	}
	
	public static enum Depth {
		INFINITE,
		CURRENT_CHILDREN
	}
}
