package com.junkchen.okhttpdemo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView txtv_content;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtv_content = (TextView) findViewById(R.id.txtv_content);

//        JSON.parseObject()

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                doGet();
//            }
//        }).start();

        OkHttpUtils.getInstance().doGetAsync("http://junkchen.com", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            txtv_content.setText(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        OkHttpUtils.doPostAsync(
                "url",
                new HashMap<String, String>(),
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                }
        );
    }

    private void doGet() {
        OkHttpClient httpClient = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "");
//        Headers headers =  new Headers.Builder();

        Request request = new Request.Builder()
                .url("http://junkchen.com")
                .get()
                .build();
//        Headers headers = new Headers();

        Call call = httpClient.newCall(request);

        try {
            Response response = call.execute();
            final String responseStr = response.body().string();

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    txtv_content.setText(responseStr);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
