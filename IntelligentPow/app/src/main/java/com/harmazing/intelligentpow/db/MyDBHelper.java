package com.harmazing.intelligentpow.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jtl on 2015/4/15.
 * 数据库
 */
public class MyDBHelper extends SQLiteOpenHelper {
    private final String TAG = "MyDBHelper";
    private static final String DBNAME = "spms.db" ;
    private static final int DBVVERSION = 1;

    /**
     * MyDBHelper¹¹Ôì·½·¨
     * @param context context
     *  数据库名称
     *  工厂
     *  版本
     */
    public MyDBHelper(Context context) {
        super(context,DBNAME, null, DBVVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS httpHead (headId varchar(20) primary key ,head varchar(20))");     //
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS httpHead");
        onCreate(sqLiteDatabase);
    }
}
