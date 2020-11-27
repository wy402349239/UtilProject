package com.utilproject.wy;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * created by wangyu on 2019/4/29
 * description :
 */
public class AppMessageUtil {

    /**
     * 获取当前APP版本名称
     * @param context 上下文对象
     * @return APP版本名称
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

//    /**
//     * 获取当前APP版本号
//     * @param context 上下文对象
//     * @return APP版本号
//     */
//    public static String getAppVersionCode(Context context) {
//        String versionCode = "";
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
//            versionCode = String.valueOf(pi.versionCode);
//            if (TextUtils.isEmpty(versionCode)) {
//                return "";
//            }
//        } catch (Exception e) {
//            Log.e("VersionInfo", "Exception", e);
//        }
//        return versionCode;
//    }
}
