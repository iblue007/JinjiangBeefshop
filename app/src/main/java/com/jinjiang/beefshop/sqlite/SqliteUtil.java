package com.jinjiang.beefshop.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jinjiang.beefshop.util.FileUtil;

import java.util.ArrayList;

/**
 * Created by xuqunxing on 2017/12/18.
 */
public class SqliteUtil {

    private static SqliteUtil instance;
    private JjBeafSQLiteOpenHelper dbHelper;

    public static SqliteUtil getInstance() {
        if (instance == null) {
            instance = new SqliteUtil();
        }
        return instance;
    }

    public JjBeafSQLiteOpenHelper init(Context context){
        if(dbHelper == null){
            dbHelper = new JjBeafSQLiteOpenHelper(context, "JjBeafShop.db", null, 1);
        }
        return dbHelper;
    }

    public void addStudent(Context context,String orderjson){
        init(context);
        if(dbHelper != null){
            SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
            writableDatabase.execSQL("insert into jjbeafshop(orderjson) values(?)",new Object[]{orderjson});
            writableDatabase.close();
        }
    }
//
//    public void updateStudent(String ordername, int orderprice, String ordertime, String ordernumber,String orderpeoplecount,int id){
//        if(dbHelper != null){
//            SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
//            writableDatabase.execSQL("update jjbeafshop set ordername=?, orderprice=?,ordertime=?,ordernumber=?,orderpeoplecount=? where _id=?",
//                    new Object[]{ordername, orderprice, ordertime, ordernumber,orderpeoplecount,id});
//        }
//    }

    public void deleteStudent(int id){
        if(dbHelper != null){
            SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
            writableDatabase.execSQL("delete from jjbeafshop where _id=?",new Object[]{id});
            writableDatabase.close();
        }
    }



    public ArrayList<JjbeafSqlBean> queryAllHistoryOrder(Context context){
        init(context);
        ArrayList<JjbeafSqlBean> list = new ArrayList<JjbeafSqlBean>();
        if(dbHelper != null){
            SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
            Cursor cursor = writableDatabase.rawQuery("select * from jjbeafshop",null);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                int id = cursor.getInt(0);
                String string = cursor.getString(1);
                Log.e("====","====query:"+string);
                JjbeafSqlBean jjbeafSqlBean = FileUtil.parseHistoryOrderJson(string);
                jjbeafSqlBean.setID(id);
                list.add(jjbeafSqlBean);
            }
            cursor.close();
            writableDatabase.close();
        }
        return list;
    }

    public void clearTableData(Context context){
        init(context);
        if(dbHelper != null){
            SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
            writableDatabase.execSQL("drop table jjbeafshop");
            writableDatabase.execSQL(JjBeafSQLiteOpenHelper.CREATE_TABLE);
            writableDatabase.close();
        }
    }

//    public JjbeafSqlBean findStudent(String name){
//        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
//        Cursor cursor = writableDatabase.rawQuery("select * from jjbeafshop where name=?",new String[]{name});
//        if(cursor.moveToNext()){
//            JjbeafSqlBean info = new JjbeafSqlBean();
//            info.setAge(cursor.getInt(0));
//            info.setSex(cursor.getString(1));
//            info.setWhichclass(cursor.getString(2));
//            return info;
//        }
//        cursor.close();
//        return null;
//    }
}
