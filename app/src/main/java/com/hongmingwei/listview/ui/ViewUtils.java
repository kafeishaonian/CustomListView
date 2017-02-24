package com.hongmingwei.listview.ui;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hongmingwei on 2016/9/15
 */
public class ViewUtils {

    /**
     * 测量View大小
     * @param child
     */
    public static void masureView(View child){
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null){
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, params.width);
        int lpHeight = params.height;
        int childHeightSpec;
        if (lpHeight > 0){
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }


}
