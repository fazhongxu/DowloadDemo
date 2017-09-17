package com.xxl.downloaddemo.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xxl.downloaddemo.bean.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xxl on 2017/9/13.
 */

public class ThreadDaoImpl implements ThreadDao {
    private DBHelper mDBHelper;
    public ThreadDaoImpl(Context context) {
        mDBHelper = new DBHelper(context);
    }

    @Override
    public void insertThreadInfo(ThreadInfo threadInfo) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.execSQL("insert into thread_info(_id,url,start,end,finished) values(?,?,?,?,?)",
                new Object[]{threadInfo.getId(),threadInfo.getUrl(),threadInfo.getStart(),threadInfo.getEnd(),threadInfo.getFinished()});
        db.close();
    }

    @Override
    public void deleteThreadInfo(String url) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.execSQL("delete from thread_info where url = ?",new String[]{url});
        db.close();
    }

    @Override
    public void updateThreadInfo(int id, String url,int finished) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.execSQL("update thread_info set finished = ? where _id = ? and url = ?",new Object[]{finished,id,url});
    }

    @Override
    public List<ThreadInfo> selectThreadInfo(String url) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ?", new String[]{url});
        List<ThreadInfo> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            ThreadInfo threadInfo = new ThreadInfo();
            threadInfo.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            list.add(threadInfo);
        }
        cursor.close();
//        db.close();
        return list;
    }

    @Override
    public boolean threadExist(String url) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ?", new String[]{url});
        boolean b = cursor.moveToNext();
        return b;
    }
}
