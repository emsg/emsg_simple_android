package com.vurtnewk.emsgdemo.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.base.BaseActivity;
import com.vurtnewk.emsgdemo.constants.UrlConstants;
import com.vurtnewk.emsgdemo.entity.SearchUserReturnEntity;
import com.vurtnewk.emsgdemo.entity.SimpleData;
import com.vurtnewk.emsgdemo.utils.VToast;
import com.vurtnewk.emsgdemo.utils.http.HttpClient;
import com.vurtnewk.emsgdemo.utils.http.HttpListener;
import com.vurtnewk.emsgdemo.utils.http.HttpRequestParams;

/**
 * @author VurtneWk
 * @time created on 2016/3/21.21:50
 */
public class OtherUserInfoActivity extends BaseActivity {

    TextView mTvNickName;
    String userId;
    Button mBtnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_info);
        mTvNickName = (TextView) findViewById(R.id.mTvNickName);
        mBtnAdd = (Button)findViewById(R.id.mBtnAdd);
        userId = getIntent().getStringExtra("userId");
        getUserInfo();
    }

    private void getUserInfo() {
        HttpRequestParams httpRequestParams = new HttpRequestParams(UrlConstants.USER_SERVICE, UrlConstants.USER_METHOD_GET_USER_INFO);
        httpRequestParams.put("userid" , userId);
        HttpClient.post(httpRequestParams, new HttpListener() {
            @Override
            public void onSuccess(String result) {
                SearchUserReturnEntity mSearchUserReturnEntity = new Gson().fromJson(result, SearchUserReturnEntity.class);
                refreshUi(mSearchUserReturnEntity);
            }

            @Override
            public void onFailure(String error) {
                VToast.showShortToast(mContext, error);
            }

            @Override
            public void onStart() {

            }
        });
    }

    private void refreshUi(SearchUserReturnEntity mSearchUserReturnEntity) {
        mTvNickName.setText(mSearchUserReturnEntity.getEntity().user.getNickname());
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnAdd();
            }
        });
    }

    private void mBtnAdd() {
        HttpRequestParams httpRequestParams = new HttpRequestParams(UrlConstants.USER_SERVICE, UrlConstants.USER_METHOD_CONTACT);
        httpRequestParams.put("action","add");
        httpRequestParams.put("contact_id",userId);
        HttpClient.post(httpRequestParams, new HttpListener() {
            @Override
            public void onSuccess(String result) {
                SimpleData mSimpleData = new Gson().fromJson(result,SimpleData.class);
                if(mSimpleData.isSuccess()){
                    VToast.showShortToast(mContext,"已提交申请");
                }
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
