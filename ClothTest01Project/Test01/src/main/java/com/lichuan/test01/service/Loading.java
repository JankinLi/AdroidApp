package com.lichuan.test01.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.lichuan.test01.MainActivity;
import com.lichuan.test01.datamodel.Box;
import com.lichuan.test01.datamodel.Cloth;
import com.lichuan.test01.datamodel.RootData;
import com.lichuan.test01.datamodel.Suit;
import com.lichuan.test01.service.db.BoxDb;
import com.lichuan.test01.service.db.BoxMappingClothDb;
import com.lichuan.test01.service.db.ClothDb;
import com.lichuan.test01.service.db.SuitDB;
import com.lichuan.test01.service.db.SuitMappingClothDB;

/**
 * Created by guoym on 15-6-3.
 */
public class Loading extends Service {
    private final IBinder binder = new MyBinder();
    private final String TAG_Loading = "LoadingService";

    private MyDB mMyDB;

    public static final int SUCCESS = 100;
    public static final int FAIL = 0;

    public class MyBinder extends Binder {
        public Loading getService() {
            return Loading.this;
        }
    }


    public IBinder onBind(Intent intent) {
        Log.d(TAG_Loading, "Loading onBind");
        return binder;
    }

    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG_Loading, "Loading onRebind");
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG_Loading, "Loading onCreate");
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG_Loading, "Loading onDestroy");
    }

    public void setMyDB(MyDB db) {
        mMyDB = db;
    }

    public void pullDataFromDB() {
        if (mMyDB == null) {
            sendBroadcastMessage(FAIL);
            return;
        }

        Log.d(TAG_Loading, "pullDataFromDB mMyDB isn't null");
        new Thread(new Runnable() {
            @Override
            public void run() {
                makeObjectFromDB();
            }
        }).start();


    }

    private void makeObjectFromDB() {
        SQLiteDatabase dbHandle = mMyDB.getReadableDatabase();
        if (dbHandle == null) {
            Log.d(TAG_Loading, "makeObjectFromDB getReadableDatabase fail.");
            sendBroadcastMessage(FAIL);
            return;
        }

        RootData root = RootData.getInstance();
        root.reset();

        Log.d(TAG_Loading, "makeObjectFromDB getReadableDatabase finish");

        Cursor cursor = dbHandle.query(BoxDb.TABLE_NAME, BoxDb.getColumns(), null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0)
                Log.d(TAG_Loading, "select box ,count=" + cursor.getCount());

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Log.d(TAG_Loading, "ColumnCount=" + cursor.getColumnCount());

                Box b = BoxDb.createFromCursor(cursor);
                root.addBox(b);
                cursor.moveToNext();
            }
            cursor.close();

            sendBroadcastMessage(SUCCESS / 10);
        }

        cursor = dbHandle.query(ClothDb.TABLE_NAME, ClothDb.getColumns(), null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0)
                Log.d(TAG_Loading, "select cloth ,count=" + cursor.getCount());

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Cloth cloth = ClothDb.createFromCursor(cursor);
                root.addCloth(cloth);
                cursor.moveToNext();
            }
            cursor.close();

            sendBroadcastMessage(SUCCESS / 8);
        }

        cursor = dbHandle.query(BoxMappingClothDb.TABLE_NAME, BoxMappingClothDb.getColumns(), null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0)
                Log.d(TAG_Loading, "select box_cloth ,count=" + cursor.getCount());

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                BoxMappingClothDb.makeMappingBetweenBoxAndCloth(cursor, root);
                cursor.moveToNext();
            }
            cursor.close();

            sendBroadcastMessage(SUCCESS / 5);
        }

        cursor = dbHandle.query(SuitDB.TABLE_NAME, SuitDB.getColumns(), null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0)
                Log.d(TAG_Loading, "select suit ,count=" + cursor.getCount());

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Suit suit = SuitDB.createFromCursor(cursor);
                root.addSuit(suit);
                cursor.moveToNext();
            }
            cursor.close();

            sendBroadcastMessage(SUCCESS / 4);
        }

        cursor = dbHandle.query(SuitMappingClothDB.TABLE_NAME, SuitMappingClothDB.getColumns(), null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0)
                Log.d(TAG_Loading, "select suit_cloth ,count=" + cursor.getCount());

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                SuitMappingClothDB.makeMappingBetweenSuitAndCloth(cursor, root);
                cursor.moveToNext();
            }
            cursor.close();

            sendBroadcastMessage(SUCCESS / 2);
        }

        dbHandle.close();
        Log.d(TAG_Loading, "makeObjectFromDB : this is finish which parse object from DB. ");

        sendBroadcastMessage(SUCCESS);
    }

    private void sendBroadcastMessage(int extraParam) {
        //发送特定action的广播
        Intent intent = new Intent();
        intent.setAction(MainActivity.FLAG_MY_CLOTH_MAIN_RECEIVER);
        intent.putExtra(MainActivity.FLAG_EXTRA_PARAM, extraParam);
        sendBroadcast(intent);
    }
}
