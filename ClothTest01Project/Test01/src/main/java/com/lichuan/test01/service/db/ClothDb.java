package com.lichuan.test01.service.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.lichuan.test01.datamodel.Cloth;

import java.util.ArrayList;

/**
 * Created by guoym on 15-6-4.
 */
public class ClothDb {
    public final static String TABLE_NAME = "cloth";

    public final static String Field_id = "id";
    public final static String Field_description = "description";
    public final static String Field_imageFilePath = "imageFilePath";
    public final static String Field_type = "type";

    public static String[] getColumns() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(Field_id);
        list.add(Field_description);
        list.add(Field_imageFilePath);
        list.add(Field_type);

        String[] tmp = new String[list.size()];
        return list.toArray(tmp);
    }

    public static String createTable() {
        StringBuffer sb;
        sb = new StringBuffer("Create Table ");
        sb.append(TABLE_NAME);
        sb.append(" ( ");
        sb.append(" ").append(Field_id).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" ").append(Field_description).append(" TEXT , ");
        sb.append(" ").append(Field_imageFilePath).append(" TEXT ,");
        sb.append(" ").append(Field_type).append(" INTEGER ");
        sb.append(" ); ");

        Log.d("ClothDb", "create table: " + sb.toString());
        return sb.toString();
    }

    public static Cloth createFromCursor(Cursor c) {
        long uid = c.getLong(c.getColumnIndex(Field_id));
        String description = c.getString(c.getColumnIndex(Field_description));
        String filePath = c.getString(c.getColumnIndex(Field_imageFilePath));
        int type = c.getInt(c.getColumnIndex(Field_type));
        Cloth cloth = new Cloth(Long.toString(uid), description);
        cloth.setImageFilePath(filePath);
        cloth.setType(type);
        return cloth;
    }


    public static ContentValues createValuesForInsert(String filePath, String desc, int clothType) {
        ContentValues values = new ContentValues();

        values.put(Field_imageFilePath, filePath);
        values.put(Field_description, desc);
        values.put(Field_type, clothType);

        return values;
    }

    public static ContentValues createValuesForUpdate(String desc, int clothType) {
        ContentValues values = new ContentValues();

        values.put(Field_description, desc);
        values.put(Field_type, clothType);

        return values;
    }

    public static String getWhereConditionByID() {
        return Field_id + " = ?";
    }
}
