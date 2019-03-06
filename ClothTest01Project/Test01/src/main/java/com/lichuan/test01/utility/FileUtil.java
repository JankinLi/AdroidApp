package com.lichuan.test01.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by guoym on 15-6-18.
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    public static void writeInputStreamIntoOutputStream(InputStream is, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;

        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
    }

    /**
     * InputStream to byte
     *
     * @param inStream resource stream
     * @return array of byte
     * @throws Exception stream errors
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }

        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();

        return data;
    }

    /**
     * Byte to bitmap
     *
     * @param bytes input array
     * @param opts optional parameter for read setting
     * @return bitmap instance of image.
     */
    public static Bitmap getBitmapFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null) {
            if (opts != null) {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            } else {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }

        return null;
    }

    public static Bitmap getBitmapFromStream(InputStream inStream, int width, int height) throws Exception {
        byte[] array = readInputStream(inStream);
        if (array.length <= 0) {
            Log.d(TAG, "array.length is invalid");
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(array, 0, array.length, options);

        if (options.outHeight == -1 || options.outWidth == -1) {
            Log.d(TAG, "Height or width is invalid, options.outWidth=" + options.outWidth + ",options.outHeight=" + options.outHeight);
            return null;
        }

        int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

        Log.d(TAG, "getBitmapFromStream options.outWidth=" + options.outWidth + ",options.outHeight=" + options.outHeight);

        int inSampleSize = 1;
        if (heightRatio > 1 && widthRatio > 1) {
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
            Log.d(TAG, "getBitmapFromStream inSampleSize=" + inSampleSize);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return getBitmapFromBytes(array, options);
    }

    public static Bitmap getBitmapFromFile(String filePath, int width, int height) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

        Log.d(TAG, "getBitmapFromFile options.outWidth=" + options.outWidth + ",options.outHeight=" + options.outHeight);

        int inSampleSize = 1;
        if (heightRatio > 1 && widthRatio > 1) {
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
            Log.d(TAG, "getBitmapFromFile inSampleSize=" + inSampleSize);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(filePath, options);
    }
}
