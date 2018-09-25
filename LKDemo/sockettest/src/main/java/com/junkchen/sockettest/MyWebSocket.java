package com.junkchen.sockettest;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import javax.net.SocketFactory;

/**
 * Created by Junk on 2017/11/29.
 */

public class MyWebSocket {
    public static final String TAG = MyWebSocket.class.getSimpleName();

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

            if (socket.isConnected()) {
                OutputStream os = socket.getOutputStream();
                os.write("getQRCode".getBytes());
                os.flush();

                InputStream is = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                int count = 2;
                while (--count < 0) {
                    len = is.read(buffer);
                    String responseText = new String(buffer, 0, len);
                    Log.i(TAG, "Server said: " + responseText);
                }
                os.close();
                is.close();
                socket.close();
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
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
