package com.lichuan.test01.service.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.lichuan.test01.datamodel.Suit;

import java.util.ArrayList;

/**
 * Created by guoym on 15-7-5.
 */
public class SuitDB {
    public final static String TABLE_NAME = "suit";

    public final static String Field_id = "id";
    public final static String Field_description = "description";
    public final static String Field_publish = "publish";

    public static String[] getColumns() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(Field_id);
        list.add(Field_description);
        list.add(Field_publish);
        String[] tmp = new String[list.size()];
        return list.toArray(tmp);
    }

    public static String createTable() {
        StringBuffer sb = new StringBuffer("Create Table ");
        sb.append(TABLE_NAME);
        sb.append(" ( ");
        sb.append(" ").append(Field_id).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" ").append(Field_description).append(" TEXT , ");
        sb.append(" ").append(Field_publish).append(" TEXT ");
        sb.append(" ); ");

        Log.d("SuitDb", "create table: " + sb.toString());
        return sb.toString();
    }

    public static String dropTable() {
        return "Drop Table " + TABLE_NAME + " ; ";
    }

    public static Suit createFromCursor(Cursor c) {
        long uid = c.getLong(c.getColumnIndex(Field_id));
        String description = c.getString(c.getColumnIndex(Field_description));
        String publish = c.getString(c.getColumnIndex(Field_publish));
        Suit suit = new Suit(Long.toString(uid), description);
        suit.setPublish(publish);
        return suit;
    }

    public static ContentValues createValuesForInsert(String desc, String publish) {
        ContentValues values = new ContentValues();

        values.put(Field_description, desc);
        values.put(Field_publish, publish);
        return values;
    }

    public static ContentValues createValuesForUpdate(String desc) {
        ContentValues values = new ContentValues();

        values.put(Field_description, desc);
        return values;
    }

    public static String getWhereConditionByID() {
        return Field_id + " = ?";
    }
}
