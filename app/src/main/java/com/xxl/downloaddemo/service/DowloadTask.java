package com.xxl.downloaddemo.service;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.xxl.downloaddemo.MainActivity;
import com.xxl.downloaddemo.bean.FileInfo;
import com.xxl.downloaddemo.bean.ThreadInfo;
import com.xxl.downloaddemo.dao.ThreadDao;
import com.xxl.downloaddemo.dao.ThreadDaoImpl;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by xxl on 2017/9/16.
 */

public class DowloadTask {
    private FileInfo fileInfo;
    private ThreadDao threadDao;
    private int mFinished = 0;
    private Context mContext;
    public static boolean isPause;

    public DowloadTask(Context context, FileInfo fileInfo) {
        this.fileInfo = fileInfo;
        this.mContext = context;
        threadDao = new ThreadDaoImpl(context);
    }

    public void download() {
        //从数据库中获取线程数据
        List<ThreadInfo> list = threadDao.selectThreadInfo(fileInfo.getUrl());
        ThreadInfo threadInfo = null;
        if (list.size() == 0) {
            threadInfo = new ThreadInfo(0, fileInfo.getUrl(), 0, fileInfo.getSize(), 0);
        } else {
            threadInfo = list.get(0);
        }
        new DownloadThread(threadInfo).start();
    }

    class DownloadThread extends Thread {
        private ThreadInfo threadInfo;

        public DownloadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
            super.run();
            //向数据库中插入线程信息
            if (!threadDao.threadExist(threadInfo.getUrl())) {
                threadDao.insertThreadInfo(threadInfo);
            }
            InputStream inputStream = null;
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                URL url = new URL(threadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(3000);
                conn.setConnectTimeout(3000);

                //设置文件下载位置
                int start = threadInfo.getStart() + threadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
                File file = new File(DowloadService.FILE_DOWNLOAD_PATH, fileInfo.getName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                mFinished += threadInfo.getFinished();

                Intent intent = new Intent(MainActivity.UPDATE_PROGRESS);

                //开始下载
                if (conn.getResponseCode() == 206) {
                    inputStream = conn.getInputStream();
                    byte[] buffer = new byte[8 * 1024];
                    int len = -1;
                    int time = (int) SystemClock.currentThreadTimeMillis();
                    while ((len = inputStream.read(buffer)) != -1) {
                        raf.write(buffer, 0, len);
                        mFinished += len;
                        if (SystemClock.currentThreadTimeMillis() - time > 100) {
                            intent.putExtra("progress", mFinished * 100 / fileInfo.getSize());
                            mContext.sendBroadcast(intent);
                            time = (int) SystemClock.currentThreadTimeMillis();
                        }
                        //点击停止下载把数据存到数据库中
                        if (isPause) {
                            threadDao.updateThreadInfo(threadInfo.getId(), threadInfo.getUrl(), mFinished);
                            return;
                        }
                    }
                }
                //下载完成删除线程信息
                threadDao.deleteThreadInfo(threadInfo.getUrl());
                Log.e("aaa", "下载完成");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.disconnect();
                    inputStream.close();
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
