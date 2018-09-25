package com.junkchen.accessibilitydemo

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Created by Junk on 2018/2/27.
 */
class MyAccessibilityService : AccessibilityService() {
    val TAG = "MyAccessibilityService"

    /**
     * 服务启动时调用
     */
    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    /**
     * 中断服务的回调
     */
    override fun onInterrupt() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 监听窗口变化的回调
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val mEventType = event!!.eventType
        val mPackageName = event.packageName//com.android.systemui
        val mClassName = event.className
        Log.i(TAG, "mEventType: $mEventType, mPackageName: $mPackageName, mClassName: $mClassName")
        when (mEventType) {
        //当窗口内容发生改变时
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                if (mPackageName.equals("com.android.packageinstaller")) {
                    //应用安装完成时窗口
                    Log.i(TAG, "Enter apk install")
                    val nodeInfo = rootInActiveWindow
                    if (nodeInfo != null) {
                        Log.i(TAG, "child count: ${nodeInfo.childCount}")
                        val appNameNode = nodeInfo.findAccessibilityNodeInfosByViewId("com.android.packageinstaller:id/app_name")//荔康医疗

                        val launchButtonNode = nodeInfo.findAccessibilityNodeInfosByViewId("com.android.packageinstaller:id/launch_button")//打开
                        val doneButtonNode = nodeInfo.findAccessibilityNodeInfosByViewId("com.android.packageinstaller:id/done_button")//完成

                        val appName = appNameNode.first().text
                        Log.i(TAG, "onAccessibilityEvent: appName: $appName")

                        if (launchButtonNode.isNotEmpty()) {
                            val launchText = launchButtonNode.first().text
                            val doneText = doneButtonNode.first().text
                            Log.i(TAG, "onAccessibilityEvent: launchText: $launchText, doneText: $doneText")
                        }

                        if (appName.equals("荔康医疗")) {
                            try {
                                Thread.sleep(300)
                                for (item in launchButtonNode) {
                                    Log.i(TAG, "onAccessibilityEvent: item text: ${item.text}, className: ${item.className}")
                                    if (item.isClickable)
                                        item.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                }
                            } catch (exception: Exception) {

                            }
                        }
                        nodeInfo.recycle()
                    }
                }
            }
        //当窗口状态发生改变时
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (mClassName.equals("android.app.Dialog")) {
                    Log.i(TAG, "Enter usb permission request")
                    val nodeInfo = rootInActiveWindow
                    if (nodeInfo != null) {
                        Log.i(TAG, "child count: ${nodeInfo.childCount}")
                        val list = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/button1")//确定
                        val titleNode = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/alertTitle")//荔康医疗
                        val messageNode = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/message")//允许应用“荔康医疗”访问该USB设备吗？

                        val title = titleNode.first().text
                        val message = messageNode.first().text
                        val confirm = list.first().text
                        Log.i(TAG, "title: $title, message: $message, confirm: $confirm")
                        Log.i(TAG, "list.size = ${list.size}")

                        if (title.equals("荔康医疗")
                                && message.equals("允许应用“荔康医疗”访问该USB设备吗？")) {
                            try {
                                Thread.sleep(300)
                                for (item in list) {
                                    Log.i(TAG, "item text: ${item.text}, className: ${item.className}")
                                    if (item.isClickable)
                                        item.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                }
                            } catch (exception: Exception) {

                            }
                        }
                        nodeInfo.recycle()
                    }
                } else if (mPackageName.equals("com.android.packageinstaller")) {
                    //应用安装时窗口
                    Log.i(TAG, "Enter apk install")
                    val nodeInfo = rootInActiveWindow
                    if (nodeInfo != null) {
                        Log.i(TAG, "child count: ${nodeInfo.childCount}")
                        val appNameNode = nodeInfo.findAccessibilityNodeInfosByViewId("com.android.packageinstaller:id/app_name")//荔康医疗

                        val okButtonNode = nodeInfo.findAccessibilityNodeInfosByViewId("com.android.packageinstaller:id/ok_button")//安装
                        val cancelButtonNode = nodeInfo.findAccessibilityNodeInfosByViewId("com.android.packageinstaller:id/cancel_button")//取消

                        val appName = appNameNode.first().text
                        Log.i(TAG, "onAccessibilityEvent: appName: $appName")

                        if (okButtonNode.isNotEmpty()) {
                            val okText = okButtonNode.first().text
                            val cancelText = cancelButtonNode.first().text
                            Log.i(TAG, "onAccessibilityEvent: okText: $okText, cancelText: $cancelText")
                        }

                        if (appName.equals("荔康医疗")) {
                            try {
                                Thread.sleep(300)
                                for (item in okButtonNode) {
                                    Log.i(TAG, "item text: ${item.text}, className: ${item.className}")
                                    if (item.isClickable)
                                        item.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                }
                            } catch (exception: Exception) {

                            }
                        }
                        nodeInfo.recycle()
                    }
                }
            }
        }
    }
}