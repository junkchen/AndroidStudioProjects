package com.junkchen.androidusbdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
public class BloodPressureActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    //Constant
    private static final String ACTION_USB_PERMISSION = "com.junkchen.USB_PERMISSION";

    private static final byte[] COMMAND_CONNECT =
            new byte[]{(byte) 0xCC, (byte) 0x80, 0x03, 0x03, 0x01, 0x01, 0x00, 0x00};

    private static final byte[] COMMAND_START =
            new byte[]{(byte) 0xCC, (byte) 0x80, 0x03, 0x03, 0x01, 0x02, 0x00, 0x03};

    private static final byte[] COMMAND_STOP =
            new byte[]{(byte) 0xCC, (byte) 0x80, 0x03, 0x03, 0x01, 0x03, 0x00, 0x02};

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
    private TextView txtv_blood;

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

                            configUsbCh340(connection, 115200);
//                            if (UartInit()) {
//                                SetConfig(115200, (byte) 8, (byte)1, (byte)0, (byte)0);
//                            }

//                            readData();
                            boolean claimInterface = connection.claimInterface(usbInterface, true);
                            Log.i(TAG, "readData: claimInterface: " + claimInterface);
                            if (claimInterface) {
                                byte[] bytes = new byte[8];
                                connection.bulkTransfer(endpointIn, bytes, bytes.length, 500);
                                Log.i(TAG, "onReceive: bytes: " + bytesToHexString(bytes));
//                                Log.i(TAG, "onReceive: SpO2: " + (bytes[4] & 0x7F) + "%, hr: " + (bytes[2] & 0x80 + bytes[3] & 0x7F) + "bpm");
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
        txtv_blood = (TextView) findViewById(R.id.txtv_blood);

        btn_enumerate_usb.setOnClickListener(this);
        findViewById(R.id.btn_connect).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
    }

    private void initAdapter() {
        usbList = new ArrayList<>();
        usbAdapter = new CommonAdapter<UsbDevice>(this, R.layout.item_usb, usbList) {
            @Override
            public void convert(ViewHolder holder, final UsbDevice usbDevice) {
                holder.setText(R.id.txtv_name, usbDevice.getDeviceName());

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

                            configUsbCh340(connection, 115200);
//                           if (UartInit()) {
//                               SetConfig(115200, (byte) 8, (byte)1, (byte)0, (byte)0);
//                           }

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
//                                    Log.i(TAG, "onClick: SpO2: " + (bytes[4] & 0x7F) + "%, hr: " + (((bytes[2] & 0x40) << 1) + (bytes[3] & 0x7F)) + "bpm");
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
        switch (v.getId()) {
            case R.id.btn_enumerate_usb: {
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
            break;
            case R.id.btn_connect:
                writeData(COMMAND_CONNECT);
                break;
            case R.id.btn_start:
                writeData(COMMAND_START);
                break;
            case R.id.btn_stop:
                writeData(COMMAND_STOP);
                break;
        }

    }

    private void writeData(byte[] datas) {
        boolean claimInterface = connection.claimInterface(usbInterface, true);
        Log.i(TAG, "onClick: claimInterface: " + claimInterface);
        if (claimInterface == true) {
//            byte[] buffer = new byte[]{(byte) 0xFA, (byte) 0x82, 0x01, 0x00, (byte) 0x83};
            int transfer = connection.bulkTransfer(endpointOut, datas, datas.length, 500);
            Log.i(TAG, "writeData: 写入数据，transfer: " + transfer + "，datas: " + bytesToHexString(datas));
        }
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
                configUsbCh340(connection, 115200);
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
    private int detect_count = 0;

    private void parseData(byte[] data) {
        if (data.length <= 0) return;
        Log.i(TAG, "parseData: interval time: " + (System.currentTimeMillis() - l));
        l = System.currentTimeMillis();
        if (data[0] == (byte) 0xAA
                && data[1] == (byte) 0x80) {
            Log.i(TAG, "parseData: 这是心率血压测量模块");
            if (data[3] == 0x03 && data[4] == 0x01) {
                if (data[5] == 0x01) {//连接血压计应答
                    if (data[6] == 0x00) {
                        Log.i(TAG, "parseData: 连接血压计成功");
                    } else if (data[6] == 0x00) {
                        Log.i(TAG, "parseData: 连接血压计失败");
                    }
                } else if (data[5] == 0x02) {//启动测量应答
                    if (data[6] == 0x00) {
                        detect_count = 0;
                        Log.i(TAG, "parseData: 启动测量成功");
                    } else if (data[6] == 0x00) {
                        Log.i(TAG, "parseData: 启动测量失败");
                    }
                } else if (data[5] == 0x03) {//停止测量应答
                    if (data[6] == 0x00) {
                        Log.i(TAG, "parseData: 停止测量成功");
                    } else if (data[6] == 0x00) {
                        Log.i(TAG, "parseData: 停止测量失败");
                    }
                }
            } else if (data[3] == 0x04 && data[4] == 0x01 && data[5] == 0x05) {//血压计发送的实时压力
                detect_count++;
                final int systolic = (data[6] & 0xFF00) + (data[7] & 0xFF);
                Log.i(TAG, "parseData: 实时压力值：" + systolic);
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        txtv_blood.setText("血压心率测量，实时压力值：" + systolic);
                    }
                });
            } else if (data[3] == 0x0F && data[4] == 0x01 && data[5] == 0x06) {//血压计发送测量结果
                final int systolic = (data[13] & 0xFF00) + (data[14] & 0xFF);
                final int diastolic = (data[15] & 0xFF00) + (data[16] & 0xFF);
                final int pulse = (data[17] & 0xFF00) + (data[18] & 0xFF);
                Log.i(TAG, "parseData: 血压心率测量结果，收缩压：" + systolic
                        + "mmHg, 舒张压: " + diastolic
                        + "mmHg, 心率: " + pulse + "bpm, detect_count: " + detect_count);

                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        txtv_blood.setText("血压心率测量结果，收缩压：" + systolic
                                + "mmHg, 舒张压: " + diastolic
                                + "mmHg, 心率: " + pulse + "bpm");
                    }
                });
            }
        } else if (data[0] == (byte) 0xFA) {
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
        } else if (data[0] >= (byte) 0x80 && data.length == 5) {
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


    /**
     * ---------------------------------------------------------------------------------------------
     */
    private int DEFAULT_TIMEOUT = 500;

    public boolean UartInit() {
        int ret;
        int size = 8;
        byte[] buffer = new byte[size];
        Uart_Control_Out(UartCmd.VENDOR_SERIAL_INIT, 0x0000, 0x0000);
        ret = Uart_Control_In(UartCmd.VENDOR_VERSION, 0x0000, 0x0000, buffer, 2);
        if (ret < 0)
            return false;
        Uart_Control_Out(UartCmd.VENDOR_WRITE, 0x1312, 0xD982);
        Uart_Control_Out(UartCmd.VENDOR_WRITE, 0x0f2c, 0x0004);
        ret = Uart_Control_In(UartCmd.VENDOR_READ, 0x2518, 0x0000, buffer, 2);
        if (ret < 0)
            return false;
        Uart_Control_Out(UartCmd.VENDOR_WRITE, 0x2727, 0x0000);
        Uart_Control_Out(UartCmd.VENDOR_MODEM_OUT, 0x00ff, 0x0000);
        return true;
    }

    public int Uart_Control_Out(int request, int value, int index) {
        int retval = 0;
        retval = connection.controlTransfer(UsbType.USB_TYPE_VENDOR
                        | UsbType.USB_RECIP_DEVICE | UsbType.USB_DIR_OUT, request,
                value, index, null, 0, DEFAULT_TIMEOUT);

        return retval;
    }

    public int Uart_Control_In(int request, int value, int index,
                               byte[] buffer, int length) {
        int retval = 0;
        retval = connection.controlTransfer(UsbType.USB_TYPE_VENDOR
                        | UsbType.USB_RECIP_DEVICE | UsbType.USB_DIR_IN, request,
                value, index, buffer, length, DEFAULT_TIMEOUT);
        return retval;
    }

    private int Uart_Set_Handshake(int control) {
        return Uart_Control_Out(UartCmd.VENDOR_MODEM_OUT, ~control, 0);
    }

    public int Uart_Tiocmset(int set, int clear) {
        int control = 0;
        if ((set & UartModem.TIOCM_RTS) == UartModem.TIOCM_RTS)
            control |= UartIoBits.UART_BIT_RTS;
        if ((set & UartModem.TIOCM_DTR) == UartModem.TIOCM_DTR)
            control |= UartIoBits.UART_BIT_DTR;
        if ((clear & UartModem.TIOCM_RTS) == UartModem.TIOCM_RTS)
            control &= ~UartIoBits.UART_BIT_RTS;
        if ((clear & UartModem.TIOCM_DTR) == UartModem.TIOCM_DTR)
            control &= ~UartIoBits.UART_BIT_DTR;

        return Uart_Set_Handshake(control);
    }

    public boolean SetConfig(int baudRate, byte dataBit, byte stopBit,
                             byte parity, byte flowControl) {
        int value = 0;
        int index = 0;
        char valueHigh = 0, valueLow = 0, indexHigh = 0, indexLow = 0;
        switch (parity) {
            case 0: /* NONE */
                valueHigh = 0x00;
                break;
            case 1: /* ODD */
                valueHigh |= 0x08;
                break;
            case 2: /* Even */
                valueHigh |= 0x18;
                break;
            case 3: /* Mark */
                valueHigh |= 0x28;
                break;
            case 4: /* Space */
                valueHigh |= 0x38;
                break;
            default: /* None */
                valueHigh = 0x00;
                break;
        }

        if (stopBit == 2) {
            valueHigh |= 0x04;
        }

        switch (dataBit) {
            case 5:
                valueHigh |= 0x00;
                break;
            case 6:
                valueHigh |= 0x01;
                break;
            case 7:
                valueHigh |= 0x02;
                break;
            case 8:
                valueHigh |= 0x03;
                break;
            default:
                valueHigh |= 0x03;
                break;
        }

        valueHigh |= 0xc0;
        valueLow = 0x9c;

        value |= valueLow;
        value |= (int) (valueHigh << 8);

        switch (baudRate) {
            case 50:
                indexLow = 0;
                indexHigh = 0x16;
                break;
            case 75:
                indexLow = 0;
                indexHigh = 0x64;
                break;
            case 110:
                indexLow = 0;
                indexHigh = 0x96;
                break;
            case 135:
                indexLow = 0;
                indexHigh = 0xa9;
                break;
            case 150:
                indexLow = 0;
                indexHigh = 0xb2;
                break;
            case 300:
                indexLow = 0;
                indexHigh = 0xd9;
                break;
            case 600:
                indexLow = 1;
                indexHigh = 0x64;
                break;
            case 1200:
                indexLow = 1;
                indexHigh = 0xb2;
                break;
            case 1800:
                indexLow = 1;
                indexHigh = 0xcc;
                break;
            case 2400:
                indexLow = 1;
                indexHigh = 0xd9;
                break;
            case 4800:
                indexLow = 2;
                indexHigh = 0x64;
                break;
            case 9600:
                indexLow = 2;
                indexHigh = 0xb2;
                break;
            case 19200:
                indexLow = 2;
                indexHigh = 0xd9;
                break;
            case 38400:
                indexLow = 3;
                indexHigh = 0x64;
                break;
            case 57600:
                indexLow = 3;
                indexHigh = 0x98;
                break;
            case 115200:
                indexLow = 3;
                indexHigh = 0xcc;
                break;
            case 230400:
                indexLow = 3;
                indexHigh = 0xe6;
                break;
            case 460800:
                indexLow = 3;
                indexHigh = 0xf3;
                break;
            case 500000:
                indexLow = 3;
                indexHigh = 0xf4;
                break;
            case 921600:
                indexLow = 7;
                indexHigh = 0xf3;
                break;
            case 1000000:
                indexLow = 3;
                indexHigh = 0xfa;
                break;
            case 2000000:
                indexLow = 3;
                indexHigh = 0xfd;
                break;
            case 3000000:
                indexLow = 3;
                indexHigh = 0xfe;
                break;
            default: // default baudRate "9600"
                indexLow = 2;
                indexHigh = 0xb2;
                break;
        }

        index |= 0x88 | indexLow;
        index |= (int) (indexHigh << 8);

        Uart_Control_Out(UartCmd.VENDOR_SERIAL_INIT, value, index);
        if (flowControl == 1) {
            Uart_Tiocmset(UartModem.TIOCM_DTR | UartModem.TIOCM_RTS, 0x00);
        }
        return true;
    }

    public final class UartModem {
        public static final int TIOCM_LE = 0x001;
        public static final int TIOCM_DTR = 0x002;
        public static final int TIOCM_RTS = 0x004;
        public static final int TIOCM_ST = 0x008;
        public static final int TIOCM_SR = 0x010;
        public static final int TIOCM_CTS = 0x020;
        public static final int TIOCM_CAR = 0x040;
        public static final int TIOCM_RNG = 0x080;
        public static final int TIOCM_DSR = 0x100;
        public static final int TIOCM_CD = TIOCM_CAR;
        public static final int TIOCM_RI = TIOCM_RNG;
        public static final int TIOCM_OUT1 = 0x2000;
        public static final int TIOCM_OUT2 = 0x4000;
        public static final int TIOCM_LOOP = 0x8000;
    }

    public final class UsbType {
        public static final int USB_TYPE_VENDOR = (0x02 << 5);
        public static final int USB_RECIP_DEVICE = 0x00;
        public static final int USB_DIR_OUT = 0x00; /* to device */
        public static final int USB_DIR_IN = 0x80; /* to host */
    }

    public final class UartCmd {
        public static final int VENDOR_WRITE_TYPE = 0x40;
        public static final int VENDOR_READ_TYPE = 0xC0;
        public static final int VENDOR_READ = 0x95;
        public static final int VENDOR_WRITE = 0x9A;
        public static final int VENDOR_SERIAL_INIT = 0xA1;
        public static final int VENDOR_MODEM_OUT = 0xA4;
        public static final int VENDOR_VERSION = 0x5F;
    }

    public final class UartState {
        public static final int UART_STATE = 0x00;
        public static final int UART_OVERRUN_ERROR = 0x01;
        public static final int UART_PARITY_ERROR = 0x02;
        public static final int UART_FRAME_ERROR = 0x06;
        public static final int UART_RECV_ERROR = 0x02;
        public static final int UART_STATE_TRANSIENT_MASK = 0x07;
    }

    public final class UartIoBits {
        public static final int UART_BIT_RTS = (1 << 6);
        public static final int UART_BIT_DTR = (1 << 5);
    }
}
