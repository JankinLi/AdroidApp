package com.lichuan.test01.image;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by guoym on 15-6-8.
 */
public class ImagePicker implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ImagePicker";
    private AppCompatActivity mActivity;
    private Bundle mState;
    private ArrayList<ImageData> mData;
    private ImageLoadingFinish mHandler;

    private final static int MY_LOADER_ID = 999;

    public ImagePicker(AppCompatActivity a, Bundle state) {
        mActivity = a;
        mState = state;
        mData = new ArrayList<ImageData>();
        mHandler = null;
    }

    public void setFinishHandler(ImageLoadingFinish handler) {
        mHandler = handler;
    }

    ///pull all images from device.
    public void pullAllImage() {
        LoaderManager lm = mActivity.getSupportLoaderManager();
        lm.initLoader(MY_LOADER_ID, mState, this);
    }

    public ArrayList<ImageData> getAllImage() {
        return mData;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(mActivity,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case MY_LOADER_ID:
                // The asynchronous load is complete and the data
                // is now available for use. Only now can we associate
                // the queried Cursor with the SimpleCursorAdapter.
                saveImageData(cursor);
                if (mHandler != null) {
                    mHandler.loadFinish();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        switch (cursorLoader.getId()) {
            case MY_LOADER_ID:
                clearImageData();
                break;
        }
    }

    private void saveImageData(Cursor cursor) {
        if (cursor.getCount() == 0) {
            Log.d(TAG, "saveImageData cursor.getCount() is zero.");
            return;
        }

        mData = new ArrayList<ImageData>();

        Log.d(TAG, "saveImageData cursor.getCount() is " + cursor.getCount());
        cursor.moveToFirst();

        int colCount = cursor.getColumnCount();
        String[] colArray = cursor.getColumnNames();
        StringBuffer colBuffer = new StringBuffer();
        for (String s : colArray) {
            colBuffer.append("col :").append(s);
            colBuffer.append(" , ");
        }
        Log.d(TAG, "saveImageData ColumnNames : " + colBuffer.toString());
        while (!cursor.isAfterLast()) {
            int pos = cursor.getPosition();
            long uid = cursor.getLong(cursor.getColumnIndex("_id"));
            String fileName = cursor.getString(cursor.getColumnIndex("_display_name"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            //long date_added = cursor.getLong(cursor.getColumnIndex("date_added"));
            long date_modified = cursor.getLong(cursor.getColumnIndex("date_modified"));

            //String description = cursor.getString( cursor.getColumnIndex("description"));

            //int width = cursor.getInt(cursor.getColumnIndex("width"));
            //int height = cursor.getInt(cursor.getColumnIndex("height"));

            //Date d = new Date(date_added*1000);
            //Date d = new Date(date_modified * 1000);

//            Log.d(TAG, "saveImageData Content pos: " + pos + ", _id : " + uid  + " , " +
//                    "_display_name" + ":" + fileName + " , title=" + title +
//                    //" ,date_added= "+ date_added  +
//                    " ,date_modified= "+d.toString() +
//                    " , width=" + width + " , height="+height);

            ImageData data = new ImageData(uid);
            data.setInfo(fileName, title, date_modified);
            mData.add(data);

            cursor.moveToNext();
        }

        Collections.sort(mData, new DateComparator());
        Log.d(TAG, "saveImageData finish.");
    }

    private void clearImageData() {
        mData = new ArrayList<ImageData>();
    }

    private class DateComparator implements Comparator<ImageData> {

        @Override
        public int compare(ImageData imageData, ImageData imageData2) {
            if (imageData.isNewer(imageData2)) {
                return -1;
            } else if (imageData.isOlder(imageData2)) {
                return 1;
            }
            return 0;
        }
    }


}
