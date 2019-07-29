package com.cjt2325.cameralibrary;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.cjt2325.cameralibrary.listener.*;
import com.cjt2325.cameralibrary.state.BorrowVideoState;
import com.cjt2325.cameralibrary.state.CameraMachine;
import com.cjt2325.cameralibrary.state.PreviewState;
import com.cjt2325.cameralibrary.util.FileUtil;
import com.cjt2325.cameralibrary.util.LogUtil;
import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.cjt2325.cameralibrary.view.CameraView;

import java.io.IOException;


/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.0.4
 * 创建日期：2017/4/25
 * 描    述：
 * =====================================
 */
public class JCamera2View extends FrameLayout implements CameraInterface.CameraOpenOverCallback, CameraView {
    private static final String TAG = JCamera2View.class.getName();

    /**
     * Camera状态机
     */
    private CameraMachine machine;

    /**
     * 闪关灯状态
     */
    private static final int TYPE_FLASH_AUTO = 0x021;
    private static final int TYPE_FLASH_ON = 0x022;
    private static final int TYPE_FLASH_OFF = 0x023;
    private int type_flash = TYPE_FLASH_OFF;

    /**
     * 拍照浏览时候的类型
     */
    public static final int TYPE_PICTURE = 0x001;
    public static final int TYPE_VIDEO = 0x002;
    public static final int TYPE_SHORT = 0x003;
    public static final int TYPE_DEFAULT = 0x004;

    /**
     * 录制视频比特率
     */
    public static final int MEDIA_QUALITY_HIGH = 20 * 100000;
    public static final int MEDIA_QUALITY_MIDDLE = 16 * 100000;
    public static final int MEDIA_QUALITY_LOW = 12 * 100000;
    public static final int MEDIA_QUALITY_POOR = 8 * 100000;
    public static final int MEDIA_QUALITY_FUNNY = 4 * 100000;
    public static final int MEDIA_QUALITY_DESPAIR = 2 * 100000;
    public static final int MEDIA_QUALITY_SORRY = 1 * 80000;


    /**
     * 只能拍照
     */
    public static final int BUTTON_STATE_ONLY_CAPTURE = 0x101;
    /**
     * 只能录像
     */
    public static final int BUTTON_STATE_ONLY_RECORDER = 0x102;
    /**
     * 拍着和录像两者都可以
     */
    public static final int BUTTON_STATE_BOTH = 0x103;


    //回调监听
    private JCameraListener jCameraListener;
    private ClickListener leftClickListener;
    private ClickListener rightClickListener;

    private Context mContext;
    //    private VideoView mVideoView;
    private AutoFitTextureView aftv_preview;
    private AutoFitTextureView aftv_video;
    private ImageView mPhoto;
    private ImageView mSwitchCamera;
    private ImageView mFlashLamp;
    private CaptureLayout mCaptureLayout;
    private FoucsView mFoucsView;

    private Surface mSurface;
    private MediaPlayer mMediaPlayer;

    private int layout_width;
    private float screenProp = 0f;

    private Bitmap captureBitmap;   //捕获的图片
    private Bitmap firstFrame;      //第一帧图片
    private String videoUrl;        //视频URL


    /**
     * 切换摄像头按钮的参数
     */
    private int iconSize = 0;  // 图标大小
    private int iconMargin = 0;     //右上边距
    private int iconSrc = 0;        //图标资源
    private int iconLeft = 0;       //左图标
    private int iconRight = 0;      //右图标
    private int duration = 0;       //录制时间

    /**
     * 缩放梯度
     */
    private int zoomGradient = 0;

    private boolean firstTouch = true;
    private float firstTouchLength = 0;

    /**
     * [TextureView.SurfaceTextureListener] handles several lifecycle events on a
     * [TextureView].
     */
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.i(TAG, "onSurfaceTextureAvailable: width=" + width + ", height=" + height);
            if (machine.getState() instanceof PreviewState) {
                Log.i(TAG, "onSurfaceTextureAvailable: 预览状态");
                Camera2Interface.INSTANCE.openCamera(width, height);
            } else if (machine.getState() instanceof BorrowVideoState) {
                Log.i(TAG, "onSurfaceTextureAvailable: 浏览视频状态");
//                mSurface = new Surface(surface);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Camera2Interface.INSTANCE.configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            surface = null;
//            mSurface = null;
//            mMediaPlayer.stop();
//            mMediaPlayer.release();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    public JCamera2View(Context context) {
        this(context, null);
    }

    public JCamera2View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JCamera2View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //get AttributeSet
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JCameraView, defStyleAttr, 0);
        iconSize = a.getDimensionPixelSize(R.styleable.JCameraView_iconSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 35, getResources().getDisplayMetrics()));
        iconMargin = a.getDimensionPixelSize(R.styleable.JCameraView_iconMargin, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        iconSrc = a.getResourceId(R.styleable.JCameraView_iconSrc, R.drawable.ic_camera);
        iconLeft = a.getResourceId(R.styleable.JCameraView_iconLeft, 0);
        iconRight = a.getResourceId(R.styleable.JCameraView_iconRight, 0);
        // 设置默认为10s
        duration = a.getInteger(R.styleable.JCameraView_duration_max, 10 * 1000);
        a.recycle();
        initData();
        initView();
    }

    private void initData() {
        layout_width = ScreenUtils.getScreenWidth(mContext);
        //缩放梯度
        zoomGradient = (int) (layout_width / 16f);
        LogUtil.i("zoom = " + zoomGradient);
        machine = new CameraMachine(getContext(), this);
    }

    private void initView() {
        setWillNotDraw(false);
        View view = LayoutInflater.from(mContext).inflate(R.layout.camera2_view, this);
        aftv_preview = view.findViewById(R.id.aftv_preview);
        aftv_video = view.findViewById(R.id.aftv_video);
        aftv_preview.setSurfaceTextureListener(mSurfaceTextureListener);
        mPhoto = (ImageView) view.findViewById(R.id.image_photo);
        mSwitchCamera = (ImageView) view.findViewById(R.id.image_switch);
        mSwitchCamera.setImageResource(iconSrc);
        mFlashLamp = (ImageView) view.findViewById(R.id.image_flash);
        mFlashLamp.setVisibility(GONE);
        setFlashRes();
        mFlashLamp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                type_flash++;
                if (type_flash > 0x023) {
                    type_flash = TYPE_FLASH_AUTO;
                }
                setFlashRes();
            }
        });
        mCaptureLayout = (CaptureLayout) view.findViewById(R.id.capture_layout);
        mCaptureLayout.setDuration(duration);
        mCaptureLayout.setIconSrc(iconLeft, iconRight);
        mFoucsView = (FoucsView) view.findViewById(R.id.fouce_view);
//        mVideoView.getHolder().addCallback(this);
        // 切换摄像头
        mSwitchCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                machine.swtich(screenProp);
            }
        });
        // 拍照 录像
        mCaptureLayout.setCaptureLisenter(new CaptureListener() {
            @Override
            public void takePictures() {
                mSwitchCamera.setVisibility(INVISIBLE);
                mFlashLamp.setVisibility(INVISIBLE);
                machine.capture();
            }

            @Override
            public void recordStart() {
                mSwitchCamera.setVisibility(INVISIBLE);
                mFlashLamp.setVisibility(INVISIBLE);
                machine.record(screenProp);
            }

            @Override
            public void recordShort(final long time) {
                mCaptureLayout.setTextWithAnimation("录制时间过短");
                mSwitchCamera.setVisibility(VISIBLE);
//                mFlashLamp.setVisibility(VISIBLE);
                mFlashLamp.setVisibility(INVISIBLE);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        machine.stopRecord(true, time);
                    }
                }, 1500 - time);
            }

            @Override
            public void recordEnd(long time) {
                machine.stopRecord(false, time);
            }

            @Override
            public void recordZoom(float zoom) {
                LogUtil.i("recordZoom");
                machine.zoom(zoom, CameraInterface.TYPE_RECORDER);
            }

            @Override
            public void recordError() {
                if (errorLisenter != null) {
                    errorLisenter.audioPermissionError();
                }
            }
        });
        // 确认 取消
        mCaptureLayout.setTypeLisenter(new TypeListener() {
            @Override
            public void cancel() {
                machine.cancle(screenProp);
                aftv_preview.setVisibility(View.VISIBLE);
                aftv_video.setVisibility(View.GONE);
            }

            @Override
            public void confirm() {
                machine.confirm();
            }
        });
        // 退出
//        mCaptureLayout.setReturnLisenter(new ReturnListener() {
//            @Override
//            public void onReturn() {
//                if (jCameraListener != null) {
//                    jCameraListener.quit();
//                }
//            }
//        });
        mCaptureLayout.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                if (leftClickListener != null) {
                    leftClickListener.onClick();
                }
            }
        });
        mCaptureLayout.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                if (rightClickListener != null) {
                    rightClickListener.onClick();
                }
            }
        });

        aftv_video.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mSurface = new Surface(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                mSurface = null;
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    public void init(Activity activity) {
        mMediaPlayer = new MediaPlayer();
        Camera2Interface.INSTANCE.init(activity, mContext, aftv_preview);
        Camera2Interface.INSTANCE.registerSensorManager(mContext);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float widthSize = aftv_preview.getMeasuredWidth();
        float heightSize = aftv_preview.getMeasuredHeight();
        if (screenProp == 0) {
            screenProp = heightSize / widthSize;
        }
    }

    @Override
    public void cameraHasOpened() {
//        CameraInterface.getInstance().doStartPreview(mVideoView.getHolder(), screenProp);
//        Camera2Interface.INSTANCE.createCameraPreviewSession();
    }

    /**
     * 生命周期onResume
     */
    public void onResume() {
        LogUtil.i("JCameraView onResume");
        // 重置状态
        resetState(TYPE_DEFAULT);
        Camera2Interface.INSTANCE.setTv_preview(aftv_preview);
//        CameraInterface.getInstance().registerSensorManager(mContext);
//        CameraInterface.getInstance().setSwitchView(mSwitchCamera, mFlashLamp);
//        machine.start(mVideoView.getHolder(), screenProp);
        Camera2Interface.INSTANCE.onResume();
    }

    /**
     * 生命周期onPause
     */
    public void onPause() {
        LogUtil.i("JCameraView onPause");
        stopVideo();
        resetState(TYPE_PICTURE);
        Camera2Interface.INSTANCE.setMIsRecordingVideo(false);
        Camera2Interface.INSTANCE.onPause();
//        CameraInterface.getInstance().isPreview(false);
        Camera2Interface.INSTANCE.unregisterSensorManager(mContext);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() == 1) {
                    // 显示对焦指示器
                    setFocusViewWidthAnimation(event.getX(), event.getY());
                }
                if (event.getPointerCount() == 2) {
                    Log.i("CJT", "ACTION_DOWN = " + 2);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    firstTouch = true;
                }
                if (event.getPointerCount() == 2) {
                    //第一个点
                    float point_1_X = event.getX(0);
                    float point_1_Y = event.getY(0);
                    //第二个点
                    float point_2_X = event.getX(1);
                    float point_2_Y = event.getY(1);

                    float result = (float) Math.sqrt(Math.pow(point_1_X - point_2_X, 2) + Math.pow(point_1_Y -
                            point_2_Y, 2));

                    if (firstTouch) {
                        firstTouchLength = result;
                        firstTouch = false;
                    }
                    if ((int) (result - firstTouchLength) / zoomGradient != 0) {
                        firstTouch = true;
                        machine.zoom(result - firstTouchLength, CameraInterface.TYPE_CAPTURE);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                firstTouch = true;
                break;
        }
        return true;
    }

    /**
     * 对焦框指示器动画
     */
    private void setFocusViewWidthAnimation(float x, float y) {
        machine.foucs(x, y, new Camera2Interface.FocusCallback() {
            @Override
            public void focusSuccess() {
                mFoucsView.setVisibility(INVISIBLE);
            }
        });
    }

    private void updateVideoViewSize(int videoWidth, int videoHeight) {
        Log.i(TAG, "updateVideoViewSize: videoWidth=" + videoWidth + ", videoHeight=" + videoHeight);
//        if (videoWidth > videoHeight) {
//            LayoutParams videoViewParam;
//            int height = (int) ((videoHeight / videoWidth) * getWidth());
//            videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, height);
//            videoViewParam.gravity = Gravity.CENTER;
//            aftv_video.setLayoutParams(videoViewParam);
//        }
        aftv_video.setAspectRatio(videoWidth, videoHeight);
    }

    /**************************************************
     * 对外提供的API                     *
     **************************************************/

    public void setSaveVideoPath(String path) {
        CameraInterface.getInstance().setSaveVideoPath(path);
    }


    public void setJCameraLisenter(JCameraListener jCameraLisenter) {
        this.jCameraListener = jCameraLisenter;
    }


    private ErrorListener errorLisenter;

    /**
     * 设置 Camera 错误回调
     */
    public void setErrorLisenter(ErrorListener errorListener) {
        this.errorLisenter = errorListener;
//        CameraInterface.getInstance().setErrorLinsenter(errorListener);
    }

    /**
     * 设置 CaptureButton 功能（拍照和录像）
     *
     * @param state 标记
     */
    public void setFeatures(int state) {
        this.mCaptureLayout.setButtonFeatures(state);
    }

    /**
     * 设置录制质量
     *
     * @param quality 质量
     */
    public void setMediaQuality(int quality) {
//        CameraInterface.getInstance().setMediaQuality(quality);
    }

    @Override
    public void resetState(int type) {
        switch (type) {
            case TYPE_VIDEO:
                // 停止播放
                stopVideo();
                //初始化VideoView
                FileUtil.deleteFile(videoUrl);
                aftv_preview.setVisibility(View.VISIBLE);
                aftv_video.setVisibility(View.GONE);
                break;
            case TYPE_PICTURE:
                mPhoto.setVisibility(INVISIBLE);
                break;
            case TYPE_SHORT:
//                machine.start(screenProp);
                break;
            case TYPE_DEFAULT:
                aftv_preview.setVisibility(View.VISIBLE);
                aftv_video.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        mSwitchCamera.setVisibility(VISIBLE);
//        mFlashLamp.setVisibility(VISIBLE);
        mFlashLamp.setVisibility(INVISIBLE);
        mCaptureLayout.resetCaptureLayout();
        machine.start(screenProp);
    }

    @Override
    public void confirmState(int type) {
        switch (type) {
            case TYPE_VIDEO:
                stopVideo();
                aftv_preview.setVisibility(View.VISIBLE);
                aftv_video.setVisibility(View.GONE);
                if (jCameraListener != null) {
                    jCameraListener.recordSuccess(videoUrl, firstFrame);
                }
                break;
            case TYPE_PICTURE:
                mPhoto.setVisibility(INVISIBLE);
                if (jCameraListener != null) {
                    jCameraListener.captureSuccess(captureBitmap);
                }
                break;
            case TYPE_SHORT:
                break;
            case TYPE_DEFAULT:
                break;
            default:
                break;
        }
        mCaptureLayout.resetCaptureLayout();
        machine.start(screenProp);
    }

    @Override
    public void showPicture(final Bitmap bitmap, final boolean isVertical) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                mPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                captureBitmap = bitmap;
                mPhoto.setImageBitmap(bitmap);
                mPhoto.setVisibility(VISIBLE);
                mCaptureLayout.startAlphaAnimation();
                mCaptureLayout.startTypeBtnAnimator();
            }
        });
    }

    @Override
    public void playVideo(Bitmap firstFrame, final String url) {
        videoUrl = url;
        JCamera2View.this.firstFrame = firstFrame;
        getHandler().post(
                new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        try {
                            if (mMediaPlayer == null) {
                                Log.i(TAG, "run: 重新创建 MediaPlayer");
                                mMediaPlayer = new MediaPlayer();
                            } else {
                                Log.i(TAG, "run: 已存在 MediaPlayer ,则重新配置");
                                if (mMediaPlayer.isPlaying()) {
                                    mMediaPlayer.stop();
                                    mMediaPlayer.release();
                                }
                                mMediaPlayer.reset();
                            }
                            mMediaPlayer.setDataSource(url);
                            Log.i(TAG, "run: 播放的视频地址：" + url);
//                            // 隐藏预览的视图 TextureView
                            aftv_preview.setVisibility(View.GONE);
                            aftv_video.setVisibility(View.VISIBLE);
                            mMediaPlayer.setSurface(mSurface);
                            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            Log.i(TAG, "playVideo: videoWidth=" + mMediaPlayer.getVideoWidth() + ", videoHeight=" + mMediaPlayer.getVideoHeight());
                            mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                @Override
                                public void
                                onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                    Log.i(TAG, "onVideoSizeChanged: 视频大小宽高发生改变");
                                    updateVideoViewSize(width, height);
                                }
                            });
                            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    Log.i(TAG, "onPrepared: 开始播放");
                                    mMediaPlayer.start();
                                }
                            });
                            mMediaPlayer.setLooping(true);
                            mMediaPlayer.prepare();
                            Log.i(TAG, "run: 开始加载。。。");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    @Override
    public void stopVideo() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        // 显示播放录制视频的 VideoView
//        mVideoView.setVisibility(View.GONE);
        // 隐藏预览的视图 TextureView
//        aftv_preview.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTip(String tip) {
        mCaptureLayout.setTip(tip);
    }

    @Override
    public void startPreviewCallback() {
        LogUtil.i("startPreviewCallback");
        handlerFoucs(mFoucsView.getWidth() / 2, mFoucsView.getHeight() / 2);
    }

    @Override
    public boolean handlerFoucs(float x, float y) {
        if (y > mCaptureLayout.getTop()) {
            return false;
        }
        mFoucsView.setVisibility(VISIBLE);
        if (x < mFoucsView.getWidth() / 2) {
            x = mFoucsView.getWidth() / 2;
        }
        if (x > layout_width - mFoucsView.getWidth() / 2) {
            x = layout_width - mFoucsView.getWidth() / 2;
        }
        if (y < mFoucsView.getWidth() / 2) {
            y = mFoucsView.getWidth() / 2;
        }
        if (y > mCaptureLayout.getTop() - mFoucsView.getWidth() / 2) {
            y = mCaptureLayout.getTop() - mFoucsView.getWidth() / 2;
        }
        mFoucsView.setX(x - mFoucsView.getWidth() / 2);
        mFoucsView.setY(y - mFoucsView.getHeight() / 2);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFoucsView, "scaleX", 1, 0.6f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFoucsView, "scaleY", 1, 0.6f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFoucsView, "alpha", 1f, 0.4f, 1f, 0.4f, 1f, 0.4f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleX).with(scaleY).before(alpha);
        animSet.setDuration(400);
        animSet.start();
        return true;
    }

    public void setLeftClickListener(ClickListener clickListener) {
        this.leftClickListener = clickListener;
    }

    public void setRightClickListener(ClickListener clickListener) {
        this.rightClickListener = clickListener;
    }

    private void setFlashRes() {
        switch (type_flash) {
            case TYPE_FLASH_AUTO:
                mFlashLamp.setImageResource(R.drawable.ic_flash_auto);
                machine.flash(Camera.Parameters.FLASH_MODE_AUTO);
                break;
            case TYPE_FLASH_ON:
                mFlashLamp.setImageResource(R.drawable.ic_flash_on);
                machine.flash(Camera.Parameters.FLASH_MODE_ON);
                break;
            case TYPE_FLASH_OFF:
                mFlashLamp.setImageResource(R.drawable.ic_flash_off);
                machine.flash(Camera.Parameters.FLASH_MODE_OFF);
                break;
        }
    }
}
