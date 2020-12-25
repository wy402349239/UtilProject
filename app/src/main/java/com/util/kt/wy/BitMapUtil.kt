package com.util.kt.wy

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream

/**
 * @Description: bitmap相关工具类
 * @Author: jx_wy
 * @Date: 2020/12/24 7:57 PM
 *
 * Observable.create(new ObservableOnSubscribe<Object>() {
 *      @Override
 *      public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
 *          String aPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Screenshots/1.jpg";
 *          String bPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Screenshots/2.jpg";
 *          Bitmap aB = BitmapFactory.decodeFile(aPath);
 *          Bitmap bB = BitmapFactory.decodeFile(bPath);
 *          BitMapUtil mapUtil = new BitMapUtil();
 *          Bitmap c = mapUtil.comPoseBitmap(aB, bB, BitMapUtil.BitmapLocal.CT);
 *          mapUtil.saveBitmap(c, Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/", "d.jpg");
 *      }
 *  }).subscribeOn(Schedulers.io()).subscribe();
 */
class BitMapUtil {

    /**
     * 检查读写权限
     */
    fun checkPermission(ctx: Context): Boolean {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    /**
     * 两个bitmap合成
     * local：位置
     */
    fun comPoseBitmap(m1: Bitmap, m2: Bitmap, local: BitmapLocal): Bitmap {
        val m3: Bitmap
        if (m2.width > m1.width || m2.height > m1.height) {
            //尺寸太大  缩放
            val w: Float = m2.width * 1.0F / m1.width
            val h: Float = m2.height * 1.0F / m1.height
            val scale: Float = Math.max(w, h)
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            m3 = Bitmap.createBitmap(m2, 0, 0, m2.width, m2.height, matrix, true)
        } else {
            m3 = m2
        }
        val result: Bitmap = Bitmap.createBitmap(m1.width, m1.height, m1.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(m1, 0F, 0F, null)
        if (local == BitmapLocal.RT) {
            canvas.drawBitmap(m3, (m1.width - m3.width).toFloat(), 0F, null)
        } else if (local == BitmapLocal.LB) {
            canvas.drawBitmap(m3, 0F, (m1.height - m3.width).toFloat(), null)
        } else if (local == BitmapLocal.RB) {
            canvas.drawBitmap(m3, (m1.width - m3.width).toFloat(), (m1.height - m3.width).toFloat(), null)
        } else if (local == BitmapLocal.CT) {
            val l = (m1.width - m3.width).toFloat() / 2
            val t = (m1.height - m3.height).toFloat() / 2
            canvas.drawBitmap(m3, l, t, null)
        } else {
            canvas.drawBitmap(m3, 0F, 0F, null)
        }
        return result
    }

    fun saveBitmap(bitmap: Bitmap, parent: String, name: String) {
        try {
            val parentFile = File(parent)
            if (!parentFile.exists() || parentFile.isFile) {
                parentFile.mkdirs()
            }
            val file = File(parentFile, name)
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 左上，右上，左下，右下，居中
     */
    enum class BitmapLocal {
        LT, RT, LB, RB, CT
    }
}