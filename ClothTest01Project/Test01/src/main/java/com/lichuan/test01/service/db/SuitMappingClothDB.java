package com.lichuan.test01.service.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.lichuan.test01.datamodel.Cloth;
import com.lichuan.test01.datamodel.RootData;
import com.lichuan.test01.datamodel.Suit;

import java.util.ArrayList;

/**
 * Created by guoym on 15-7-5.
 */
public class SuitMappingClothDB {
    public final static String TABLE_NAME = "suit_cloth";

    public final static String Field_id = "id";

    public final static String Field_suit_id = "boxId";

    public final static String Field_cloth_id = "clothId";

    private static final String TAG = "SuitMappingClothDb";

    public static String[] getColumns() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(Field_id);
        list.add(Field_suit_id);
        list.add(Field_cloth_id);

        String[] tmp = new String[list.size()];
        return list.toArray(tmp);
    }

    public static String createTable() {
        StringBuffer sb;
        sb = new StringBuffer("Create Table ");
        sb.append(TABLE_NAME);
        sb.append(" ( ");
        sb.append(" ").append(Field_id).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" ").append(Field_suit_id).append(" INTEGER , ");
        sb.append(" ").append(Field_cloth_id).append(" INTEGER ");
        sb.append(" ); ");

        return sb.toString();
    }

    public static void makeMappingBetweenSuitAndCloth(Cursor c, RootData root) {
        long uid = c.getLong(c.getColumnIndex(Field_id));
        String suitName = c.getString(c.getColumnIndex(Field_suit_id));
        String clothName = c.getString(c.getColumnIndex(Field_cloth_id));

        Suit suit = root.findSuit(suitName);
        if (suit == null) {
            Log.d(TAG, "makeMappingBetweenSuitAndCloth mapping fail. box is not found.uid=" + uid + ", suitName=" + suitName);
            return;
        }

        Cloth cloth = root.findCloth(clothName);
        if (cloth == null) {
            Log.d(TAG, "makeMappingBetweenSuitAndCloth mapping fail. cloth is not found.uid=" + uid + ", suitName=" + suitName + ",clothName=" + clothName);
            return;
        }

        suit.addCloth(cloth);
    }

    public static String createWhereStatement() {
        return Field_suit_id + "=?" + " AND " + Field_cloth_id + "=?";
    }

    public static ContentValues createValuesForInsert(int suitId, int clothId) {
        ContentValues values = new ContentValues();

        values.put(Field_suit_id, suitId);
        values.put(Field_cloth_id, clothId);

        return values;
    }

    public static String createWhereStatementForDeleteCloth() {
        return Field_suit_id + "=?" ;
    }
}
