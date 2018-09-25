package com.junkchen.customchart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private LineChart lineChart;
    private LinesChart multipleLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = (LineChart) findViewById(R.id.lineChart);
        lineChart.setRange(150, 30);
        lineChart.setNormalRange(new NormalRange<Integer>(60, 100));
        lineChart.setShowPointNumber(50);

        multipleLineChart = (LinesChart) findViewById(R.id.multipleLineChart);
        multipleLineChart.setRange(30, 180);
        List<NormalRange<Float>> normalRanges = new ArrayList<>();
        normalRanges.add(new NormalRange<>(100f, 140f));
        normalRanges.add(new NormalRange<>(60f, 90f));
        multipleLineChart.setNormalRanges(normalRanges);
        multipleLineChart.setShowPointNumber(50);
    }

    private ArrayList<LinePoint> makeData() {
        ArrayList<LinePoint> points = new ArrayList<>();
        Random random = new Random();
        int count = 10 + random.nextInt(40);
        for (int i = 0; i < count; i++) {
            LinePoint linePoint = new LinePoint();
//            linePoint.setX("#" + i);
            linePoint.setX("8/30 12:" + i);
            linePoint.setY(50 + random.nextInt(70));
            points.add(linePoint);
        }
        return points;
    }

    private List<List<LinePoint>> makeDoubleData() {
        List<List<LinePoint>> mMultipleLinePoints = new ArrayList<>();

        Random random = new Random();
        int count = 10 + random.nextInt(40);

        ArrayList<LinePoint> topPoints = new ArrayList<>();
        ArrayList<LinePoint> bottomPoints = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            LinePoint topPoint = new LinePoint();
            topPoint.setX("8/30 12:" + i);
            topPoint.setY(100 + random.nextInt(50));
            topPoints.add(topPoint);

            LinePoint bottomPoint = new LinePoint();
            bottomPoint.setX("8/30 12:" + i);
            bottomPoint.setY(50 + random.nextInt(50));
            bottomPoints.add(bottomPoint);
        }

        mMultipleLinePoints.add(topPoints);
        mMultipleLinePoints.add(bottomPoints);

        return mMultipleLinePoints;
    }

    public void changeData(View view) {
        lineChart.setData(makeData());
        multipleLineChart.setData(makeDoubleData());
    }
}
