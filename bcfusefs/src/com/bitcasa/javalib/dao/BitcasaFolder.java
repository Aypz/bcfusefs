package com.bitcasa.javalib.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BitcasaFolder extends BitcasaItem {

	/**	This property will only be returned for folder creation request **/
	public static final String TAG_STATUS		= "status";
	
	// folder create status
	public static final String STATUS_CREATED	= "created";
	public static final String STATUS_EXIST		= "exist";		// TODO: come back and check the actual text
	
	@JsonProperty(TAG_STATUS)
	private String mStatus;
	
	public String getCreateFolderStatus() {
		return mStatus;
	}

	@Override
	public String toString() {
		return "BitcasaFolder [mStatus=" + mStatus + ", mCategory=" + mCategory
				+ ", mType=" + mType + ", mName=" + mName + ", mPath=" + mPath
				+ ", mModifiedTime=" + mModifiedTime + ", mMirrored="
				+ mMirrored + ", mMountPoint=" + mMountPoint + ", mDeleted="
				+ mDeleted + ", mOriginDevice=" + mOriginDevice
				+ ", mOriginDeviceId=" + mOriginDeviceId + ", mSyncType="
				+ mSyncType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((mStatus == null) ? 0 : mStatus.hashCode());
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
		BitcasaFolder other = (BitcasaFolder) obj;
		if (mStatus == null) {
			if (other.mStatus != null)
				return false;
		} else if (!mStatus.equals(other.mStatus))
			return false;
		return true;
	}
}
