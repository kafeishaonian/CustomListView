package com.hongmingwei.listview.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongmingwei.listview.R;

/**
 * 加载出错时UI
 * Created by hongmingwei on 2016/9/16
 */
public class ErrorView extends RelativeLayout implements View.OnClickListener {

    private Context mContext;

    private LinearLayout mTextLayout;
    private ImageView mIconImage;
    private TextView mTitleText;
    private TextView mSubTitleText;
    private TextView mRetryTitleText;

    private LinearLayout mProgressLayout;
    private TextView mProgressText;

    private static final int STATUS_GONE = 0;
    private static final int STATUS_EMPTY = 1;
    private static final int STATUS_LOADING = 2;
    private static final int STATUS_ERROR = 3;

    private int mStatus;

    // icon 资源
    private int mIconResId = 0;
    // text 资源
    private int mTextResId = 0;

    private OnClickListener mRetryClickListener;

    private OnClickListener mEmptyClickListener;


    public ErrorView(Context context) {
        super(context);
        initView(context);
    }

    public ErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.vw_mask_layout, this);
        mTextLayout = (LinearLayout) findViewById(R.id.textLayout);
        mIconImage = (ImageView) findViewById(R.id.icon);
        mTitleText = (TextView) findViewById(R.id.title);
        mSubTitleText = (TextView) findViewById(R.id.subTitle);
        mRetryTitleText = (TextView) findViewById(R.id.retryTitle);

        mProgressLayout = (LinearLayout) findViewById(R.id.progressLayout);
        mProgressText = (TextView) findViewById(R.id.progressTitle);

        hide();
        mRetryTitleText.setOnClickListener(this);

        /**
         * 设置数据 为空时的回调
         */
        mTextLayout.setOnClickListener(mEmptyListener);
    }


    public void setErrorStatus(String errorSubTitle){
        show();
        mProgressLayout.setVisibility(View.GONE);
        mTextLayout.setVisibility(View.VISIBLE);
        mTitleText.setVisibility(View.GONE);
        mIconImage.setImageBitmap(DefaultImageTools.getNoticeErrorBitmap(mContext));

        if (StringUtils.isNotBlank(errorSubTitle)) {
            mSubTitleText.setVisibility(View.VISIBLE);
            mSubTitleText.setText(errorSubTitle);
        } else {
            mSubTitleText.setVisibility(View.GONE);
        }

        mRetryTitleText.setVisibility(View.VISIBLE);

        mStatus = STATUS_ERROR;
    }

    public void setErrorStatus() {
        String errorTitle = mContext.getString(R.string.hint_network_error);
        setErrorStatus(errorTitle);
    }

    public void setErrorStatus(int resId) {
        String errorTitle = mContext.getString(resId);
        setErrorStatus(errorTitle);
    }

    public void setLoadingStatus(int resId) {
        String loadingText = mContext.getString(resId);
        setLoadingStatus(loadingText);
    }

    public void setLoadingStatus(String loadingText) {
        show();
        mProgressLayout.setVisibility(View.VISIBLE);
        mTextLayout.setVisibility(View.GONE);

        if (StringUtils.isNotBlank(loadingText)) {
            mProgressText.setVisibility(View.VISIBLE);
            mProgressText.setText(loadingText);
        } else {
            mProgressText.setVisibility(View.GONE);
        }

        mStatus = STATUS_LOADING;
    }

    public void setLoadingStatus() {
        setLoadingStatus(mContext.getString(R.string.hint_loading_home));
    }
    public void setIdeaTicking(){
        setLoadingStatus(mContext.getString(R.string.hint_loading_idea));
    }
    public void setLoginLoading(){
        setLoadingStatus(mContext.getString(R.string.hint_loading));
    }

    public void setUpdateUserNameLoadingStatus(){
        setLoadingStatus(mContext.getString(R.string.hint_loading_username));
    }

    public void setEmptyStatus(int resId) {
        String emptyText = mContext.getString(resId);
        setEmptyStatus(emptyText);
    }

    public void setEmptyStatus(String emptyText) {
        show();
        mProgressLayout.setVisibility(View.GONE);
        mTextLayout.setVisibility(View.VISIBLE);
        mTitleText.setVisibility(View.VISIBLE);
        mTitleText.setText(emptyText);
        mSubTitleText.setVisibility(View.GONE);
        mIconImage.setImageBitmap(getEmptyBitmap(mContext));
        mRetryTitleText.setVisibility(View.GONE);
        mStatus = STATUS_EMPTY;
    }

    private Bitmap getEmptyBitmap(Context mContext) {
        if (mIconResId == 0) {
            return DefaultImageTools.getNoticeEmptyBitmap(mContext);
        } else {
            return ImageUtils.getBitmapFromRes(mContext, mIconResId);
        }
    }

    private String getEmptyText(String emptyText) {
        if (mTextResId == 0) {
            return emptyText;
        } else {
            return getResources().getString(mTextResId);
        }
    }

    public void setEmptyStatus(int topResId, int bottomResId) {
        show();
        String topTxt = "";
        if (topResId != 0) {
            topTxt = mContext.getString(topResId);
        }
        String bottomTxt = "";
        if (bottomResId != 0) {
            bottomTxt = mContext.getString(bottomResId);
        }
        mProgressLayout.setVisibility(View.GONE);
        mTextLayout.setVisibility(View.VISIBLE);
        mTitleText.setVisibility(View.GONE);
        mSubTitleText.setVisibility(View.GONE);
        mIconImage.setImageBitmap(getEmptyBitmap(mContext));
        if (StringUtils.isNotBlank(bottomTxt)) {
            mSubTitleText.setVisibility(View.VISIBLE);
            mSubTitleText.setText(bottomTxt);
        } else {
            mSubTitleText.setVisibility(View.GONE);
        }
        mRetryTitleText.setVisibility(View.GONE);
        mStatus = STATUS_EMPTY;
    }

    /**
     * 图标资源
     *
     * @return
     */
    public int getIconResId() {
        return mIconResId;
    }

    public void setIconResId(int mIconResId) {
        this.mIconResId = 0;
        if (mIconResId != -1)
            this.mIconResId = mIconResId;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int mTextResId) {
        this.mTextResId = mTextResId;
    }

    public void setEmptyStatus() {
        setEmptyStatus(mContext.getString(R.string.hint_empty_list));
    }

    public void setVisibleGone() {
        hide();
    }

    private void show() {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }
    }

    private void hide() {
        if (getVisibility() != View.GONE) {
            setVisibility(View.GONE);
        }
        mStatus = STATUS_GONE;
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
     * 数据为空时，点击回调
     * @param listener
     */
    public void setOnEmptyClickListener(OnClickListener listener){
        mEmptyClickListener = listener;
    }

    private OnClickListener mEmptyListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mEmptyClickListener != null){
                mEmptyClickListener.onClick(v);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == null || mStatus != STATUS_ERROR || mRetryClickListener == null){
            return;
        }
        setLoadingStatus();
        if (R.id.retryTitle == v.getId()){
            mRetryClickListener.onClick(v);
        }
    }
}
