package com.junkchen.hetdemo;

import android.content.Context;

/**
* @author yangzheng
* @time 2017/8/4 17:55
* @description dp、px转换 统一接口
*/
public class DensityUtil {
    /**
     * dp转px    结果取整
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * density + 0.5F);
    }

    /**
     * px转dp    结果取整
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / density + 0.5F);
    }
}
