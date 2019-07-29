package com.cjt2325.cameralibrary.state;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.cjt2325.cameralibrary.Camera2Interface;
import com.cjt2325.cameralibrary.view.CameraView;

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/9/8
 * 描    述：
 * =====================================
 */
public class CameraMachine implements State {

    private Context context;
    private State state;
    private CameraView view;

    /**
     * 浏览状态(空闲)
     */
    private State previewState;

    /**
     * 浏览图片
     */
    private State borrowPictureState;

    /**
     * 浏览视频
     */
    private State borrowVideoState;

    public CameraMachine(Context context, CameraView view) {
        this.context = context;
        previewState = new PreviewState(this);
        borrowPictureState = new BorrowPictureState(this);
        borrowVideoState = new BorrowVideoState(this);
        //默认设置为空闲状态
        this.state = previewState;
        this.view = view;
    }

    public CameraView getView() {
        return view;
    }

    public Context getContext() {
        return context;
    }

    public void setState(State state) {
        this.state = state;
    }

    /**
     * 获取浏览图片状态
     */
    State getBorrowPictureState() {
        return borrowPictureState;
    }

    /**
     * 获取浏览视频状态
     */
    State getBorrowVideoState() {
        return borrowVideoState;
    }

    /**
     * 获取空闲状态
     */
    State getPreviewState() {
        return previewState;
    }

    @Override
    public void start(float screenProp) {
        state.start(screenProp);
    }

    @Override
    public void stop() {
        state.stop();
    }

    @Override
    public void foucs(float x, float y, Camera2Interface.FocusCallback callback) {
        state.foucs(x, y, callback);
    }

    @Override
    public void swtich(float screenProp) {
        state.swtich(screenProp);
    }

    @Override
    public void restart() {
        state.restart();
    }

    @Override
    public void capture() {
        state.capture();
    }

    @Override
    public void record(float screenProp) {
        state.record(screenProp);
    }

    @Override
    public void stopRecord(boolean isShort, long time) {
        state.stopRecord(isShort, time);
    }

    @Override
    public void cancle(float screenProp) {
        state.cancle(screenProp);
    }

    @Override
    public void confirm() {
        state.confirm();
    }


    @Override
    public void zoom(float zoom, int type) {
        state.zoom(zoom, type);
    }

    @Override
    public void flash(String mode) {
        state.flash(mode);
    }

    public State getState() {
        return this.state;
    }
}
