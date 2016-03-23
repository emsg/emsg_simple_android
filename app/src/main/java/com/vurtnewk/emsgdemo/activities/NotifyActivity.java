package com.vurtnewk.emsgdemo.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vurtnewk.emsg.db.MessageInfoDaoImpl;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.base.BaseActivity;
import com.vurtnewk.emsgdemo.base.BaseApplication;
import com.vurtnewk.emsgdemo.constants.UrlConstants;
import com.vurtnewk.emsgdemo.entity.MsgAttr;
import com.vurtnewk.emsgdemo.entity.SimpleData;
import com.vurtnewk.emsgdemo.eventbus.RefreshContactListEvent;
import com.vurtnewk.emsgdemo.eventbus.RefreshMsgSessionEvent;
import com.vurtnewk.emsgdemo.utils.VToast;
import com.vurtnewk.emsgdemo.utils.http.HttpClient;
import com.vurtnewk.emsgdemo.utils.http.HttpListener;
import com.vurtnewk.emsgdemo.utils.http.HttpRequestParams;

import java.util.Arrays;

/**
 * @author VurtneWk
 * @time created on 2016/3/22.14:16
 */
public class NotifyActivity extends BaseActivity implements View.OnClickListener {

    Toolbar mToolbar;
    TextView mToolbarTitle;
    Button mBtnAgree;
    Button mBtnReject;
    TextView mTvContent;
    private MsgAttr mMsgAttr;
    private MessageInfoDaoImpl msgDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        initView();
        initData();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbarTitle = (TextView) findViewById(R.id.mToolbarTitle);
        mBtnAgree = (Button) findViewById(R.id.mBtnAgree);
        mBtnReject = (Button) findViewById(R.id.mBtnReject);
        mTvContent = (TextView) findViewById(R.id.mTvContent);
        mToolbarTitle.setText("通知");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        mMsgAttr = (MsgAttr) getIntent().getSerializableExtra("msgAttr");
        switch (mMsgAttr.action) {
            case "add":
                mTvContent.setText(mMsgAttr.contact_nickname + "请求添加您为好友");
                mBtnAgree.setOnClickListener(this);
                mBtnReject.setOnClickListener(this);
                break;
            case "accept":
                mTvContent.setText(mMsgAttr.contact_nickname + "同意了您的好友请求");
                mBtnAgree.setVisibility(View.INVISIBLE);
                mBtnReject.setVisibility(View.INVISIBLE);
                break;
            case "reject":
                mTvContent.setText(mMsgAttr.contact_nickname + "拒绝了您的好友请求");
                mBtnAgree.setVisibility(View.INVISIBLE);
                mBtnReject.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnAgree:
                mBtnAgree();
                break;
            case R.id.mBtnReject:
                mBtnReject();
                break;
        }
    }

    private void mBtnReject() {
        switch (mMsgAttr.action) {
            case "add":
                contact("reject");
                break;
        }
    }

    private void mBtnAgree() {
        switch (mMsgAttr.action) {
            case "add":
                contact("accept");
                break;
        }
    }

    private void contact(String action) {
        HttpRequestParams httpRequestParams = new HttpRequestParams(UrlConstants.USER_SERVICE, UrlConstants.USER_METHOD_CONTACT);
        httpRequestParams.put("action", action);
        httpRequestParams.put("contact_id", mMsgAttr.contact_id);
        HttpClient.post(httpRequestParams, new HttpListener() {
            @Override
            public void onSuccess(String result) {
                SimpleData mSimpleData = new Gson().fromJson(result, SimpleData.class);
                if (mSimpleData.isSuccess()) {
                    VToast.showShortToast(mContext, "已提交申请");
                }
                msgDao = new MessageInfoDaoImpl(mContext);
                //SID 获取
                String str[] = {mMsgAttr.contact_id, BaseApplication.getInstance().getUserInfo().getId()};
                Arrays.sort(str);
                String sid = str[0] + str[1];
                msgDao.DeleteMsgSession(null,sid,"100");
                mEventBus.post(new RefreshMsgSessionEvent());
                mEventBus.post(new RefreshContactListEvent());
                finish();
            }

            @Override
            public void onFailure(String error) {
            }

            @Override
            public void onStart() {
            }
        });
    }
}
