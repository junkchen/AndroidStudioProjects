package com.junkchen.sockettest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import javax.net.SocketFactory;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doClick(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                connectSocket2();
                webSocket();
            }
        }).start();
    }

    private Socket mSocket = null;
    private String mHost;
//    private int mPort;
    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;

    private void connectSocket() {
        try {
            mSocket = new Socket(mHost, mPort);
            if (mSocket.isConnected()) {
                mInputStream = mSocket.getInputStream();
                mOutputStream = mSocket.getOutputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(OutputStream os, byte[] data) {
        try {
            os.write(data);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectSocket2() {
        try {
            Socket socket = new Socket("192.168.1.104", 1234);
            if (socket.isConnected()) {
                int count = 5;
                InputStream is = null;
                OutputStream os = null;
//                while (count-- > 0) {
                is = socket.getInputStream();
                os = socket.getOutputStream();
                socket.getOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                len = is.read(buffer);
                String responseText = new String(buffer, 0, len);
                Log.i(TAG, "Server said: " + responseText);

                os.write("hello server!".getBytes());
                os.flush();
//                }
                os.close();
                is.close();
                socket.close();
                Log.i(TAG, "client end.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String uriText = "ws://172.31.71.197:8866/websocket";
    URI mUri = null;
    String mHostName = "";
    int mPort = 80;
    int mTimeout = 6000;
    private void webSocket() {
        try {
            initUri(uriText);
            Socket socket = SocketFactory.getDefault().createSocket();
            socket.connect(new InetSocketAddress(mHostName, mPort), mTimeout);
            socket.setSoTimeout(0);
            socket.setTcpNoDelay(true);
            Log.i(TAG, "webSocket: socket.isConnected: " + socket.isConnected());
            if (socket.isConnected()) {

                OutputStream os = socket.getOutputStream();
                os.write("getQRCode".getBytes("UTF-8"));
                os.flush();
                Log.i(TAG, "webSocket: send getQRCode to server.");

                InputStream is = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                int count = 3;
                while (count-- > 0) {
                    Log.i(TAG, "webSocket: count = " + count);
                    len = is.read(buffer);
                    if (len != -1) {
                        String responseText = new String(buffer, 0, len);
                        Log.i(TAG, "Server said: " + responseText);
                    }
                }
                os.close();
                is.close();
                socket.close();
                Log.i(TAG, "webSocket: socket.isClosed: " + socket.isClosed());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUri(String uri) {
        try {
            mUri = new URI(uri);
            mHostName = mUri.getHost();
            if (mHostName == null) throw new NullPointerException("no host specified in WebSockets URI");
            if (mUri.getPort() == -1) {
                mPort = 80;
            } else {
                mPort = mUri.getPort();
            }
            Log.i(TAG, "initUri: mUri: " + mUri.toString() + ", mHostName: " + mHostName + ", mPort: " + mPort);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
