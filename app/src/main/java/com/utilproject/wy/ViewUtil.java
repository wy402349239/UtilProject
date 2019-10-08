package com.utilproject.wy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;

/**
 * created by wangyu on 2019-10-08
 * description :
 */
public class ViewUtil {

    /**
     * 获取当前焦点view
     *
     * @param act act
     * @return view
     */
    public static View getFocusView(Activity act) {
        if (act == null || act.isFinishing()) {
            return null;
        }
        View decorView = act.getWindow().getDecorView();
        return decorView.findFocus();
    }

    /**
     * 对某一个view截图
     *
     * @param v view
     * @return bitmap
     */
    public static Bitmap getViewBp(View v) {
        if (null == v) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();

        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        Bitmap b = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return b;
    }
}
