package com.lichuan.test01.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.BaseAdapter;

import com.lichuan.test01.image.ImageData;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by guoym on 15-6-18.
 */
public class LoadImageHelper {
    private static final String TAG = "LoadImageHelper";

    private Context mContext;
    private Vector<ImageData> mList;
    private Thread mWorker;
    private boolean mFlag;
    private final static Object mLock = new Object();

    private BaseAdapter mAdapter;

    private int mScreenWidth;
    private int mScreenHeight;

    private final Handler mNotifyHandler = new Handler();

    private final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateUI();
        }
    };

    public LoadImageHelper(Context c) {
        mContext = c;

        mList = new Vector<ImageData>();

        mWorker = null;

        mFlag = true;

        mAdapter = null;
    }

    public void setBaseAdapter(BaseAdapter adp) {
        mAdapter = adp;
    }

    public void setScreenInfo(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
    }

    public void beginWorker() {
        mFlag = true;
        mWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mFlag) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "beginWorker sleep occur InterruptedException.");
                    }
                    synchronized (mLock) {
                        if (mList.size() == 0) {
                            continue;
                        } else {
                            ImageData data = mList.get(0);
                            mList.remove(0);
                            doWork(data);
                        }
                    }
                }
            }
        });
        mWorker.start();
    }

    public void stopWorker() {
        mFlag = false;
    }

    public void clear() {
        synchronized (mLock) {
            mList = new Vector<ImageData>();
        }
    }

//    public void loadPicture(ImageData data){
//        if( data.getPicture()!=null){
//            Log.d(TAG,"loadPicture fail, data.getPicture() is not null.");
//            return;
//        }
//
//        long uid = data.getUID();
//
//        synchronized (mLock){
//            for(ImageData value: mList){
//                if( value.getUID() == uid ){
//                    Log.d(TAG,"loadPicture fail, UID already begin loading." + uid);
//                    return;
//                }
//            }
//
//            mList.add(data);
//        }
//    }

    public void setData(ArrayList<ImageData> src) {
        synchronized (mLock) {
            mList = new Vector<ImageData>();
            mList.addAll(src);
        }
    }

    private void doWork(ImageData data) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(Long.toString(data.getUID())).build();
        Log.d(TAG, "doWork Image UID: " + data.getUID() + ", uri : " + uri.getPath());

        if (!mFlag) return;

        ContentResolver resolver = mContext.getContentResolver();

        if (!mFlag) return;

        int previewSize = ComputeImagePreviewSize();

        if (!mFlag) return;

        // 从Uri中读取图片资源
        try {
            //ParcelFileDescriptor fileDes = resolver.openFileDescriptor(uri,"r");
            //Log.d(TAG, " getFileDescriptor = "+fileDes.getFileDescriptor().toString());

            //byte[] mContent = FileUtil.readInputStream(resolver.openInputStream(Uri.parse(uri.toString())));
            Bitmap b = FileUtil.getBitmapFromStream(resolver.openInputStream(Uri.parse(uri.toString())), previewSize, previewSize);
            if (!mFlag) return;

            //Bitmap b = FileUtil.getBitmapFromFile(Uri.parse(uri.toString()).getEncodedPath(), 300, 300);
            data.setBitmap(b);
            if (b != null) {
                data.setImgBad(false);
                if (mFlag) {
                    mNotifyHandler.post(mUpdateResults);
                }
            } else {
                data.setImgBad(true);
            }
        } catch (Exception e) {
            data.setBitmap(null);
            Log.d(TAG, "Read Image resource fail.e=" + e.toString());
        }
    }

    private void updateUI() {
        if (mAdapter != null && mFlag) {
            mAdapter.notifyDataSetChanged();
            Log.d(TAG, "updateUI mAdapter.notifyDataSetChanged.");
        }
    }

    private int ComputeImagePreviewSize() {
        int ret = ComputePreviewSizeByScreenWH(mScreenWidth, mScreenHeight);
        Log.d(TAG, "ComputeImagePreviewSize ret=" + ret);
        return ret;
    }

    public static int ComputePreviewSizeByScreenWH(int width, int height) {
        int value = Math.min(width, height);
        int value1 = value / 5;
        int value2 = value1 / 100;
        int value3 = value2 * 100;
        return Math.max(100, value3);
    }
}
