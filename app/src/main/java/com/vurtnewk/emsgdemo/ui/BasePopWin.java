package com.vurtnewk.emsgdemo.ui;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.vurtnewk.emsgdemo.R;


public abstract class BasePopWin implements OnClickListener {

    protected Activity mActivity;
    protected PopupWindow mPopupWindow;

    public BasePopWin(Activity activity) {
        this.mActivity = activity;
    }

    protected PopupWindow getPopWin(View v) {
        PopupWindow mPopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.popwin_anim_style_01);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setActivityAlpha(mActivity, 1.0f);
            }
        });
        return mPopupWindow;
    }

    public void show() {
        setActivityAlpha(mActivity, 0.5f);
        mPopupWindow.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    public static void setActivityAlpha(Activity activity, float alpha) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }
}
