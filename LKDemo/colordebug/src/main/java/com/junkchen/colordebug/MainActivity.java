package com.junkchen.colordebug;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Button btn_set;
    private EditText[] edt_colors = new EditText[5];
    private EditText edt_color1;
    private EditText edt_color2;
    private EditText edt_color3;
    private EditText edt_color4;
    private EditText edt_color5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt_color1 = findViewById(R.id.edt_color1);
        edt_color2 = findViewById(R.id.edt_color2);
        edt_color3 = findViewById(R.id.edt_color3);
        edt_color4 = findViewById(R.id.edt_color4);
        edt_color5 = findViewById(R.id.edt_color5);

        edt_colors[0] = edt_color1;
        edt_colors[1] = edt_color2;
        edt_colors[2] = edt_color3;
        edt_colors[3] = edt_color4;
        edt_colors[4] = edt_color5;

        findViewById(R.id.btn_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String colorValue = edt_color1.getText().toString();
//                int color = Color.parseColor(colorValue);
//                edt_color1.setBackgroundColor(color);
//                edt_color1.setTextColor(color);

                for (EditText edt_color : edt_colors) {
                    String inputContent = edt_color.getText().toString();
                    int length = inputContent.length();
                    Log.i(TAG, "onClick: length = " + length);
                    if (!(length == 6 || length == 8)) {
                        inputContent = "FFFFFF";
                    }
                    String colorValue = "#" + inputContent;
                    Log.i(TAG, "onClick: colorValue: " + colorValue);
                    int color = Color.parseColor(colorValue);
                    edt_color.setBackgroundColor(color);
                }
//                for (int i = 0; i < edt_colors.length; i++) {
//                    EditText edt_color = edt_colors[i];
//                    String inputContent = edt_color.getText().toString();
//                    int length = inputContent.length();
//                    Log.i(TAG, "onClick: length = " + length);
//                    if (!(length == 6 || length == 8)) {
//                        inputContent = "FFFFFF";
//                    }
//                    String colorValue = "#" + inputContent;
//                    Log.i(TAG, "onClick: colorValue: " + colorValue);
//                    int color = Color.parseColor(colorValue);
//                    edt_color.setBackgroundColor(color);
//                }
            }
        });
    }
}
