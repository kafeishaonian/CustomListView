package com.hongmingwei.listview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.hongmingwei.listview.R;

/**
 * 能自旋转的imageView
 * Created by hongmingwei on 2016/9/14
 */
public class RotateImageView extends ImageView {

    private static final String TAG = RotateImageView.class.getSimpleName();

    Animation animationRotate;
    private boolean mIsRotating = false;

    {
        animationRotate = AnimationUtils.loadAnimation(getContext(), R.anim.clockwise_rotate_animation);
        LinearInterpolator lir = new LinearInterpolator();
        animationRotate.setInterpolator(lir);
    }

    public RotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 开始旋转
     */
    public void startRotate(){
        Log.d(TAG, "startRotate");
        if (mIsRotating){
            return;
        }
        startAnimation(animationRotate);
        mIsRotating = true;
    }

    /**
     * 停止旋转
     */
    public void stopRotate(){
        Log.d(TAG, "stopRotate");
        if (!mIsRotating){
            return;
        }
        clearAnimation();
        mIsRotating = false;
    }

    /**
     * 是否正在旋转
     * @return
     */
    public boolean isRotating(){
        return mIsRotating;
    }

}
