package com.lichuan.test01.datamodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TextView;

import com.lichuan.test01.R;

import java.util.ArrayList;

/**
 * Created by guoym on 15-5-30.
 */
public class Box implements GridItem {
    private ArrayList<Cloth> mClothes;
    private String mName;
    private String mDescription;
    private int mImageType;
    private String mImagePath;

    public final static String InvalidPath = "_";

    public final static String dumbBoxString = "_0";

    public Box(String name, String description) {
        mName = name;
        mDescription = description;

        mClothes = new ArrayList<Cloth>();

        mImageType = BoxImageType.TheArmoireType;

        mImagePath = InvalidPath;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getImageType() {
        return mImageType;
    }

    public void setImageType(int type) {
        mImageType = type;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String path) {
        mImagePath = path;
    }

    public void addCloth(Cloth c) {
        if (mClothes == null) {
            mClothes = new ArrayList<Cloth>();
        }

        mClothes.add(c);
    }

    public boolean removeCloth(Cloth c) {
        if (mClothes == null) {
            return false;
        }
        int i = 0;
        for (Cloth th : mClothes) {
            if (th.getName().equals(c.getName())) {
                mClothes.remove(i);
                return true;
            }
            i++;
        }
        return false;
    }

    public void clearCloth() {
        if (mClothes == null) {
            return;
        }

        mClothes.clear();
    }

    public int getClothCount() {
        return (mClothes != null ? mClothes.size() : 0);
    }

    public boolean containCloth(Cloth c) {
        if (getClothCount() == 0) {
            return false;
        }

        for (Cloth th : mClothes) {
            if (th.getName().equals(c.getName())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Cloth> getAllClothesBelongThisBox() {
        ArrayList<Cloth> ret = new ArrayList<Cloth>();
        if (mClothes != null) {
            ret.addAll(mClothes);
        }
        return ret;
    }

    @Override
    public boolean isSet() {
        return true;
    }

    @Override
    public boolean isBitmapImage() {
        return false;
    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }

    @Override
    public int getDrawableResId() {
        return BoxImageType.computeResourceIdByImageType(mImageType);
    }

    @Override
    public String getMainInfo() {
        return mDescription;
    }

    @Override
    public void setOtherInfo(Context c, TextView view) {
        int countOfClothes = getClothCount();
        String fmtStr = c.getString(R.string.box_other_info_format);
        String message = String.format(fmtStr, countOfClothes);
        view.setText(message);
    }
}

