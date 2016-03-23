package com.vurtnewk.emsgdemo.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;

/**
 * @author VurtneWk
 * @time Created on 2016/3/16 14:45
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * TAG
     */
    protected String TAG;
    /**
     * ApplicationContext
     */
    protected Context mContext;

    protected EventBus mEventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mContext = this;
        mEventBus = EventBus.getDefault();
//        initViews();
//        initData(savedInstanceState);
    }

//    protected abstract void initViews();
//
//    protected abstract void initData(Bundle savedInstanceState);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
    }
}
