package com.lichuan.test01.service.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.lichuan.test01.datamodel.Box;

import java.util.ArrayList;

/**
 * Created by guoym on 15-6-4.
 */
public class BoxDb {
    public final static String TABLE_NAME = "box";

    public final static String Field_id = "id";
    public final static String Field_description = "description";
    public final static String Field_image_type = "imageType";
    public final static String Field_image_path = "imagePath";

    public static String[] getColumns() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(Field_id);
        list.add(Field_description);
        list.add(Field_image_type);
        list.add(Field_image_path);

        String[] tmp = new String[list.size()];
        return list.toArray(tmp);
    }

    public static String createTable() {
        StringBuffer sb = new StringBuffer("Create Table ");
        sb.append(BoxDb.TABLE_NAME);
        sb.append(" ( ");
        sb.append(" ").append(Field_id).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" ").append(Field_description).append(" TEXT , ");
        sb.append(" ").append(Field_image_path).append(" TEXT , ");
        sb.append(" ").append(Field_image_type).append(" INTEGER ");
        sb.append(" ); ");

        Log.d("BoxDb", "create table: " + sb.toString());
        return sb.toString();
    }

    public static String dropTable() {
        return "Drop Table " + TABLE_NAME + " ; ";
    }

    public static Box createFromCursor(Cursor c) {
        long uid = c.getLong(c.getColumnIndex(Field_id));
        String description = c.getString(c.getColumnIndex(Field_description));
        int imageType = c.getInt(c.getColumnIndex(Field_image_type));

        String imagePath = c.getString(c.getColumnIndex(Field_image_path));

        Box box = new Box(Long.toString(uid), description);
        box.setImagePath(imagePath);
        box.setImageType(imageType);
        return box;
    }

    public static ContentValues createValuesForInsert(String desc, int imageType, String path) {
        ContentValues values = new ContentValues();

        values.put(Field_description, desc);
        values.put(Field_image_path, path);
        values.put(Field_image_type, imageType);

        return values;
    }

    public static ContentValues createValuesForUpdate(String desc, int imageType) {
        ContentValues values = new ContentValues();

        values.put(Field_description, desc);
        values.put(Field_image_type, imageType);

        return values;
    }

    public static String getWhereConditionByID() {
        return Field_id + " = ?";
    }
}
