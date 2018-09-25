package com.junkchen.customkeyboard.keyboard2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Junk on 2017/10/23.
 */

public class CustomKeyboardView extends KeyboardView {
    public static final String TAG = CustomKeyboardView.class.getSimpleName();

    private int rLabelTextSize;
    private int rKeyTextSize;
    private int rKeyTextColor;
    private float rShadowRadius;
    private int rShadowColor;

    private Drawable rKeyBackground;
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
        rLabelTextSize = (int) getFieldValue(this, "mLabelTextSize");
        rKeyTextSize = (int) getFieldValue(this, "mKeyTextSize");
        rKeyTextColor = (int) getFieldValue(this, "mKeyTextColor");
        rShadowRadius = (float) getFieldValue(this, "mShadowRadius");
        rShadowColor = (int) getFieldValue(this, "mShadowColor");
        rKeyBackground = (Drawable) getFieldValue(this, "mKeyBackground");
    }

    @Override
    public void onDraw(Canvas canvas) {
        rClipRegion = (Rect) getFieldValue(this, "mClipRegion");
        rInvalidatedKey = (Keyboard.Key) getFieldValue(this, "mInvalidatedKey");
        super.onDraw(canvas);
        refreshKey(canvas);
    }

    private void refreshKey(Canvas canvas) {
        final Paint paint = (Paint) getFieldValue(this, "mPaint");
        final Rect padding = (Rect) getFieldValue(this, "mPadding");

        final Drawable keyBackground = rKeyBackground;
        final Rect clipRegion = rClipRegion;
        final int kbdPaddingLeft = getPaddingLeft();
        final int kbdPaddingTop = getPaddingTop();
        List<Key> keyList = getKeyboard().getKeys();
        final Key[] keys = keyList.toArray(new Key[keyList.size()]);
        final Key invalidKey = rInvalidatedKey;

        paint.setColor(rKeyTextColor);
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

        canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
        final int keyCount = keys.length;
        for (int i = 0; i < keyCount; i++) {
            final Key key = keys[i];
            if (drawSingleKey && invalidKey != key) {
                continue;
            }
            int[] drawableState = key.getCurrentDrawableState();
            keyBackground.setState(drawableState);

            // Switch the character to uppercase if shift is pressed
            String label = key.label == null ? null : adjustCase(key.label).toString();

            final Rect bounds = keyBackground.getBounds();
            if (key.width != bounds.right ||
                    key.height != bounds.bottom) {
                keyBackground.setBounds(0, 0, key.width, key.height);
            }
            canvas.translate(key.x + kbdPaddingLeft, key.y + kbdPaddingTop);
            keyBackground.draw(canvas);

            if (label != null) {
                // For characters, use large font. For labels like "Done", use small font.
                if (label.length() > 1 && key.codes.length < 2) {
                    paint.setTextSize(rLabelTextSize);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    paint.setTextSize(rKeyTextSize);
                    paint.setTypeface(Typeface.DEFAULT);
                }
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

    private Field getDeclaredField(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getSuperclass().getDeclaredField(fieldName);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

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
