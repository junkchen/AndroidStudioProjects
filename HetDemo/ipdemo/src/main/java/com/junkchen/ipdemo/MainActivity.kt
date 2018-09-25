package com.junkchen.ipdemo

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.Inet4Address
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_getIp.setOnClickListener {
            tv_address.text = getIpAddress(this)
        }
    }

    private fun getIpAddress(context: Context): String? {
        val networkInfo = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                val networkInterfaces = NetworkInterface.getNetworkInterfaces()
                for (networkInterface in networkInterfaces) {
                    val inetAddresses = networkInterface.inetAddresses
                    for (address in inetAddresses) {
                        if (!address.isLoopbackAddress && address is Inet4Address) {
                            return address.hostAddress
                        }
                    }
                }
            } else if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                return intIpToStringIp(wifiInfo.ipAddress)
            }
        }
        return null
    }

    private fun intIpToStringIp(ip: Int) = (ip and 0xFF).toString() + "." +
            (ip shr 8 and 0xFF) + "." +
            (ip shr 16 and 0xFF) + "." +
            (ip shr 24 and 0xFF)
}
