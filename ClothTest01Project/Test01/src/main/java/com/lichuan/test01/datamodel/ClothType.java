package com.lichuan.test01.datamodel;

import android.content.Context;

import com.lichuan.test01.R;

/**
 * Created by guoym on 15-5-30.
 */
public class ClothType {
    public static final int UpperBody = 1;
    public static final int LowerBody = 2;

    public static String convertString(Context c, int clothType) {
        if (UpperBody == clothType) {
            return c.getString(R.string.cloth_type_upper_body);
        }
        if (LowerBody == clothType) {
            return c.getString(R.string.cloth_type_lower_body);
        }
        return null;
    }
}
