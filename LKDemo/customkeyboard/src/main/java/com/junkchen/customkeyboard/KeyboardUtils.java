package com.junkchen.customkeyboard;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Junk on 2017/10/20.
 */

public class KeyboardUtils {
    private Context mContext;
    private Activity mActivity;
    private KeyboardView mKeyboardView;
    private Keyboard mNumberKeyboard;//数字键盘

    private EditText mEditText;

    public KeyboardUtils(Context context, Activity activity, EditText editText) {
        this.mContext = context;
        this.mActivity = activity;
        this.mEditText = editText;

        mNumberKeyboard = new Keyboard(mContext, R.xml.numbers);
        mKeyboardView = (KeyboardView) mActivity.findViewById(R.id.keyboardView);
        mKeyboardView.setEnabled(true);
        mKeyboardView.setKeyboard(mNumberKeyboard);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(keyboardActionListener);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                }
            }
        });
    }

    private KeyboardView.OnKeyboardActionListener keyboardActionListener =
            new KeyboardView.OnKeyboardActionListener() {
                @Override
                public void onPress(int primaryCode) {
                    playClickAudio(primaryCode);
                }

                @Override
                public void onRelease(int primaryCode) {

                }

                @Override
                public void onKey(int primaryCode, int[] keyCodes) {
                    Editable editable = mEditText.getText();
                    int start = mEditText.getSelectionStart();
                    //Cancel
                    if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                        hideKeyboard();
                    } else if (primaryCode == Keyboard.KEYCODE_DELETE) {//回退，删除
                        if (editable != null && editable.length() > 0) {
                            editable.delete(start - 1, start);
                        }
                    } else {//输入键盘值
                        editable.insert(start, Character.toString((char)primaryCode));
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
            };

    public void hideKeyboard() {
        if (mKeyboardView.getVisibility() == View.VISIBLE) {
            mKeyboardView.setVisibility(View.GONE);
        }
    }

    public void showKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.INVISIBLE || visibility == View.GONE) {
            mKeyboardView.setVisibility(View.VISIBLE);
        }
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
}
