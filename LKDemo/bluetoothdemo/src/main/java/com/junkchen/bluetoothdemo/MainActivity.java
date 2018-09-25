package com.junkchen.bluetoothdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: ----------------------------------------");

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null) {
            verifyIfRequestPermission();
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        }

        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        if (!bluetoothAdapter.isDiscovering()) {
            Log.i(TAG, "onCreate: bluetoothAdapter.startDiscovery(): " + bluetoothAdapter.startDiscovery());
        }
    }

    public void doClick(View view) {
        Log.i(TAG, "onCreate: bluetoothAdapter.startDiscovery(): " + bluetoothAdapter.startDiscovery());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: action: " + intent.getAction());
        }
    };

    private void verifyIfRequestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.i(TAG, "onCreate: checkSelfPermission");
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onCreate: Android 6.0 动态申请权限");

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    Log.i(TAG, "*********onCreate: shouldShowRequestPermissionRationale**********");
                    Toast.makeText(this, "只有允许访问位置才能搜索到蓝牙设备", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ACCESS_COARSE_LOCATION);
                }
            } else {
//                showDialog(getResources().getString(R.string.scanning));
//                mBleService.scanLeDevice(true);
                Log.i(TAG, "onCreate: bluetoothAdapter.startDiscovery(): " + bluetoothAdapter.startDiscovery());
            }
        } else {
//            showDialog(getResources().getString(R.string.scanning));
//            mBleService.scanLeDevice(true);
            Log.i(TAG, "onCreate: bluetoothAdapter.startDiscovery(): " + bluetoothAdapter.startDiscovery());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            Log.i(TAG, "onRequestPermissionsResult: permissions.length = " + permissions.length +
                    ", grantResults.length = " + grantResults.length);
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

//                showDialog(getResources().getString(R.string.scanning));
//                mBleService.scanLeDevice(true);
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(MainActivity.this, "位置访问权限被拒绝将无法搜索到ble设备", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Show dialog
     */
    private ProgressDialog progressDialog;

    private void showDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissDialog() {
        if (progressDialog == null) return;
        progressDialog.dismiss();
        progressDialog = null;
    }
}
