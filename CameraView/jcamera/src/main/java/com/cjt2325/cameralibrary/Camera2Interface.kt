package com.cjt2325.cameralibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.widget.Toast
import com.cjt2325.cameralibrary.util.AngleUtil
import com.cjt2325.cameralibrary.util.FileUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * Created by Junk Chen on 2019/7/8.
 */
@SuppressLint("StaticFieldLeak")
object Camera2Interface {
    private val TAG = Camera2Interface::class.java.name

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private val DEFAULTORIENTATIONS = SparseIntArray()
    private val INVERSE_ORIENTATIONS = SparseIntArray()
    private val SENSOR_ORIENTATION_DEFAULT_DEGREES = 90
    private val SENSOR_ORIENTATION_INVERSE_DEGREES = 270

    init {
        DEFAULTORIENTATIONS.append(Surface.ROTATION_0, 90)
        DEFAULTORIENTATIONS.append(Surface.ROTATION_90, 0)
        DEFAULTORIENTATIONS.append(Surface.ROTATION_180, 270)
        DEFAULTORIENTATIONS.append(Surface.ROTATION_270, 180)

        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270)
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180)
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90)
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0)
    }

    /**
     * Camera state: Showing camera preview.
     */
    private val STATE_PREVIEW = 0

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private val STATE_WAITING_LOCK = 1

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private val STATE_WAITING_PRECAPTURE = 2

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private val STATE_WAITING_NON_PRECAPTURE = 3

    /**
     * Camera state: Picture was taken.
     */
    private val STATE_PICTURE_TAKEN = 4

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private val MAX_PREVIEW_WIDTH = 1920

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private val MAX_PREVIEW_HEIGHT = 1080

    private lateinit var activity: Activity
    private lateinit var context: Context

    /**
     * ID of the current [CameraDevice].
     */
    private var mCameraId: String? = null

    private var angle = 0
    /**
     * 摄像头角度   默认为90 度
     */
    private var cameraAngle = 90

    /**
     * 是否后置摄像头
     */
    private var isFacingBack = true

    /**
     * An [AutoFitTextureView] for camera preview.
     */
    var tv_preview: AutoFitTextureView? = null

    /**
     * A [CameraCaptureSession] for camera preview.
     */
    private var mPreviewSession: CameraCaptureSession? = null

    /**
     * A reference to the opened [CameraDevice].
     */
    private var mCameraDevice: CameraDevice? = null

    /**
     * The [android.util.Size] of camera preview.
     */
    private var mPreviewSize: Size? = null

    /**
     * The [android.util.Size] of video recording.
     */
    private var mVideoSize: Size? = null

    /**
     * MediaRecorder
     */
    private var mMediaRecorder: MediaRecorder? = null

    /**
     * 是否正在录制视频
     * Whether the app is recording video now
     */
    var mIsRecordingVideo: Boolean = false

    /**
     * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
     */
    private val mStateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release()
            mCameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
            val activity = activity
            activity?.finish()
        }
    }

    /**
     * 一个不阻塞UI的后台线程
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private var mBackgroundThread: HandlerThread? = null

    /**
     * Handler 用于处理运行的后台任务
     * A [Handler] for running tasks in the background.
     */
    private var mBackgroundHandler: Handler? = null

    /**
     * ImageReader 用于处理拍摄的照片
     * An [ImageReader] that handles still image capture.
     */
    private var mImageReader: ImageReader? = null

    /**
     * 图片输出文件
     * This is the output file for our picture.
     */
    private var mFile: File? = null

    /**
     * 当拍摄的图片要保存时会调用 ImageReader 的 onImageAvailable 方法
     * This a callback object for the [ImageReader]. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        Log.i(TAG, "OnImageAvailableListener: 获取到第一帧图像结果或拍摄的图片")
        val image = reader.acquireNextImage()
        try {
            val buffer = image.planes[0].buffer
            val byteArray = ByteArray(buffer.remaining())
            buffer.get(byteArray)
            Log.i(TAG, "OnImageAvailableListener: bytesize=${byteArray.size}")
            videoFirstFrame = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            Log.i(TAG, "OnImageAvailableListener: 拍摄的图片 bitmap size: ${videoFirstFrame?.width}x${videoFirstFrame?.height}")
            Log.i(TAG, "OnImageAvailableListener: videoFirstFrame=$videoFirstFrame")
            val matrix = Matrix()
//            matrix.setRotate(90F)
            Log.i(TAG, "OnImageAvailableListener: 摄像头 lensFacing=$lensFacing")
            if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                matrix.setRotate(nowAngle.toFloat())
            } else if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                matrix.setRotate((360 - nowAngle).toFloat())
                matrix.postScale(-1f, 1f)
            }
//            videoFirstFrame = Bitmap.createBitmap(
//                videoFirstFrame, 0, 0,
//                videoFirstFrame!!.width, videoFirstFrame!!.height, matrix, true
//            )
            videoFirstFrame = Bitmap.createBitmap(
                videoFirstFrame, 0, 0,
                mPreviewSize!!.width, mPreviewSize!!.height, matrix, true
            )
            videoFirstFrame?.let {
                Log.i(TAG, "OnImageAvailableListener: 拍摄的图片 bitmap size: ${it.byteCount}")
                Log.i(TAG, "OnImageAvailableListener: 拍摄的图片 bitmap size: ${it.width}x${it.height}")
                takePictureCallback?.captureResult(it, nowAngle == 90 || nowAngle == 270)
            }
            Log.i(TAG, "OnImageAvailableListener: 获取到第一帧图像结果或拍摄的图片")
//                mFile?.let {
//                    Log.i(TAG, "OnImageAvailableListener: 保存图片")
//                    ImageSaver(image, it).run()
//                }
        } finally {
            image.close()
        }
    }

    /**
     * [CaptureRequest.Builder] for the camera preview
     */
    private var mPreviewBuilder: CaptureRequest.Builder? = null

    /**
     * [CaptureRequest] generated by [.mPreviewBuilder]
     */
    private var mPreviewRequest: CaptureRequest? = null

    /**
     * 拍照时相机当前状态
     * The current state of camera state for taking pictures.
     *
     * @see .mCaptureCallback
     */
    private var mState = STATE_PREVIEW

    /**
     * 一个在相机关闭前阻止应用退出的信号
     * A [Semaphore] to prevent the app from exiting before closing the camera.
     */
    private val mCameraOpenCloseLock = Semaphore(1)

    /**
     * 当前相机设备是否支持闪光灯
     * Whether the current camera device supports Flash or not.
     */
    private var mFlashSupported: Boolean = false

    /**
     * 相机传感器的方向
     * Orientation of the camera sensor
     */
    private var mSensorOrientation: Int = 0

    /**
     * 视频存储路径
     */
    private var mNextVideoAbsolutePath: String? = null

    /**
     * 视频第一帧图像
     */
    private var videoFirstFrame: Bitmap? = null

    /**
     * 处理拍照时相关的回调事件
     * A [CameraCaptureSession.CaptureCallback] that handles events related to JPEG capture.
     */
    private val mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {

        /**
         * 处理拍摄的结果
         */
        private fun process(result: CaptureResult) {
            when (mState) {
                // 预览
                STATE_PREVIEW -> {
//                    Log.i(TAG, "process: 预览状态 STATE_PREVIEW")
                }// We have nothing to do when the camera preview is working normally.
                STATE_WAITING_LOCK -> {
                    val afState = result.get(CaptureResult.CONTROL_AF_STATE)
                    Log.i(TAG, "process: 拍照锁定状态 STATE_WAITING_LOCK， afState=$afState")
                    if (afState == null) {
                        captureStillPicture()
                    } else if (
                        CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState
                        || CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState
                    ) {
                        // CONTROL_AE_STATE can be null on some devices
                        val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN
                            captureStillPicture()
                        } else {
                            runPrecaptureSequence()
                        }
                    }
                }
                STATE_WAITING_PRECAPTURE -> {
                    // CONTROL_AE_STATE can be null on some devices
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null ||
                        aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                        aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED
                    ) {
                        mState = STATE_WAITING_NON_PRECAPTURE
                    }
                }
                STATE_WAITING_NON_PRECAPTURE -> {
                    // CONTROL_AE_STATE can be null on some devices
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN
                        captureStillPicture()
                    }
                }
            }
        }

        /**
         * 拍摄进度
         */
        override fun onCaptureProgressed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            partialResult: CaptureResult
        ) {
            Log.i(TAG, "onCaptureProgressed: 拍摄中。。。")
            process(partialResult)
        }

        /**
         * 拍照完成
         */
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
//            Log.i(TAG, "onCaptureCompleted: 拍摄完成")
            process(result)
        }
    }

    private lateinit var cameraManager: CameraManager

    fun init(activity: Activity, context: Context, textureView: AutoFitTextureView) {
        this.activity = activity
        this.context = context
        this.tv_preview = textureView
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mFile = File("${Environment.getExternalStorageDirectory()}", "pic.jpg")
        mFile?.createNewFile()
    }

    fun onResume() {
        startBackgroundThread()
        mPreviewSize?.apply { openCamera(width, height) }
    }

    fun onPause() {
        closeCamera()
        stopBackgroundThread()
    }

    /**
     * 使用指定的相机
     * Opens the camera specified by {@link Camera2BasicFragment#mCameraId}.
     */
    @SuppressLint("MissingPermission")
    fun openCamera(width: Int, height: Int) {
        // 2、设置相机
        setUpCameraOutputs(width, height)
        configureTransform(width, height)
        mMediaRecorder = MediaRecorder()
//        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        if (!mCameraOpenCloseLock.tryAcquire(2500L, TimeUnit.MILLISECONDS)) {
            throw RuntimeException("Time out waiting to lock camera opening.")
        }
        cameraManager.openCamera(mCameraId!!, mStateCallback, mBackgroundHandler)
    }

    /**
     * 关闭当前相机设备
     * Closes the current {@link CameraDevice}.
     */
    private fun closeCamera() {
        mCameraOpenCloseLock.acquire()
        mPreviewSession?.close()
        mPreviewSession = null
        mCameraDevice?.close()
        mCameraDevice = null
        mImageReader?.close()
        mImageReader = null
        mCameraOpenCloseLock.release()
    }

    private var lensFacing = CameraCharacteristics.LENS_FACING_BACK
    /**
     * 设置相机相关的参数
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview，相机预览宽度
     * @param height The height of available size for camera preview，相机预览高度
     */
    private fun setUpCameraOutputs(width: Int, height: Int) {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)

            // We don't use a front facing camera in this sample.在这个demo中我们不使用前置摄像头
            val lens = characteristics.get(CameraCharacteristics.LENS_FACING) ?: 1
            Log.i(TAG, "setUpCameraOutputs: 摄像头 lens=$lens")
            if (lens == CameraCharacteristics.LENS_FACING_FRONT && isFacingBack) {
                continue
            }
            if (lens == CameraCharacteristics.LENS_FACING_BACK && !isFacingBack) {
                continue
            }
            lensFacing = lens

            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue

            // For still image captures, we use the largest available size.
            val largest = Collections.max(
                Arrays.asList(*map.getOutputSizes(ImageFormat.JPEG)),
                CompareSizesByArea()
            )


            // Find out if we need to swap dimension to get the preview size relative to sensor coordinate.
            val displayRotation = activity?.windowManager?.defaultDisplay?.rotation
            // noinspection ConstantConditions
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
            Log.i(TAG, "setUpCameraOutputs: 相机传感器方向 mSensorOrientation=$mSensorOrientation, displayRotation=$displayRotation")
            var swappedDimensions = false
            when (displayRotation) {
                Surface.ROTATION_0, Surface.ROTATION_180 -> {
                    if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                        swappedDimensions = true
                    }
                }
                Surface.ROTATION_90, Surface.ROTATION_270 -> {
                    if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                        swappedDimensions = true
                    }
                }
                else -> Log.e(TAG, "Display rotation is invalid: $displayRotation")
            }

            val displaySize = Point()
            activity?.windowManager?.defaultDisplay?.getSize(displaySize)
            var rotatePreviewWidth = width
            var rotatePreviewHeight = height
            var maxPreviewWidth = displaySize.x
            var maxPreviewHeight = displaySize.y

            if (swappedDimensions) {
                rotatePreviewWidth = height
                rotatePreviewHeight = width
                maxPreviewWidth = displaySize.y
                maxPreviewHeight = displaySize.x
            }

            if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                maxPreviewWidth = MAX_PREVIEW_WIDTH
            }
            if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                maxPreviewHeight = MAX_PREVIEW_HEIGHT
            }

            // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
            // bus' bandwidth limitation, resulting in gorgeous previews but the storage of garbage capture data.
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder::class.java))
//            mPreviewSize = chooseOptimalSize(
//                map.getOutputSizes(SurfaceTexture::class.java),
//                rotatePreviewWidth, rotatePreviewHeight, maxPreviewWidth, maxPreviewHeight, largest
//            )
            Log.i(TAG, "setUpCameraOutputs: largest size: $largest")
            Log.i(TAG, "setUpCameraOutputs: mVideoSize width=${mVideoSize?.width}, height=${mVideoSize?.height}")
            mPreviewSize = chooseOptimalSize(
                map.getOutputSizes(SurfaceTexture::class.java),
                rotatePreviewWidth, rotatePreviewHeight, largest
            )
            mPreviewSize = Size(height, width)
//            mVideoSize = Size(height, width)

            mImageReader = ImageReader.newInstance(
//                largest.width, largest.height,
                mPreviewSize!!.width, mPreviewSize!!.height,
                ImageFormat.JPEG, /*maxImages*/1
            )
            mImageReader?.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler)

            // We fit the aspect ratio of TextureView to the size of preview we picked.
            val orientation = context.resources.configuration.orientation
            Log.i(TAG, "setUpCameraOutputs: mVideoSize width=${mVideoSize?.width}, height=${mVideoSize?.height}")
            Log.i(TAG, "setUpCameraOutputs: mPreviewSize width=${mPreviewSize?.width}, height=${mPreviewSize?.height}")
            Log.i(TAG, "setUpCameraOutputs: max preview width=${maxPreviewWidth}, height=${maxPreviewHeight}")
            Log.i(TAG, "setUpCameraOutputs: texture width=${rotatePreviewWidth}, height=${rotatePreviewHeight}")
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                tv_preview?.setAspectRatio(mPreviewSize?.width!!, mPreviewSize?.height!!)
//                tv_preview?.setAspectRatio(rotatePreviewWidth, rotatePreviewHeight)
            } else {
                tv_preview?.setAspectRatio(mPreviewSize?.height!!, mPreviewSize?.width!!)
//                tv_preview?.setAspectRatio(rotatePreviewHeight, rotatePreviewWidth)
            }

            // Check if the flash is supported.
            mFlashSupported = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false

            mCameraId = cameraId
        }
    }

    /**
     * Creates a new [CameraCaptureSession] for camera preview.
     */
    fun createCameraPreviewSession() {
        try {
            closePreviewSession()
            val texture = tv_preview?.surfaceTexture

            // We configure the size of default buffer to be the size of camera preview we want.
            texture?.setDefaultBufferSize(mPreviewSize?.width!!, mPreviewSize?.height!!)

            // This is the output Surface we need to start preview.
            val surface = Surface(texture)

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewBuilder?.addTarget(surface)

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice?.createCaptureSession(
                Arrays.asList(surface, mImageReader?.surface),
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        // The camera is already closed
                        if (null == mCameraDevice) {
                            return
                        }

                        // When the session is ready, we start displaying the preview.
                        mPreviewSession = cameraCaptureSession
                        try {
                            // Auto focus should be continuous for camera preview.
                            mPreviewBuilder?.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )
                            // Flash is automatically enabled when necessary.
                            setAutoFlash(mPreviewBuilder!!)

                            // Finally, we start displaying the camera preview.
                            mPreviewRequest = mPreviewBuilder?.build()
                            mPreviewSession?.setRepeatingRequest(
                                mPreviewRequest!!,
                                mCaptureCallback, mBackgroundHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }

                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        TODO("")
//                        showToast("Failed")
                    }
                }, null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    fun configureTransform(viewWidth: Int, viewHeight: Int) {
        val activity = activity
        if (null == tv_preview || null == mPreviewSize || null == activity) {
            return
        }
        val rotation = activity.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0F, 0F, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0F, 0F, mPreviewSize!!.width.toFloat(), mPreviewSize!!.height.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale = Math.max(
                viewHeight * 1.0F / mPreviewSize?.height!!,
                viewWidth * 1.0F / mPreviewSize?.width!!
            )
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180F, centerX, centerY)
        }
    }

    /**
     * 启动一个后台线程和他的 Handler
     * Starts a background thread and its {@link Handler}.
     */
    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread?.looper)
    }

    /**
     * 停止后台线程和它的 Handler
     * Stops the background thread and its {@link Handler}.
     */
    private fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()
        mBackgroundThread?.join()
        mBackgroundThread = null
        mBackgroundHandler = null
    }

    /**
     * 拍照
     */
    private var nowAngle: Int = 0

    /**
     * Initiate a still image capture.
     */
    fun takePicture(callback: TakePictureCallback?) {
        Log.i(TAG, "takePicture: 开始拍照")
        this.takePictureCallback = callback
        when (cameraAngle) {
            90 -> nowAngle = Math.abs(angle + cameraAngle) % 360
            270 -> nowAngle = Math.abs(cameraAngle - angle)
        }
        lockFocus(callback)
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private fun lockFocus(callback: TakePictureCallback?) {
        try {
            Log.i(TAG, "lockFocus: ---lockFocus start----")
            // This is how to tell the camera to lock focus.
            mPreviewBuilder?.set(
                CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_START
            )
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK
            mPreviewSession?.capture(
                mPreviewBuilder?.build()!!, mCaptureCallback,
                mBackgroundHandler
            )
            Log.i(TAG, "lockFocus: ---lockFocus end----")
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 拍照结果回调
     */
    private var takePictureCallback: TakePictureCallback? = null

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private fun unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewBuilder?.set(
                CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_CANCEL
            )
            mPreviewBuilder?.let { setAutoFlash(it) }
            mPreviewSession?.capture(
                mPreviewBuilder?.build()!!, mCaptureCallback,
                mBackgroundHandler
            )
            // After this, the camera will go back to the normal state of preview.
//            mState = STATE_PREVIEW
//            mPreviewSession?.setRepeatingRequest(
//                mPreviewRequest!!, mCaptureCallback,
//                mBackgroundHandler
//            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 设置多媒体录制器
     */
    private fun setUpMediaRecorder() {
        val activity = activity ?: return
        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath?.isEmpty()!!) {
            mNextVideoAbsolutePath = getVideoFilePath(activity)
        }
        mMediaRecorder?.setOutputFile(mNextVideoAbsolutePath)
        mMediaRecorder?.setVideoEncodingBitRate(10000000)
        mMediaRecorder?.setVideoFrameRate(30)
        Log.i(TAG, "setUpMediaRecorder: 录制的视频尺寸: width=${mVideoSize?.width}, height=${mVideoSize?.height}")
        mMediaRecorder?.setVideoSize(mVideoSize?.width!!, mVideoSize?.height!!)
        mMediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        val rotation = activity.windowManager.defaultDisplay.rotation
        Log.i(TAG, "setUpMediaRecorder: 显示方向 mSensorOrientation=$mSensorOrientation, rotation=$rotation, angle=$angle")
        when (mSensorOrientation) {
            SENSOR_ORIENTATION_DEFAULT_DEGREES ->
                mMediaRecorder?.setOrientationHint(DEFAULTORIENTATIONS.get(angle))
            SENSOR_ORIENTATION_INVERSE_DEGREES ->
                mMediaRecorder?.setOrientationHint(INVERSE_ORIENTATIONS.get(angle))
        }

        mMediaRecorder?.prepare()
    }

    private fun getVideoFilePath(context: Context): String {
        val dir = context.getExternalFilesDir(null)
        return ((if (dir == null) "" else dir.absolutePath + "/")
                + System.currentTimeMillis() + ".mp4")
    }

    fun recording() {
        if (mIsRecordingVideo) {
            stopRecordingVideo(callback = null)
        } else {
            startRecordingVideo()
        }
    }

    fun startRecordingVideo() {
        if (null == mCameraDevice || !tv_preview?.isAvailable!! || null == mPreviewSize) {
            return
        }
        closePreviewSession()
        setUpMediaRecorder()
        val texture = tv_preview?.surfaceTexture
        texture?.setDefaultBufferSize(mPreviewSize?.width!!, mPreviewSize?.height!!)
        mPreviewBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
        var surfaces = arrayListOf<Surface>()

        // Set up Surface for the camera preview
        val previewSurface = Surface(texture)
        surfaces.add(previewSurface)
        mPreviewBuilder?.addTarget(previewSurface)

        // Set up Surface for the MediaRecorder
        val recorderSurface = mMediaRecorder?.surface
        surfaces.add(recorderSurface!!)
        mPreviewBuilder?.addTarget(recorderSurface)

        // Start a capture session
        // Once the session starts, we can update the UI and start recording
        mCameraDevice?.createCaptureSession(surfaces, object : CameraCaptureSession.StateCallback() {
            override fun onConfigureFailed(session: CameraCaptureSession) {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onConfigured(session: CameraCaptureSession) {
                Log.i(TAG, "onConfigured: 配置成功，开始录制")
                mPreviewSession = session
                updatePreview()
                mIsRecordingVideo = true
                mMediaRecorder?.start()
            }
        }, mBackgroundHandler)
    }

    /**
     * 结束/停止录制视频
     * @param isShort 录制时间是否过短，true 表示录制时间过短
     */
    fun stopRecordingVideo(isShort: Boolean = false, callback: StopRecordCallback?) {
        Log.i(TAG, "stopRecordingVideo: 停止录制")
        // UI
        mIsRecordingVideo = false
//        btn_video.text = "录制"

        // Stop recording
        mMediaRecorder?.stop()
        mMediaRecorder?.reset()

        Log.i(TAG, "stopRecordingVideo: 视频存放地址: video path: $mNextVideoAbsolutePath")
        if (isShort) {
            // 录制时间太短，则删除临时录制的内容
            FileUtil.deleteFile(mNextVideoAbsolutePath)
//            createCameraPreviewSession()
        }

        callback?.recordResult(mNextVideoAbsolutePath, videoFirstFrame)
        mNextVideoAbsolutePath = null
    }

    /**
     * Update the camera preview. [.startPreview] needs to be called in advance.
     */
    private fun updatePreview() {
        if (null == mCameraDevice) {
            return
        }
        try {
            setUpCaptureRequestBuilder(mPreviewBuilder!!)
            val thread = HandlerThread("CameraPreview")
            thread.start()
            mPreviewSession?.setRepeatingRequest(mPreviewBuilder?.build()!!, null, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun setUpCaptureRequestBuilder(builder: CaptureRequest.Builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
    }

    /**
     * 关闭预览
     */
    fun closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession?.close()
            mPreviewSession = null
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in [.mCaptureCallback] from [.lockFocus].
     */
    private fun runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewBuilder?.set(
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START
            )
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE
            mPreviewSession?.capture(
                mPreviewBuilder?.build()!!, mCaptureCallback,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * [.mCaptureCallback] from both [.lockFocus].
     */
    private fun captureStillPicture() {
        try {
            Log.i(TAG, "captureStillPicture: 开始拍摄照片 start")
            val activity = activity
            if (null == activity || null == mCameraDevice) {
                return
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            val captureBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder?.addTarget(mImageReader?.surface!!)

            // Use the same AE and AF modes as the preview.
            captureBuilder?.set(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )
            captureBuilder?.let { setAutoFlash(it) }

            // Orientation
            val rotation = activity.windowManager.defaultDisplay.rotation
            captureBuilder?.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation))

            val captureCallback = object : CameraCaptureSession.CaptureCallback() {

                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
//                    TODO()
//                    showToast("Saved: $mFile")
                    Log.d(TAG, mFile.toString())
                    unlockFocus()
                }
            }

            mPreviewSession?.stopRepeating()
            mPreviewSession?.abortCaptures()
            mPreviewSession?.capture(captureBuilder?.build()!!, captureCallback, null)
            Log.i(TAG, "captureStillPicture: 开始拍摄照片 end")
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 设置闪光灯
     */
    private fun setAutoFlash(requestBuilder: CaptureRequest.Builder) {
        if (mFlashSupported) {
            requestBuilder.set(
                CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
            )
        }
    }

    /**
     * 切换前后摄像头
     */
    fun switchCamera() {
        isFacingBack = !isFacingBack
        closeCamera()
        mPreviewSize?.apply { openCamera(width, height) }
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (Sensor.TYPE_ACCELEROMETER != event.sensor.type) {
                return
            }
            val values = event.values
            angle = AngleUtil.getSensorAngle(values[0], values[1])
//            rotationAnimation()
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    /**
     * 摄像头传感器管理器
     */
    private var sm: SensorManager? = null

    fun registerSensorManager(context: Context) {
        if (sm == null) {
            sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        sm?.apply {
            registerListener(
                sensorEventListener,
                this.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun unregisterSensorManager(context: Context) {
        if (sm == null) {
            sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        sm?.unregisterListener(sensorEventListener)
    }

    fun handleFocus(context: Context, x: Float, y: Float, callback: FocusCallback?) {
        if (mCameraDevice == null) {
            return
        }
//        final Camera . Parameters params = mCamera.getParameters();
//        Rect focusRect = calculateTapArea (x, y, 1f, context);
//        mCamera.cancelAutoFocus();
//        if (params.getMaxNumFocusAreas() > 0) {
//            List<Camera.Area> focusAreas = new ArrayList<>();
//            focusAreas.add(new Camera . Area (focusRect, 800));
//            params.setFocusAreas(focusAreas);
//        } else {
//            Log.i(TAG, "focus areas not supported")
//            callback?.focusSuccess()
//            return
//        }
//        final String currentFocusMode = params.getFocusMode();
//        try {
//            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            mCamera.setParameters(params);
//            mCamera.autoFocus(new Camera . AutoFocusCallback () {
//                @Override
//                public void onAutoFocus(boolean success, Camera camera) {
//                    if (success || handlerTime > 10) {
//                        Camera.Parameters params = camera . getParameters ();
//                        params.setFocusMode(currentFocusMode);
//                        camera.setParameters(params);
//                        handlerTime = 0;
//                        callback?.focusSuccess()
//                    } else {
//                        handlerTime++;
//                        handleFocus(context, x, y, callback)
//                    }
//                }
//            });
//        } catch (e: Exception) {
//            Log.e(TAG, "autoFocus failer");
//        }
    }

    /**
     * 获取最大预览尺寸
     *
     * @param outputSizes
     * @return
     */
    private fun getMaxSize(outputSizes: Array<Size>?): Size? {
        var sizeMax: Size? = null
        if (outputSizes != null) {
            sizeMax = outputSizes[0]
            for (size in outputSizes) {
                if (size.width * size.height > sizeMax!!.width * sizeMax.height) {
                    sizeMax = size
                }
            }
        }
        return sizeMax
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private fun getOrientation(rotation: Int): Int {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from DEFAULTORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (DEFAULTORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360
    }

    /**
     * 保存图片到指定文件
     * Saves a JPEG [Image] into the specified [File].
     */
    private class ImageSaver internal constructor(
        /**
         * The JPEG image
         */
        private val mImage: Image,
        /**
         * The file we save the image into.
         */
        private val mFile: File
    ) : Runnable {

        override fun run() {
            val buffer = mImage.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            var output: FileOutputStream? = null
            try {
                Log.i(TAG, "run: image size: ${bytes.size}")
                output = FileOutputStream(mFile)
                output.write(bytes)
                Log.i(TAG, "run: file length: ${mFile.length()}")
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                mImage.close()
                if (null != output) {
                    try {
                        output.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    /**
     * Given `choices` of `Size`s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     * class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal `Size`, or an arbitrary one if none were big enough
     */
    private fun chooseOptimalSize(
        choices: Array<Size>, textureViewWidth: Int, textureViewHeight: Int, maxWidth: Int,
        maxHeight: Int, aspectRatio: Size
    ): Size {

        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough = ArrayList<Size>()
        // Collect the supported resolutions that are smaller than the preview Surface
        val notBigEnough = ArrayList<Size>()
        val w = aspectRatio.width
        val h = aspectRatio.height
        for (option in choices) {
            Log.i(TAG, "chooseOptimalSize: width=${option.width}, height=${option.height}")
            if (option.width <= maxWidth && option.height <= maxHeight &&
                option.height == option.width * h / w
            ) {
                if (option.width >= textureViewWidth && option.height >= textureViewHeight) {
                    bigEnough.add(option)
                } else {
                    notBigEnough.add(option)
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size > 0) {
            return Collections.min(bigEnough, CompareSizesByArea())
        } else if (notBigEnough.size > 0) {
            return Collections.max(notBigEnough, CompareSizesByArea())
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size")
            return choices[0]
        }
    }

    /**
     * Given `choices` of `Size`s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal `Size`, or an arbitrary one if none were big enough
     */
    private fun chooseOptimalSize(choices: Array<Size>, width: Int, height: Int, aspectRatio: Size): Size {
        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough = ArrayList<Size>()
        val w = aspectRatio.width
        val h = aspectRatio.height
        for (option in choices) {
            if (option.height == option.width * h / w &&
                option.width >= width && option.height >= height
            ) {
                bigEnough.add(option)
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size > 0) {
            return Collections.min(bigEnough, CompareSizesByArea())
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size")
            return choices[0]
        }
    }

    /**
     * In this sample, we choose a video size with 9x16 aspect ratio. Also, we don't use sizes
     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private fun chooseVideoSize(choices: Array<Size>): Size {
        for (size in choices) {
            Log.i(TAG, "chooseVideoSize: video size: $size")
            if (size.width == size.height * 4 / 3 && size.width <= 1080) {
                return size
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size")
        return choices[choices.size - 1]
    }

    /**
     * Compares two `Size`s based on their areas.
     */
    class CompareSizesByArea : Comparator<Size> {

        override fun compare(lhs: Size, rhs: Size): Int {
            // We cast here to ensure the multiplications won't overflow
            return java.lang.Long.signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)
        }

    }

    interface CameraOpenOverCallback {
        fun cameraHasOpened()
    }

    /**
     * 拍照回调
     */
    interface TakePictureCallback {
        fun captureResult(bitmap: Bitmap, isVertical: Boolean)
    }

    interface StopRecordCallback {
        fun recordResult(url: String?, firstFrame: Bitmap?)
    }

    /**
     * 聚焦回调
     */
    interface FocusCallback {
        fun focusSuccess()
    }
}