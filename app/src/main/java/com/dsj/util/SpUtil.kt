package com.dsj.util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Description: this is sp存储工具类
 * @Author: jx_wy
 * @Date: 2020/11/30 3:20 PM
 */
class SpUtil(context: Context, name: String) {

    var mSp: SharedPreferences? = null

    init {
        if (TextUtils.isEmpty(name)) {
            mSp = context.getSharedPreferences(Default_Sp_Name, Context.MODE_PRIVATE)
            mSpName = Default_Sp_Name
        } else {
            mSp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            mSpName = name
        }
    }

    companion object {

        val Default_Sp_Name = "Default_Sp_Dsj";

        var mUtil: SpUtil? = null
        var mSpName: String? = null
        fun getInstance(context: Context, name: String) = mUtil ?: synchronized(this) {
            mUtil ?: SpUtil(context, name).also { mUtil = it }
        }
    }

    fun putValue(key: String, value: Any) {
        if (mSp == null || TextUtils.isEmpty(key)) {
            return
        }
        val toString = value.toString()
        if (value is String) {
            mSp!!.edit().putString(key, toString).apply()
        } else if (value is Int) {
            mSp!!.edit().putInt(key, toString.toInt()).apply()
        } else if (value is Boolean) {
            val boolean = java.lang.Boolean.parseBoolean(toString)
            mSp!!.edit().putBoolean(key, boolean).apply()
        } else if (value is Float) {
            mSp!!.edit().putFloat(key, value.toFloat()).apply()
        } else if (value is Long) {
            mSp!!.edit().putLong(key, value.toLong()).apply()
        }
    }

    fun getString(key: String): String? {
        if (mSp == null || TextUtils.isEmpty(key)) {
            return ""
        }
        return mSp!!.getString(key, null)
    }

    fun getInt(key: String): Int {
        if (mSp == null || TextUtils.isEmpty(key)) {
            return 0
        }
        return mSp!!.getInt(key, 0)
    }

    fun getBoolean(key: String): Boolean {
        if (mSp == null || TextUtils.isEmpty(key)) {
            return false
        }
        return mSp!!.getBoolean(key, false)
    }

    fun getFloat(key: String): Float {
        if (mSp == null || TextUtils.isEmpty(key)) {
            return 0f
        }
        return mSp!!.getFloat(key, 0f)
    }

    fun getLong(key: String): Long {
        if (mSp == null || TextUtils.isEmpty(key)) {
            return 0
        }
        return mSp!!.getLong(key, 0)
    }

    /**
     * 根据一个开头的key，存储两对键值，第一对是为了保存第二队的key，第二对才是需要的值
     * 主要是在某些根据日期保存的场景使用
     *
     * keyStart     开头的key
     *
     * value        ...
     *
     * delete       是否删除原有的值
     *
     */
    fun putDate(keyStart: String, value: Any, delete: Boolean) {
        if (mSp == null || TextUtils.isEmpty(keyStart)) {
            return
        }
        //当前日期
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())
        //旧有的key，以sp存储，key值为keyStart，value为旧有的key
        val oldKey = getString(keyStart)
        //新的key
        val newKey = keyStart + date
        if (!TextUtils.equals(oldKey, newKey) && delete) {
            //日期不一样导致key不同，删除旧key和对应的值，保证对应的数据只有两条，不会无限增长
            removeKey(oldKey)
        }
        //存放新的
        putValue(keyStart, newKey)
        putValue(newKey, value)
    }

    fun removeKey(key: String?) {
        if (mSp == null || TextUtils.isEmpty(key)) {
            return
        }
        if (mSp!!.contains(key)) {
            mSp!!.edit().remove(key).apply()
        }
    }

    fun release() {
        mUtil = null
        mSp = null
        mSpName = null
    }
}