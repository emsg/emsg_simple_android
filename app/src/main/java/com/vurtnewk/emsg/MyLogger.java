package com.vurtnewk.emsg;

import android.util.Log;

import java.util.Date;

public class MyLogger {

    private boolean isDebug = true;

    private final String TAG_E_MSG_CLIENT = "LogFromEMSGClient";

    private Class<?> clazz = null;

    public MyLogger(Class<?> c) {
        clazz = c;
    }

    public void info(String tag) {
        if (isDebug)
            Log.i(TAG_E_MSG_CLIENT, tag);
    }

    public void info(Object o) {
        if (isDebug)
            Log.i(TAG_E_MSG_CLIENT, "[" + new Date() + "]" + clazz.getName() + " : " + o.toString());
    }

    public void debug(Object o) {
        if (isDebug)
            Log.d(TAG_E_MSG_CLIENT, "[" + new Date() + "]" + clazz.getName() + " : " + o.toString());
    }

    public void error(String e, Throwable t) {
        if (isDebug)
            Log.e(TAG_E_MSG_CLIENT, "[" + new Date() + "]" + clazz.getName() + " : " + e, t);
    }


}
