package com.harmazing.intelligentpow.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Administrator on 2015/4/15.
 */
public class DBManageImpl implements DBManager {
    private final String TAG = "DBManageImpl";

    private MyDBHelper myDBHelper = null;

    private static DBManageImpl DBInstance = null;

    private DBManageImpl(Context context){
        this.myDBHelper = new MyDBHelper(context);
    }

    public static synchronized DBManageImpl getDBInstance(Context context){
        if(null==DBInstance){
            DBInstance = new DBManageImpl(context);
        }
        return DBInstance;

    }
    @Override
    public String findByHead(String head) {
        SQLiteDatabase dbwrite = myDBHelper.getWritableDatabase();
        Cursor cursor =	dbwrite.rawQuery("select headId from httpHead where head=?",new String[]{head});
        String id="";
        while(cursor.moveToNext()){
            id = cursor.getString(cursor.getColumnIndex("headId"));
        }
        cursor.close();
        return id;

    }

    @Override
    public void addItem(String head, String headId) {
        SQLiteDatabase dbwrite = myDBHelper.getWritableDatabase();
        dbwrite.execSQL("insert into httpHead (headId,head) values (?,?)",new String[]{headId,head});
        dbwrite.close();
    }

//    @Override
//    public void deleteItem(String id) {
//        SQLiteDatabase dbwrite = myDBHelper.getWritableDatabase();
//        dbwrite.execSQL("delete from spms where id=?",new String[]{id});
//        dbwrite.close();
//    }

//    @Override
//    public Boolean findCityById(String id) {
//        Boolean bool;
//        SQLiteDatabase dbwrite = myDBHelper.getReadableDatabase(); //´ò¿ªÊý¾Ý¿â£¬¶ÔÊý¾Ý½øÐÐ¸ü¸ÄÊÇÓÃ´Ë·½·¨Éú³ÉSQLiteDatabase¶ÔÏó
//        Cursor cursor =	dbwrite.rawQuery("select * from spms where id=?",new String[]{id});
//        bool = false;
//        try {
//            if(cursor.moveToFirst()){
//                bool=true;
//            }else{
//                bool= false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.i(TAG, e.toString());
//        }finally{
//            cursor.close();
//        }
//        Log.i(TAG, "ÊÇ·ñ´æÔÚ"+bool);
//        return bool;
//    }

    @Override
    public void updateItem(String headId) {
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("headId",headId);//key为字段名，value为值
        db.update("httpHead", values, "head=?", new String[]{"head"});
        db.close();
    }
}
