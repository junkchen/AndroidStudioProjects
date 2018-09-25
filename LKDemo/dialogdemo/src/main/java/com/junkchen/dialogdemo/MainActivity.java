package com.junkchen.dialogdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.btn_showdialog:
//                showCustomDialog();
                UserProtocolDialog dialog = new UserProtocolDialog();
                dialog.show(getSupportFragmentManager(), "df");
                break;
            case R.id.btn_showdialog2:
                showCustomDialog2();
                break;
            case R.id.btn_showdialog3:
                showCustomDialog3();
                break;
        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title")
                .setMessage("Custom message")
                .setNegativeButton("NegativeButton", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "negative button was clicked.", Toast.LENGTH_SHORT).show();
                    }
                }).setNeutralButton("NeutralButton", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Neutral button was clicked.", Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton("PositiveButton", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Positive button was clicked.", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
//        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.END;
        dialogWindow.setAttributes(layoutParams);

        dialog.show();
    }

    private void showCustomDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.custom_dialog);
        AlertDialog dialog = builder.create();

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        layoutParams.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(layoutParams);

        dialog.show();
    }

    private void showCustomDialog3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user_registration_protocol, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
//        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        dialogWindow.setAttributes(layoutParams);
        dialog.show();
    }

    private void showDialogFragment() {
        /*DialogFragment df = new DialogFragment(){
            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                     @Nullable Bundle savedInstanceState) {
//                getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
                View view = inflater.inflate(R.layout.dialog_user_registration_protocol, container);
                return view;
            }
        };*/
//        df.show(getSupportFragmentManager(), "tag");
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//            decorView.setSystemUiVisibility(uiOptions);
////
////            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
////                @Override
////                public void onSystemUiVisibilityChange(int visibility) {
////                    setHideVirtualKey(getWindow());
////                }
////            });
//        }
//    }
}
