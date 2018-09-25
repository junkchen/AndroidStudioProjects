package com.junkchen.customkeyboard;

import android.app.Dialog;
import android.content.DialogInterface;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.junkchen.customkeyboard.keyboard.BaseKeyboard;
import com.junkchen.customkeyboard.keyboard.CustomKeyboardManager;
import com.junkchen.customkeyboard.keyboard2.KeyboardManager;
import com.junkchen.customkeyboard.keydialog.LPKeyBoard;
import com.junkchen.customkeyboard.keydialog.LPTextField;
import com.junkchen.customkeyboard.keydialog.LPUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBeforeSetContentView();
        setContentView(R.layout.activity_main);

        final EditText edt_number = (EditText) findViewById(R.id.edt_number);
        final EditText edt_name = (EditText) findViewById(R.id.edt_name);
        final EditText edt_temp = (EditText) findViewById(R.id.edt_temp);

        edt_number.setInputType(InputType.TYPE_NULL);
        edt_number.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new KeyboardUtils(MainActivity.this, MainActivity.this, edt_number).showKeyboard();
                return false;
            }
        });

//        edt_name.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int inputType = edt_name.getInputType();
//                edt_name.setInputType(InputType.TYPE_NULL);
//                new KeyboardUtils(MainActivity.this, MainActivity.this, edt_name).showKeyboard();
//                edt_name.setInputType(inputType);
//                return false;
//            }
//        });
        CustomKeyboardManager keyboardManager = new CustomKeyboardManager(this);
        keyboardManager.attach(edt_name, new BaseKeyboard(this, R.xml.numbers));

//        edt_temp.setInputType(InputType.TYPE_NULL);
        KeyboardManager keyboardManager1 = new KeyboardManager(this);
        keyboardManager1.attach(edt_temp, new Keyboard(this, R.xml.numbers));
    }



    public InputMethodManager inputManager_;
    public Dialog dlg;

    public void OnCreateInputWindow(final LPTextField edit) {

        LPKeyBoard lpKeyBoard = new LPKeyBoard(this, edit);
        LinearLayout ll = new LinearLayout(this);
        ll.addView(lpKeyBoard);
        dlg = new Dialog(this, R.style.popupAnimation);
        dlg.setContentView(ll);
        // 点击dialog以外的区域关闭dialog
        dlg.setCanceledOnTouchOutside(true);
        lpKeyBoard.dlg_ = dlg;
        // 设置dialog位置
        Window mWindow = dlg.getWindow();
        mWindow.setWindowAnimations(R.style.popupAnimation);
        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // 使对话框位于屏幕的底部并居中
        mWindow.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL);
        // 使对话框二边没有空隙
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (!dlg.isShowing()) {
            dlg.show();
            dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (edit.isFocused()) {
                        edit.clearFocus();
                    }
                }
            });
        }
    }

    private void initBeforeSetContentView() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        LPUtils.screenWidth_ = dm.widthPixels;
        LPUtils.screenHeight_ = dm.heightPixels;
        final float w = 320;
        final float h = 480;
        float wRate = dm.widthPixels / w;
        float hRate = dm.heightPixels / h;
        LPUtils.setScaledParams(Math.min(wRate, hRate));
    }
}
