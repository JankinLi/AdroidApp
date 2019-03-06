package com.lichuan.test01.datamodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TextView;

import com.lichuan.test01.R;

/**
 * Created by guoym on 15-5-30.
 */
public class Cloth implements BitmapOwner, GridItem, OptionalItem {
    private final String mName;
    private String mDescription;
    private String mImageFilePath;
    private int mType;

    private boolean mIsBad;
    private Bitmap mImg;

    private boolean mSelected;

    public Cloth(String name, String Description) {
        mName = name;
        mDescription = Description;
        mImageFilePath = null;
        mType = ClothType.UpperBody;

        mImg = null;
        mIsBad = false;

        mSelected = false;
    }

    public void setDescription(String Description) {
        mDescription = Description;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getImagePath() {
        return mImageFilePath;
    }

    public void setImageFilePath(String path) {
        mImageFilePath = path;
    }

    public int getType() {
        return mType;
    }

    public void setType(int Type) {
        mType = Type;
    }

    @Override
    public Bitmap getBitmap() {
        return mImg;
    }

    @Override
    public void setBitmap(Bitmap img) {
        mImg = img;
    }

    @Override
    public boolean isBad() {
        return mIsBad;
    }

    @Override
    public void setBadFlag(boolean b) {
        mIsBad = b;
    }

    @Override
    public boolean isSet() {
        return false;
    }

    @Override
    public boolean isBitmapImage() {
        if (isBad()) {
            return false;
        }
        return mImg != null;
    }

    @Override
    public int getDrawableResId() {
        if (isBad()) {
            return R.drawable.invalid;
        }

        if (mImg == null) {
            return R.drawable.ic_launcher;
        }
        return R.drawable.ic_launcher;
    }

    @Override
    public String getMainInfo() {
        return getDescription();
    }

    @Override
    public void setOtherInfo(Context c, TextView view) {
        view.setText(ClothType.convertString(c, mType));
    }

    @Override
    public boolean isSelected() {
        return mSelected;
    }

    @Override
    public void setSelected(boolean b) {
        mSelected = b;
    }
}
