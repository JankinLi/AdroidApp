package com.lichuan.test01.datamodel;

import android.graphics.Bitmap;

/**
 * Created by guoym on 15-6-26.
 */
public interface BitmapOwner {
    Bitmap getBitmap();

    void setBitmap(Bitmap img);

    boolean isBad();

    void setBadFlag(boolean b);

    String getImagePath();
}
