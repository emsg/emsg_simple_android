package com.vurtnewk.emsgdemo.fragment;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.activities.LoginActivity;
import com.vurtnewk.emsgdemo.activities.MyInfoActivity;
import com.vurtnewk.emsgdemo.base.BaseApplication;
import com.vurtnewk.emsgdemo.base.BaseFragment;
import com.vurtnewk.emsgdemo.constants.SettingsConstants;
import com.vurtnewk.emsgdemo.constants.UrlConstants;
import com.vurtnewk.emsgdemo.entity.SimpleData;
import com.vurtnewk.emsgdemo.entity.UserInfo;
import com.vurtnewk.emsgdemo.utils.ACache;
import com.vurtnewk.emsgdemo.utils.VToast;
import com.vurtnewk.emsgdemo.utils.http.HttpClient;
import com.vurtnewk.emsgdemo.utils.http.HttpListener;
import com.vurtnewk.emsgdemo.utils.http.HttpRequestParams;

/**
 * @author VurtneWk
 * @time created on 2016/3/18.17:15
 */
public class MyCenterFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout mRlToUserInfo;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private RelativeLayout mRlLogout;
    ImageView mIvAvatar;
    TextView mTvNickName;

    @Override
    protected void initData() {
        UserInfo user = (UserInfo) ACache.get(getActivity()).getAsObject(SettingsConstants.CACHE_USER);
        ImageLoader.getInstance().displayImage(user.getIcon(), mIvAvatar);
        mTvNickName.setText(user.getNickname());
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View rootView = inflater.inflate(R.layout.fragment_my_center, null);
        mRlToUserInfo = (RelativeLayout) rootView.findViewById(R.id.mRlToUserInfo);
        mRlLogout = (RelativeLayout) rootView.findViewById(R.id.mRlLogout);
        mToolbar = (Toolbar) rootView.findViewById(R.id.mToolbar);
        mToolbar.setNavigationIcon(null);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbarTitle = (TextView) rootView.findViewById(R.id.mToolbarTitle);
        mIvAvatar = (ImageView) rootView.findViewById(R.id.mIvAvatar);
        mTvNickName = (TextView) rootView.findViewById(R.id.mTvNickName);

        mRlToUserInfo.setOnClickListener(this);
        mRlLogout.setOnClickListener(this);
        mToolbarTitle.setText("我");
        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mRlToUserInfo:
                startActivity(new Intent(mContext, MyInfoActivity.class));
                break;
            case R.id.mRlLogout:
                mRlLogout();
                break;
        }
    }

    private void mRlLogout() {
        HttpRequestParams httpRequestParams = new HttpRequestParams(UrlConstants.USER_SERVICE, UrlConstants.USER_METHOD_LOGOUT);
        HttpClient.post(httpRequestParams, new HttpListener() {
            @Override
            public void onSuccess(String result) {
                SimpleData simpleData = new Gson().fromJson(result, SimpleData.class);
                if (simpleData.isSuccess()) {
                    ACache.get(mContext).put(SettingsConstants.CACHE_USER, "");
                    ACache.get(mContext).put(SettingsConstants.CACHE_TOKEN, "");
                    BaseApplication.getInstance().setUser(null);
                    if (BaseApplication.getInstance().getEmsgClient() != null) {
                        BaseApplication.getInstance().getEmsgClient().closeClient();
                    }
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                } else {
                    VToast.showShortToast(mContext, "退出登录失败:" + simpleData.getEntity().reason);
                }

            }

            @Override
            public void onFailure(String error) {
                VToast.showShortToast(mContext, "退出登录失败:" + error);
            }

            @Override
            public void onStart() {
                VToast.showShortToast(mContext, "退出进行中...");
            }
        });

    }
}
