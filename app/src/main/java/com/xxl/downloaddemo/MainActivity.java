package com.xxl.downloaddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.xxl.downloaddemo.bean.FileInfo;
import com.xxl.downloaddemo.service.DowloadService;
import com.xxl.downloaddemo.service.DowloadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private String downloadUrl = "http://www.imooc.com/mobile/mukewang.apk";
    public static final String UPDATE_PROGRESS = "update progress";

    @BindView(R.id.pb_download)
    ProgressBar pbDownload;
    private UpdateReceiver mUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        pbDownload.setMax(100);
        mUpdateReceiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_PROGRESS);
        registerReceiver(mUpdateReceiver, intentFilter);
    }

    @OnClick({R.id.btn_download_start, R.id.btn_download_stop})
    public void onViewClicked(View view) {
        Intent intent = new Intent(this, DowloadService.class);
        FileInfo fileInfo = new FileInfo(0, downloadUrl, 0, "mook.apk");
        switch (view.getId()) {
            case R.id.btn_download_start:
                intent.setAction(DowloadService.ACTION_START_SERVICE);
                intent.putExtra("fileInfo", fileInfo);
                DowloadTask.isPause = false;
                startService(intent);
                break;
            case R.id.btn_download_stop:
                intent.setAction(DowloadService.ACTION_STOP_SERVICE);
                intent.putExtra("fileInfo", fileInfo);
                startService(intent);
                DowloadTask.isPause = true;
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUpdateReceiver);
    }

    class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);
            pbDownload.setProgress(progress);
        }
    }
}
