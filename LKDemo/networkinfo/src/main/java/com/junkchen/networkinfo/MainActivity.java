package com.junkchen.networkinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
//    ConnectivityManager connectivityManager;
//    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        TextView txtv_networkInfo = (TextView) findViewById(R.id.txtv_networkInfo);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            txtv_networkInfo.setText(networkInfo.toString() + "\n\n");

            txtv_networkInfo.append("Type: " + networkInfo.getType() +
                    ", TypeName:  " + networkInfo.getTypeName() + "\n");

            txtv_networkInfo.append("Subtype: " + networkInfo.getSubtype() +
                    ", SubtypeName:  " + networkInfo.getSubtypeName() + "\n");

            txtv_networkInfo.append("State: " + networkInfo.getState() +
                    ", DetailedState:  " + networkInfo.getDetailedState() + "\n");

            txtv_networkInfo.append("ExtraInfo: " + networkInfo.getExtraInfo() + "\n");

            txtv_networkInfo.append("isConnected: " + networkInfo.isConnected() +
                    ", isAvailable:  " + networkInfo.isAvailable() + "\n");

            txtv_networkInfo.append("isFailover: " + networkInfo.isFailover() +
                    ", isRoaming:  " + networkInfo.isRoaming() + "\n");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//                Log.i(TAG, "onReceive: noConnectivity: " + noConnectivity);
//                if (noConnectivity) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    NetworkInfo.State state = networkInfo.getState();
                    if (state == NetworkInfo.State.CONNECTED) {
                        Log.i(TAG, "onReceive: network is connected.");
                    } else if (state == NetworkInfo.State.DISCONNECTED) {
                        Log.i(TAG, "onReceive: network is disconnected.");
                    } else if (state == NetworkInfo.State.CONNECTING) {
                        Log.i(TAG, "onReceive: network is connecting.");
                    } else if (state == NetworkInfo.State.DISCONNECTING) {
                        Log.i(TAG, "onReceive: network is disconnecting.");
                    }
                    boolean available = networkInfo.isAvailable();
                    Log.i(TAG, "onReceive: current network if available: " + available);
                }
//                } else {
//                    Log.i(TAG, "onReceive: current no network.");
//                }
            }
        }
    };
}
