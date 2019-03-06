package com.lichuan.test01.datamodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TextView;

/**
 * Created by guoym on 15-6-28.
 */
public interface GridItem {
    boolean isSet();

    boolean isBitmapImage();

    Bitmap getBitmap();

    int getDrawableResId();

    String getMainInfo();

    void setOtherInfo(Context c, TextView view);
}
