package com.hongmingwei.listview.ui;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by hongmingwei on 2016/9/16
 */
public class PullListViewController {
    private static final String TAG = PullListViewController.class.getSimpleName();


    public enum ListViewState {
        /** 首次加载，正在加载 */
        EMPTY_LOADING,
        /** 首次加载，出错重试页面 */
        EMPTY_RETRY,
        /** 首次加载，没有数据 */
        EMPTY_BLANK,
        /** 正常显示List,有更多数据 */
        LIST_NORMAL_HAS_MORE,
        /** List加载更多数据*/
        LIST_LOAD_MORE,
        /** 列表，重新刷新数据,强制显示HeadView的正在刷新 ,并且调用回调onRefresh */
        LIST_REFRESHING_AND_REFRESH,
        /** 下拉刷新完成，收起下拉HeadView */
        LIST_REFRESH_COMPLETE,
        /** 列表,没有更多数据 */
        LIST_NO_MORE,
        /** 列表,出错重试页面 */
        LIST_RETRY,
        /** 恢复之前ListView的状态 */
        DISMISS_MASK;
    }

    private final PullListView mListView;
    private final ErrorView mMaskView;

    private OnClickListener mRetryClickListener;
    private PullListView.OnRefreshListener mRefreshListener;
    private PullListView.OnClickFootViewListener mFootViewListener;

    private OnClickListener mEmptyCLickListener;

    public PullListViewController(PullListView listView, ErrorView maskView) {
        this.mListView = listView;
        this.mMaskView = maskView;
        initListener();
    }

    private void initListener() {
        mMaskView.setOnRetryClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mRetryClickListener != null) {
                    mRetryClickListener.onClick(v);
                }
            }
        });

        mMaskView.setOnEmptyClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mEmptyCLickListener != null) {
                    mMaskView.setLoadingStatus();
                    mEmptyCLickListener.onClick(v);
                }
            }
        });

        mListView.setOnRefreshListener(new PullListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (mRefreshListener != null) {
                    mRefreshListener.onRefresh();
                }
            }
        });

        mListView.setOnClickFootViewListener(new PullListView.OnClickFootViewListener() {

            @Override
            public void onClickFootView() {
                mListView.showLoadingMore();
                if (mFootViewListener != null) {
                    Log.d(TAG,
                            "PullListMaskController mFootViewListener  != null");
                    mFootViewListener.onClickFootView();
                } else {
                    Log.d(TAG, "PullListMaskController mFootViewListener  = null");
                }
            }
        });
    }

    public void setOnEmptyClickListener(OnClickListener listener) {
        mEmptyCLickListener = listener;
    }

    /**
     * 在出现错误时，点击回调
     *
     * @param listener
     */
    public void setOnRetryClickListener(OnClickListener listener) {
        mRetryClickListener = listener;
    }

    /**
     * 1、拖动ListView的下拉刷新，松开后，调用该回调 2、点击Mask页面的点击刷新按钮
     *
     * @param onRefreshListener
     */
    public void setOnRefreshListener(PullListView.OnRefreshListener onRefreshListener) {
        mRefreshListener = onRefreshListener;
    }

    /**
     * 1、下拉到底部的时候自动调用 2、List底部点击重试后调用
     *
     * @param listener
     */
    public void setOnLoadMoreListener(PullListView.OnClickFootViewListener listener) {
        mFootViewListener = listener;
    }

    public void showViewStatus(ListViewState state) {
        if (mListView == null || mMaskView == null) {
            return;
        }

        switch (state) {
            case EMPTY_LOADING: {
                mListView.setVisibility(View.INVISIBLE);
                mMaskView.setVisibility(View.VISIBLE);
                mMaskView.setLoadingStatus();
                break;
            }
            case EMPTY_RETRY: {
                mListView.setVisibility(View.GONE);
                mMaskView.setVisibility(View.VISIBLE);
                mMaskView.setErrorStatus();
                break;
            }

            case EMPTY_BLANK: {
                mListView.setVisibility(View.GONE);
                mMaskView.setVisibility(View.VISIBLE);
                mMaskView.setEmptyStatus();
                break;
            }

            case LIST_NORMAL_HAS_MORE: {
                mMaskView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.setFootViewAddMore(true, true, false);
                break;
            }
            case LIST_LOAD_MORE: {
                mMaskView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.setAutoLoading(true);
                break;
            }

            case LIST_REFRESHING_AND_REFRESH: {
                mMaskView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.showRefreshingState();
                break;
            }

            case LIST_REFRESH_COMPLETE: {
                mMaskView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.onRefreshComplete();
                mListView.setFootViewAddMore(true, true, false);
                break;
            }

            case LIST_RETRY: {
                mMaskView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.setFootViewAddMore(true, true, true);
                break;
            }

            case LIST_NO_MORE: {
                mMaskView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.setFootViewAddMore(true, false, false);
                break;
            }

            case DISMISS_MASK: {
                mMaskView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                break;
            }
            default:
                break;
        }
    }

}
