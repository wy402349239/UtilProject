package com.dsj.util

import android.content.Context

object AppUtil {

    /**
     * 获取当前APP版本名称
     * @param context 上下文对象
     * @return APP版本名称
     */
    fun getVersionName(context: Context): String {
        var verName: String
        try {
            verName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            verName = ""
        }
        return verName
    }

    /**
     * 获取版本号
     */
    fun getAppVersionCode(context: Context): Int {
        var verName: Int
        try {
            verName = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (e: Exception) {
            verName = 0
        }
        return verName
    }

    /**
     * 判断是否安装某个包名的应用,packageBame 包名
     */
    fun installed(packageBame: String, context: Context): Boolean {
        var result: Boolean
        try {
            val packageInfo = context.packageManager.getPackageInfo(packageBame, 0)
            result = packageInfo != null
        } catch (e: Exception) {
            result = false
        }
        return result
    }

    /**
     * 打开(跳转)指定包名应用
     */
    fun openApp(packageBame: String, context: Context) {
        val launchIntentForPackage = context.packageManager.getLaunchIntentForPackage(packageBame)
        context.startActivity(launchIntentForPackage)
    }

    /**
     * 跳转应用市场
     */
    fun openMarket(context: Context) {
        val markers = arrayListOf<String>()
        markers.add("com.huawei.appmarket")
        markers.add("com.tencent.android.qqdownloader")
        markers.add("com.xiaomi.market")
        markers.add("com.oppo.market")
        markers.add("com.bbk.appstore")
        for (marker in markers) {
            try {
                if (installed(marker, context)) {
                    openApp(marker, context)
                    break
                }
            } catch (e: Exception) {
                continue
            }
        }
    }

    //Utils installApp
}