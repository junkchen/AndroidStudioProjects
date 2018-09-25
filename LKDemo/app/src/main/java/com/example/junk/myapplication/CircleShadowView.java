package com.example.junk.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Junk on 2017/8/10.
 */

public class CircleShadowView extends View {
    public CircleShadowView(Context context) {
        this(context, null);
    }

    public CircleShadowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
//        paint.setColor(Color.argb(30, 255, 0, 0));
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setShadowLayer(8, getWidth(), getHeight()/2, Color.WHITE);
        canvas.drawColor(Color.YELLOW);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(-getWidth()/2, -getHeight()/2, getWidth()/3, paint);
        canvas.drawCircle(-getWidth()/2, 0, getWidth()/3, paint);

        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setColor(Color.GREEN);
        canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/3-5, paint2);
    }
}
