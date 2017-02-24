package com.hongmingwei.listview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.hongmingwei.listview.R;

/**
 * 下拉刷新自定义圆圈
 * Created by hongmingwei on 2016/9/14
 */
public class PullDownCircleProgressBar extends View {

    private static final int DEFAULT_MAX_VALUE = 100; //默认进度条最大值
    private static final int DEFAULT_PAINT_WIDTH = 10; //默认画笔宽度
    private static final int DEFAULT_PAINT_COLOR = 0xffffcc00; //默认画笔颜色
    private static final boolean DEFAULT_FILL_MODE = true; //默认填充模式
    private static final int DEFAULT_INSIDE_VALUE = 0; //默认缩进距离

    private CircleAttribute circleAttribute;  //圆形进度条的基本属性

    private int mMaxProgress; //进度条最大值
    private int mMainCurProgress; //主进度条当前值
    private Drawable mBackgroundPicture; //背景图

    public PullDownCircleProgressBar(Context context) {
        super(context);
    }

    public PullDownCircleProgressBar(Context context, AttributeSet attrs){
        super(context, attrs);

        initDefaultParam();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        mMaxProgress = array.getInteger(R.styleable.CircleProgressBar_max, DEFAULT_MAX_VALUE); //获取进度条最大值
        boolean bFill = array.getBoolean(R.styleable.CircleProgressBar_fill, DEFAULT_FILL_MODE); //获取填充模式
        int paintWidth = array.getInt(R.styleable.CircleProgressBar_Paint_Width, DEFAULT_PAINT_WIDTH); //获取画笔宽度
        circleAttribute.setFill(bFill);
        if (bFill == false){
            circleAttribute.setPaintWidth(paintWidth);
        }

        int paintColor = array.getColor(R.styleable.CircleProgressBar_Paint_Color, DEFAULT_PAINT_COLOR); //获取画笔颜色
        circleAttribute.setPaintColor(paintColor);
        circleAttribute.mSidePaintInterval = array.getInt(R.styleable.CircleProgressBar_Inside_Interval,
                DEFAULT_INSIDE_VALUE); //圆环缩进距离
        array.recycle(); //一定要调用，否则会有问题
    }

    /**
     * 默认参数
     */
    private void initDefaultParam(){
        circleAttribute = new CircleAttribute();
        mMaxProgress = DEFAULT_MAX_VALUE;
        mMainCurProgress = 0;
    }

    /**
     * 设置视图大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        @SuppressWarnings("unused")
        int height = MeasureSpec.getSize(heightMeasureSpec);

        mBackgroundPicture = getBackground();
        if (mBackgroundPicture != null){
            width = mBackgroundPicture.getMinimumWidth();
            height = mBackgroundPicture.getMinimumHeight();
        }
        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(width, heightMeasureSpec));
    }

    public void onSizeChanged(int widht, int height, int oldw, int oldh){
        super.onSizeChanged(widht, height, oldw, oldh);
        circleAttribute.autoFix(widht, height);
    }

    public void  onDraw(Canvas canvas){
        super.onDraw(canvas);
        float rate = mMainCurProgress / mMaxProgress;
        float sweep = 360 * rate;
        canvas.drawArc(circleAttribute.mRoundOval, circleAttribute.mDrawPos, sweep,
                circleAttribute.mBRoundPaintsFill, circleAttribute.mMainPaints);
    }

    /**
     * 设置主进度值
     * @param progress
     */
    public synchronized void setMainProgress(int progress){
        mMainCurProgress = progress;
        if (mMainCurProgress < 0){
            mMainCurProgress = 0;
        }
        if (mMainCurProgress > mMaxProgress){
            mMainCurProgress = mMaxProgress;
        }
        invalidate();
    }

    public synchronized int getMainProgress(){
        return mMainCurProgress;
    }

    class CircleAttribute{
        public RectF mRoundOval; //圆形所在的矩形区域
        public boolean mBRoundPaintsFill; //是否填充以填充模式给绘制圆形
        public int mSidePaintInterval; //圆形向里缩进的距离
        public int mPaintWidth; //圆形画笔宽度（填充模式下的无视）
        public int mPaintColor; //画笔颜色（即主进度条画笔颜色， 子进度条画笔颜色为其半透明值）
        public int mDrawPos; //绘制圆形的起点， （默认为 -90度即12点钟方向）
        public Paint mMainPaints; //主进度条画笔

        public CircleAttribute(){
            mRoundOval = new RectF();
            mBRoundPaintsFill = DEFAULT_FILL_MODE;
            mSidePaintInterval = DEFAULT_INSIDE_VALUE;
            mPaintWidth = 0;
            mPaintColor = DEFAULT_PAINT_COLOR;
            mDrawPos = -90;

            mMainPaints = new Paint();
            mMainPaints.setAntiAlias(true);
            mMainPaints.setStyle(Paint.Style.FILL);
            mMainPaints.setStrokeWidth(mPaintWidth);
            mMainPaints.setColor(mPaintColor);
        }

        /**
         * 设置画笔宽度
         * @param width
         */
        public void setPaintWidth(int width){
            mMainPaints.setStrokeWidth(width);
        }

        /**
         * 设置画笔颜色
         * @param color
         */
        public void setPaintColor(int color){
            mMainPaints.setColor(color);
        }

        /**
         * 设置填充模式
         * @param fill
         */
        public void setFill(boolean fill){
            mBRoundPaintsFill = fill;
            if (fill){
                mMainPaints.setStyle(Paint.Style.FILL);
            } else {
                mMainPaints.setStyle(Paint.Style.STROKE);
            }
        }

        /**
         * 自动修正
         * @param width
         * @param height
         */
        public void autoFix(int width, int height){
            if (mSidePaintInterval != 0){
                mRoundOval.set(mPaintWidth / 2 + mSidePaintInterval, mPaintWidth / 2 + mSidePaintInterval,
                        width - mPaintWidth / 2 - mSidePaintInterval, height - mPaintWidth / 2 - mSidePaintInterval);
            } else {
                int left = getPaddingLeft();
                int right = getPaddingRight();
                int top = getPaddingTop();
                int bottom = getPaddingBottom();
                mRoundOval.set(left + mPaintWidth / 2, top + mPaintWidth / 2, width - right - mPaintWidth / 2,
                        height - bottom - mPaintWidth / 2);
            }
        }

    }
}
