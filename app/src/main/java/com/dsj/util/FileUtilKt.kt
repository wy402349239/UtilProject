package com.dsj.util

import android.content.Context
import android.text.TextUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.regex.Pattern

object FileUtilKt {

    fun downloadFile(url: String, context: Context) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        val fileName = getFileName(url)
        val cachePath: String = context.filesDir.absolutePath + File.separator + fileName
        val cacheFile = File(cachePath)
        if (cacheFile.exists() && cacheFile.isFile && cacheFile.length() > 0) {
            //文件已存在，不需要再下载
            return
        }
        val launch = GlobalScope.launch {
            val localPath = context.filesDir.absolutePath + "/temp_" + fileName
            val file = File(localPath)
            val openConnection = URL(url).openConnection()
            openConnection.connectTimeout = 10000
            openConnection.readTimeout = 10000
            var inputStream = openConnection.getInputStream()
            if (file.exists())
                file.delete()
            val fileOutputStream = FileOutputStream(file)
            var numread = -1
            var buf = ByteArray(4096)
            while (`inputStream`.read(buf).also { numread = it } > 0) {
                fileOutputStream.write(buf, 0, numread)
            }
            fileOutputStream.close()
            inputStream.close()
            if (cacheFile.exists())
                cacheFile.delete()
            file.renameTo(cacheFile)
        }
    }

    fun getFileName(url: String): String {
        try {
            val suffixes = "avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|pdf|rar|zip|docx|doc"
            val matcher = Pattern.compile("[\\w]+[\\.](" + suffixes + ")").matcher(url)
            while (matcher.find()) {
                return matcher.group().split(".")[0]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return url
    }
}