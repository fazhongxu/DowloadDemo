package com.xxl.downloaddemo.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xxl on 2017/9/13.
 */

public class DBHelper extends SQLiteOpenHelper{
    private static final String THREAD_INFO = "thread_info.db";
    private static final int THREAD_INFO_VERSION = 1;
    private static final String THREAD_INFO_TABLE = "thread_info";
    private String CREATE_TABLE = "create table "+THREAD_INFO_TABLE+"(_id integer primary key autoincrement,url text,start integer,end integer,finished integer)";
    private String DROP_TABLE = "drop table if exist "+THREAD_INFO_TABLE+"";
    public DBHelper(Context context) {
        super(context, THREAD_INFO, null, THREAD_INFO_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sq) {
        sq.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sq, int i, int i1) {
        sq.execSQL(DROP_TABLE);
        sq.execSQL(CREATE_TABLE);
    }
}
