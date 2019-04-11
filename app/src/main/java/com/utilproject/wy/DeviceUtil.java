package com.utilproject.wy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowInsets;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * @author jx_wy
 */
public class DeviceUtil {

    /**
     * 获取屏幕宽度
     *
     * @param context 上下文
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        if (context == null) {
            return 0;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕高度
     *
     * @param context 上下文
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        if (context == null) {
            return 0;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * 导航栏是否显示
     *
     * @param act
     * @return
     */
    public static boolean NavigationBarIsShow(Activity act) {
        if (act == null || act.isFinishing()) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Display display = act.getWindowManager().getDefaultDisplay();
            Point screen = new Point();
            Point app = new Point();
            display.getSize(screen);
            display.getRealSize(app);
            return app.y != screen.y;
        } else {
            boolean menu = ViewConfiguration.get(act).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return menu && back;
        }
    }

    /**
     * 显示或者隐藏导航栏
     *
     * @param act
     * @param show
     */
    public static void hideShowNavigationBar(Activity act, boolean show) {
        if (act == null || act.isFinishing()) {
            return;
        }
        View decorView = act.getWindow().getDecorView();
        int uiOptions;
        if (show) {
            uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE//重新透明状态栏
                    | View.SYSTEM_UI_FLAG_VISIBLE;//显示导航栏，
        } else {
            uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;//隐藏导航栏
        }
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * 获取路由器Mac地址
     *
     * @param ctx
     * @return
     */
    public static String getWifiMac(Context ctx) {
        String result = "";
        if (ctx == null) {
            return getMacAddr();
        }
        android.net.wifi.WifiManager manager = (android.net.wifi.WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager != null) {
            WifiInfo connectionInfo = manager.getConnectionInfo();
            if (connectionInfo != null) {
                result = connectionInfo.getBSSID();
                if (TextUtils.isEmpty(result)) {
                    //未连接WiFi时，获取手机Mac地址
                    result = getMacAddr();
                }
            } else {
                result = getMacAddr();
            }
        }
        return result;
    }

    /**
     * 获取手机Mac地址
     *
     * @return
     */
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 判断是否为流海屏
     *
     * @param act 上下文
     * @return 是否为流海屏
     */
    public static boolean hasNotch(Activity act) {
        return hasNotchAtHuawei(act) || hasNotchAtXiaoMi(act) ||
                hasNotchAtOPPO(act) || hasNotchAtVivo(act) || hasNotchP(act);
    }

    /**
     * android P 是否流海屏
     *
     * @param act
     * @return
     */
    public static boolean hasNotchP(Activity act) {
        if (Build.VERSION.SDK_INT >= 28) {
            View decorView = act.getWindow().getDecorView();
            WindowInsets rootWindowInsets = decorView.getRootWindowInsets();
            if (rootWindowInsets != null) {
                DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
                if (displayCutout != null) {
                    List<Rect> boundingRects = displayCutout.getBoundingRects();
                    if (boundingRects != null && !boundingRects.isEmpty()) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * 华为是否有流海屏
     *
     * @param context context
     * @return 是否有流海屏
     */
    public static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {

        } finally {
            return ret;
        }
    }

    /**
     * 小米是否流海屏
     *
     * @param act
     * @return
     */
    public static boolean hasNotchAtXiaoMi(Activity act) {
        String s = SystemPropertiesProxy.get(act, "ro.miui.notch", "0");
        if (s.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * oppo是否有流海屏
     *
     * @param context context
     * @return 是否有流海屏
     */
    public static boolean hasNotchAtOPPO(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    /**
     * vivo是否有流海屏
     *
     * @param context context
     * @return 是否有流海屏
     */
    public static boolean hasNotchAtVivo(Context context) {
        final int VIVO_NOTCH = 0x00000020;//是否有刘海
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (Exception e) {

        } finally {
            return ret;
        }
    }

    /**
     * 修改状态栏背景
     *
     * @param act 上下文
     * @param id  资源ID
     */
    public static void setStatuResouce(final Activity act, final int id) {
        if (act == null || act.isFinishing()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = act.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            act.getWindow().setStatusBarColor(0xFF24CCFF);//必须设置颜色,不然下面的代码无效
            act.getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    int identifier = act.getResources().getIdentifier("statusBarBackground", "id", "android");
                    View statuBar = act.getWindow().findViewById(identifier);
                    if (statuBar != null) {
                        statuBar.setBackgroundColor(Color.TRANSPARENT);
                        statuBar.setBackgroundResource(id);
                    }
                }
            });
        }
    }

    /**
     * 修改状态栏背景
     *
     * @param act   上下文
     * @param color 颜色
     */
    public static void setStatuColor(final Activity act, final int color) {
        if (act == null || act.isFinishing()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = act.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            act.getWindow().setStatusBarColor(0xFF24CCFF);//必须设置颜色,不然下面的代码无效
            act.getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    int identifier = act.getResources().getIdentifier("statusBarBackground", "id", "android");
                    View statuBar = act.getWindow().findViewById(identifier);
                    if (statuBar != null) {
                        statuBar.setBackgroundResource(0);
                        statuBar.setBackgroundColor(color);
                    }
                }
            });
        }
    }

    /**
     * 获取屏幕原始尺寸高度，包括虚拟按键NavigationBar
     * @param context
     * @return 原始尺寸高度
     */
    public static int getFullScreenHeight(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * dip转为 px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px 转为 dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context context
     * @param spValue value
     * @return parse value
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static String getAndroidId(Context context) {
        String androidId = "123a";
        try {
            if (!TextUtils.isEmpty(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID))) {
                androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (Exception e) {

        }
        return androidId;
    }

    /**
     * 获取状态栏高度
     * @param context 上下文对象
     * @return 状态栏高度
     */
    public static int getStateBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取导航栏高度
     *
     * @param context 上下文
     * @return 导航栏高度
     */
    public static int getNavigationHeight(Activity context) {
        int result = 0;
        int resourceId = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }
}
