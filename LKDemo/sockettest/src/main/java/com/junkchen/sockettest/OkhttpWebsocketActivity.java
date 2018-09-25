package com.junkchen.sockettest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class OkhttpWebsocketActivity extends AppCompatActivity {
    public static final String TAG = OkhttpWebsocketActivity.class.getSimpleName();

    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp_websocket);

        TextView txtv_websocket = (TextView) findViewById(R.id.txtv_websocket);
        txtv_websocket.setOnClickListener(v -> websocket());

        webview = (WebView) findViewById(R.id.webview);
    }

    private void websocket() {
        String uriText = "ws://172.31.71.197:8866/websocket";
        String hostName = "172.31.71.197";
        int port = 8866;
//        String wsUrl = "ws://" + hostName + ":" + port + "/" + "websocket";
        String wsUrl = "ws://zkw.lkyiliao.com/websocket";
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(wsUrl).build();
        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                Log.i(TAG, "onOpen: -----");
                webSocket.send("getQRCode");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                //{"result":true,"picUrl":"http://test.lkyiliao.com\\attachment\\weixin_qr_code\\61yE647JQ5pshRrZ.png","stateId":"61yE647JQ5pshRrZ","errorCode":null}
                Log.i(TAG, "onMessage: text: " + text);
                try {
                    if (text.contains("picUrl") && text.contains("stateId")) {
                        JSONObject jsonObj = new JSONObject(text);
                        boolean result = jsonObj.getBoolean("result");
                        if (result) {
                            String picUrl = jsonObj.getString("picUrl");
                            if (picUrl != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        webview.loadUrl(picUrl);
                                    }
                                });
                            }
                        }
                    }else if (text.contains("token")) {
                        Log.i(TAG, "onMessage: 登录成功");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                Log.i(TAG, "onMessage: -----bytes");
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                Log.i(TAG, "onClosing: -----");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.i(TAG, "onClosed: -----");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                Log.i(TAG, "onFailure: -----");
            }
        });
    }
}
