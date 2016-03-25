package com.vurtnewk.emsgdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vurtnewk.emsg.EmsgCallBack;
import com.vurtnewk.emsg.EmsgClient;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.base.BaseActivity;
import com.vurtnewk.emsgdemo.base.BaseApplication;
import com.vurtnewk.emsgdemo.constants.SettingsConstants;
import com.vurtnewk.emsgdemo.constants.UrlConstants;
import com.vurtnewk.emsgdemo.entity.LoginReturnEntity;
import com.vurtnewk.emsgdemo.entity.UserInfo;
import com.vurtnewk.emsgdemo.ui.LoadingView;
import com.vurtnewk.emsgdemo.utils.ACache;
import com.vurtnewk.emsgdemo.utils.VLog;
import com.vurtnewk.emsgdemo.utils.VToast;
import com.vurtnewk.emsgdemo.utils.http.HttpClient;
import com.vurtnewk.emsgdemo.utils.http.HttpListener;
import com.vurtnewk.emsgdemo.utils.http.HttpRequestParams;

import java.util.UUID;

/**
 * @author VurtneWk
 * @time 2016 created on 2016/3/16.14:43
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    EditText mEtUserAccount;
    EditText mEtPassword;
    Button mBtnLogin;
    Button mBtnRegister;
    Toolbar mToolbar;
    LoadingView mLoadingView;
    TextView mToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEtUserAccount = (EditText) findViewById(R.id.mEtUserAccount);
        mEtPassword = (EditText) findViewById(R.id.mEtPassword);
        mBtnLogin = (Button) findViewById(R.id.mBtnLogin);
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mLoadingView = (LoadingView) findViewById(R.id.mLoadingView);
        mBtnRegister = (Button) findViewById(R.id.mBtnRegister);
        mToolbarTitle = (TextView) findViewById(R.id.mToolbarTitle);

        //toolbar设置
        mToolbarTitle.setText(R.string.login);
//        setSupportActionBar(mToolbar);
//        mToolbar.setNavigationIcon(null);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
    }

    public void mBtnLogin() {
        String userAccount = mEtUserAccount.getText().toString();
        if (TextUtils.isEmpty(userAccount)) {
            VToast.showShortToast(BaseApplication.getAppContext(), "账号不能为空");
            return;
        }
        String password = mEtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            VToast.showShortToast(BaseApplication.getAppContext(), "密码不能为空");
            return;
        }
        HttpRequestParams httpRequestParams = new HttpRequestParams(UrlConstants.USER_SERVICE, UrlConstants.USER_METHOD_LOGIN);
        httpRequestParams.put("username", userAccount);
        httpRequestParams.put("password", password);
        httpRequestParams.put("geo", "");
        HttpClient.post(httpRequestParams, new HttpListener() {
            @Override
            public void onSuccess(String result) {
                mLoadingView.hide();
                LoginReturnEntity mLoginReturnEntity = new Gson().fromJson(result, LoginReturnEntity.class);
                VLog.d(TAG, mLoginReturnEntity.toString());
                if (mLoginReturnEntity.isSuccess()) {
                    //保存数据
                    ACache.get(mContext).put(SettingsConstants.CACHE_USER, mLoginReturnEntity.getEntity().getUser());
                    ACache.get(BaseApplication.getAppContext()).put(SettingsConstants.CACHE_TOKEN, mLoginReturnEntity.getEntity().getToken());
                    //保存信息
                    ACache.get(mContext).put(EmsgClient.EMSG_INFO_HOST, mLoginReturnEntity.getEntity().getEmsg_server().getHost());
                    ACache.get(mContext).put(EmsgClient.EMSG_INFO_PORT, mLoginReturnEntity.getEntity().getEmsg_server().getPort());
                    ACache.get(mContext).put(EmsgClient.EMSG_INFO_DOMAIN, mLoginReturnEntity.getEntity().getEmsg_server().getDomain());
                    loginEmsg();
                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                } else {
                    VToast.showShortToast(mContext, mLoginReturnEntity.getEntity().getReason());
                }
            }

            @Override
            public void onFailure(String error) {
                mLoadingView.hide();
                VToast.showShortToast(mContext, error);
            }

            @Override
            public void onStart() {
                mLoadingView.show();
            }
        });
    }

    public void mBtnRegister() {
        startActivityForResult(new Intent(this, RegisterActivity.class), 1);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnLogin:
                mBtnLogin();
                break;
            case R.id.mBtnRegister:
                mBtnRegister();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loginEmsg();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void loginEmsg() {
        final EmsgClient mEmsgClient = BaseApplication.getInstance().getEmsgClient();
        final UserInfo user = BaseApplication.getInstance().getUserInfo();
        if (user != null) {
            if (mEmsgClient != null) {
                if (mEmsgClient.isClose()) {
                    final String domain = ACache.get(mContext).getAsString(EmsgClient.EMSG_INFO_DOMAIN);
                    if (!TextUtils.isEmpty(domain)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mEmsgClient.auth(user.getId() + "@" + domain + "/" + UUID.randomUUID().toString(),
                                        BaseApplication.getInstance().getToken()
                                        , new EmsgCallBack() {
                                            @Override
                                            public void onSuccess() {
                                                VLog.i("EMSG", "emsg========登录成功");
                                            }

                                            @Override
                                            public void onError(TypeError mTypeError) {
                                                VLog.i("EMSG", "emsg========登录失败");
                                            }
                                        });
                            }
                        }).start();
                    }
                }
            }
        }
    }
}
