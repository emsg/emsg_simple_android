package com.vurtnewk.emsgdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.base.BaseActivity;
import com.vurtnewk.emsgdemo.constants.SettingsConstants;
import com.vurtnewk.emsgdemo.entity.UserInfo;
import com.vurtnewk.emsgdemo.utils.ACache;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                UserInfo user = (UserInfo) ACache.get(mContext).getAsObject(SettingsConstants.CACHE_USER);
                String token = ACache.get(mContext).getAsString(SettingsConstants.CACHE_TOKEN);
                if (!TextUtils.isEmpty(token) && user != null) {
                    startActivity(new Intent(mContext, MainActivity.class));
                } else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                finish();
            }
        }.start();

    }

}
