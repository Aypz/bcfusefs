package com.bitcasa.javalib.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BitcasaFile extends BitcasaItem {

	public static final String TAG_ALBUM			= "album";
	public static final String TAG_EXTENSION		= "extension";
	public static final String TAG_MANIFEST_NAME	= "manifest_name";
	public static final String TAG_MIME				= "mime";
	public static final String TAG_ID				= "id";
	public static final String TAG_INCOMPLETE		= "incomplete";
	public static final String TAG_SIZE				= "size";
	
	@JsonProperty(TAG_ALBUM)
	protected String mAlbum;
	@JsonProperty(TAG_EXTENSION)
	protected String mExtension;
	@JsonProperty(TAG_MANIFEST_NAME)
	protected String mManifestName;
	@JsonProperty(TAG_MIME)
	protected String mMime;
	@JsonProperty(TAG_ID)
	protected String mId;
	@JsonProperty(TAG_INCOMPLETE)
	protected boolean mIncomplete;
	@JsonProperty(TAG_SIZE)
	protected long mSize;
	
	public String getAlbum() {
		return mAlbum;
	}
	
	public String getExtension() {
		return mExtension;
	}
	
	public String getManifestName() {
		return mManifestName;
	}
	
	public String getMime() {
		return mMime;
	}
	
	public String getId() {
		return mId;
	}
	
	public boolean isComplete() {
		return !mIncomplete;
	}
	
	public long getSize() {
		return mSize;
	}

	@Override
	public String toString() {
		return "BitcasaFile [mAlbum=" + mAlbum + ", mExtension=" + mExtension
				+ ", mManifestName=" + mManifestName + ", mMime=" + mMime
				+ ", mId=" + mId + ", mIncomplete=" + mIncomplete + ", mSize="
				+ mSize + ", mCategory=" + mCategory + ", mType=" + mType
				+ ", mName=" + mName + ", mPath=" + mPath + ", mModifiedTime="
				+ mModifiedTime + ", mMirrored=" + mMirrored + ", mMountPoint="
				+ mMountPoint + ", mDeleted=" + mDeleted + ", mOriginDevice="
				+ mOriginDevice + ", mOriginDeviceId=" + mOriginDeviceId
				+ ", mSyncType=" + mSyncType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((mAlbum == null) ? 0 : mAlbum.hashCode());
		result = prime * result
				+ ((mExtension == null) ? 0 : mExtension.hashCode());
		result = prime * result + ((mId == null) ? 0 : mId.hashCode());
		result = prime * result + (mIncomplete ? 1231 : 1237);
		result = prime * result
				+ ((mManifestName == null) ? 0 : mManifestName.hashCode());
		result = prime * result + ((mMime == null) ? 0 : mMime.hashCode());
		result = prime * result + (int) (mSize ^ (mSize >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BitcasaFile other = (BitcasaFile) obj;
		if (mAlbum == null) {
			if (other.mAlbum != null)
				return false;
		} else if (!mAlbum.equals(other.mAlbum))
			return false;
		if (mExtension == null) {
			if (other.mExtension != null)
				return false;
		} else if (!mExtension.equals(other.mExtension))
			return false;
		if (mId == null) {
			if (other.mId != null)
				return false;
		} else if (!mId.equals(other.mId))
			return false;
		if (mIncomplete != other.mIncomplete)
			return false;
		if (mManifestName == null) {
			if (other.mManifestName != null)
				return false;
		} else if (!mManifestName.equals(other.mManifestName))
			return false;
		if (mMime == null) {
			if (other.mMime != null)
				return false;
		} else if (!mMime.equals(other.mMime))
			return false;
		if (mSize != other.mSize)
			return false;
		return true;
	}
}
