package com.junkchen.alilivedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.alivc.live.pusher.AlivcLivePushCameraTypeEnum;
import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePushError;
import com.alivc.live.pusher.AlivcLivePushErrorListener;
import com.alivc.live.pusher.AlivcLivePushInfoListener;
import com.alivc.live.pusher.AlivcLivePushNetworkListener;
import com.alivc.live.pusher.AlivcLivePusher;
import com.alivc.live.pusher.AlivcQualityModeEnum;
import com.alivc.live.pusher.AlivcResolutionEnum;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String mPushUrl = "rtmp://video-center.alivecdn.com/lklive/test?vhost=live.lkyiliao.com";

    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSurfaceView = findViewById(R.id.mSurfaceView);
//        Log.i(TAG, "onCreate: maxOf(10, 20) is: " + new MathUtils().maxOf(10, 20));
        init();
    }

    public void doClick(View view) {
        if (null == mAlivcLivePusher) return;
        try {
            switch (view.getId()) {
                case R.id.btn_startPreview:
                    mAlivcLivePusher.startPreviewAysnc(mSurfaceView);
                    break;
                case R.id.btn_startPush:
                    mAlivcLivePusher.startPushAysnc(mPushUrl);
//                    mAlivcLivePusher.restartPushAync();
                    break;
                case R.id.btn_stopPush:
                    mAlivcLivePusher.stopPush();
                    break;
                case R.id.btn_stopPreview:
                    mAlivcLivePusher.stopPreview();
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "doClick: ", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mAlivcLivePusher) {
//            mAlivcLivePusher.startPreviewAysnc(mSurfaceView);
        }
    }

    @Override
    protected void onDestroy() {
        if (null != mAlivcLivePusher) {
            mAlivcLivePusher.destroy();
        }
        super.onDestroy();
    }

    private AlivcLivePushConfig mAlivcLivePushConfig;
    private AlivcLivePusher mAlivcLivePusher;

    private void init() {
        //AlivcLivePushConfig 推流参数配置
        mAlivcLivePushConfig = new AlivcLivePushConfig();
        //1、设置分辨率
        mAlivcLivePushConfig.setResolution(AlivcResolutionEnum.RESOLUTION_480P);
        //2、码控参数设置
        mAlivcLivePushConfig.setQualityMode(AlivcQualityModeEnum.QM_RESOLUTION_FIRST);
        //3、设置美颜开关
        mAlivcLivePushConfig.setBeautyOn(false);//关闭美颜
        //4、设置推流方向
//        mAlivcLivePushConfig.setPreviewOrientation(AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT);//竖屏推流
        //5、设置帧率
//        mAlivcLivePushConfig.setFps(AlivcFpsEnum.FPS_20);//帧率20
        //6、设置音频编码模式
//        mAlivcLivePushConfig.setAudioProfile(AlivcAudioAACProfileEnum.AAC_LC);

        mAlivcLivePushConfig.setCameraType(AlivcLivePushCameraTypeEnum.CAMERA_TYPE_BACK);//设置使用后置摄像头

        //AlivcLivePusher推流
        //1、初始化
        mAlivcLivePusher = new AlivcLivePusher();
        try {
            mAlivcLivePusher.init(getApplicationContext(), mAlivcLivePushConfig);
        } catch (Exception e) {
            Log.e(TAG, "init: AlivcLivePusher init exception.", e);
            return;
        }
        //2、注册回调通知
        /**
         * 设置推流错误事件
         */
        mAlivcLivePusher.setLivePushErrorListener(new AlivcLivePushErrorListener() {
            @Override
            public void onSystemError(AlivcLivePusher alivcLivePusher, AlivcLivePushError error) {
                if (error != null) {
                    //添加UI提示或者用户自定义的错误处理
                    Log.w(TAG, "onSystemError: msg: " + error.getMsg() + ", code: " + error.getCode());
                    Log.w(TAG, "onSystemError: " + error.toString());
                }
            }

            @Override
            public void onSDKError(AlivcLivePusher alivcLivePusher, AlivcLivePushError error) {
                if (error != null) {
                    //添加UI提示或者用户自定义的错误处理
                    Log.w(TAG, "onSDKError: " + error.toString());
                    Log.w(TAG, "onSDKError: msg: " + error.getMsg() + ", code: " + error.getCode());

                }
            }
        });
        /**
         * 设置推流通知事件
         */
        mAlivcLivePusher.setLivePushInfoListener(new AlivcLivePushInfoListener() {
            @Override
            public void onPreviewStarted(AlivcLivePusher pusher) {
                //预览开始通知
                Log.w(TAG, "onPreviewStarted: 预览开始通知");
            }

            @Override
            public void onPreviewStoped(AlivcLivePusher pusher) {
                //预览结束通知
                Log.w(TAG, "onPreviewStoped: 预览结束通知");
            }

            @Override
            public void onPushStarted(AlivcLivePusher pusher) {
                //推流开始通知
                Log.w(TAG, "onPushStarted: 推流开始通知");
            }

            @Override
            public void onPushPauesed(AlivcLivePusher pusher) {
                //推流暂停通知
                Log.w(TAG, "onPushPauesed: 推流暂停通知");
            }

            @Override
            public void onPushResumed(AlivcLivePusher pusher) {
                //推流恢复通知
                Log.w(TAG, "onPushResumed: 推流恢复通知");
            }

            @Override
            public void onPushStoped(AlivcLivePusher pusher) {
                //推流停止通知
                Log.w(TAG, "onPushStoped: 推流停止通知");
            }

            @Override
            public void onPushRestarted(AlivcLivePusher pusher) {
                //推流重启通知
                Log.w(TAG, "onPushRestarted: 推流重启通知");
            }

            @Override
            public void onFirstFramePreviewed(AlivcLivePusher pusher) {
                //首帧渲染通知
                Log.w(TAG, "onFirstFramePreviewed: 首帧渲染通知");
            }

            @Override
            public void onDropFrame(AlivcLivePusher pusher, int countBef, int countAft) {
                //丢帧通知
                Log.w(TAG, "onDropFrame: 丢帧通知，countBef = " + countBef + ", countAft = " + countAft);
            }

            @Override
            public void onAdjustBitRate(AlivcLivePusher pusher, int curBr, int targetBr) {
                //调整码率通知
                Log.w(TAG, "onAdjustBitRate: 调整码率通知，curBr = " + curBr + ", targetBr = " + targetBr);
            }

            @Override
            public void onAdjustFps(AlivcLivePusher pusher, int curFps, int targetFps) {
                //调整帧率通知
                Log.w(TAG, "onAdjustFps: 调整帧率通知，curFps = " + curFps + ", targetFps = " + targetFps);
            }
        });

        /**
         * 设置网络通知事件
         *
         * @param infoListener 通知监听器
         */
        mAlivcLivePusher.setLivePushNetworkListener(new AlivcLivePushNetworkListener() {
            @Override
            public void onNetworkPoor(AlivcLivePusher pusher) {
                //网络差通知
                Log.w(TAG, "onNetworkPoor: 网络差通知");
            }

            @Override
            public void onNetworkRecovery(AlivcLivePusher pusher) {
                //网络恢复通知
                Log.w(TAG, "onNetworkRecovery: 网络恢复通知");
            }

            @Override
            public void onReconnectStart(AlivcLivePusher pusher) {
                //重连开始通知
                Log.w(TAG, "onReconnectStart: 重连开始通知");
            }

            @Override
            public void onReconnectFail(AlivcLivePusher pusher) {
                //重连失败通知
                Log.w(TAG, "onReconnectFail: 重连失败通知");
            }

            @Override
            public void onReconnectSucceed(AlivcLivePusher pusher) {
                //重连成功通知
                Log.i(TAG, "onReconnectSucceed: 重连成功通知");
            }

            @Override
            public void onSendDataTimeout(AlivcLivePusher pusher) {
                //发送数据超时通知
                Log.w(TAG, "onSendDataTimeout: 发送数据超时通知");
            }

            @Override
            public void onConnectFail(AlivcLivePusher pusher) {
                //连接失败通知
                Log.w(TAG, "onConnectFail: 连接失败通知");
            }
        });
//        //3、开始预览
//        mAlivcLivePusher.startPreview(mSurfaceView);//异步预览
//        //4、开始推流
//        mAlivcLivePusher.startPushAysnc(mPushUrl);
        //5、停止推流
//        mAlivcLivePusher.stopPush();
//        //6、停止预览
//        mAlivcLivePusher.stopPreview();
//        //7、销毁
//        mAlivcLivePusher.destroy();
    }
}

/*
W/linker: libAliEffectModule.so: unused DT entry: type 0x6ffffffe arg 0xc2f8
W/linker: libAliEffectModule.so: unused DT entry: type 0x6fffffff arg 0x2
W/linker: libAliFaceAlignmentModule.so: unused DT entry: type 0x6ffffffe arg 0x5580
W/linker: libAliFaceAlignmentModule.so: unused DT entry: type 0x6fffffff arg 0x2
W/linker: libalivc_audio.so: unused DT entry: type 0x6ffffffe arg 0x2c9c
W/linker: libalivc_audio.so: unused DT entry: type 0x6fffffff arg 0x2
W/linker: liblive-rtmp.so: unused DT entry: type 0x6ffffffe arg 0x27c4
W/linker: liblive-rtmp.so: unused DT entry: type 0x6fffffff arg 0x1
W/linker: liblive-fdkaac.so: unused DT entry: type 0x6ffffffe arg 0x10ec4
W/linker: liblive-fdkaac.so: unused DT entry: type 0x6fffffff arg 0x2
W/linker: liblive-pusher.so: unused DT entry: type 0x6ffffffe arg 0xb988
W/linker: liblive-pusher.so: unused DT entry: type 0x6fffffff arg 0x3

W/System.err: java.lang.RuntimeException: setParameters failed
W/System.err:     at android.hardware.Camera.native_setParameters(Native Method)
W/System.err:     at android.hardware.Camera.setParameters(Camera.java:1876)

应用集成阿里视频直播推流后，在预览或者推流开启时，几分钟后会导致机器自动关机。请问这种问题大概是什么原因导致的？该如何解决这个问题？ 集成的推流SDK版本是 Android-V3.2.0 ，引入的相应文件是该目录下的：AlivcLivePusher_v3.2.0__20180112Android/AlivcLivePusher_v3.2.0_Android/sdk/AlivcLivePusherSDK 。

 */
