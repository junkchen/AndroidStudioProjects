package com.junkchen.customchart;

/**
 * Created by Junk on 2017/9/6.
 * 正常值范围
 */

public class NormalRange<T> {
    T min;
    T max;

    public NormalRange() {
    }

    public NormalRange(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }
}
