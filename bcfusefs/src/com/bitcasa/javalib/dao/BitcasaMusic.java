package com.bitcasa.javalib.dao;

public class BitcasaMusic extends BitcasaItem {

	@Override
	public String toString() {
		return "BitcasaMusic [mCategory=" + mCategory + ", mType=" + mType
				+ ", mName=" + mName + ", mPath=" + mPath + ", mModifiedTime="
				+ mModifiedTime + ", mMirrored=" + mMirrored + ", mMountPoint="
				+ mMountPoint + ", mDeleted=" + mDeleted + ", mOriginDevice="
				+ mOriginDevice + ", mOriginDeviceId=" + mOriginDeviceId
				+ ", mSyncType=" + mSyncType + "]";
	}

}
