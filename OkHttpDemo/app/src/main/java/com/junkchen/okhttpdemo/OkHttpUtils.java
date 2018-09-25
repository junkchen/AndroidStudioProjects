package com.junkchen.okhttpdemo;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Junk on 2017/8/2.
 */

public class OkHttpUtils {
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;

    private OkHttpUtils() {
        mOkHttpClient = new OkHttpClient.Builder()
                .build();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public Response doGet(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

        try {
            return call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void doGetAsync(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = mOkHttpClient.newCall(request);

        call.enqueue(callback);
    }

    private void _doPostAsync(String url, Map<String, String> params, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();

        if (params == null) {
            params = new HashMap<>();
        }

        Iterator<Map.Entry<String, String>> paramsIterator = params.entrySet().iterator();
        while (paramsIterator.hasNext()) {
            Map.Entry<String, String> param = paramsIterator.next();
            builder.add(param.getKey(), param.getValue());
        }

        FormBody body = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = mOkHttpClient.newCall(request);

        call.enqueue(callback);
    }

    /**
     * 对外暴露的方法
     */

    public static void doPostAsync(String url, Map<String, String> params, Callback resultBack) {
        getInstance()._doPostAsync(url, params, resultBack);
    }

    public static final class Param {
        private String name;
        private String value;

        public Param(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }


}
