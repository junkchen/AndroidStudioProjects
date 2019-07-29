package com.cjt2325.cameralibrary.state;

import com.cjt2325.cameralibrary.Camera2Interface;

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/9/8
 * 描    述：
 * =====================================
 */
public interface State {

    void start(float screenProp);

    void stop();

    void foucs(float x, float y, Camera2Interface.FocusCallback callback);

    void swtich(float screenProp);

    void restart();

    void capture();

    void record(float screenProp);

    void stopRecord(boolean isShort, long time);

    void cancle(float screenProp);

    void confirm();

    void zoom(float zoom, int type);

    void flash(String mode);
}
