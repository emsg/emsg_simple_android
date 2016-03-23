package com.vurtnewk.emsgdemo.base;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vurtnewk.emsg.EmsgClient;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.constants.SettingsConstants;
import com.vurtnewk.emsgdemo.entity.UserInfo;
import com.vurtnewk.emsgdemo.utils.ACache;
import com.vurtnewk.emsgdemo.utils.VLog;

import butterknife.ButterKnife;

/**
 * @author VurtneWk
 *         Created on 2016/3/16.14:30
 */
public class BaseApplication extends Application {

    private static Context mAppContext;
    private UserInfo mUserInfo;
    private String token;
    private static BaseApplication instance;
    private EmsgClient mEmsgClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initImageLoad();
        VLog.setDebug(true);
        ButterKnife.setDebug(false);
        setAppContext(this);
        initEMSG();
    }

    public EmsgClient getEmsgClient() {
        if (mEmsgClient == null) {
            mEmsgClient = EmsgClient.getInstance();
        }
        return mEmsgClient;
    }

    public void initEMSG() {
        mEmsgClient = EmsgClient.getInstance();
        mEmsgClient.init(this);
        mEmsgClient.setEmsStCallBack(new EmsgClient.EmsStateCallBack() {

            @Override
            public void onAnotherClientLogin() {
                mEmsgClient.closeClient();
            }

            @Override
            public void onEmsgClosedListener() {
            }

            @Override
            public void onEmsgOpenedListener() {
            }
        });
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public static void setAppContext(Context mContext) {
        BaseApplication.mAppContext = mContext;
    }


    public String getToken() {
        token = ACache.get(this).getAsString(SettingsConstants.CACHE_TOKEN);
        return token;
    }

    public UserInfo getUserInfo() {
        if (mUserInfo != null) {
            return mUserInfo;
        }
        Object obj = ACache.get(this).getAsObject(SettingsConstants.CACHE_USER);
        UserInfo userInfo = null;
        if (obj != null && obj instanceof UserInfo) {
            userInfo = (UserInfo) obj;
        }
        if (userInfo != null) {
            mUserInfo = userInfo;
            return userInfo;
        }
        return null;
    }

    public void setUser(UserInfo user){
        this.mUserInfo = user;
    }

    private void initImageLoad() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.drawable.ic_mesg)
                .showImageOnFail(R.drawable.ic_mesg)
                .cacheInMemory (true)
                .cacheOnDisk(true)
//                .cacheOnDisc(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder
                (getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions).discCacheSize(100 * 1024 * 1024)//
                .discCacheFileCount(100)// 缓存一百张图片
//                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);


    }

}
