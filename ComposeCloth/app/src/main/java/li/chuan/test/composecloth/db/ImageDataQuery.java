package li.chuan.test.composecloth.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import li.chuan.test.composecloth.entity.ImageData;

public class ImageDataQuery {
    private static final String TAG = "ImageDataQuery";
    private ImageInfoSQLiteHelper mHelper;
    public ImageDataQuery(Context context ){
        mHelper = new ImageInfoSQLiteHelper(context, "Infos", null, 1);
    }

    public List<ImageData> queryAll(){
        SQLiteDatabase db = mHelper.getReadableDatabase();

        ArrayList<ImageData> result = new ArrayList<>();

        //执行相应的Api
        Cursor cursor = db.query("ImageInfo", null, null, null, null, null, null);
        if( cursor == null ){
            db.close();
            return result;
        }

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex("id"));
            Log.d(TAG, "id=" + id);
            String name_value = cursor.getString(cursor.getColumnIndex("name"));
            Log.d(TAG, "name_value=" + name_value);
            String path_value = cursor.getString(cursor.getColumnIndex("path"));
            Log.d(TAG, "path_value=" + path_value);
            int kind_value = cursor.getInt(cursor.getColumnIndex("kind"));
            ImageData data = new ImageData(name_value, path_value, kind_value);
            result.add(data);
        }
        cursor.close();
        db.close();
        return result;
    }

    public boolean queryPath(String path){
        List<ImageData> all_data = queryAll();
        if (all_data == null){
            return false;
        }

        if (all_data.size() == 0){
            return false;
        }

        for (ImageData data:all_data){
            if (path.equals(data.getPath())) {
                return true;
            }
        }
        return false;
    }
}
