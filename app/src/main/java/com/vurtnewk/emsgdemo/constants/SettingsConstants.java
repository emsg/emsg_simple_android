package com.vurtnewk.emsgdemo.constants;

import android.os.Environment;

import java.io.File;

/**
 * @author VurtneWk
 * @time created on 2016/3/16.14:38
 */
public interface SettingsConstants {

    String USER_ID = "user_id";

    String CACHE_USER = "user";
    String CACHE_TOKEN = "token";

    public static final File upLoadFileDir = new File(
            Environment.getExternalStorageDirectory(), "emsg_0");
}
