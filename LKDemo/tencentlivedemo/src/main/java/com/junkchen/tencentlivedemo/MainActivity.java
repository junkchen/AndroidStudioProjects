package com.junkchen.tencentlivedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tencent.liteav.network.TXCStreamUploader;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String mPushUrl = "rtmp://20508.livepush.myqcloud.com/live/20508_85b523a1e3?bizid=20508&txSecret=bca30420afbdd88253478debb09efdf8&txTime=5A675BFF";
    private TXLivePusher mLivePusher;
    private TXCloudVideoView mCaptureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        String sdkver = TXLiveBase.getSDKVersionStr();
        Log.i(TAG, "onCreate: liteav sdk version is : " + sdkver);

        mLivePusher = new TXLivePusher(getApplicationContext());
        TXLivePushConfig mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.setFrontCamera(false);
        mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
        mLivePusher.setConfig(mLivePushConfig);

        mCaptureView = findViewById(R.id.video_view);
        mLivePusher.startCameraPreview(mCaptureView);
//        mLivePusher.startCameraPreview(new TXCloudVideoView(this));

//        String rtmpUrl = "rtmp://2157.livepush.myqcloud.com/live/xxxxxx";
//        mLivePusher.startPusher(mPushUrl);

        mLivePusher.setPushListener(new ITXLivePushListener() {
            @Override
            public void onPushEvent(int event, Bundle param) {
                Log.w(TAG, "onPushEvent: event = " + event);
                String msg = param.getString(TXLiveConstants.EVT_DESCRIPTION);
                String pushEventLog = "receive event: " + event + ", description: " + msg;
                Log.w(TAG, "onPushEvent: " + pushEventLog);
            }

            @Override
            public void onNetStatus(Bundle status) {
                Log.w(TAG, "Current status, CPU:" + status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE) +
                        ", RES:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) + "*" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT) +
                        ", SPD:" + status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED) + "Kbps" +
                        ", FPS:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS) +
                        ", ARA:" + status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE) + "Kbps" +
                        ", VRA:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE) + "Kbps");
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (null != mLivePusher) {
            if (mLivePusher.isPushing()) {
                mLivePusher.stopCameraPreview(true);
                mLivePusher.stopPusher();
                mLivePusher.setPushListener(null);   //解绑 listener
            }
        }
        super.onDestroy();
    }

    public void doClick(View view) {
        if (null == mLivePusher) return;
        try {
            switch (view.getId()) {
                case R.id.btn_startPreview:
                    mLivePusher.startCameraPreview(mCaptureView);
                    break;
                case R.id.btn_startPush:
                    mLivePusher.startPusher(mPushUrl);
                    Log.i(TAG, "doClick: start push stream");
                    break;
                case R.id.btn_stopPush:
                    mLivePusher.stopPusher();
                    Log.i(TAG, "doClick: stop push stream");
                    break;
                case R.id.btn_stopPreview:
                    mLivePusher.stopCameraPreview(true);
                    break;
                case R.id.btn_other:
                    startActivity(new Intent(this, OtherActivity.class));
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "doClick: ------------------------", e);
        }
    }
}


/**
 E/TXCIntelligentRoute: Nearest IP: 220.248.42.64 Port: 443 Q Channel: true
 E/TXCIntelligentRoute: Nearest IP: 220.248.42.13 Port: 443 Q Channel: true
 E/TXCIntelligentRoute: Nearest IP: 157.255.8.109 Port: 1935 Q Channel: false
 E/TXCIntelligentRoute: Nearest IP: 157.255.8.11 Port: 1935 Q Channel: false
 E/TXCIntelligentRoute: Nearest IP: 20508.livepush.myqcloud.com Port: 1935 Q Channel: false
 E/TXCStreamUploader: onFetchDone: code = 0 ip count = 5
 E/c: onRecordError code = 1:NO-AEC,采样率(48000|48000),声道数1
 */