package li.chuan.test.composecloth.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ImageInfoSaver {
    private ImageInfoSQLiteHelper mHelper;
    public ImageInfoSaver(Context context ){
        mHelper = new ImageInfoSQLiteHelper(context, "Infos", null, 1);
    }

    public void save(String name_value, String path_value, int kind_value){
        SQLiteDatabase db= mHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name_value);
        contentValues.put("path", path_value);
        contentValues.put("kind", kind_value);
        db.insert("ImageInfo", null, contentValues);
        db.close();
    }

    public void delete(String path_value){
        SQLiteDatabase db= mHelper.getWritableDatabase();
        db.delete("ImageInfo", "path = ?", new String[]{path_value});
        db.close();
    }
}
