package com.lichuan.test01.msg;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guoym on 15-6-22.
 */
public class PicViewObject implements Parcelable {
    private int mSource;
    private long mUID;
    private String mPath;

    public static final int FROM_PHOTO = 1;
    public static final int FROM_CLOTH = 2;

    public PicViewObject(int src, long uid, String path) {
        mSource = src;
        mPath = path;
        mUID = uid;
    }

    public PicViewObject(long uid) {
        mSource = FROM_PHOTO;
        mPath = "";
        mUID = uid;
    }

    public PicViewObject(String path) {
        mSource = FROM_CLOTH;
        mPath = path;
        mUID = 0;
    }

    public String getPath() {
        return mPath;
    }

    public long getUID() {
        return mUID;
    }

    public int getSource() {
        return mSource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mSource);
        parcel.writeLong(mUID);
        parcel.writeString(mPath);
    }

    private PicViewObject(Parcel in) {
        mSource = in.readInt();
        mUID = in.readLong();
        mPath = in.readString();
    }

    public static final Parcelable.Creator<PicViewObject> CREATOR
            = new Parcelable.Creator<PicViewObject>() {
        public PicViewObject createFromParcel(Parcel in) {
            return new PicViewObject(in);
        }

        public PicViewObject[] newArray(int size) {
            return new PicViewObject[size];
        }
    };
}
