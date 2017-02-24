package com.hongmingwei.listview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongmingwei.listview.R;

/**
 * Created by hongmingwei on 2016/9/15
 */
public class LoadAndRetryBar extends RelativeLayout {

    private Context mContext;

    private LinearLayout mLoadingLayout;
    private TextView mRetryText;

    private OnClickListener mRetryClickListener;

    public LoadAndRetryBar(Context context) {
        super(context);
        initView(context);
    }

    public LoadAndRetryBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadAndRetryBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.vw_pull_list_footer, this, true);
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        mRetryText = (TextView) findViewById(R.id.retry_textview);

        mLoadingLayout.setVisibility(GONE);
        mRetryText.setVisibility(INVISIBLE);
        mRetryText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRetryClickListener != null) {
                    mRetryClickListener.onClick(v);
                }
            }
        });
    }

    public void showLoadingBar(){
        mLoadingLayout.setVisibility(VISIBLE);
        mRetryText.setVisibility(INVISIBLE);
    }

    public void showRetryStatus(){
        mLoadingLayout.setVisibility(GONE);
        mRetryText.setVisibility(VISIBLE);
    }

    public void setonRetryClickListener(OnClickListener listener){
        mRetryClickListener = listener;
    }


}
