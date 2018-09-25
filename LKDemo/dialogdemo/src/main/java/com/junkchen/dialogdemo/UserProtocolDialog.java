package com.junkchen.dialogdemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class UserProtocolDialog extends DialogFragment {
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.custom_dialog, null);
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user_registration_protocol, null);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_user_registration_protocol, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(false);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.setAttributes(params);
        return dialog;
    }
}
