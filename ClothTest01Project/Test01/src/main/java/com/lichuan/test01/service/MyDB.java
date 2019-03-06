package com.lichuan.test01.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lichuan.test01.service.db.BoxDb;
import com.lichuan.test01.service.db.BoxMappingClothDb;
import com.lichuan.test01.service.db.ClothDb;
import com.lichuan.test01.service.db.SuitDB;
import com.lichuan.test01.service.db.SuitMappingClothDB;

/**
 * Created by guoym on 15-6-4.
 */
public class MyDB extends SQLiteOpenHelper {
    private final static String TAG = "MyDB";
    private final static String DATABASE_NAME = "myAllCloth";
    private final static int VERSION = 1;


    public MyDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "MyDB onCreate");

        sqLiteDatabase.execSQL(BoxDb.createTable());

        sqLiteDatabase.execSQL(ClothDb.createTable());

        sqLiteDatabase.execSQL(BoxMappingClothDb.createTable());

        sqLiteDatabase.execSQL(SuitDB.createTable());

        sqLiteDatabase.execSQL(SuitMappingClothDB.createTable());

        Log.d(TAG, "MyDB onCreate finish");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        Log.d(TAG, "MyDB onUpgrade i=" + i + "," + "i2=" + i2);

//        if( (i==1) && (i2 == 2) ) {
//            //suit upgrade
//            SuitTableUpgrade(sqLiteDatabase);
//            return;
//        }

        //TODO in further , if some table is changed or we need append new table, then write some code for upgrade.
    }

    private void BoxTableUpgrade(SQLiteDatabase sqLiteDatabase) {
        //I had changed box table. must be delete box table, create it again.
        sqLiteDatabase.execSQL(BoxDb.dropTable());
        sqLiteDatabase.execSQL(BoxDb.createTable());
    }

    private void SuitTableUpgrade(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SuitDB.createTable());

        sqLiteDatabase.execSQL(SuitMappingClothDB.createTable());
    }

    public void onOpen(android.database.sqlite.SQLiteDatabase db) {
        super.onOpen(db);

        Log.d(TAG, "MyDB onOpen");
    }
}
