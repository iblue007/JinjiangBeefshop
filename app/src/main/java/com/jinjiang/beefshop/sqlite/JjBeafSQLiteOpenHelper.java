package com.jinjiang.beefshop.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xuqunxing on 2017/12/18.
 */
public class JjBeafSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_TABLE = "create table jjbeafshop (_id integer primary key autoincrement,orderjson text not null);";
    private Context mContext;

    public JjBeafSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists peopleinfo");
        onCreate(sqLiteDatabase);
    }
}
