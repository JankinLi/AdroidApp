package com.lichuan.test01.msg;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guoym on 15-6-27.
 */
public class SourceObject implements Parcelable {
    private int mSrc;

    public final static int SOURCE_START = 1;
    public final static int SOURCE_CLOTH_LIST = 2;
    public final static int SOURCE_Add_CLOTH = 3;
    public final static int SOURCE_Add_BOX = 4;
    public final static int SOURCE_Add_SUIT = 5;

    public final static String SourceName = "Source";

    public SourceObject(int src) {
        mSrc = src;
    }

    public int getSrcFrom() {
        return mSrc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mSrc);
    }

    private SourceObject(Parcel in) {
        mSrc = in.readInt();
    }

    public static final Parcelable.Creator<SourceObject> CREATOR
            = new Parcelable.Creator<SourceObject>() {
        public SourceObject createFromParcel(Parcel in) {
            return new SourceObject(in);
        }

        public SourceObject[] newArray(int size) {
            return new SourceObject[size];
        }
    };
}
