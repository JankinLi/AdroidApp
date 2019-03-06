package com.lichuan.test01.service.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.lichuan.test01.datamodel.Box;
import com.lichuan.test01.datamodel.Cloth;
import com.lichuan.test01.datamodel.RootData;

import java.util.ArrayList;

/**
 * Created by guoym on 15-6-4.
 */
public class BoxMappingClothDb {

    public final static String TABLE_NAME = "box_cloth";

    public final static String Field_id = "id";

    public final static String Field_box_id = "boxId";

    public final static String Field_cloth_id = "clothId";

    private static final String TAG = "BoxMappingClothDb";

    public static String[] getColumns() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(Field_id);
        list.add(Field_box_id);
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
        sb.append(" ").append(Field_box_id).append(" INTEGER , ");
        sb.append(" ").append(Field_cloth_id).append(" INTEGER ");
        sb.append(" ); ");

        return sb.toString();
    }

    public static void makeMappingBetweenBoxAndCloth(Cursor c, RootData root) {
        long uid = c.getLong(c.getColumnIndex(Field_id));
        String boxName = c.getString(c.getColumnIndex(Field_box_id));
        String clothName = c.getString(c.getColumnIndex(Field_cloth_id));

        Box box = root.findBox(boxName);
        if (box == null) {
            Log.d(TAG, "makeMappingBetweenBoxAndCloth mapping fail. box is not found.uid=" + uid + ", boxName=" + boxName);
            return;
        }

        Cloth cloth = root.findCloth(clothName);
        if (cloth == null) {
            Log.d(TAG, "makeMappingBetweenBoxAndCloth mapping fail. cloth is not found.uid=" + uid + ", boxName=" + boxName + ",clothName=" + clothName);
            return;
        }

        box.addCloth(cloth);
    }

    public static String createWhereStatement() {
        return Field_box_id + "=?" + " AND " + Field_cloth_id + "=?";
    }

    public static String createBoxWhereStatement() {
        return Field_box_id + "=?";
    }

    public static String createClothWhereStatement() {
        return Field_cloth_id + "=?";
    }

    public static ContentValues createValuesForInsert(int boxId, int clothId) {
        ContentValues values = new ContentValues();

        values.put(Field_box_id, boxId);
        values.put(Field_cloth_id, clothId);

        return values;
    }
}
