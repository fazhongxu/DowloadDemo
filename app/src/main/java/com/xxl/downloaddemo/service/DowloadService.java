package com.xxl.downloaddemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xxl.downloaddemo.bean.FileInfo;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xxl on 2017/9/13.
 */

public class DowloadService extends Service {
    public static final String ACTION_START_SERVICE = "action_start_service";
    public static final String ACTION_STOP_SERVICE = "action_stop_service";
    public static final String FILE_DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/down";
    private static final int HANDLER_FILEINFO =101;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_FILEINFO:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    Log.e("handler", "handleMessage: "+fileInfo);
                    new DowloadTask(DowloadService.this,fileInfo).download();
                    break;
            }
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
        if (ACTION_START_SERVICE.equals(intent.getAction())) {
            Log.e("aaa", "onStartCommand: " + fileInfo.toString());
            new InitThread(fileInfo).start();
        } else if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            Log.e("aaa", "stop");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class InitThread extends Thread {
        private FileInfo fileInfo;

        public InitThread(FileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        @Override
        public void run() {
            super.run();
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                URL url = new URL(fileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(3000);
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                if (conn.getResponseCode() == 200) {
                    int contentLength = conn.getContentLength();//获取文件长度
                    File file = new File(FILE_DOWNLOAD_PATH);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File downloadFile = new File(file,fileInfo.getName());
                    raf = new RandomAccessFile(downloadFile, "rwd");//本地创建一个大小相同的文件
                    raf.setLength(contentLength);
                    fileInfo.setSize(contentLength);//设置fileInfo的大小
                    Message message = new Message();
                    message.what = HANDLER_FILEINFO;
                    message.obj = fileInfo;
                    mHandler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.disconnect();
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
