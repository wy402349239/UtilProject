package com.dsj.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.utilproject.wy.NetUtilJava
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

object NetUtil {
    // 下面是联网类型的定义
    private const val TYPE_2G: Byte = 1
    private const val TYPE_3G: Byte = 2
    private const val TYPE_4G: Byte = 3
    private const val TYPE_WIFI: Byte = 4
    private const val TYPE_UNKNOWN: Byte = 5
    private const val TYPE_DISCONNECT = TYPE_UNKNOWN


    /**
     * Network type is unknown
     */
    private const val NETWORK_TYPE_UNKNOWN = 0

    /**
     * Current network is GPRS
     */
    private const val NETWORK_TYPE_GPRS = 1

    /**
     * Current network is EDGE
     */
    private const val NETWORK_TYPE_EDGE = 2

    /**
     * Current network is UMTS
     */
    private const val NETWORK_TYPE_UMTS = 3

    /**
     * Current network is CDMA: Either IS95A or IS95B
     */
    private const val NETWORK_TYPE_CDMA = 4

    /**
     * Current network is EVDO revision 0
     */
    private const val NETWORK_TYPE_EVDO_0 = 5

    /**
     * Current network is EVDO revision A
     */
    private const val NETWORK_TYPE_EVDO_A = 6

    /**
     * Current network is 1xRTT
     */
    private const val NETWORK_TYPE_1XRTT = 7

    /**
     * Current network is HSDPA
     */
    private const val NETWORK_TYPE_HSDPA = 8

    /**
     * Current network is HSUPA
     */
    private const val NETWORK_TYPE_HSUPA = 9

    /**
     * Current network is HSPA
     */
    private const val NETWORK_TYPE_HSPA = 10

    /**
     * Current network is iDen
     */
    private const val NETWORK_TYPE_IDEN = 11

    /**
     * Current network is EVDO revision B
     */
    private const val NETWORK_TYPE_EVDO_B = 12

    /**
     * Current network is LTE
     */
    private const val NETWORK_TYPE_LTE = 13

    /**
     * Current network is eHRPD
     */
    private const val NETWORK_TYPE_EHRPD = 14

    /**
     * Current network is HSPA+
     */
    private const val NETWORK_TYPE_HSPAP = 15

    /**
     * Current network is GSM
     */
    private const val NETWORK_TYPE_GSM = 16

    /**
     * 获取 WiFi mac地址的新方法
     *
     * @param context 上下文对象
     * @return WiFi Mac地址
     */
    fun getMacAddrNew(context: Context): String {
        var mac = ""
        if (Build.VERSION.SDK_INT >= 23) {
            mac = getWifiMacAddressForAndroid23()
            if (TextUtils.isEmpty(mac)) {
                mac = "02:00:00:00:00:00"
            }
            return mac
        }
        val wifiManager = context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        mac = tryGetMAC(wifiManager)
        if (!TextUtils.isEmpty(mac)) {
            return mac
        } else {
            return "02:00:00:00:00:00"
        }
//        for (index in 0..2) {
//            if (index != 0) {
//                try {
//                    Thread.sleep(100)
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }
//            mac = tryGetMAC(wifiManager)
//            if (!TextUtils.isEmpty(mac)) {
//                break
//            }
//        }
//        return mac
    }

    /**
     * 获取WiFi Mac地址
     *
     * @param manager manager
     * @return Mac地址
     */
    @SuppressLint("HardwareIds")
    fun tryGetMAC(manager: WifiManager): String {
        try {
            val wifiInfo = manager.connectionInfo
            if (wifiInfo == null || TextUtils.isEmpty(wifiInfo.macAddress)) {
                return ""
            }
            return wifiInfo.macAddress
        } catch (e: Exception) {
            //联想手机在4.*.*上可能出现NPE，这里只能加一个保护
            e.printStackTrace()
        }
        return ""
    }

    /**
     * Android 6.0上增强了数据保护，取到的MAC地址是02:00:00:00:00:00
     * http://stackoverflow.com/questions/31329733/how-to-get-the-missing-wifi-mac-address-on-android-m-preview
     */
    private fun getWifiMacAddressForAndroid23(): String {
        try {
            val interfaceName = "wlan0"
            val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (!intf.name.equals(interfaceName, ignoreCase = true)) {
                    continue
                }
                val mac = intf.hardwareAddress ?: return ""
                val buf = StringBuilder()
                for (aMac in mac) {
                    buf.append(String.format("%02X:", aMac))
                }
                if (buf.length > 0) {
                    buf.deleteCharAt(buf.length - 1)
                }
                return buf.toString()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    /**
     * 获取外网IP地址
     *
     * @param context 上下文对象
     * @return 外网地址
     */
    fun generateIPAddress(context: Context): String {
        val info = (context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (info != null && info.isConnected) {
            if (info.type == ConnectivityManager.TYPE_MOBILE) { //当前使用2G/3G/4G网络
                try {
//                    Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    val en = NetworkInterface.getNetworkInterfaces()
                    while (en.hasMoreElements()) {
                        val intf = en.nextElement()
                        val enumIpAddr = intf.inetAddresses
                        while (enumIpAddr.hasMoreElements()) {
                            val inetAddress = enumIpAddr.nextElement()
                            if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                return inetAddress.getHostAddress()
                            }
                        }
                    }
                } catch (e: SocketException) {
                    e.printStackTrace()
                }
            } else if (info.type == ConnectivityManager.TYPE_WIFI) { //当前使用无线网络
                val wifiManager = context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                return intIP2StringIP(wifiInfo.ipAddress)
            }
        }
        return ""
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return ip地址
     */
    private fun intIP2StringIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }

    /**
     * 判断网络是否已经连接
     *
     * @param context 上下文对象
     * @return 连接状态
     */
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfos = cm.allNetworkInfo
        if (networkInfos != null) {
            for (ni in networkInfos) {
                if (ni.isConnected) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * WiFi连接状态
     *
     * @param context 上下文对象
     * @return 连接状态
     */
    fun isWifiConnected(context: Context): Boolean {
        val connecManager = context.applicationContext.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: NetworkInfo? = null
        try {
            networkInfo = connecManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        } catch (ex: Exception) {
        }
        return networkInfo != null && networkInfo.isConnected
    }

    /**
     * 判断当前联网类型，是 WiFi, 2G 还是 3G
     *
     * @param context 上下文对象
     * @return 当前网络类型
     */
    fun getConnectionType(context: Context): Byte {
        if (!isConnected(context)) {
            return TYPE_DISCONNECT
        }
        if (isWifiConnected(context)) {
            return TYPE_WIFI
        }
        val telephonyManager = context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val networkType = telephonyManager.networkType
        return when (networkType) {
            NETWORK_TYPE_GPRS, NETWORK_TYPE_GSM, NETWORK_TYPE_EDGE, NETWORK_TYPE_CDMA, NETWORK_TYPE_1XRTT, NETWORK_TYPE_IDEN -> TYPE_2G
            NETWORK_TYPE_UMTS, NETWORK_TYPE_EVDO_0, NETWORK_TYPE_EVDO_A, NETWORK_TYPE_HSDPA, NETWORK_TYPE_HSUPA, NETWORK_TYPE_HSPA, NETWORK_TYPE_EVDO_B, NETWORK_TYPE_EHRPD, NETWORK_TYPE_HSPAP -> TYPE_3G
            NETWORK_TYPE_LTE -> TYPE_4G
            else -> TYPE_UNKNOWN
        }
    }

    /**
     * 获取当前网络状态
     * @param context 上下文对象
     * @return 网络名称
     */
    fun getNetTypeString(context: Context): String {
        var netType = "unknown"
        val connectionType = NetUtilJava.getConnectionType(context)
        if (connectionType == NetUtilJava.TYPE_2G) {
            netType = "2g"
        } else if (connectionType == NetUtilJava.TYPE_3G) {
            netType = "3g"
        } else if (connectionType == NetUtilJava.TYPE_4G) {
            netType = "4g"
        } else if (connectionType == NetUtilJava.TYPE_WIFI) {
            netType = "wifi"
        }
        return netType
    }

    /**
     * 获取当前网络状态
     * @param context context
     * @return 数字
     */
    fun getNetTypeInt(context: Context): Int {
        var netType = 99
        val connectionType = NetUtilJava.getConnectionType(context)
        netType = if (connectionType == NetUtilJava.TYPE_2G) {
            1
        } else if (connectionType == NetUtilJava.TYPE_3G) {
            2
        } else if (connectionType == NetUtilJava.TYPE_4G) {
            3
        } else if (connectionType == NetUtilJava.TYPE_WIFI) {
            4
        } else {
            5
        }
        return netType
    }
}
