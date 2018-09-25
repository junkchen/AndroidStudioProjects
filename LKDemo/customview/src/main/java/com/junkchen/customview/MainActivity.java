package com.junkchen.customview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.junkchen.customview.widget.FlowLayout;
import com.junkchen.customview.widget.SectionView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((FlowLayout) findViewById(R.id.flowLayout)).setOnItemClickListener(
                new FlowLayout.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int index) {
                        String content = ((TextView) view).getText().toString();
                        Toast.makeText(MainActivity.this,
                                "Index: " + index + ", content: " + content,
                                Toast.LENGTH_SHORT).show();
                    }
                });

        SectionView sectionView = (SectionView) findViewById(R.id.sectionView);
        sectionView.setSectionConfig(4,
                new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW},
                new String[]{"Google", "Apple", "Linux", "Kotlin"},
                2);
    }
}
