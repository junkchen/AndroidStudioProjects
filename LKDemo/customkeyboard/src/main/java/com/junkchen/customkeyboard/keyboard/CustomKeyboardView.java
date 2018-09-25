package com.junkchen.customkeyboard.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Junk on 2017/10/21.
 */

public class CustomKeyboardView extends KeyboardView {
    private static final String TAG = CustomKeyboardView.class.getSimpleName();

    private Drawable rKeyBackground;
    private int rLabelTextSize;
    private int rKeyTextSize;
    private int rKeyTextColor;
    private float rShadowRadius;
    private int rShadowColor;

    private Rect rClipRegion;
    private Keyboard.Key rInvalidatedKey;

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        rKeyBackground = (Drawable) getFieldValue(this, "mKeyBackground");
        rLabelTextSize = (int) getFieldValue(this, "mLabelTextSize");
        rKeyTextSize = (int) getFieldValue(this, "mKeyTextSize");
        rKeyTextColor = (int) getFieldValue(this, "mKeyTextColor");
        rShadowColor = (int) getFieldValue(this, "mShadowColor");
        rShadowRadius = (float) getFieldValue(this, "mShadowRadius");
    }

    @Override
    public void onDraw(Canvas canvas) {
        //说明：CustomKeyboardView 只针对 BaseKeyboard 键盘进行重绘,且 BaseKeyboard 必需有设置
        // CustomKeyStyle 的回调接口实现, 才进行重绘, 这才有意义
        Keyboard keyboard = getKeyboard();
        if (null == keyboard || !(keyboard instanceof BaseKeyboard)
//                || null == ((BaseKeyboard) keyboard).getCustomKeyStyle()
                ) {
            super.onDraw(canvas);
            return;
        }
        rClipRegion = (Rect) getFieldValue(this, "mClipRegion");
        rInvalidatedKey = (Keyboard.Key) getFieldValue(this, "mInvalidatedKey");
        super.onDraw(canvas);
        refreshKey(canvas);
    }

    /**
     * refreshKey() 是对父类的 onBufferDraw() 进行的重写. 只是对 key 的绘制过程中进行了重新设置。
     *
     * @param canvas
     */
    private void refreshKey(Canvas canvas) {
        final Paint paint = (Paint) getFieldValue(this, "mPaint");
        final Rect padding = (Rect) getFieldValue(this, "mPadding");

        paint.setColor(rKeyTextColor);
        final int kbdPaddingLeft = getPaddingLeft();
        final int kbdPaddingTop = getPaddingTop();
        Drawable keyBackground = null;

        final Rect clipRegion = rClipRegion;
        final Keyboard.Key invalidKey = rInvalidatedKey;
        boolean drawSingleKey = false;
        if (invalidKey != null && canvas.getClipBounds(clipRegion)) {
            // Is clipRegion completely contained within the invalidated key?
            if (invalidKey.x + kbdPaddingLeft - 1 <= clipRegion.left &&
                    invalidKey.y + kbdPaddingTop - 1 <= clipRegion.top &&
                    invalidKey.x + invalidKey.width + kbdPaddingLeft + 1 >= clipRegion.right &&
                    invalidKey.y + invalidKey.height + kbdPaddingTop + 1 >= clipRegion.bottom) {
                drawSingleKey = true;
            }
        }

        //获取当前键盘被弹起的输入源 和 键盘为每个key的定制实现customKeyStyle
        EditText curEditText = ((BaseKeyboard) getKeyboard()).getEditText();
        BaseKeyboard.CustomKeyStyle customKeyStyle = ((BaseKeyboard) getKeyboard()).getCustomKeyStyle();

        List<Keyboard.Key> keys = getKeyboard().getKeys();
        final int keyCount = keys.size();
//        canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
        for (int i = 0; i < keyCount; i++) {
            final Keyboard.Key key = keys.get(i);
            if (drawSingleKey && invalidKey != key) {
                continue;
            }

            //获取 Key 的自定义背景, 若没有定制, 使用 KeyboardView 的默认属性 keyBackground 设置
//            keyBackground = customKeyStyle.getKeyBackground(key, curEditText);
//            if (null == keyBackground) {
                keyBackground = rKeyBackground;
//            }

            int[] drawableState = key.getCurrentDrawableState();
            keyBackground.setState(drawableState);

            //获取 Key 自定义的 Label, 若没有定制, 使用 xml 布局中指定的
//            CharSequence keyLabel = customKeyStyle.getKeyLabel(key, curEditText);
            CharSequence keyLabel;
//            if (null == keyLabel) {
                keyLabel = key.label;
//            }
            // Switch the character to uppercase if shift is pressed
            String label = keyLabel == null ? null : adjustCase(keyLabel).toString();

            final Rect bounds = keyBackground.getBounds();
            if (key.width != bounds.right ||
                    key.height != bounds.bottom) {
                keyBackground.setBounds(0, 0, key.width, key.height);
            }
            canvas.translate(key.x + kbdPaddingLeft, key.y + kbdPaddingTop);
            keyBackground.draw(canvas);

            if (label != null) {
                //获取为Key的Label的字体大小, 若没有定制, 使用KeyboardView的默认属性keyTextSize设置
//                Float customKeyTextSize = customKeyStyle.getKeyTextSize(key, curEditText);
//                // For characters, use large font. For labels like "Done", use small font.
//                if (null != customKeyTextSize) {
//                    paint.setTextSize(customKeyTextSize);
//                    paint.setTypeface(Typeface.DEFAULT_BOLD);
//                } else {
                    if (label.length() > 1 && key.codes.length < 2) {
                        paint.setTextSize(rLabelTextSize);
//                        paint.setTextSize(rKeyTextSize);
                        paint.setTypeface(Typeface.DEFAULT_BOLD);
                    } else {
                        paint.setTextSize(rKeyTextSize);
                        paint.setTypeface(Typeface.DEFAULT);
                    }
//                }

                //获取为Key的Label的字体颜色, 若没有定制, 使用KeyboardView的默认属性keyTextColor设置
//                Integer customKeyTextColor = customKeyStyle.getKeyTextColor(key, curEditText);
//                if (null != customKeyTextColor) {
//                    paint.setColor(customKeyTextColor);
//                } else {
                    paint.setColor(rKeyTextColor);
//                }
                // Draw a drop shadow for the text
//                paint.setShadowLayer(rShadowRadius, 0, 0, rShadowColor);
                // Draw the text
                canvas.drawText(label,
                        (key.width - padding.left - padding.right) / 2
                                + padding.left,
                        (key.height - padding.top - padding.bottom) / 2
                                + (paint.getTextSize() - paint.descent()) / 2 + padding.top,
                        paint);
                // Turn off drop shadow
//                paint.setShadowLayer(0, 0, 0, 0);
            } else if (key.icon != null) {
                final int drawableX = (key.width - padding.left - padding.right
                        - key.icon.getIntrinsicWidth()) / 2 + padding.left;
                final int drawableY = (key.height - padding.top - padding.bottom
                        - key.icon.getIntrinsicHeight()) / 2 + padding.top;
                canvas.translate(drawableX, drawableY);
                key.icon.setBounds(0, 0,
                        key.icon.getIntrinsicWidth(), key.icon.getIntrinsicHeight());
                key.icon.draw(canvas);
                canvas.translate(-drawableX, -drawableY);
            }
            canvas.translate(-key.x - kbdPaddingLeft, -key.y - kbdPaddingTop);
        }
        rInvalidatedKey = null;
    }

    private CharSequence adjustCase(CharSequence label) {
        if (getKeyboard().isShifted() && label != null && label.length() < 3
                && Character.isLowerCase(label.charAt(0))) {
            label = label.toString().toUpperCase();
        }
        return label;
    }

    /**
     * @param obj
     * @param fieldName
     * @return
     */
    private Field getDeclaredField(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getSuperclass().getDeclaredField(fieldName);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

//        Field field = null;
//        Class<?> clazz = obj.getClass();
//        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
//            try {
//                field = clazz.getDeclaredField(fieldName);
//                return field;
//            } catch (Exception e) {
//                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
//                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
//            }
//        }
        return null;
    }

    /**
     * Get field value
     *
     * @param obj
     * @param fieldName
     * @return
     */
    private Object getFieldValue(Object obj, String fieldName) {
        Field field = getDeclaredField(obj, fieldName);
        field.setAccessible(true);
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
