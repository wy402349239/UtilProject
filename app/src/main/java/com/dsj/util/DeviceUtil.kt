package com.dsj.util

import android.app.Activity
import android.graphics.Point
import android.view.View

object DeviceUtil {

    /**
     * 导航栏是否展示
     */
    fun NavigationBarIsShow(act: Activity): Boolean {
        if (act.isFinishing)
            return false
        val defaultDisplay = act.windowManager.defaultDisplay
        val pointApp = Point()
        val pointScreen = Point()
        defaultDisplay.getSize(pointApp)
        defaultDisplay.getRealSize(pointScreen)
        return pointApp.y != pointScreen.y
    }

    /**
     * 展示或者隐藏导航栏
     */
    fun showNavigationView(act: Activity, show: Boolean) {
        if (act.isFinishing) {
            return
        }
        val decorView = act.window.decorView
        if (show) {//全屏 透明状态栏  展示导航栏
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_VISIBLE
        } else {//隐藏导航栏
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }
}