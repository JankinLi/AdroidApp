package li.chuan.test.composecloth.ui.list;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import li.chuan.test.composecloth.entity.ImageInfo;

public class ListViewModel extends ViewModel {
    private final static String TAG = "ListViewModel";

    private MutableLiveData<List<ImageInfo>> info;
    public LiveData<List<ImageInfo>> getImageInfo(Activity a) {
        if (info == null) {
            info = new MutableLiveData<List<ImageInfo>>();
            loadImageInfo(a);
        }
        return info;
    }

    private void loadImageInfo(Activity a) {
        // do async operation to fetch image info
        Log.d(ListViewModel.TAG, "load Image Info");
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projImage = { MediaStore.Images.Media._ID
                , MediaStore.Images.Media.DATA
                ,MediaStore.Images.Media.SIZE
                ,MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = a.getContentResolver().query(mImageUri, projImage,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED+" desc");
        if (cursor == null){
            Log.e(TAG, "cursor is null");
            return;
        }
        scanCursor(cursor);
        cursor.close();
    }

    private void scanCursor(Cursor cursor){
        List<ImageInfo> all_info = new ArrayList<>();
        while (cursor.moveToNext()) {
            // 获取图片的路径
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))/1024;
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

            ImageInfo imageInfo = new ImageInfo(displayName, path, size);
            all_info.add(imageInfo);

            //用于展示相册初始化界面
            //mediaBeen.add(new MediaBean(MediaBean.Type.Image,path,size,displayName));

            // 获取该图片的父路径名
            String dirPath = new File(path).getParentFile().getAbsolutePath();
            imageInfo.setParentPath(dirPath);

            //存储对应关系
//            if (allPhotosTemp.containsKey(dirPath)) {
//                List<MediaBean> data = allPhotosTemp.get(dirPath);
//                data.add(new MediaBean(MediaBean.Type.Image,path,size,displayName));
//                continue;
//            } else {
//                List<MediaBean> data = new ArrayList<>();
//                data.add(new MediaBean(MediaBean.Type.Image,path,size,displayName));
//                allPhotosTemp.put(dirPath,data);
//            }

        }
        Log.d(TAG, "image info size=" + all_info.size());
        info.postValue(all_info);
    }

}
