package com.bitcasa.javalib.dao;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = As.PROPERTY,
	property = "category",
	visible = true
)
@JsonSubTypes({
	@JsonSubTypes.Type(value = BitcasaFolder.class, name = "folders"),
	@JsonSubTypes.Type(value = BitcasaDocument.class, name = "documents"),
	@JsonSubTypes.Type(value = BitcasaIcon.class, name = "icons"),
	@JsonSubTypes.Type(value = BitcasaMusic.class, name = "music"),
	@JsonSubTypes.Type(value = BitcasaPhoto.class, name = "photos"),
	@JsonSubTypes.Type(value = BitcasaVideo.class, name = "videos"),
	@JsonSubTypes.Type(value = BitcasaFile.class, name = "other")
})

public abstract class BitcasaItem {
	
	public static final String TAG_CATEGORY 				= "category";
	public static final String TAG_TYPE 					= "type";
	public static final String TAG_NAME						= "name";
	public static final String TAG_PATH						= "path";
	public static final String TAG_MTIME					= "mtime";
	public static final String TAG_MIRRORED					= "mirrored";
	public static final String TAG_MOUNT_POINT				= "mount_point";
	public static final String TAG_DELETED					= "deleted";
	public static final String TAG_ORIGIN_DEVICE			= "origin_device";
	public static final String TAG_ORIGIN_DEVICE_ID			= "origin_device_id";
	public static final String TAG_SYNC_TYPE				= "sync_type";
	
	// sync type
	private static final String SYNC_TYPE_BACKUP			= "backup";
	private static final String SYNC_TYPE_INFINITE_DRIVE 	= "infinite drive";
	private static final String SYNC_TYPE_REGULAR			= "regular";
	private static final String SYNC_TYPE_SYNC				= "sync";
	private static final String SYNC_TYPE_MIRRORED_FOLDER	= "mirrored_folder";	// only used on client side
	private static final String SYNC_TYPE_DEVICE			= "device";
	// category types
	public static final String CATEGORY_FOLDERS 			= "folders";
	public static final String CATEGORY_DOCUMENTS			= "documents";
	public static final String CATEGORY_PHOTOS				= "photos";
	public static final String CATEGORY_ICONS				= "icons";
	public static final String CATEGORY_MUSIC				= "music";
	public static final String CATEGORY_VIDEOS				= "videos";
	public static final String CATEGORY_OTHER				= "other";
	
	// item type
	public static final int TYPE_FILE						= 0;
	public static final int TYPE_FOLDER						= 1;
	
	@JsonProperty(TAG_CATEGORY)
	protected Category mCategory;
	@JsonProperty(TAG_TYPE)
	protected Type mType;
	@JsonProperty(TAG_NAME)
	protected String mName;
	@JsonProperty(TAG_PATH)
	protected String mPath;
	@JsonProperty(TAG_MTIME)
	protected BigInteger mModifiedTime;
	@JsonProperty(TAG_MIRRORED)
	protected boolean mMirrored;
	@JsonProperty(TAG_MOUNT_POINT)
	protected String mMountPoint;
	@JsonProperty(TAG_DELETED)
	protected boolean mDeleted;
	@JsonProperty(TAG_ORIGIN_DEVICE)
	protected String mOriginDevice;
	@JsonProperty(TAG_ORIGIN_DEVICE_ID)
	protected String mOriginDeviceId;
	@JsonProperty(TAG_SYNC_TYPE)
	protected SyncType mSyncType;
	
	public static enum SyncType {
		BACKUP(SYNC_TYPE_BACKUP),
		DEVICE(SYNC_TYPE_DEVICE),
		INFINITE_DRIVE(SYNC_TYPE_INFINITE_DRIVE),
		MIRRORED_FOLDER(SYNC_TYPE_MIRRORED_FOLDER),
		REGULAR(SYNC_TYPE_REGULAR),
		SYNC(SYNC_TYPE_SYNC)
		;
		
		private final String mSyncType;
		private SyncType(String syncType) {
			mSyncType = syncType;
		}
		
		@JsonCreator
		public static SyncType getValue(String syncType) {
			if (syncType.toLowerCase().equals(SYNC_TYPE_BACKUP))
				return SyncType.BACKUP;
			else if (syncType.toLowerCase().equals(SYNC_TYPE_INFINITE_DRIVE))
				return SyncType.INFINITE_DRIVE;
			else if (syncType.toLowerCase().equals(SYNC_TYPE_REGULAR))
				return SyncType.REGULAR;
			else if (syncType.toLowerCase().equals(SYNC_TYPE_SYNC))
				return SyncType.SYNC;
			else
				return null;
		}
	}
	
	public static enum Category {
		DOCUMENTS(CATEGORY_DOCUMENTS),
		FOLDERS(CATEGORY_FOLDERS),
		ICONS(CATEGORY_ICONS),
		MUSIC(CATEGORY_MUSIC),
		PHOTOS(CATEGORY_PHOTOS),
		OTHER(CATEGORY_OTHER),
		VIDEOS(CATEGORY_VIDEOS);
		
		private String mCategory;
		private Category(String category) {
			mCategory = category;
		}
		
		@JsonCreator
		public static Category getValue(String category) {
			if (category.equals(CATEGORY_DOCUMENTS)) {
				return Category.DOCUMENTS;
			} else if (category.equals(CATEGORY_FOLDERS)) {
				return Category.FOLDERS;
			} else if (category.equals(CATEGORY_ICONS)) {
				return Category.ICONS;
			} else if (category.equals(CATEGORY_MUSIC)) {
				return Category.MUSIC;
			} else if (category.equals(CATEGORY_OTHER)) {
				return Category.OTHER;
			} else if (category.equals(CATEGORY_PHOTOS)) {
				return Category.PHOTOS;
			} else if (category.equals(CATEGORY_VIDEOS)) {
				return Category.VIDEOS;
			} else {
				return null;
			}
		}
		
		@Override
		public String toString() {
			return mCategory;
		}
	}
	
	public static enum Type {
		FILE,
		FOLDER;
	}
	
	public Category getCategory() {
		return mCategory;
	}

	public void setCategory(Category category) {
		mCategory = category;
	}
	
	public Type getType() {
		return mType;
	}
	
	public void setType(Type type) {
		mType = type;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public String getPath() {
		return mPath;
	}
	
	public void setPath(String path) {
		mPath = path;
	}
	
	public BigInteger getModifiedTime() {
		return mModifiedTime;
	}
	
	public boolean isMirrored() {
		return mMirrored;
	}

	public void setMirrored(boolean mirrored) {
		mMirrored = mirrored;
	}
	
	public boolean isEditable() {
		return !mMirrored && mSyncType != SyncType.BACKUP && mSyncType != SyncType.SYNC;
	}
	
	public String getOriginDevice() {
		return mOriginDevice;
	}
	
	public SyncType getSyncType() {
		return mSyncType;
	}
	
	public void setSyncType(SyncType syncType) {
		mSyncType = syncType;
	}
	
	@Override
	public String toString() {
		return "BitcasaItem [mCategory=" + mCategory + ", mType=" + mType
				+ ", mName=" + mName + ", mPath=" + mPath + ", mModifiedTime="
				+ mModifiedTime + ", mMirrored=" + mMirrored + ", mMountPoint="
				+ mMountPoint + ", mDeleted=" + mDeleted + ", mOriginDevice="
				+ mOriginDevice + ", mOriginDeviceId=" + mOriginDeviceId
				+ ", mSyncType=" + mSyncType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mCategory == null) ? 0 : mCategory.hashCode());
		result = prime * result + (mDeleted ? 1231 : 1237);
		result = prime * result + (mMirrored ? 1231 : 1237);
		result = prime * result
				+ ((mModifiedTime == null) ? 0 : mModifiedTime.hashCode());
		result = prime * result
				+ ((mMountPoint == null) ? 0 : mMountPoint.hashCode());
		result = prime * result + ((mName == null) ? 0 : mName.hashCode());
		result = prime * result
				+ ((mOriginDevice == null) ? 0 : mOriginDevice.hashCode());
		result = prime * result
				+ ((mOriginDeviceId == null) ? 0 : mOriginDeviceId.hashCode());
		result = prime * result + ((mPath == null) ? 0 : mPath.hashCode());
		result = prime * result
				+ ((mSyncType == null) ? 0 : mSyncType.hashCode());
		result = prime * result + ((mType == null) ? 0 : mType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BitcasaItem other = (BitcasaItem) obj;
		if (mCategory != other.mCategory)
			return false;
		if (mDeleted != other.mDeleted)
			return false;
		if (mMirrored != other.mMirrored)
			return false;
		if (mModifiedTime == null) {
			if (other.mModifiedTime != null)
				return false;
		} else if (!mModifiedTime.equals(other.mModifiedTime))
			return false;
		if (mMountPoint == null) {
			if (other.mMountPoint != null)
				return false;
		} else if (!mMountPoint.equals(other.mMountPoint))
			return false;
		if (mName == null) {
			if (other.mName != null)
				return false;
		} else if (!mName.equals(other.mName))
			return false;
		if (mOriginDevice == null) {
			if (other.mOriginDevice != null)
				return false;
		} else if (!mOriginDevice.equals(other.mOriginDevice))
			return false;
		if (mOriginDeviceId == null) {
			if (other.mOriginDeviceId != null)
				return false;
		} else if (!mOriginDeviceId.equals(other.mOriginDeviceId))
			return false;
		if (mPath == null) {
			if (other.mPath != null)
				return false;
		} else if (!mPath.equals(other.mPath))
			return false;
		if (mSyncType != other.mSyncType)
			return false;
		if (mType != other.mType)
			return false;
		return true;
	}
}
