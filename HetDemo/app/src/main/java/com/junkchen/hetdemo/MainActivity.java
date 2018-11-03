package com.junkchen.hetdemo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        TextView tv_hello = findViewById(R.id.tv_hello);
        String hours = "1";
        int hoursLen = hours.length();
        String minutes = "25";
        int minutesLen = minutes.length();
        String text = "1小时25分钟";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK),
                0, hoursLen, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK),
                hoursLen + 2, hoursLen + 2 + minutesLen, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                0, hoursLen, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                hoursLen + 2, hoursLen + 2 + minutesLen, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, metrics)),
                0, hoursLen, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.66F),
                hoursLen + 2, hoursLen + 2 + minutesLen, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_hello.setText(spannableString);

    }

    public void doClick(View view) {
        if (view.getId() == R.id.tv_hello) {
            startActivity(new Intent(this, SurveyActivity.class));
        } else {
            startActivity(new Intent(this, AnswerActivity.class));
        }
    }
}
