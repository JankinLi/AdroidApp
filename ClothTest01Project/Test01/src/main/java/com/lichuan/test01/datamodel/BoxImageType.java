package com.lichuan.test01.datamodel;

import com.lichuan.test01.R;

/**
 * Created by guoym on 15-6-28.
 */
public class BoxImageType {

    public final static int Use_ImagePath = 0;
    public final static int TheArmoireType = 1;
    public final static int TheCabinetType = 2;
    public final static int ThePinkBoxType = 3;
    public final static int TheHyalineBoxType = 4;
    public final static int TheRedBoxType = 5;
    public final static int TheGripeSackType = 6;


    private static int mImageLibTypeList[] = {
            TheArmoireType,
            TheCabinetType,
            ThePinkBoxType,
            TheHyalineBoxType,
            TheRedBoxType,
            TheGripeSackType
    };

    private final static int mAllImagesIDList[] = {
            R.drawable.box_1,
            R.drawable.box_2,
            R.drawable.box_3,
            R.drawable.box_4,
            R.drawable.box_5,
            R.drawable.box_6,
    };


    public static int getImageLibCount() {
        return mImageLibTypeList.length;
    }

    public static int getImageTypeByIndex(int pos) {
        return mImageLibTypeList[pos];
    }

    public static int getImageTypeIndex(int value) {
        int i = 0;
        for (int v : mImageLibTypeList) {
            if (v == value) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static int computeResourceIdByImageType(int value) {
        int index = getImageTypeIndex(value);
        return mAllImagesIDList[index];
    }

    public static int computeResourceIdByIndex(int index) {
        return mAllImagesIDList[index];
    }
}
