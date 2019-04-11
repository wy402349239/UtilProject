package com.utilproject.wy;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * created by wangyu on 2019/4/11
 * description : SharedPreferences工具类
 */
public class SpUtil {

    private SharedPreferences mSp = null;
    private static final String mName = "GitSp";
    private static SpUtil mUtil = null;

    public SpUtil(Context context) {
        mSp = context.getSharedPreferences(mName, Context.MODE_PRIVATE);
    }

    public static SpUtil getInstance(Context context) {
        if (mUtil == null) {
            synchronized (SpUtil.class) {
                if (mUtil == null) {
                    mUtil = new SpUtil(context);
                }
            }
        }
        return mUtil;
    }

    public void putValue(String key, Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof String) {
            mSp.edit().putString(key, obj.toString()).apply();
        }
        if (obj instanceof Integer) {
            mSp.edit().putInt(key, Integer.parseInt(obj.toString())).apply();
        }
        if (obj instanceof Boolean) {
            mSp.edit().putBoolean(key, Boolean.parseBoolean(obj.toString())).apply();
        }
        if (obj instanceof Float) {
            mSp.edit().putFloat(key, Float.parseFloat(obj.toString())).apply();
        }
        if (obj instanceof Long) {
            mSp.edit().putLong(key, Long.parseLong(obj.toString())).apply();
        }
    }

    public String getStr(String key, String defaultStr) {
        return mSp.getString(key, defaultStr);
    }

    public int getInt(String key, int defaultInt) {
        return mSp.getInt(key, defaultInt);
    }

    public boolean getBool(String key, boolean defaultBoolean) {
        return mSp.getBoolean(key, defaultBoolean);
    }

    public long getLong(String key, long defaultLong) {
        return mSp.getLong(key, defaultLong);
    }

    public float getFloat(String key, float defaultFloat) {
        return mSp.getFloat(key, defaultFloat);
    }
}
