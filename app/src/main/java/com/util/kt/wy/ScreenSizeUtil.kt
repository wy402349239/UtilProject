package com.dsj.util

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager

object ScreenSizeUtil {

    /**
     * 获取屏幕尺寸(包含状态栏，导航栏)
     */
    fun getFullScreenSize(context: Context): IntArray {
        val intArray = IntArray(2)
        val systemServiceWm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        val defaultDisplay = systemServiceWm.defaultDisplay
        var widthPixels: Int
        var heightPixels: Int
        if (isMiDevice()) {
            defaultDisplay.getMetrics(displayMetrics)
            widthPixels = displayMetrics.widthPixels
            heightPixels = displayMetrics.heightPixels
            if (Settings.Global.getInt(context.contentResolver, "force_fsg_nav_bar", 0) != 0) {
                //已展示虚拟键
                val navigqation = StatuBarUtil.getNavigationBarHeight(context)
                if (widthPixels > heightPixels) {
                    widthPixels += navigqation
                } else {
                    heightPixels += navigqation
                }
            }
        } else {
            widthPixels = Display::class.java.getMethod("getRawWidth").invoke(defaultDisplay) as Int
            heightPixels = Display::class.java.getMethod("getRawHeight").invoke(defaultDisplay) as Int
        }
        intArray[0] = Math.min(widthPixels, heightPixels)
        intArray[1] = Math.max(widthPixels, heightPixels)
        return intArray
    }

    /**
     * 获取全屏宽度
     */
    fun getFullWidth(context: Context): Int {
        return getFullScreenSize(context)[0]
    }

    /**
     * 获取全屏高度
     */
    fun getFullHeight(context: Context): Int {
        return getFullScreenSize(context)[1]
    }

    fun isMiDevice(): Boolean {
        val manufacturer = Build.MANUFACTURER
        return "Xiaomi".equals(manufacturer, ignoreCase = true)
    }

}