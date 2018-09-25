package com.junkchen.customcamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class Camera2Activity extends AppCompatActivity {
    public static final String TAG = Camera2Activity.class.getSimpleName();

    private CameraDevice mCamera;
    private CaptureRequest mCaptureRequest;

    private SurfaceView mSurfaceView;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        mSurfaceView =  findViewById(R.id.mSurfaceView);

        final CameraManager mCameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdList = mCameraManager.getCameraIdList();
            Log.i(TAG, "onCreate: cameraIdList: " + cameraIdList.toString());
            for (String s : cameraIdList) {
                Log.i(TAG, "onCreate: --- cameraId: " + s);
            }

//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }

            mCameraManager.openCamera(cameraIdList[0], new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    if (null != null) {
                        mCamera = camera;

                        List<Surface> outputs = new ArrayList<>();
                        outputs.add(mSurfaceView.getHolder().getSurface());
                        try {
                            CaptureRequest.Builder builder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                            builder.addTarget(mSurfaceView.getHolder().getSurface());
                            mCaptureRequest = builder.build();
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }

                        try {
                            mCamera.createCaptureSession(outputs, new CameraCaptureSession.StateCallback() {
                                @Override
                                public void onConfigured(@NonNull CameraCaptureSession session) {
                                    try {
                                        session.capture(mCaptureRequest, new CameraCaptureSession.CaptureCallback() {
                                            @Override
                                            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                                                super.onCaptureStarted(session, request, timestamp, frameNumber);
                                            }
                                        }, new Handler());
                                    } catch (CameraAccessException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                                }
                            }, new Handler());
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    Log.i(TAG, "onError: error: " + error);
                }
            }, new Handler());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
