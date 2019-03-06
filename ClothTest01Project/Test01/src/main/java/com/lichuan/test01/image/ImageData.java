package com.lichuan.test01.image;

import android.graphics.Bitmap;

/**
 * Created by guoym on 15-6-8.
 */
public class ImageData {
    private static final String TAG = "ImageData";

    private long mUID;
    private String mDisplayName;
    private String mTitle;
    private long mModifiedDate;

    private boolean mImgIsBad;
    private Bitmap mBitmap;

    public ImageData(long uid) {
        mUID = uid;

        mDisplayName = "";
        mTitle = "";
        mBitmap = null;

        mImgIsBad = false;
    }

    public long getUID() {
        return mUID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDisplayName;
    }

    public void setInfo(String name, String title, long modified) {
        mDisplayName = name;
        mTitle = title;
        mModifiedDate = modified;
    }

    public boolean isNewer(ImageData other) {
        return mModifiedDate > other.mModifiedDate;
    }

    public boolean isOlder(ImageData other) {
        return mModifiedDate < other.mModifiedDate;
    }

    public Bitmap getPicture() {
        return mBitmap;
    }

    public boolean isImgBad() {
        return mImgIsBad;
    }

    public void setImgBad(boolean b) {
        mImgIsBad = b;
    }


    public void setBitmap(Bitmap b) {
        mBitmap = b;
    }

    public long getModifiedDate() {
        return mModifiedDate;
    }
}





