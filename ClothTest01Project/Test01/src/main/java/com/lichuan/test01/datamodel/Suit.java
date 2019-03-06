package com.lichuan.test01.datamodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TextView;

import com.lichuan.test01.R;

import java.util.ArrayList;

/**
 * Created by guoym on 15-7-5.
 */
public class Suit implements GridItem {
    private ArrayList<Cloth> mClothes;
    private String mName;
    private String mDescription;
    private int mClothIndex;

    private String mPublish;

    public final static String UNKNOWN_PUBLISH = "-";

    public Suit(String name, String desc) {
        mName = name;

        mDescription = desc;

        mClothes = new ArrayList<Cloth>();

        mClothIndex = -1;

        mPublish = UNKNOWN_PUBLISH;
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

    public String getPublish() {
        return mPublish;
    }

    public void setPublish(String publish) {
        mPublish = publish;
    }

    @Override
    public boolean isSet() {
        return true;
    }

    @Override
    public boolean isBitmapImage() {
        return mClothes.size() != 0;

    }

    @Override
    public Bitmap getBitmap() {
        if (mClothes.size() == 0) {
            return null;
        }
        mClothIndex++;
        if (mClothIndex >= mClothes.size()) {
            mClothIndex = 0;
        }

        Cloth c = mClothes.get(mClothIndex);
        if (c.isBad()) {
            return null;
        }
        return c.getBitmap();
    }

    @Override
    public int getDrawableResId() {
        return R.drawable.ic_launcher;
    }

    @Override
    public String getMainInfo() {
        return mDescription;
    }

    public int getClothCount() {
        return (mClothes != null ? mClothes.size() : 0);
    }

    @Override
    public void setOtherInfo(Context c, TextView view) {
        int countOfClothes = getClothCount();
        String fmtStr = c.getString(R.string.suit_other_info_format);
        String message = String.format(fmtStr, countOfClothes);
        view.setText(message);
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

    public ArrayList<Cloth> getAllClothesBelongThisSuit() {
        ArrayList<Cloth> ret = new ArrayList<Cloth>();
        if (mClothes != null) {
            ret.addAll(mClothes);
        }
        return ret;
    }

    public void clearAllCloth(){
        if( mClothes!= null ){
            mClothes.clear();
        }
        mClothes = new ArrayList<Cloth>();
    }
}
