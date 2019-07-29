package com.junkchen.cameraview


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_jcamera.*

/**
 * A simple [Fragment] subclass.
 * Use the [JcameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class JcameraFragment : Fragment() {

    private val VIDEO_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )
    private val REQUEST_CAMERA_PERMISSION = 1
    private val FRAGMENT_DIALOG = "dialog"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jcamera, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 1、检查权限
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
            return
        }
        jcameraview.init(activity)
    }

    override fun onResume() {
        super.onResume()
//        jcameraview.onResume()
    }

    override fun onPause() {
//        jcameraview.onPause()
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
                    Camera2sFragment.ErrorDialog.newInstance(getString(R.string.request_permission))
                        .show(childFragmentManager, FRAGMENT_DIALOG)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        /**
         * Use this factory method to create a new instance of this fragment
         *
         * @return A new instance of fragment JcameraFragment.
         */
        @JvmStatic
        fun newInstance() = JcameraFragment()
    }
}
