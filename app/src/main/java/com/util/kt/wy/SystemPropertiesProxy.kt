package com.util.kt.wy

import android.content.Context
import dalvik.system.DexFile
import java.io.File

/**
 * @Description:
 * @Author: jx_wy
 * @Date: 2020/12/25 2:19 PM
 */
object SystemPropertiesProxy {

    /**
     * 根据给定Key获取值.
     *
     * @return 如果不存在该key则返回空字符串
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    @Throws(IllegalArgumentException::class)
    operator fun get(context: Context, key: String): String {
        var ret = ""
        try {
            val cl = context.classLoader
            val SystemProperties = cl.loadClass("android.os.SystemProperties")
            //参数类型
            val paramTypes: Array<Class<*>?> = arrayOfNulls(1)
            paramTypes[0] = String::class.java
            val get = SystemProperties.getMethod("get", *paramTypes)
            //参数
            val params = arrayOfNulls<Any>(1)
            params[0] = key
            ret = get.invoke(SystemProperties, *params) as String
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            ret = ""
            //TODO
        }
        return ret
    }

    /**
     * 根据Key获取值.
     *
     * @return 如果key不存在, 并且如果def不为空则返回def否则返回空字符串
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    @Throws(IllegalArgumentException::class)
    operator fun get(
        context: Context,
        key: String,
        def: String
    ): String {
        var ret = def
        try {
            val cl = context.classLoader
            val SystemProperties = cl.loadClass("android.os.SystemProperties")
            //参数类型
            val paramTypes: Array<Class<*>?> = arrayOfNulls(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = String::class.java
            val get = SystemProperties.getMethod("get", *paramTypes)
            //参数
            val params = arrayOfNulls<Any>(2)
            params[0] = key
            params[1] = def
            ret = get.invoke(SystemProperties, *params) as String
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            ret = def
            //TODO
        }
        return ret
    }

    /**
     * 根据给定的key返回int类型值.
     *
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个int类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    @Throws(IllegalArgumentException::class)
    fun getInt(context: Context, key: String, def: Int): Int {
        var ret = def
        try {
            val cl = context.classLoader
            val SystemProperties = cl.loadClass("android.os.SystemProperties")
            //参数类型
            val paramTypes = arrayOfNulls<Class<*>?>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = Int::class.javaPrimitiveType
            val getInt = SystemProperties.getMethod("getInt", *paramTypes)
            //参数
            val params = arrayOfNulls<Any>(2)
            params[0] = key
            params[1] = def
            ret = getInt.invoke(SystemProperties, *params) as Int
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            ret = def
            //TODO
        }
        return ret
    }

    /**
     * 根据给定的key返回long类型值.
     *
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个long类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    @Throws(IllegalArgumentException::class)
    fun getLong(
        context: Context,
        key: String,
        def: Long
    ): Long {
        var ret = def
        try {
            val cl = context.classLoader
            val SystemProperties = cl.loadClass("android.os.SystemProperties")
            //参数类型
            val paramTypes = arrayOfNulls<Class<*>?>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = Long::class.javaPrimitiveType
            val getLong =
                SystemProperties.getMethod("getLong", *paramTypes)
            //参数
            val params = arrayOfNulls<Any>(2)
            params[0] = key
            params[1] = def
            ret = getLong.invoke(SystemProperties, *params) as Long
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            ret = def
            //TODO
        }
        return ret
    }

    /**
     * 根据给定的key返回boolean类型值.
     * 如果值为 'n', 'no', '0', 'false' or 'off' 返回false.
     * 如果值为'y', 'yes', '1', 'true' or 'on' 返回true.
     * 如果key不存在, 或者是其它的值, 则返回默认值.
     *
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个boolean类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    @Throws(IllegalArgumentException::class)
    fun getBoolean(
        context: Context,
        key: String,
        def: Boolean
    ): Boolean {
        var ret = def
        try {
            val cl = context.classLoader
            val SystemProperties = cl.loadClass("android.os.SystemProperties")
            //参数类型
            val paramTypes = arrayOfNulls<Class<*>?>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = Boolean::class.javaPrimitiveType
            val getBoolean =
                SystemProperties.getMethod("getBoolean", *paramTypes)
            //参数
            val params = arrayOfNulls<Any>(2)
            params[0] = key
            params[1] = def
            ret = getBoolean.invoke(SystemProperties, *params) as Boolean
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            ret = def
            //TODO
        }
        return ret
    }

    /**
     * 根据给定的key和值设置属性, 该方法需要特定的权限才能操作.
     *
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     * @throws IllegalArgumentException 如果value超过92个字符则抛出该异常
     */
    @Throws(IllegalArgumentException::class)
    operator fun set(
        context: Context,
        key: String,
        `val`: String
    ) {
        try {
            val df = DexFile(File("/system/app/Settings.apk"))
            val cl = context.classLoader
            val SystemProperties =
                Class.forName("android.os.SystemProperties")
            //参数类型
            val paramTypes: Array<Class<*>?> = arrayOfNulls(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = String::class.java
            val set = SystemProperties.getMethod("set", *paramTypes)
            //参数
            val params = arrayOfNulls<Any>(2)
            params[0] = key
            params[1] = `val`
            set.invoke(SystemProperties, *params)
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
//TODO
        }
    }
}