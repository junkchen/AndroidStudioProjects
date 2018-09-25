package com.junkchen.customchart;

import java.util.Map;

/**
 * Created by Junk on 2017/8/30.
 */

public class LinePoint {
    private String x;
    private float y;

    public LinePoint() {
    }

    public LinePoint(String x, float y) {
        this.x = x;
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
