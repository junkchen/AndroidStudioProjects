package com.junkchen.versionupdate;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private DownloadManager mDownloadManager;
    private DownloadCompleteReceiver mDownloadCompleteReceiver;
    private long mDownloadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloadCompleteReceiver = new DownloadCompleteReceiver();
//        downloadApk();
        TextView download = findViewById(R.id.download);
        String url = "http://gdown.baidu.com/data/wisegame/bec3a8982e9e7550/sougoushurufa_810.apk";
//        download.setOnClickListener(v -> downloadApk());
        download.setOnClickListener(view -> VersionUpdateUtils.getInstance(this).downloadApk(url));
//        download.setOnClickListener(v -> installAPk(Environment.getExternalStorageDirectory().getPath() + "/sougoushurufa_810.apk"));
    }

    @Override
    protected void onDestroy() {
        if (mDownloadCompleteReceiver != null) {
            unregisterReceiver(mDownloadCompleteReceiver);
        }
        super.onDestroy();
    }

    private void installAPk(String apkPath) {
        Log.i(TAG, "installAPk: apkPath: " + apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                "application/vnd.android.package-archive");
        try {
            startActivity(intent);
        } finally {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void downloadApk() {
        mDownloadManager =
                (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        String uriString = "http://gdown.baidu.com/data/wisegame/bec3a8982e9e7550/sougoushurufa_810.apk";
        Uri uri = Uri.parse(uriString);
        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setTitle("likangyiliao");
//        request.setDescription("123456");
        //下载完成后在通知栏显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //设置下载后文件存放的位置
        String apkName = uri.getLastPathSegment();
        String path = uri.getPath();
        Log.i(TAG, "downloadApk: path: " + path + ", apkName: " + apkName);
//        request.setDestinationInExternalFilesDir(this, Environment.getExternalStorageDirectory().getPath(), apkName);
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, apkName);
        mDownloadId = mDownloadManager.enqueue(request);
        registerReceiver(mDownloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            checkStatus();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Uri uriForDownloadedFile = mDownloadManager.getUriForDownloadedFile(downloadId);
                Log.i(TAG, "onReceive: uriForDownloadedFile: " + uriForDownloadedFile);
                if (null != uriForDownloadedFile) {
                    installApk(uriForDownloadedFile);
                }
                unregisterReceiver(mDownloadCompleteReceiver);
            }
        }
    }

    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mDownloadId);
        Cursor cursor = mDownloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED://下载暂停
                    Log.i(TAG, "checkStatus: download paused");
                    break;
                case DownloadManager.STATUS_PENDING://下载延迟
                    Log.i(TAG, "checkStatus: download pending");
                    break;
                case DownloadManager.STATUS_RUNNING://正在下载
                    Log.i(TAG, "checkStatus: download running");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL://下载完成
                    Log.i(TAG, "checkStatus: download successful");
                    installAPk();
                    break;
                case DownloadManager.STATUS_FAILED://下载失败
                    Log.i(TAG, "checkStatus: download failed");
                    Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void installAPk() {
        //获取下载文件的Uri
        Uri downloadFileUri = mDownloadManager.getUriForDownloadedFile(mDownloadId);
        if (downloadFileUri != null) {
            Intent intent= new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void installApk(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        try {
            this.startActivity(intent);
//                android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
//                Log.e(TAG, "installApk: " + e.getCause().toString(), e);
        }
    }
}
