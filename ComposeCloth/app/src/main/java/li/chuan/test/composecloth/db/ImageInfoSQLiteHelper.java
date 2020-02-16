package li.chuan.test.composecloth.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ImageInfoSQLiteHelper extends SQLiteOpenHelper {

    //数据库建表语句
    public static final String sql = "create table ImageInfo (id integer primary key autoincrement, name text(50),path text(255), kind integer)";

    public ImageInfoSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);//创建数据库调用方法
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}