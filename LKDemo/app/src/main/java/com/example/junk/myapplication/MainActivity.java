package com.example.junk.myapplication;

import android.app.DatePickerDialog;
import android.icu.util.GregorianCalendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText edt_px;
    private TextView txtv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt_px = (EditText) findViewById(R.id.edt_px);
        txtv_result = (TextView) findViewById(R.id.txtv_result);

//        DatePickerDialog datePickerDialog = new DatePickerDialog(this);

        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis());
        datePicker.setCalendarViewShown(false);
        datePicker.setSpinnersShown(true);
//        datePicker.setBackgroundTintMode();
//        Log.i(TAG, "onCreate: " + datePicker.getmode);
//        NumberPicker number = new NumberPicker(this);

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(20);
        numberPicker.setMinValue(0);

        CustomNumberPicker customNumberPicker = (CustomNumberPicker) findViewById(R.id.customNumberPicker);
        customNumberPicker.setMaxValue(12);
        customNumberPicker.setMinValue(1);
//        customNumberPicker.setNumberPickerDividerColor(customNumberPicker);

//        GregorianCalendar
    }

    public void convert(View view) {
        String pxStr = edt_px.getText().toString();
        if (!pxStr.isEmpty()) {
            int dpStr = Integer.parseInt(pxStr) / 3;
            txtv_result.setText(dpStr);
        }
    }
}
