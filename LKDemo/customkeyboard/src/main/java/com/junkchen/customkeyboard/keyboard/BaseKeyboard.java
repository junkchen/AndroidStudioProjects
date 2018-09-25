package com.junkchen.customkeyboard.keyboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.support.annotation.XmlRes;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Junk on 2017/10/21.
 */

public class BaseKeyboard extends Keyboard implements KeyboardView.OnKeyboardActionListener {
    private static final String TAG = BaseKeyboard.class.getSimpleName();

    private Context mContext;
    protected EditText mEditText;
    protected View mNextFocusView;
    protected CustomKeyStyle mCustomKeyStyle;

    public BaseKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
        this.mContext = context;
    }

    public BaseKeyboard(Context context, @XmlRes int xmlLayoutResId, int modeId, int width, int height) {
        super(context, xmlLayoutResId, modeId, width, height);
        this.mContext = context;
    }

    public BaseKeyboard(Context context, @XmlRes int xmlLayoutResId, int modeId) {
        super(context, xmlLayoutResId, modeId);
        this.mContext = context;
    }

    public BaseKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
        this.mContext = context;
    }

//    public int getKeyCode(int resId) {
//        if (null != mEditText) {
//
//        }
//    }

    public EditText getEditText() {
        return mEditText;
    }

    public void setEditText(EditText editText) {
        this.mEditText = editText;
    }

    public View getNextFocusView() {
        return mNextFocusView;
    }

    public void setNextFocusView(View nextFocusView) {
        this.mNextFocusView = nextFocusView;
    }

    public CustomKeyStyle getCustomKeyStyle() {
        return mCustomKeyStyle;
    }

    public void setCustomKeyStyle(CustomKeyStyle customKeyStyle) {
        this.mCustomKeyStyle = customKeyStyle;
    }

    public void hideKeyboard() {
        //hideSoftKeyboard(etCurrent);
        if (null != mNextFocusView) mNextFocusView.requestFocus();
    }

    private void playClickAudio(int keycode) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        switch (keycode) {
            case Keyboard.KEYCODE_DELETE:
                audioManager.playSoundEffect(audioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                audioManager.playSoundEffect(audioManager.FX_KEYPRESS_STANDARD);
                break;
        }
    }

    //KeyboardView.OnKeyboardActionListener start---------------------------------------------------
    @Override
    public void onPress(int primaryCode) {
        playClickAudio(primaryCode);
    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (null != mEditText && mEditText.hasFocus()) {
            Editable editable = mEditText.getText();
            int start = mEditText.getSelectionStart();
            Log.i(TAG, "onKey: selection start: " + start);
            //Cancel
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                hideKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {//回退，删除
                if (editable != null && start > 0) {
                    editable.delete(start - 1, start);
                }
            } else {//输入键盘值
                editable.insert(start, Character.toString((char)primaryCode));
            }
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
    //KeyboardView.OnKeyboardActionListener end-----------------------------------------------------

    /**
     * 自定义每个按键的样式
     */
    public interface CustomKeyStyle {
        Drawable getKeyBackground(Key key, EditText etCur);

        Float getKeyTextSize(Key key, EditText etCur);

        Integer getKeyTextColor(Key key, EditText etCur);

        CharSequence getKeyLabel(Key key, EditText etCur);
    }

    public static class SimpleCustomKeyStyle implements CustomKeyStyle {

        @Override
        public Drawable getKeyBackground(Key key, EditText etCur) {
            return key.iconPreview;
        }

        @Override
        public Float getKeyTextSize(Key key, EditText etCur) {
            return null;
        }

        @Override
        public Integer getKeyTextColor(Key key, EditText etCur) {
            return null;
        }

        @Override
        public CharSequence getKeyLabel(Key key, EditText etCur) {
            return key.label;
        }

        protected int getKeyCode(Context context, int resId) {
            if (null != context) {
                return context.getResources().getInteger(resId);
            } else {
                return Integer.MIN_VALUE;
            }
        }

        protected Drawable getDrawable(Context context, int resId) {
            if (null != context) {
                return context.getResources().getDrawable(resId);
            }
            return null;
        }
    }
}
