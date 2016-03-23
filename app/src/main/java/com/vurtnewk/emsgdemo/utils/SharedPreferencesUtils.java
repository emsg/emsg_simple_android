package com.vurtnewk.emsgdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author VurtneWk
 *         created at 2016/3/16 14:26
 *         配置文件工具类
 */
public class SharedPreferencesUtils {

    private static SharedPreferences mSharedPreferences;
    private static SharedPreferencesUtils instance;
    private static final String SP_NAME = "EMSGDEMO";

    private SharedPreferencesUtils() {
    }

    public static SharedPreferencesUtils getInstance(Context context) {
        if (instance == null || mSharedPreferences == null) {
            instance = new SharedPreferencesUtils();
            mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return instance;
    }

    /**
     * @param context               上下文
     * @param sharedPreferencesName 配置文件名字
     * @return
     */
    public static SharedPreferencesUtils getInstance(Context context, String sharedPreferencesName) {
        if (instance == null || mSharedPreferences == null) {
            instance = new SharedPreferencesUtils();
            mSharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        }
        return instance;
    }

    public boolean setParam(String key, String value) {
        Editor edit = mSharedPreferences.edit();
        edit.putString(key, value);
        return edit.commit();
    }

    public String getParam(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public boolean setParam(String key, boolean bool) {
        Editor edit = mSharedPreferences.edit();
        edit.putBoolean(key, bool);
        return edit.commit();
    }

    /**
     * 默认false
     */
    public boolean getParamBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean setParam(String key, int value) {
        Editor edit = mSharedPreferences.edit();
        edit.putInt(key, value);
        return edit.commit();
    }

    public int getParamInt(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    public long getParamLong(String key) {
        return mSharedPreferences.getLong(key, 0);
    }


    public boolean setParam(String key, long value) {
        Editor edit = mSharedPreferences.edit();
        edit.putLong(key, value);
        return edit.commit();
    }
}
