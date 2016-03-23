package com.vurtnewk.emsgdemo.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.vurtnewk.emsgdemo.utils.VLog;

public class LoadingView extends LinearLayout {

    private ProgressBar mProgressBar;
    private Context mContext;

    public LoadingView(Context context) {
        super(context);
        initViews(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        mContext = context;
        setBackgroundColor(0x55000000);
        mProgressBar = new ProgressBar(context);
        this.addView(mProgressBar);
        setGravity(Gravity.CENTER);
        setClickable(true);
        setVisibility(View.GONE);
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        setVisibility(View.INVISIBLE);
    }

    public void show(int marginTop) {
        this.removeAllViews();
        setBackgroundColor(0x55000000);
        mProgressBar = new ProgressBar(mContext);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.topMargin = marginTop;
        VLog.i("LoadingView", "marginTop:" + marginTop);
        mProgressBar.setLayoutParams(lp);
        this.addView(mProgressBar);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setClickable(true);
        setVisibility(View.VISIBLE);
    }

}
