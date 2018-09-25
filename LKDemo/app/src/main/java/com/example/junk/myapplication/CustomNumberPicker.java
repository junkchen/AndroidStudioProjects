package com.example.junk.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

/**
 * Created by Junk on 2017/8/26.
 */

public class CustomNumberPicker extends NumberPicker {
    public CustomNumberPicker(Context context) {
        super(context);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        this.addView(child, null);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        this.addView(child, -1, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setNumberPicker(child);
        setNumberPickerDividerColor(this);
    }

    /**
     * 设置CustomNumberPicker的属性 颜色 大小
     *
     * @param view
     */
    private void setNumberPicker(View view) {
        if (view instanceof EditText) {
            ((EditText) view).setTextColor(0xff363636);
            ((EditText) view).setTextSize(48);
        }
    }

    /**
     * 设置分割线的颜色值
     *
     * @param numberPicker
     */
    public void setNumberPickerDividerColor(NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pickerField : pickerFields) {
            if (pickerField.getName().equals("mSelectionDivider")) {
                pickerField.setAccessible(true);
                try {
                    pickerField.set(picker, new ColorDrawable(Color.rgb(0x6F, 0xB0, 48)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
            if (pickerField.getName().equals("mSelectionDividersDistance")) {
                pickerField.setAccessible(true);
                try {
                    pickerField.set(picker, 192);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
