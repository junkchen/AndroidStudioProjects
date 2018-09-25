package com.junkchen.globalexceptionhandle;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Junk on 2017/11/23.
 */

class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = CrashHandler.class.getSimpleName();

    private Context mContext;
    private Application mApp;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private static final CrashHandler ourInstance = new CrashHandler();

    static CrashHandler getInstance() {
        return ourInstance;
    }

    private CrashHandler() {
    }

    public void init(Context context, Application application) {
        this.mContext = context;
        this.mApp = application;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 如果用户没有处理则让系统默认的异常处理器来处理
        if (!handleException(e) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, e);
        } else {
            mApp.onTerminate();
        }
    }

    /**
     * @param e
     * @return
     */
    private boolean handleException(Throwable e) {
        if (e == null) {
            return false;
        }
        String errorInfo = getErrorInfo(e);
        String deviceInfo = getDeviceInfo();
        String versionInfo = getVersionInfo();
        Log.e(TAG, "handleException: " + mDateFormat.format(System.currentTimeMillis()) + "\n" +
                versionInfo + deviceInfo + errorInfo);
        return true;
    }

    /**
     * Get exception error information.
     *
     * @param ex
     * @return
     */
    private String getErrorInfo(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        printWriter.close();
        String errorText = writer.toString();
        return errorText;
    }

    /**
     * Get mobile phone hardware information
     *
     * @return
     */
    private String getDeviceInfo() {
        StringBuilder sb = new StringBuilder();
        //通过反射获取系统的硬件信息
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                //暴力反射 ,获取私有的信息
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Get application version information.
     *
     * @return
     */
    private String getVersionInfo() {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), 0);
            return "Version name: " + info.versionName + "\nVersion code: " + info.versionCode + "\n";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown versionName.";
        }
    }
}
