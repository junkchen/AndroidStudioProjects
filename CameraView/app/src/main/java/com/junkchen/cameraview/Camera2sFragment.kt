package com.junkchen.cameraview


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_camera2.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 * Android 相机 Camera2 API 的使用
 * Created by Junk Chen on 2019/7/2.
 */
class Camera2sFragment : Fragment() {
    private val TAG = Camera2sFragment::class.java.name

    private val VIDEO_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )
    private val REQUEST_CAMERA_PERMISSION = 1
    private val FRAGMENT_DIALOG = "dialog"

    private val SENSOR_ORIENTATION_DEFAULT_DEGREES = 90
    private val SENSOR_ORIENTATION_INVERSE_DEGREES = 270

    /**
     * [TextureView.SurfaceTextureListener] handles several lifecycle events on a
     * [TextureView].
     */
    private val mSurfaceTextureListener = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            Camera2Interface.openCamera(width, height)
        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            Camera2Interface.configureTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}

    }


    /**
     * An [AutoFitTextureView] for camera preview.
     */
    private var mTextureView: TextureView? = null



    /**
     * 在UI线程中显示一个吐司提示
     * Shows a [Toast] on the UI thread.
     *
     * @param text The message to show（要显示的信息）
     */
    private fun showToast(text: String) {
        activity?.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i(TAG, "onActivityCreated: ------------onActivityCreated-----------")
        btn_capture.setOnClickListener {
            Camera2Interface.takePicture()
//            takePicture()
        }
        btn_video.setOnClickListener {
            if (Camera2Interface.mIsRecordingVideo) {
                Camera2Interface.stopRecordingVideo()
            } else {
                Camera2Interface.startRecordingVideo()
            }
//            Camera2Interface.recording()
        }
        btn_switch.setOnClickListener {
            Camera2Interface.switchCamera()
        }

        // 1、检查权限
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
            return
        }
        tv_preview.surfaceTextureListener = mSurfaceTextureListener
        Camera2Interface.init(activity!!, context!!, tv_preview)
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ---onResume()---tv_preview.isAvailable=${tv_preview.isAvailable}")
        Camera2Interface.onResume()

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
//        if (tv_preview.isAvailable) {
//            openCamera(tv_preview.width, tv_preview.height)
//        } else {
//            tv_preview.surfaceTextureListener = mSurfaceTextureListener
//        }
    }

    override fun onPause() {
        Camera2Interface.onPause()
        super.onPause()
    }

    /**
     * Gets whether you should show UI with rationale for requesting permissions.
     *
     * @param permissions The permissions your app wants to request.
     * @return Whether you can show permission rationale UI.
     */
    private fun shouldShowRequestPermissionRationale(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
                return true
            }
        }
        return false
    }

    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(VIDEO_PERMISSIONS)) {
            ConfirmationDialog().show(childFragmentManager, FRAGMENT_DIALOG)
        } else {
            requestPermissions(VIDEO_PERMISSIONS, REQUEST_CAMERA_PERMISSION)
        }
    }

    /**
     * 权限请求响应的结果处理
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ErrorDialog.newInstance(getString(R.string.request_permission))
                        .show(childFragmentManager, FRAGMENT_DIALOG)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * 显示一个错误消息的弹框
     * Shows an error message dialog.
     */
    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity = activity
            return AlertDialog.Builder(activity)
                .setMessage(arguments!!.getString(ARG_MESSAGE))
                .setPositiveButton(
                    android.R.string.ok
                ) { dialogInterface, i -> activity!!.finish() }
                .create()
        }

        companion object {

            private val ARG_MESSAGE = "message"

            fun newInstance(message: String): ErrorDialog {
                val dialog = ErrorDialog()
                val args = Bundle()
                args.putString(ARG_MESSAGE, message)
                dialog.arguments = args
                return dialog
            }
        }

    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    @SuppressLint("ValidFragment")
    inner class ConfirmationDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val parent = parentFragment
            return AlertDialog.Builder(activity)
                .setMessage(R.string.request_permission)
                .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                    parent!!.requestPermissions(VIDEO_PERMISSIONS, REQUEST_CAMERA_PERMISSION)
                })
                .setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { dialog, which ->
                        val activity = parent!!.activity
                        activity?.finish()
                    })
                .create()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = Camera2sFragment()
    }
}
