package com.dsj.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.view.View
import com.utilproject.wy.SystemPropertiesProxy

object StatuBarUtil {

    /**
     * 获取状态栏高度
     */
    fun getStateBarHeight(context: Context): Int {
        var result: Int = 0
        var res = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (res > 0) {
            result = context.resources.getDimensionPixelOffset(res)
        }
        return result
    }

    /**
     * 获取导航栏高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        var result: Int = 0
        var res = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (res > 0) {
            result = context.resources.getDimensionPixelOffset(res)
        }
        return result
    }

    /**
     * 官方提供的判断方法，当前设备是否流海屏，只有在28及以上才有效
     */
    fun hasNotchP(act: Activity): Boolean {
        if (act.isFinishing) {
            return false
        }
        if (Build.VERSION.SDK_INT >= 28) {
            val decorView = act.window.decorView
            val rootWindowInsets = decorView.rootWindowInsets
            if (rootWindowInsets != null) {
                val displayCutout = rootWindowInsets.displayCutout
                if (displayCutout != null) {
                    val boundingRects = displayCutout.boundingRects
                    if (boundingRects != null && !boundingRects.isEmpty()) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 判断华为流海屏
     */
    fun hasNotchAtHuawei(context: Context): Boolean {
        var result = false
        try {
            val classLoader = context.classLoader
            val HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val get = HwNotchSizeUtil.getMethod("hasNotchInScreen")
            result = get.invoke(HwNotchSizeUtil) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 判断小米流海屏
     */
    fun hasNotchAtXiaoMi(act: Activity?): Boolean {
        val s = SystemPropertiesProxy.get(act, "ro.miui.notch", "0")
        return TextUtils.equals(s, "1")
    }

    /**
     * 判断OPPO流海屏
     */
    fun hasNotchAtOPPO(context: Context): Boolean {
        return context.packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
    }

    /**
     * 判断vivo流海屏
     */
    @SuppressLint("PrivateApi")
    fun hasNotchAtVivo(context: Context): Boolean {
        val VIVO_NOTCH = 0x00000020 //是否有刘海
        var ret = false
        try {
            val classLoader = context.classLoader
            val FtFeature = classLoader.loadClass("android.util.FtFeature")
            val method = FtFeature.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
            ret = method.invoke(FtFeature, VIVO_NOTCH) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ret
    }

    /**
     * 判断流海屏
     */
    fun hasNotch(act: Activity): Boolean {
        if (act.isFinishing) {
            return false
        }
        return hasNotchP(act) || hasNotchAtHuawei(act) ||
                hasNotchAtXiaoMi(act) || hasNotchAtOPPO(act) || hasNotchAtVivo(act)
    }

    /**
     * 设置状态栏背景，res：资源ID，defaultColor：默认颜色
     */
    fun setStatuBarResource(act: Activity, res: Int, defaultColor: Int) {
        if (act.isFinishing) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            act.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            act.window.statusBarColor = defaultColor
            act.window.decorView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                val statuBar = act.window.findViewById<View>(act.resources.getIdentifier("statusBarBackground", "id", "android"))
                if (statuBar != null) {
                    statuBar.setBackgroundResource(0)
                    statuBar.setBackgroundColor(Color.TRANSPARENT)
                    statuBar.setBackgroundResource(res)
                }
            }
        }
    }

    /**
     * 设置状态栏颜色
     */
    fun setStatuBarColor(act: Activity, color: Int) {
        if (act.isFinishing) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            act.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            act.window.statusBarColor = color
            act.window.decorView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                val statuBar = act.window.findViewById<View>(act.resources.getIdentifier("statusBarBackground", "id", "android"))
                if (statuBar != null) {
                    statuBar.setBackgroundResource(0)
                    statuBar.setBackgroundColor(color)
                }
            }
        }
    }

    /**
     * dip转为 px
     */
    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    /**
     * px 转为 dip
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
}