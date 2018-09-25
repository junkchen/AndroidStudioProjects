package com.junkchen.androidusbdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.junkchen.androidusbdemo.adapter.CommonAdapter;
import com.junkchen.androidusbdemo.adapter.ViewHolder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * endpointIn = usbInterface.getEndpoint(0);//获取接收数据的 endpoint
 * endpointOut = usbInterface.getEndpoint(1);//获取发送数据的 endpoint
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    //Constant
    private static final String ACTION_USB_PERMISSION = "com.junkchen.USB_PERMISSION";

    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private UsbInterface usbInterface;
    private UsbEndpoint endpointIn;
    private UsbEndpoint endpointOut;
    private UsbDeviceConnection connection;

    private CommonAdapter<UsbDevice> usbAdapter;
    private List<UsbDevice> usbList;
    private PendingIntent mPendingIntent;

    //Layout view
    private Button btn_enumerate_usb;
    private ListView lstv_usb;
    private TextView txtv_connected_device;
    private TextView txtv_weight;
    private TextView txtv_oxygen;

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                mUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    Log.i(TAG, "onReceive: 用户以允许访问该USB设备");
                    if (mUsbDevice != null) {
                        int interfaceCount = mUsbDevice.getInterfaceCount();
                        Log.i(TAG, "onReceive: interfaceCount = " + interfaceCount);
                        if (interfaceCount > 0) {
                            usbInterface = mUsbDevice.getInterface(0);
                            int endpointCount = usbInterface.getEndpointCount();
                            Log.i(TAG, "onReceive: endpointCount = " + endpointCount);

                            endpointIn = usbInterface.getEndpoint(0);
                            endpointOut = usbInterface.getEndpoint(1);

//                            for (int i = 0; i < endpointCount; i++) {
//                                UsbEndpoint point = usbInterface.getEndpoint(i);
//                                Log.i(TAG, "onReceive: i: " + i + ", endpoint.direction: " + point.getDirection() + ", " + point.toString());
//                                if (point.getDirection() == UsbConstants.USB_DIR_IN) {
//                                    endpointIn = point;
//                                } else if (point.getDirection() == UsbConstants.USB_DIR_OUT) {
//                                    endpointOut = point;
//                                }
//                            }

                            Log.i(TAG, "onReceive: endpointIn direction: " + endpointIn.getDirection() + ", number: " + endpointIn.getEndpointNumber());
                            connection = mUsbManager.openDevice(mUsbDevice);
                            txtv_connected_device.setText("当前连接设备为： " + mUsbDevice.getDeviceName());
                            Log.i(TAG, "onReceive: serial: " + connection.getSerial());
                            configUsbCh340(connection, 9600);
//                            readData();
                            boolean claimInterface = connection.claimInterface(usbInterface, true);
                            Log.i(TAG, "readData: claimInterface: " + claimInterface);
                            if (claimInterface) {
                                byte[] bytes = new byte[5];
                                connection.bulkTransfer(endpointIn, bytes, bytes.length, 500);
                                Log.i(TAG, "onReceive: bytes: " + bytesToHexString(bytes));
                                Log.i(TAG, "onReceive: SpO2: " + (bytes[4] & 0x7F) + "%, hr: " + (bytes[2] & 0x80 + bytes[3] & 0x7F) + "bpm");
                            }
                        }
                    }
                } else {
                    Log.i(TAG, "permission denied for device " + mUsbDevice);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        initView();
        initAdapter();

        Thread thread = new Thread("");
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

    private void initView() {
        btn_enumerate_usb = (Button) findViewById(R.id.btn_enumerate_usb);
        lstv_usb = (ListView) findViewById(R.id.lstv_usb);
        txtv_connected_device = (TextView) findViewById(R.id.txtv_connected_device);
        txtv_weight = (TextView) findViewById(R.id.txtv_weight);
        txtv_oxygen = (TextView) findViewById(R.id.txtv_oxygen);

        btn_enumerate_usb.setOnClickListener(this);
    }

    private void initAdapter() {
        usbList = new ArrayList<>();
        usbAdapter = new CommonAdapter<UsbDevice>(this, R.layout.item_usb, usbList) {
            @Override
            public void convert(ViewHolder holder, final UsbDevice usbDevice) {
                holder.setText(R.id.txtv_name, usbDevice.getDeviceName() +
                        ", VendorId: " + usbDevice.getVendorId() +
                ", ProductId: " + usbDevice.getProductId() +
                ", DeviceId: " + usbDevice.getDeviceId());
                // deviceId: 3015, productId: 8200, vendorId: 3725
                holder.getView(R.id.btn_get_permission).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isReadDataThreadStop = true;
                        if (connection != null) {
                            connection.releaseInterface(usbInterface);
                            connection.close();
                            connection = null;
                        }
                        boolean hasPermission = mUsbManager.hasPermission(usbDevice);

//                        int configurationCount = usbDevice.getConfigurationCount();
//                        Log.i(TAG, "onClick: configurationCount = " + configurationCount);
//                        UsbConfiguration usbConfiguration = usbDevice.getConfiguration(0);
//                        Log.i(TAG, "onClick: usbConfiguration.getInterfaceCount() = " + usbConfiguration.getInterfaceCount());
//                        Log.i(TAG, "onClick: usbConfiguration.isRemoteWakeup() = " + usbConfiguration.isRemoteWakeup());

                        if (!hasPermission) {
                            Log.i(TAG, "onClick: ---usbName: " + usbDevice.getDeviceName() + " 没有访问权限");
                            mUsbManager.requestPermission(usbDevice, mPendingIntent);//请求权限
                        } else {
                            connection = mUsbManager.openDevice(usbDevice);
                            mUsbDevice = usbDevice;
                            txtv_connected_device.setText("当前连接设备为： " + mUsbDevice.getDeviceName());
                            configUsbCh340(connection, 9600);
                            int interfaceCount = usbDevice.getInterfaceCount();
                            Log.i(TAG, "onClick: interfaceCount = " + interfaceCount);
                            if (interfaceCount > 0) {
                                Log.i(TAG, "onClick: 初始化USB设备");
                                connection = mUsbManager.openDevice(usbDevice);
                                Log.i(TAG, "onClick: serial: " + connection.getSerial());

                                usbInterface = usbDevice.getInterface(0);
                                int endpointCount = usbInterface.getEndpointCount();
                                Log.i(TAG, "onClick: endpointCount = " + endpointCount);

                                endpointIn = usbInterface.getEndpoint(0);
                                endpointOut = usbInterface.getEndpoint(1);

//                                for (int i = 0; i < endpointCount; i++) {
//                                    UsbEndpoint point = usbInterface.getEndpoint(i);
//                                    Log.i(TAG, "onClick: i: " + i + ", endpoint.direction: " + point.getDirection() + ", " + point.toString());
//                                    if (point.getDirection() == UsbConstants.USB_DIR_IN) {
//                                        endpointIn = point;
//                                    } else if (point.getDirection() == UsbConstants.USB_DIR_OUT) {
//                                        endpointOut = point;
//                                    }
//                                }

                                Log.i(TAG, "onClick: endpointIn direction: " + endpointIn.getDirection() + ", number: " + endpointIn.getEndpointNumber());

//                            readData();
                                boolean claimInterface = connection.claimInterface(usbInterface, true);
                                Log.i(TAG, "onClick: claimInterface: " + claimInterface);
                                if (claimInterface) {
                                    byte[] bytes = new byte[5];
                                    connection.bulkTransfer(endpointIn, bytes, bytes.length, 500);
                                    Log.i(TAG, "onClick: bytes: " + bytesToHexString(bytes));
                                    Log.i(TAG, "onClick: SpO2: " + (bytes[4] & 0x7F) + "%, hr: " + (((bytes[2] & 0x40) << 1) + (bytes[3] & 0x7F)) + "bpm");
                                }
                            }
                        }
                    }
                });

                holder.getView(R.id.btn_read).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                readData();
                                if (!isReadDataThreadStop) {
                                    isReadDataThreadStop = true;
                                } else {
                                    boolean claimInterface = connection.claimInterface(usbInterface, true);
                                    Log.i(TAG, "run: claimInterface: " + claimInterface);
                                    if (claimInterface) {
                                        isReadDataThreadStop = false;
                                        new Thread(mReadDataRunnable2).start();
                                    }
                                }
                            }
                        }).start();
                    }
                });

                holder.getView(R.id.btn_write).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean claimInterface = connection.claimInterface(usbInterface, true);
                        Log.i(TAG, "onClick: claimInterface: " + claimInterface);
                        if (claimInterface == true) {
                            byte[] buffer = new byte[]{(byte) 0xFA, (byte) 0x82, 0x01, 0x00, (byte) 0x83};
                            int transfer = connection.bulkTransfer(endpointOut, buffer, buffer.length, 0);
                        }
                    }
                });
            }
        };
        lstv_usb.setAdapter(usbAdapter);
    }

    @Override
    public void onClick(View v) {
        usbList.clear();
        HashMap<String, UsbDevice> usbDeviceHashMap = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = usbDeviceHashMap.values().iterator();
        Iterator<String> keyIterator = usbDeviceHashMap.keySet().iterator();
        while (keyIterator.hasNext()) {
            Log.i(TAG, "onClick: deviceName: " + keyIterator.next());
        }
        while (deviceIterator.hasNext()) {
            UsbDevice usbDevice = deviceIterator.next();
            usbList.add(usbDevice);
            getUsbDeviceInfo(usbDevice);
            getInterfaceEndpoint(usbDevice);
//            initUsbDevice(usbDevice);
        }
        usbAdapter.notifyDataSetChanged();
    }

    private void getUsbDeviceInfo(UsbDevice device) {
        String deviceName = device.getDeviceName();
        int deviceId = device.getDeviceId();
        int productId = device.getProductId();
        int vendorId = device.getVendorId();
        Log.i(TAG, "getUsbDeviceInfo: deviceName: " + deviceName + ", deviceId: " + deviceId +
                ", productId: " + productId + ", vendorId: " + vendorId);
        Log.i(TAG, "getUsbDeviceInfo: interfaceCount = " + device.getInterfaceCount());
        Log.i(TAG, "getUsbDeviceInfo: UsbManager.hasPermission: " + mUsbManager.hasPermission(device));
    }

    private void getInterfaceEndpoint(UsbDevice device) {
        Log.i(TAG, "getInterfaceEndpoint: device: " + device);
        if (device.getInterfaceCount() > 0) {
            usbInterface = device.getInterface(0);
            int endpointCount = usbInterface.getEndpointCount();
            if (endpointCount < 2) return;
            endpointIn = usbInterface.getEndpoint(0);
            endpointOut = usbInterface.getEndpoint(1);
            Log.i(TAG, "getInterfaceEndpoint: usbInterface: " + usbInterface);
            Log.i(TAG, "getInterfaceEndpoint: endpointIn: " + endpointIn);
            Log.i(TAG, "getInterfaceEndpoint: endpointOut: " + endpointOut);
        }
    }

    private void initUsbDevice(UsbDevice ud) {
        if (mUsbManager.hasPermission(ud)) {
            int interfaceCount = ud.getInterfaceCount();
            Log.i(TAG, "initUsbDevice: interfaceCount = " + interfaceCount);
            if (interfaceCount > 0) {
                mUsbDevice = ud;
                Log.i(TAG, "initUsbDevice: 初始化USB设备");
                connection = mUsbManager.openDevice(ud);
                txtv_connected_device.setText("当前连接设备为： " + mUsbDevice.getDeviceName());
                configUsbCh340(connection, 9600);
                Log.i(TAG, "initUsbDevice: serial: " + connection.getSerial());

                usbInterface = ud.getInterface(0);
                int endpointCount = usbInterface.getEndpointCount();
                Log.i(TAG, "initUsbDevice: endpointCount = " + endpointCount);

                endpointIn = usbInterface.getEndpoint(0);
                endpointOut = usbInterface.getEndpoint(1);

//                for (int i = 0; i < endpointCount; i++) {
//                    UsbEndpoint point = usbInterface.getEndpoint(i);
//                    Log.i(TAG, "initUsbDevice: i: " + i + ", endpoint.direction: " + point.getDirection() + ", " + point.toString());
//                    if (point.getDirection() == UsbConstants.USB_DIR_IN) {
//                        endpointIn = point;
//                    } else if (point.getDirection() == UsbConstants.USB_DIR_OUT) {
//                        endpointOut = point;
//                    }
//                }

                Log.i(TAG, "initUsbDevice: endpointIn direction: " + endpointIn.getDirection() + ", number: " + endpointIn.getEndpointNumber());
            }
        }
    }

    private void readData() {
        boolean claimInterface = connection.claimInterface(usbInterface, true);
        Log.i(TAG, "readData: claimInterface: " + claimInterface);
        if (claimInterface) {
            byte[] bytes = new byte[32];
            connection.bulkTransfer(endpointIn, bytes, bytes.length, 0);
            Log.i(TAG, "readData: bytes: " + bytesToHexString(bytes));
            Log.i(TAG, "readData: SpO2: " + (bytes[4] & 0x7F) + "%, hr: " + (((bytes[2] & 0x40) << 1) + (bytes[3] & 0x7F)) + "bpm");
//                            for (int i = 0; i < bytes.length; i++) {
//                                Log.i(TAG, "onReceive: bytes[" + i + "] = " + bytes[i]);
//                            }
        }
    }

    private boolean isReadDataThreadStop = true;
    private Runnable mReadDataRunnable = new Runnable() {
        @Override
        public void run() {
            int len = -1;
            while (true) {
                byte[] readBuf = new byte[80];
                len = connection.bulkTransfer(endpointIn, readBuf, readBuf.length, 0);
                if (len >= 0) {
                    Log.i(TAG, "run: transfer success, len = " + len);
                    Log.i(TAG, "run: readBuf: " + bytesToHexString(readBuf));
                    Log.i(TAG, "run: SpO2: " + (readBuf[4] & 0x7F) + "%, hr: " + (((readBuf[2] & 0x40) << 1) + (readBuf[3] & 0x7F)) + "bpm");
                } else {
                    Log.i(TAG, "run: transfer failure, len = " + len);
//                    isReadDataThreadStop = true;
                }
                if (isReadDataThreadStop) {
                    return;
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }//End of run
    };//End of runnable

    private Runnable mReadDataRunnable2 = new Runnable() {
        @Override
        public void run() {
            int maxPacketSize = endpointIn.getMaxPacketSize();
            Log.i(TAG, "run: maxPacketSize = " + maxPacketSize);
            ByteBuffer buffer = ByteBuffer.allocate(maxPacketSize);
            UsbRequest request = new UsbRequest();
            while (true) {
                if (isReadDataThreadStop) {
                    return;
                }
                request.initialize(connection, endpointIn);
                request.queue(buffer, maxPacketSize);
                if (connection.requestWait() == request) {
                    byte[] readData = buffer.array();
                    Log.i(TAG, "run: ------------------------------------------------------------");
                    Log.i(TAG, "run: readData size: " + readData.length);
                    Log.i(TAG, "run: readData: " + bytesToHexString(readData));
                    byte[] recBuf = new byte[buffer.position()];
//                    buffer.get(recBuf, 0, recBuf.length);
                    System.arraycopy(readData, 0, recBuf, 0, recBuf.length);
                    Log.i(TAG, "run: recBuf: " + bytesToHexString(recBuf));
                    parseData(recBuf);
//                    Log.i(TAG, "run: offset = " + buffer.arrayOffset() + ", limit = " + buffer.limit()
//                            + ", position = " + buffer.position() + ", remaining = " + buffer.remaining()
//                            + ", capacity = " + buffer.capacity());
//                    isReadDataThreadStop = true;
                }
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    };

    private long l = 0;
    private void parseData(byte[] data) {
        if (data.length <= 0) return;
        Log.i(TAG, "parseData: interval time: " + (System.currentTimeMillis() - l));
        l = System.currentTimeMillis();
        if (data[0] == (byte) 0xFA) {
            Log.i(TAG, "parseData: 这是体重测量模块");
            if (data[1] == 0x01) {//临时重量
                String weightStr = Integer.toHexString(data[4] & 0xFF) + Integer.toHexString(data[5] & 0xFF);
                final double weight = Integer.parseInt(weightStr, 16) * 1.0 / 10;
                Log.i(TAG, "parseData: 临时体重： " + weight + "kg");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtv_weight.setText("临时体重： " + weight + "kg， deviceName: " + mUsbDevice.getDeviceName());
                    }
                });
            } else if (data[1] == 0x02) {//锁定重量
                String weightStr = Integer.toHexString(data[4] & 0xFF) + Integer.toHexString(data[5] & 0xFF);
                final double weight = Integer.parseInt(weightStr, 16) * 1.0 / 10;
                Log.i(TAG, "parseData: 锁定体重： " + weight + "kg");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtv_weight.setText("锁定体重： " + weight + "kg， deviceName: " + mUsbDevice.getDeviceName());
                    }
                });

                Log.i(TAG, "parseData: 接收到锁定重量后向体重秤发送停止发送锁定的体重数据");
                boolean claimInterface = connection.claimInterface(usbInterface, true);
                Log.i(TAG, "parseData: claimInterface: " + claimInterface);
                if (claimInterface == true) {
                    byte[] buffer = new byte[]{(byte) 0xFA, (byte) 0x82, 0x01, 0x00, (byte) 0x83};
                    connection.bulkTransfer(endpointOut, buffer, buffer.length, 0);
                }
            } else if (data[1] == 0x04) {//开机
                Log.i(TAG, "parseData: 开始测量（开机）");
            } else if (data[1] == 0x05) {//关机
                Log.i(TAG, "parseData: 结束测量（关机）");
            }
        } else if (data[0] >= (byte)0x80 && data.length == 5) {
            Log.i(TAG, "parseData: 这是血氧测量模块");
            Log.i(TAG, "parseData: " + Integer.toHexString(data[0] >> 4 & 0x0F));
            Log.i(TAG, "parseData: " + Integer.toHexString(data[0] >> 4));
            if (((data[0] >> 4) & 0x0F) == 0x08) {
                final int oxygenValue = data[4] & 0x7F;
                final int heartRateValue = ((data[2] & 0x40) << 1) + (data[3] & 0x7F);
                Log.i(TAG, "parseData: (血氧)SpO2: " + oxygenValue + "%, （心率）hr: " + heartRateValue + "bpm");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtv_oxygen.setText("血氧(SpO2)：" + oxygenValue + "%, 心率(hr)：" + heartRateValue + "bpm, deviceName: " + mUsbDevice.getDeviceName());
                    }
                });
            }
        }
    }

    private boolean configUsbCh340(UsbDeviceConnection usbDeviceConnection, int paramInt) {
        byte[] arrayOfByte = new byte[8];
        usbDeviceConnection.controlTransfer(192, 95, 0, 0, arrayOfByte, 8, 1000);
        usbDeviceConnection.controlTransfer(64, 161, 0, 0, null, 0, 1000);
        long l1 = 1532620800 / paramInt;
        for (int i = 3; ; i--) {
            if ((l1 <= 65520L) || (i <= 0)) {
                long l2 = 65536L - l1;
                int j = (short) (int) (0xFF00 & l2 | i);
                int k = (short) (int) (0xFF & l2);
                usbDeviceConnection.controlTransfer(64, 154, 4882, j, null, 0, 1000);
                usbDeviceConnection.controlTransfer(64, 154, 3884, k, null, 0, 1000);
                usbDeviceConnection.controlTransfer(192, 149, 9496, 0, arrayOfByte, 8, 1000);
                usbDeviceConnection.controlTransfer(64, 154, 1304, 80, null, 0, 1000);
                usbDeviceConnection.controlTransfer(64, 161, 20511, 55562, null, 0, 1000);
                usbDeviceConnection.controlTransfer(64, 154, 4882, j, null, 0, 1000);
                usbDeviceConnection.controlTransfer(64, 154, 3884, k, null, 0, 1000);
                usbDeviceConnection.controlTransfer(64, 164, 0, 0, null, 0, 1000);
                return true;
            }
            l1 >>= 3;
        }
    }

    public String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
//            if (i % 5 == 0 && i != 0) stringBuilder.append(",");
            if (i != 0) stringBuilder.append(",");
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
