package com.utilproject.wy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * list工具类
 *
 * @author jx_wy
 */
public class ListUtil {

    /**
     * 深度复制某个集合
     *
     * @param src 源集合
     * @param <T>
     * @return 结果
     */
    public static <T> List<T> deepCopy(List<T> src) {
        if (src == null || src.isEmpty()) {
            return new ArrayList<T>();
        }
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            List<T> dest = (List<T>) in.readObject();
            return dest;
        } catch (Exception e) {
            return new ArrayList<T>();
        }
    }
}
