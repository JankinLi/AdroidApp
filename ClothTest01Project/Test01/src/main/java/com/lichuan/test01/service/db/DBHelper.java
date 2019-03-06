package com.lichuan.test01.service.db;

import android.database.Cursor;

/**
 * Created by guoym on 15-6-26.
 */
public class DBHelper {
    private static final String FIELD_ID_MAX = "IdMax";

    public static String createQuerySQLForFindMaxID(String tableName, String IDName) {
        return "select max( " + IDName + " ) " + " AS " + FIELD_ID_MAX + " from " + tableName;
    }

    public static long getIdFromCursor(Cursor c) {
        return c.getLong(c.getColumnIndex(FIELD_ID_MAX));
    }
}
