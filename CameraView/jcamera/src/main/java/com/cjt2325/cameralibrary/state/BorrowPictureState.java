package com.cjt2325.cameralibrary.state;

import android.view.Surface;
import android.view.SurfaceHolder;
import com.cjt2325.cameralibrary.Camera2Interface;
import com.cjt2325.cameralibrary.JCamera2View;
import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.util.LogUtil;

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/9/8
 * 描    述：
 * =====================================
 */
public class BorrowPictureState implements State {
    private final String TAG = BorrowPictureState.class.getName();
    private CameraMachine machine;

    public BorrowPictureState(CameraMachine machine) {
        this.machine = machine;
    }

    @Override
    public void start(float screenProp) {
//        CameraInterface.getInstance().doStartPreview(holder, screenProp);
        Camera2Interface.INSTANCE.createCameraPreviewSession();
        machine.setState(machine.getPreviewState());
    }

    @Override
    public void stop() {

    }

    //    @Override
//    public void foucs(float x, float y, CameraInterface.FocusCallback callback) {
//    }

    @Override
    public void foucs(float x, float y, Camera2Interface.FocusCallback callback) {
    }

    @Override
    public void swtich(float screenProp) {

    }

    @Override
    public void restart() {

    }

    @Override
    public void capture() {

    }

    @Override
    public void record( float screenProp) {

    }

    @Override
    public void stopRecord(boolean isShort, long time) {
    }

    @Override
    public void cancle(float screenProp) {
//        CameraInterface.getInstance().doStartPreview(holder, screenProp);
//        Camera2Interface.INSTANCE.createCameraPreviewSession();
        machine.getView().resetState(JCamera2View.TYPE_PICTURE);
        machine.setState(machine.getPreviewState());
    }

    @Override
    public void confirm() {
        machine.getView().confirmState(JCamera2View.TYPE_PICTURE);
        machine.setState(machine.getPreviewState());
    }

    @Override
    public void zoom(float zoom, int type) {
        LogUtil.i(TAG, "zoom");
    }

    @Override
    public void flash(String mode) {

    }

}
