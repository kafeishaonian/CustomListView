package com.hongmingwei.listview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hongmingwei.listview.ui.ErrorView;
import com.hongmingwei.listview.ui.PullListView;
import com.hongmingwei.listview.ui.PullListViewController;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private PullListView listView;
    private ErrorView errorView;
    private PullListViewController controller;

    private InnerHandler mHandler = new InnerHandler(this);
    private static final int MESSAGE_PARAM_INFO = 10001;
    private final int DELAYMILLIS = 200;


    /**
     * 伪造数据
     */
    private ArrayList<String> strings = new ArrayList<>();
    private CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initListener();
        initData();
    }


    private void init(){
        listView = (PullListView) findViewById(R.id.listview);
        errorView = (ErrorView) findViewById(R.id.errorview);
        controller = new PullListViewController(listView, errorView);
        adapter = new CustomAdapter(this);
        listView.setAdapter(adapter);

        controller.showViewStatus(PullListViewController.ListViewState.EMPTY_LOADING);
    }



    private void initData(){
        strings.clear();
        strings.add("泰山");
        strings.add("黄山");
        strings.add("中国");
        strings.add("日本");
        strings.add("喜马拉雅");
        strings.add("阿尔卑斯");
        strings.add("长江");
        strings.add("黄河");


        adapter.add(strings);
        controller.showViewStatus(PullListViewController.ListViewState.LIST_NORMAL_HAS_MORE);

    }


    private void initListener(){
        // 下拉刷新
        controller.setOnRefreshListener(new PullListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 避免频繁发送请求
                mHandler.removeCallbacks(refreshTaskRunnable);
                mHandler.postDelayed(refreshTaskRunnable, DELAYMILLIS);
            }
        });
        // 点击重试
        controller.setOnRetryClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 避免频繁发送请求
                mHandler.removeCallbacks(retryTaskRunnable);
                mHandler.postDelayed(retryTaskRunnable, DELAYMILLIS);
            }
        });
        // 加载更多
        controller.setOnLoadMoreListener(new PullListView.OnClickFootViewListener() {

            @Override
            public void onClickFootView() {
                // 避免频繁发送请求
                mHandler.removeCallbacks(loadmoreTaskRunnable);
                mHandler.postDelayed(loadmoreTaskRunnable, DELAYMILLIS);
            }
        });
    }

    /**
     * 下拉刷新
     */
    private Runnable refreshTaskRunnable = new Runnable() {

        @Override
        public void run() {
            controller.showViewStatus(PullListViewController.ListViewState.LIST_REFRESH_COMPLETE);
        }
    };
    /**
     * 点击重试
     */
    private Runnable retryTaskRunnable = new Runnable() {

        @Override
        public void run() {
        }
    };
    /**
     * 加载更多
     */
    private Runnable loadmoreTaskRunnable = new Runnable() {

        @Override
        public void run() {
            controller.showViewStatus(PullListViewController.ListViewState.LIST_NO_MORE);
        }
    };


    private class InnerHandler extends Handler {
        private WeakReference<MainActivity> fragmentReference;

        public InnerHandler(MainActivity fragment) {
            fragmentReference = new WeakReference<MainActivity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final MainActivity activity = fragmentReference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case MESSAGE_PARAM_INFO:
                    break;
                default:
                    break;
            }
        }
    }
}
