package com.hongmingwei.listview.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by hongmingwei on 2017/2/23 16:47
 */
public class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();

    public static Bitmap getBitmapFromRes(Context context, int resId) {
        Bitmap bm = null;
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            bm = BitmapFactory.decodeResource(context.getResources(), resId, opt);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "", e);
        }
        return bm;
    }
}
