package com.hongmingwei.listview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.hongmingwei.listview.R;

/**
 * Created by hongmingwei on 2017/2/23 10:40
 */
public class PullListView extends ListView implements AbsListView.OnScrollListener {

    private Context mContext;

    private enum ListState{
        INIT_STATE(0),
        PULL_TO_REFRESH(1),
        RELEASE_TO_REFRESH(2),
        REFRESHING(3);

        final int type;
        ListState(int type){
            this.type = type;
        }
    }

    private ListState mListState;

    private final float STEP_RATIO = 0.36f;
    private final float SCROLL_DIRECTION_STEP_UP = 200;
    private final float SCROLL_DIRECTION_STEP_DOWN = 10;

    private PullHeadView mHeadView;
    /**
     * 在首条position为0时记录第一个坐标点
     */
    private boolean mFirstPointRecorded = false;
    /**
     * 记录第一个按下点的位置
     */
    private int mFirstYPos;

    /**
     * 下拉控件的高度
     */
    private int mHeadViewHeight;
    /**
     * 刷新后的回调监听
     */
    private OnRefreshListener mOnRefreshListener;
    /**
     * FootView点击监听
     */
    private OnClickFootViewListener mFootViewListener;

    private LoadAndRetryBar mFootView;

    public boolean HAS_HEANDER = true;

    public boolean HAS_FOOTER = true;
    /**
     * 是否可以下拉刷新，多复用布局做适配
     */
    private boolean isCanPullRefresh = true;
    /**
     * 标识该列表是否有更新数据，即是否还需加载更多
     */
    private boolean hasMoreData = true;
    /**
     * 标识是否正在更新数据，即加载更多操作是否完成
     */
    private boolean isCanLoadMore = true;

    private boolean isNeedRetry = false;
    /**
     * 标识是否为自动刷新
     */
    private boolean isAutoLoading = true;
    /**
     * 监听上滑和下滑事件
     * 第一个按下的Y位置
     */
    private float mSDFirstY;
    /**
     * 第一个位置是否记录
     */
    private boolean mIsSDFirstPointRecorded = false;
    /**
     * 滑动时，第一个down和up的xy坐标
     */
    float x1, y1, x2, y2;
    /**
     * 实际的padding的距离与界面上偏移距离的比例
     */
    private final static int RATIO = 2;
    /**
     * 监听上滑和下滑事件
     */
    private OnScrollListener mOnScrollListener;

    /**
     * 监听向上向下滑动
     */
    private boolean mUpDownActionRecorded = false;
    private int mUpDownPositionY = -1;
    private OnUpDownListener mOnUpDownListener;

    public void setOnUpDownListener(OnUpDownListener listener){
        mOnUpDownListener = listener;
    }

    /**
     * 监听向上向下滑动
     */
    public LoadAndRetryBar getFootView() {
         return mFootView;
     }
    public boolean isHasMoreData(){
        return hasMoreData;
    }

    public void setHasMoreData(boolean hasMoreData){
        this.hasMoreData = hasMoreData;
    }

    public boolean ishasHeander(){
        return HAS_HEANDER;
    }

    public boolean isHasFooter(){
        return HAS_FOOTER;
    }

    public PullListView(Context context) {
        super(context);
        initView(context);
    }

    public PullListView(Context context, boolean hasHead, boolean hasFoot){
        super(context);
        HAS_HEANDER = hasHead;
        HAS_FOOTER = hasFoot;
    }

    public PullListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化header和footer
        initListViewProperty(context, attrs);
        initView(context);
    }

    public PullListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化header和footer
        initListViewProperty(context, attrs);
        initView(context);
    }

    /**
     * 配置listView的属性
     * @param context
     * @param attrs
     */
    private void initListViewProperty(Context context, AttributeSet attrs){
        TypedArray arrayType = context.obtainStyledAttributes(attrs, R.styleable.listview_header_and_footer_toggle);
        HAS_HEANDER = arrayType.getBoolean(R.styleable.listview_header_and_footer_toggle_has_header, true);
        HAS_FOOTER = arrayType.getBoolean(R.styleable.listview_header_and_footer_toggle_has_footer, true);
        arrayType.recycle();
    }

    private void initView(Context context){
        mContext = context;
        if (HAS_HEANDER){
            mHeadView = new PullHeadView(this.mContext);
            addHeaderView(mHeadView, null, false);
            ViewUtils.masureView(mHeadView);
            mHeadViewHeight = mHeadView.getMeasuredHeight();
            /**
             * 隐藏mHeadView
             */
            mHeadView.setPadding(0, -1 * mHeadViewHeight, 0 ,0);
            mHeadView.invalidate();

            mListState = ListState.INIT_STATE;
        }

        if (HAS_FOOTER){
            mFootView = new LoadAndRetryBar(mContext);
            mFootView.setonRetryClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNeedRetry || !isAutoLoading){
                        mFootView.showLoadingBar();
                        mFootViewListener.onClickFootView();
                        isCanLoadMore = false;
                    }
                }
            });
            addFooterView(mFootView, null, false);
        }
        super.setOnScrollListener(this);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        dealUpDownEvent(ev);
        if (HAS_HEANDER && isCanPullRefresh){
            dealTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 对向上滑动和向下滑动的监听
     * @param event
     */
    private void dealUpDownEvent(MotionEvent event){
        if (mOnUpDownListener == null){
            return;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                if (mUpDownPositionY == -1){
                    mUpDownPositionY = (int) event.getY();
                }
                int diff = (int) (event.getY() - mUpDownPositionY);
                if (diff > 20){
                    if (!mUpDownActionRecorded){
                        if (mOnUpDownListener != null){
                            mUpDownActionRecorded = true;
                            mOnUpDownListener.onScrollDown();
                        }
                    }
                } else if (diff < -20){
                    if (!mUpDownActionRecorded){
                        if (mOnUpDownListener != null){
                            mUpDownActionRecorded = true;
                            mOnUpDownListener.onScrollUp();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mUpDownPositionY = (int) event.getY();
                mUpDownActionRecorded = false;
                break;
            case MotionEvent.ACTION_UP:
                mUpDownActionRecorded = false;
                mUpDownPositionY = -1;
                break;
        }
    }

    /**
     * 处理触摸事件
     * @param event
     */
    private void dealTouchEvent(MotionEvent event){
        int y = (int) event.getY();
        int step = 0;
        if(mFirstPointRecorded){
            step = (int) ((y - mFirstYPos) * STEP_RATIO);
        }

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (getFirstVisiblePosition() == 0 && !mFirstPointRecorded){
                    mFirstYPos = y;
                    mFirstPointRecorded = true;
                    /**
                     * 取消侧边滑动条的显示
                     */
                    if (isVerticalFadingEdgeEnabled()){
                        setVerticalScrollBarEnabled(false);
                    }
                    changeHeadState(ListState.INIT_STATE);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                /**
                 * 侧边栏的显示和隐藏
                 */
                if (getFirstVisiblePosition() == 0){
                    if (isVerticalFadingEdgeEnabled()){
                        setVerticalScrollBarEnabled(false);
                    }
                } else {
                    if (!isVerticalScrollBarEnabled()){
                        setVerticalScrollBarEnabled(true);
                    }
                }
                if (getFirstVisiblePosition() == 0 && !mFirstPointRecorded){
                    mFirstYPos = y;
                    mFirstPointRecorded = true;
                    changeHeadState(ListState.INIT_STATE);
                }

                if (mListState != ListState.REFRESHING && mFirstPointRecorded){
                    if (mListState == ListState.RELEASE_TO_REFRESH){
                        /**
                         * 保证在往上推的过程中， ListView不会滑动
                         */
                        setSelection(0);
                        if ((step < mHeadViewHeight) && step > 0){
                            /**
                             * 往上推了，推到了屏幕足够覆盖head的程度，但是还没有推到全部覆盖的地步
                             */
                            changeHeadState(ListState.PULL_TO_REFRESH);
                        } else if (step <= 0){
                            /**
                             * 一下子推到顶了
                             */
                            changeHeadState(ListState.INIT_STATE);
                        }
                    }
                    /**
                     * 还没有达到显示松开刷新的时候，INIT_STATE或者是PULL_TO_REFRESH状态
                     */
                    if (mListState == ListState.PULL_TO_REFRESH){
                        setSelection(0);
                        /**
                         * 下拉到可以进入RELEASE_TO_REFRESH的状态
                         */
                        if (step >= mHeadViewHeight + 5){
                            changeHeadState(ListState.RELEASE_TO_REFRESH);
                        } else if (step <= 0){
                            /**
                             * 上推到顶了
                             */
                            changeHeadState(ListState.INIT_STATE);
                        }
                    }
                    /**
                     * INIT_STATE状态下
                     */
                    if (mListState == ListState.INIT_STATE){
                        if (step > 0){
                            changeHeadState(ListState.PULL_TO_REFRESH);
                        }
                    }
                    /**
                     * 更新headView的padding
                     */
                    if (mListState == ListState.PULL_TO_REFRESH || mListState == ListState.RELEASE_TO_REFRESH){
                        changeHeadPadding(step);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mListState != ListState.REFRESHING && mFirstPointRecorded){
                    if (mListState == ListState.INIT_STATE){
                        mFirstPointRecorded = false;
                    }
                    if (mListState == ListState.PULL_TO_REFRESH){
                        changeHeadState(ListState.INIT_STATE);
                        mFirstPointRecorded = false;
                    }
                    if (mListState == ListState.RELEASE_TO_REFRESH){
                        changeHeadState(ListState.REFRESHING);
                        if (mOnRefreshListener != null){
                            mOnRefreshListener.onRefresh();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 显示正在刷新的状态，并刷新数据
     */
    public void showRefreshingState(){
        changeHeadState(ListState.REFRESHING);
        if (mOnRefreshListener != null){
            mOnRefreshListener.onRefresh();
        }
    }

    /**
     * 隐藏刷新的状态，不做其他操作
     */
    public void hideRefreshingState(){
        changeHeadState(ListState.INIT_STATE);
    }

    public void dealScrollDirectionEvent(MotionEvent event){
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!mIsSDFirstPointRecorded){
                    mSDFirstY = y;
                    mIsSDFirstPointRecorded = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsSDFirstPointRecorded) {
                    mSDFirstY = y;
                    mIsSDFirstPointRecorded = true;
                }

                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsSDFirstPointRecorded = false;
                break;

            default:
                break;
        }
    }

    private void changeHeadPadding(int step){
        mHeadView.setPadding(0, step - mHeadViewHeight, 0, 0);
        mHeadView.setCircleProgress(step * 100 / mHeadViewHeight);
    }

    private void changeHeadState(ListState state){
        if (mListState != ListState.INIT_STATE && mListState == state){
            return;
        }
        switch (state){
            case INIT_STATE:
                mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
                mHeadView.showInitState();
            case PULL_TO_REFRESH:
                if (mListState == ListState.RELEASE_TO_REFRESH){
                    /**
                     * 加载时显示反转动画
                     */
                    mHeadView.showPullState(true);
                } else {
                    mHeadView.showPullState(false);
                }
                // mHeadView.setCircleProgress(paddingTop * 100 /
                // mHeadViewHeight);
                break;
            case REFRESHING:
                mHeadView.setPadding(0, 0, 0, 0);
                mHeadView.showRefreshingState();
                break;
            case RELEASE_TO_REFRESH:
                mHeadView.showReleaseState();
                // mHeadView.setCircleProgress(paddingTop * 100 /
                // mHeadViewHeight);
                break;
            default:
                break;
        }
        mListState = state;
    }

    /**
     * 在下拉刷新完成后更新状态
     */
    public void onRefreshComplete(){
        mFirstPointRecorded = false;
        changeHeadState(ListState.INIT_STATE);
        invalidateViews();
        setSelection(0);
    }

    /**
     * 注册一个回调时要调用这个列表应该刷新。
     * @param onRefreshListener
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void setOnClickFootViewListener(OnClickFootViewListener listener) {
        mFootViewListener = listener;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null){
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (HAS_FOOTER){
            int emptyCount = getHeaderViewsCount() + getFooterViewsCount();
            int mRemainItem = totalItemCount - firstVisibleItem - visibleItemCount;
            Log.d("mFootViewListenermFootViewListener", "firstVisibleItem:" + firstVisibleItem
                    + "  visibleItemCount:" + visibleItemCount + "  totalItemCount:" + totalItemCount);
            Log.d("mFootViewListenermFootViewListener", "totalItemCount" + totalItemCount + "getHeaderViewsCount"
                    + getHeaderViewsCount() + "getFooterViewsCount:" + getFooterViewsCount());
            if ((mRemainItem == 0) && (totalItemCount > emptyCount) && (isCanLoadMore) && (hasMoreData) && (HAS_FOOTER) && isAutoLoading){
                isCanLoadMore=  false;
                if (mFootViewListener != null){
                    Log.d("mFootViewListenermFootViewListener", "mFootViewListener  != null");
                    mFootViewListener.onClickFootView();
                } else {
                    Log.d("mFootViewListenermFootViewListener", "mFootViewListener  == null");
                }
            } else {
                Log.d("mFootViewListenermFootViewListener", "isCanLoadMore = false");
            }
        }
        if (mOnScrollListener != null){
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public void onRefresh(){
        if (mOnRefreshListener != null){
            if (HAS_HEANDER){
                mOnRefreshListener.onRefresh();
            }
        }
    }

    /**
     * 设置加载更多条的状态
     *
     * @param isAutoLoading true-自动加载更多，false-手动加载
     * @param hasMoreData true-有更多数据，false-无更多数据
     * @param isNeedRetry true-需要点击刷新（用于网络访问未成功时的提示）
     */
    public void setFootViewAddMore(boolean isAutoLoading, boolean hasMoreData, boolean isNeedRetry) {
        if (HAS_FOOTER) {
            isCanLoadMore = true;
            this.hasMoreData = hasMoreData;
            this.isNeedRetry = isNeedRetry;
            if (this.getFooterViewsCount() > 0) {

            } else {
                this.addFooterView(mFootView, null, false);
            }
            if (isNeedRetry) {
                mFootView.showRetryStatus();
                isCanLoadMore = false;
            } else {
                if (!hasMoreData) {
                    try {
                        this.removeFooterView(mFootView);
                    } catch (Exception e) {
                        Log.e("TAG", e.toString());
                    }
                } else {
                    if (!isAutoLoading) {
                        mFootView.showRetryStatus();
                    } else {
                        mFootView.showLoadingBar();
                    }
                    if (this.getFooterViewsCount() > 0) {

                    } else {
                        this.addFooterView(mFootView, null, false);
                    }
                }
            }
        }
    }

    public void setFootViewListener(){
        mFootView.showLoadingBar();
    }
    /**
     * 接口定义一个回调时调用应该刷新列表
     */
    public interface OnRefreshListener{
        /**
         * 调用应用刷新列表
         */
        void onRefresh();
    }

    public interface OnClickFootViewListener{
        void onClickFootView();
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }
    }

    public boolean isAutoLoading(){
        return isAutoLoading;
    }

    /**
     * 设置是否自动加载更多
     * @param isAutoLoading
     */
    public void setAutoLoading(boolean isAutoLoading){
        this.isAutoLoading = isAutoLoading;
        if (isAutoLoading){
            mFootView.showLoadingBar();
        } else{
            mFootView.showRetryStatus();
        }
    }


    public void showListLoading() {
        changeHeadState(ListState.REFRESHING);
    }

    public void setListAdapter(BaseAdapter adapter) {
        setAdapter(adapter);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    public void showLoadingMore() {
        mFootView.showLoadingBar();
    }

    public boolean isCanPullRefresh() {
        return isCanPullRefresh;
    }

    public void setCanPullTegresh(boolean isCanPullRefresh){
        this.isCanPullRefresh = isCanPullRefresh;
    }


}
