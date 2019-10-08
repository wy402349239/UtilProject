package com.utilproject.wy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * created by wangyu on 2019/4/30
 * description :
 */
public class NetUtil {

    // 下面是联网类型的定义
    public static final byte TYPE_2G = 1;
    public static final byte TYPE_3G = 2;
    public static final byte TYPE_4G = 3;
    public static final byte TYPE_WIFI = 4;
    public static final byte TYPE_UNKNOWN = 5;
    public static final byte TYPE_DISCONNECT = TYPE_UNKNOWN;


    /**
     * Network type is unknown
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /**
     * Current network is GPRS
     */
    public static final int NETWORK_TYPE_GPRS = 1;
    /**
     * Current network is EDGE
     */
    public static final int NETWORK_TYPE_EDGE = 2;
    /**
     * Current network is UMTS
     */
    public static final int NETWORK_TYPE_UMTS = 3;
    /**
     * Current network is CDMA: Either IS95A or IS95B
     */
    public static final int NETWORK_TYPE_CDMA = 4;
    /**
     * Current network is EVDO revision 0
     */
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /**
     * Current network is EVDO revision A
     */
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /**
     * Current network is 1xRTT
     */
    public static final int NETWORK_TYPE_1XRTT = 7;
    /**
     * Current network is HSDPA
     */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /**
     * Current network is HSUPA
     */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /**
     * Current network is HSPA
     */
    public static final int NETWORK_TYPE_HSPA = 10;
    /**
     * Current network is iDen
     */
    public static final int NETWORK_TYPE_IDEN = 11;
    /**
     * Current network is EVDO revision B
     */
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /**
     * Current network is LTE
     */
    public static final int NETWORK_TYPE_LTE = 13;
    /**
     * Current network is eHRPD
     */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /**
     * Current network is HSPA+
     */
    public static final int NETWORK_TYPE_HSPAP = 15;
    /**
     * Current network is GSM
     */
    public static final int NETWORK_TYPE_GSM = 16;

    /**
     * 获取 WiFi mac地址的新方法
     *
     * @param context 上下文对象
     * @return WiFi Mac地址
     */
    public static String getMacAddrNew(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            String mac = getWifiMacAddressForAndroid23();
            if (TextUtils.isEmpty(mac)) {
                mac = "02:00:00:00:00:00";
            }
            return mac;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String mac = tryGetMAC(wifiManager);

        if (!TextUtils.isEmpty(mac)) {
            return mac;
        }
        for (int index = 0; index < 3; index++) {
            if (index != 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mac = tryGetMAC(wifiManager);
            if (!TextUtils.isEmpty(mac)) {
                break;
            }
        }
        return mac;
    }

    /**
     * 获取WiFi Mac地址
     *
     * @param manager manager
     * @return Mac地址
     */
    public static String tryGetMAC(WifiManager manager) {
        try {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo == null || TextUtils.isEmpty(wifiInfo.getMacAddress())) {
                return null;
            }
            String mac = wifiInfo.getMacAddress();
            return mac;
        } catch (NullPointerException e) {
            //联想手机在4.*.*上可能出现NPE，这里只能加一个保护
            return null;
        }
    }

    /**
     * Android 6.0上增强了数据保护，取到的MAC地址是02:00:00:00:00:00
     * http://stackoverflow.com/questions/31329733/how-to-get-the-missing-wifi-mac-address-on-android-m-preview
     */
    public static String getWifiMacAddressForAndroid23() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    /**
     * 获取外网IP地址
     *
     * @param context 上下文对象
     * @return 外网地址
     */
    public static String generateIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
//                    Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return ip地址
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 判断网络是否已经连接
     *
     * @param context 上下文对象
     * @return 连接状态
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (ni.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断当前是否计费
     *
     * @param context 上下文对象
     * @return 是否计费
     */
    public synchronized static boolean isNetworkMetered(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ConnectivityManager sConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (sConnectivityManager != null) {
                return sConnectivityManager.isActiveNetworkMetered();
            }
        }
        return false;
    }

    /**
     * WiFi 是否已经连接
     *
     * @param context 上下文对象
     * @return 连接状态
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connecManager = (ConnectivityManager) context.getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        try {
            networkInfo = connecManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch (Exception ex) {
        }
        if (networkInfo != null) {
            return networkInfo.isConnected();
        } else {
            return false;
        }
    }

    /**
     * 判断当前联网类型，是 WiFi, 2G 还是 3G
     *
     * @param context 上下文对象
     * @return 当前网络类型
     */
    public static byte getConnectionType(Context context) {
        if (context == null) {
            return TYPE_UNKNOWN;
        }

        if (!isConnected(context)) {
            return TYPE_DISCONNECT;
        }

        if (isWifiConnected(context)) {
            return TYPE_WIFI;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case NETWORK_TYPE_GPRS:
                case NETWORK_TYPE_GSM:
                case NETWORK_TYPE_EDGE:
                case NETWORK_TYPE_CDMA:
                case NETWORK_TYPE_1XRTT:
                case NETWORK_TYPE_IDEN:
                    return TYPE_2G;
                case NETWORK_TYPE_UMTS:
                case NETWORK_TYPE_EVDO_0:
                case NETWORK_TYPE_EVDO_A:
                case NETWORK_TYPE_HSDPA:
                case NETWORK_TYPE_HSUPA:
                case NETWORK_TYPE_HSPA:
                case NETWORK_TYPE_EVDO_B:
                case NETWORK_TYPE_EHRPD:
                case NETWORK_TYPE_HSPAP:
                    return TYPE_3G;
                case NETWORK_TYPE_LTE:
                    return TYPE_4G;
                default:
                    return TYPE_UNKNOWN;
            }
        }

        return TYPE_UNKNOWN;
    }

    /**
     * 获取当前网络状态
     * @param context 上下文对象
     * @return 网络名称
     */
    public static String getNetTypeString(Context context) {
        String netType = "unknown";
        byte connectionType = NetUtil.getConnectionType(context);
        if (connectionType == NetUtil.TYPE_2G) {
            netType = "2g";
        } else if (connectionType == NetUtil.TYPE_3G) {
            netType = "3g";
        } else if (connectionType == NetUtil.TYPE_4G) {
            netType = "4g";
        } else if (connectionType == NetUtil.TYPE_WIFI) {
            netType = "wifi";
        }

        return netType;
    }

    /**
     * 获取当前网络状态
     * @param context context
     * @return 数字
     */
    public static int getNetTypeInt(Context context) {
        int netType = 99;
        byte connectionType = NetUtil.getConnectionType(context);
        if (connectionType == NetUtil.TYPE_2G) {
            netType = 1;
        } else if (connectionType == NetUtil.TYPE_3G) {
            netType = 2;
        } else if (connectionType == NetUtil.TYPE_4G) {
            netType = 3;
        } else if (connectionType == NetUtil.TYPE_WIFI) {
            netType = 4;
        } else {
            netType = 5;
        }
        return netType;
    }
}
