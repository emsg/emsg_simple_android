package com.vurtnewk.emsgdemo.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vurtnewk.emsg.EmsgClient;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.base.BaseActivity;
import com.vurtnewk.emsgdemo.base.BaseApplication;
import com.vurtnewk.emsgdemo.constants.SettingsConstants;
import com.vurtnewk.emsgdemo.constants.UrlConstants;
import com.vurtnewk.emsgdemo.entity.LoginReturnEntity;
import com.vurtnewk.emsgdemo.ui.LoadingView;
import com.vurtnewk.emsgdemo.utils.ACache;
import com.vurtnewk.emsgdemo.utils.VToast;
import com.vurtnewk.emsgdemo.utils.http.HttpClient;
import com.vurtnewk.emsgdemo.utils.http.HttpListener;
import com.vurtnewk.emsgdemo.utils.http.HttpRequestParams;

import java.util.Calendar;

/**
 * @author VurtneWk
 * @time created on 2016/3/16.17:41
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    EditText mEtAccount;
    EditText mEtNickName;
    RadioButton mRbMale;
    RadioButton mRbFemale;
    RadioGroup mGroupSex;
    TextView mTvBirthday;
    EditText mEtEmail;
    EditText mEtPassword;
    Button mBtnRegister;
    TextView mToolbarTitle;
    LoadingView mLoadingView;
    Toolbar mToolbar;

    boolean isSetDate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEtAccount = (EditText) findViewById(R.id.mEtAccount);
        mEtNickName = (EditText) findViewById(R.id.mEtNickName);
        mTvBirthday = (TextView) findViewById(R.id.mTvBirthday);
        mEtEmail = (EditText) findViewById(R.id.mEtEmail);
        mEtPassword = (EditText) findViewById(R.id.mEtPassword);
        mRbMale = (RadioButton) findViewById(R.id.mRbMale);
        mRbFemale = (RadioButton) findViewById(R.id.mRbFemale);
        mGroupSex = (RadioGroup) findViewById(R.id.mGroupSex);
        mBtnRegister = (Button) findViewById(R.id.mBtnRegister);
        mToolbarTitle = (TextView) findViewById(R.id.mToolbarTitle);
        mLoadingView = (LoadingView) findViewById(R.id.mLoadingView);
        mToolbarTitle.setText(R.string.register);
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);

        mBtnRegister.setOnClickListener(this);
        mTvBirthday.setOnClickListener(this);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void mBtnRegister() {
        HttpRequestParams httpRequestParams = new HttpRequestParams(UrlConstants.USER_SERVICE, UrlConstants.USER_METHOD_REGISTER);
        String account = mEtAccount.getText().toString();
        String nickname = mEtNickName.getText().toString();
        String birthday = mTvBirthday.getText().toString();
        String email = mEtEmail.getText().toString();
        String pwd = mEtPassword.getText().toString();
        String sex = mRbMale.isChecked() ? "男" : "女";

        if (TextUtils.isEmpty(account)) {
            VToast.showShortToast(mContext, "请输入账号");
            return;
        }
        if (TextUtils.isEmpty(nickname)) {
            VToast.showShortToast(mContext, "请输入昵称");
            return;
        }
        if (!isSetDate) {
            VToast.showShortToast(mContext, "请输入生日");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            VToast.showShortToast(mContext, "请输入邮箱");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            VToast.showShortToast(mContext, "请输入密码");
            return;
        }
        httpRequestParams.put("username", account);
        httpRequestParams.put("nickname", nickname);
        httpRequestParams.put("gender", sex);
        httpRequestParams.put("birthday", birthday);
        httpRequestParams.put("email", email);
        httpRequestParams.put("password", pwd);
        httpRequestParams.put("geo", "");
        HttpClient.post(httpRequestParams, new HttpListener() {
            @Override
            public void onSuccess(String result) {
                mLoadingView.hide();
                LoginReturnEntity mLoginReturnEntity = new Gson().fromJson(result, LoginReturnEntity.class);
                if (mLoginReturnEntity.isSuccess()) {
                    //保存数据
                    ACache.get(mContext).put(SettingsConstants.CACHE_USER, mLoginReturnEntity.getEntity().getUser());
                    ACache.get(BaseApplication.getAppContext()).put(SettingsConstants.CACHE_TOKEN, mLoginReturnEntity.getEntity().getToken());
                    //保存信息
                    ACache.get(mContext).put(EmsgClient.EMSG_INFO_HOST, mLoginReturnEntity.getEntity().getEmsg_server().getHost());
                    ACache.get(mContext).put(EmsgClient.EMSG_INFO_PORT, mLoginReturnEntity.getEntity().getEmsg_server().getPort());
                    ACache.get(mContext).put(EmsgClient.EMSG_INFO_DOMAIN, mLoginReturnEntity.getEntity().getEmsg_server().getDomain());

                    setResult(RESULT_OK);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnRegister:
                mBtnRegister();
                break;
            case R.id.mTvBirthday:
                mTvBirthday();
                break;
        }
    }

    private void mTvBirthday() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mTvBirthday.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                isSetDate = true;
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }
}
