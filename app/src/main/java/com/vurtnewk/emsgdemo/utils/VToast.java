package com.vurtnewk.emsgdemo.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * @author VurtneWk
 * @time Created on 2016/3/16 15:56
 */
public class VToast {

    private static Toast mToast = null;

    private static Handler mHandler = new Handler();

    private static Runnable mRunnable = new Runnable() {
        public void run() {
            mToast.cancel();
            mToast = null;
        }
    };
    private static final int INTERVAL = 1500;

    public static final void showShortToast(Context context, String msg) {
        showMsg(context, msg, Toast.LENGTH_SHORT);
    }

    public static final void showLongToast(Context context, String msg) {
        showMsg(context, msg, Toast.LENGTH_LONG);
    }

    private static final void showMsg(Context context, String msg, int length) {
        if(msg == null || "".equals(msg)){
            return;
        }
        mHandler.removeCallbacks(mRunnable);
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, length);
        } else {
            mToast.setText(msg);
        }
        mHandler.postDelayed(mRunnable, INTERVAL);
        mToast.show();
    }

    /**
     * 非工作线程时调用
     * @param context
     * @param msg
     */
    public static final void showShortToastOnUI(Context context, String msg) {
        showMsgOnUi(context, msg, Toast.LENGTH_SHORT);
    }

    /**
     * 非工作线程时调用
     * @param context
     * @param msg
     */
    public static final void showLongToastOnUI(Context context, String msg) {
        showMsgOnUi(context, msg, Toast.LENGTH_LONG);
    }


    private static final void showMsgOnUi(Context context, String msg, int length) {
        Looper.prepare();
        showMsg(context, msg, length);
        Looper.loop();
    }
}
