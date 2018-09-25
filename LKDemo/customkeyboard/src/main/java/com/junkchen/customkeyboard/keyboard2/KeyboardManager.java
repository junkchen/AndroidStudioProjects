package com.junkchen.customkeyboard.keyboard2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.junkchen.customkeyboard.R;

import java.lang.reflect.Method;

/**
 * Created by Junk on 2017/10/23.
 */

public class KeyboardManager {
    public static final String TAG = KeyboardManager.class.getSimpleName();

    private Context mContext;
    private ViewGroup mRootView;
    private FrameLayout mKeyboardViewContainer;
    private KeyboardView mKeyboardView;
    private int mKeyboardHeight;
    private FrameLayout.LayoutParams mKeyboardViewLayoutParams;
    private View mShowUnderView;
//    private View edtFocusScavenger;

    private EditText mEditText;

    public KeyboardManager(Activity activity) {
        this.mContext = activity.getApplicationContext();
        mRootView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);

        mKeyboardViewContainer = (FrameLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.layout_custom_keyboard_view2, null);
        mKeyboardView = (KeyboardView) mKeyboardViewContainer.findViewById(R.id.keyboard_view);
//        edtFocusScavenger = mKeyboardViewContainer.findViewById(R.id.edt_focus_scavenger);

//        hideSystemSoftKeyboard((EditText) edtFocusScavenger);

        mKeyboardViewLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mKeyboardViewLayoutParams.gravity = Gravity.BOTTOM;
    }

    public void attach(EditText editText, Keyboard keyboard) {
        this.mEditText = editText;
        //1. hide system soft keyboard.
        hideSystemSoftKeyboard(editText);
        //2. bind keyboard for edit text
        editText.setTag(R.id.edittext_bind_keyboard, keyboard);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v instanceof EditText) {
                    if (hasFocus) {
                        showSoftKeyboard((EditText) v);
                    } else {
                        hideSoftKeyboard((EditText) v);
                    }
                }
            }
        });
    }

    public void setShowUnderView(View view) {
        mShowUnderView = view;
    }

    private Keyboard getKeyboard(EditText editText) {
        Object tag = editText.getTag(R.id.edittext_bind_keyboard);
        if (null != tag && tag instanceof Keyboard) {
            return (Keyboard) tag;
        }
        return null;
    }

    private void refreshKeyboard(Keyboard keyboard) {
        mKeyboardView.setKeyboard(keyboard);
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
//        mKeyboardView.setOnKeyboardActionListener(keyboard);
        mKeyboardView.setOnKeyboardActionListener(keyboardActionListener);

        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mKeyboardView.measure(width, height);
        mKeyboardHeight = mKeyboardView.getMeasuredHeight();
    }

    /**
     * 计算屏幕向上移动的距离
     *
     * @param view
     * @return
     */
    private int calculateMoveHeight(View view) {
        Rect rect = new Rect();
        mRootView.getWindowVisibleDisplayFrame(rect);//Get width and height of the current visible area

        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);//Get the coordinates of this view in its window.[x, y]
        int keyboardTop = viewLocation[1] + view.getHeight() + view.getPaddingTop() + view.getPaddingTop();
        if (keyboardTop - mKeyboardHeight < 0) { //如果输入框到屏幕顶部已经不能放下键盘的高度, 则不需要移动了.
            return 0;
        }

        if (null != mShowUnderView) { //如果有基线View. 则计算基线View到屏幕的距离
            int[] underVLocation = new int[2];
            mShowUnderView.getLocationOnScreen(underVLocation);
            keyboardTop = underVLocation[1] + mShowUnderView.getHeight() + mShowUnderView.getPaddingBottom() + mShowUnderView.getPaddingTop();
        }

        //输入框或基线View的到屏幕的距离 + 键盘高度，如果超出了屏幕的承载范围, 就需要移动.
        int moveHeight = keyboardTop + mKeyboardHeight - rect.bottom;
        return moveHeight > 0 ? moveHeight : 0;
    }

    private void showSoftKeyboard(EditText editText) {
        //1. Get editText's BaseKeyboard
        Keyboard keyboard = getKeyboard(editText);
        if (null == keyboard) {
            Log.e(TAG, "The EditText no bind CustomBaseKeyboard!");
            return;
        }
        refreshKeyboard(keyboard);//Add keyboard to KeyboardView

        //Add keyboardView to root view
        if (mKeyboardViewContainer.isShown()) {
            mRootView.removeView(mKeyboardViewContainer);
        } else {
            mRootView.addView(mKeyboardViewContainer, mKeyboardViewLayoutParams);
        }
        //Set loading animation for keyboard view layout.
//        mKeyboardViewContainer.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.down_to_up));
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mKeyboardViewContainer,
                "translationY",
                mKeyboardHeight, 0)
                .setDuration(120);
        animator.start();

        /*
         * 通过传入activity获得其DecorView，添加键盘布局。将键盘布局set到屏幕底部，当输入框获得焦点
         * 时，如果设置了基线view, 则判断基线view所在位置, 否则默认以输入框为基线View，若键盘弹出会
         * 遮挡基线View，则屏幕整体向上滑动一定的距离： 屏幕移动高度为:
         * 移动距离 = 基线View到屏幕顶部距离 + 自定义键盘高度 - 整个屏幕高度
         * if 移动距离 > 0 则说明当键盘加入到根布局后, 屏幕无法完成加载, 需要屏幕向上滚动一定的偏移量.
         * if 移动距离 <= 0 则说明键盘弹出后还没有达到基线设置位置, 不需要滚动整个屏幕.
         */
        int moveHeight = calculateMoveHeight(editText);
        if (moveHeight > 0) {
            mRootView.getChildAt(0).scrollBy(0, moveHeight);//移动屏幕
        }

        editText.setTag(R.id.keyboard_view_move_height, moveHeight);
    }

    private void hideSoftKeyboard(final EditText editText) {
        int moveHeight = 0;
        Object tag = editText.getTag(R.id.keyboard_view_move_height);
        if (null != tag) {
            moveHeight = (int) tag;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mKeyboardViewContainer,
                "translationY",
                0, mKeyboardHeight)
                .setDuration(100);
        final int finalMoveHeight = moveHeight;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (finalMoveHeight > 0) {//复原屏幕
                    mRootView.getChildAt(0).scrollBy(0, -1 * finalMoveHeight);
                    editText.setTag(R.id.keyboard_view_move_height, 0);
                }
                mRootView.removeView(mKeyboardViewContainer);//Remove keyboard container from root view
            }
        });
        animator.start();
    }

    private void hideSystemSoftKeyboard(EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
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
                    if (null != mEditText && mEditText.hasFocus()) {
                        Editable editable = mEditText.getText();
                        int start = mEditText.getSelectionStart();
                        Log.i(TAG, "onKey: selection start: " + start);
                        //Cancel
                        if (primaryCode == Keyboard.KEYCODE_CANCEL) {
//                            hideSoftKeyboard(mEditText);
                            mEditText.clearFocus();
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
            };

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
