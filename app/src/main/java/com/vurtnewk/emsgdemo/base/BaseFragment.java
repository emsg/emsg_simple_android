package com.vurtnewk.emsgdemo.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;

/**
 * @author VurtneWk
 * @time created on 2016/3/18.18:28
 */
public abstract class BaseFragment extends Fragment {

    protected String TAG = "";
    protected Context mContext;
    protected View rootView;
    protected LayoutInflater mInflater;
    protected EventBus mEventBus;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mEventBus= EventBus.getDefault();
        this.mInflater = inflater;
        if(rootView == null){
            rootView = initView(mInflater);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mContext = this.getActivity();

        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroyView();
    }

    protected abstract void initData();

    protected abstract View initView(LayoutInflater inflater);
}
