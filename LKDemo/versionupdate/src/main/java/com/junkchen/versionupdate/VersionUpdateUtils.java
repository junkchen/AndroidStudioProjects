package com.junkchen.versionupdate;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * 应用下载并安装
 * Created by Junk on 2018/3/14.
 */

public class VersionUpdateUtils {
    private static final String TAG = VersionUpdateUtils.class.getSimpleName();

    volatile private static VersionUpdateUtils mInstance;
    private Context mContext;
    private DownloadManager mDownloadManager;
    private long mDownloadId;

    private VersionUpdateUtils(Context context) {
        this.mContext = context;
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static VersionUpdateUtils getInstance(Context context) {
        if (null == mInstance) {
            synchronized (VersionUpdateUtils.class) {
                if (null == mInstance) {
                    mInstance = new VersionUpdateUtils(context);
                }
            }
        }
        return mInstance;
    }

    public void downloadApk(String url) {
        Uri downUri = Uri.parse(url);
        Request request = new Request(downUri);
        //Notify after download completed
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String subPath = downUri.getLastPathSegment();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath);
        mDownloadId = mDownloadManager.enqueue(request);
        mContext.registerReceiver(mDownloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
//                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                checkStatus();
                mContext.unregisterReceiver(mDownloadReceiver);
                mInstance = null;
                System.gc();
            }
        }
    };

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
                    installApk();
                    break;
                case DownloadManager.STATUS_FAILED://下载失败
                    Log.i(TAG, "checkStatus: download failed");
                    Toast.makeText(mContext, "Download failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void installApk() {
        Uri downloadFileUri = mDownloadManager.getUriForDownloadedFile(mDownloadId);
        if (downloadFileUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

}

/**
 * 安装开始
 * package: com.android.packageinstaller, com.android.systemui
 * TextView 荔康医疗 com.android.packageinstaller:id/app_name
 * Button 安装 com.android.packageinstaller:id/ok_button
 * Button 取消 com.android.packageinstaller:id/cancel_button
 * <p>
 * 安装完成且安装成功
 * TextView 荔康医疗 com.android.packageinstaller:id/app_name
 * Button 完成 com.android.packageinstaller:id/done_button
 * Button 打开 com.android.packageinstaller:id/launch_button
 */
