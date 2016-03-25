package com.vurtnewk.emsgdemo.ui;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.vurtnewk.emsgdemo.R;


/**
 * Created by VWK on 2016/1/16.
 */
public class VBasePopWin extends BasePopWin {

    private static final String TAG = "VBasePopWin";
    private final boolean mShowCancel;
    private String[] mContent;
    private ContentListener mContentListener;
    private LinearLayout mLlContent;
    private TextView mTvCancel;

    public VBasePopWin(Activity activity, String[] content, ContentListener contentListener, boolean showCancel) {
        super(activity);
        this.mActivity = activity;
        this.mContent = content;
        this.mContentListener = contentListener;
        this.mShowCancel = showCancel;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_popwin, null);
        mLlContent = (LinearLayout) view.findViewById(R.id.mLlContent);
        mTvCancel = (TextView) view.findViewById(R.id.mTvCancel);
        if (mShowCancel) {
            mTvCancel.setVisibility(View.VISIBLE);
            mTvCancel.setOnClickListener(this);
        } else {
            mTvCancel.setVisibility(View.GONE);
        }
        for (int i = 0; i < mContent.length; i++) {
            TextView textView = getTextView(i);
            mLlContent.addView(textView);
            if (mContent.length > 1 && i < mContent.length - 1) {
                mLlContent.addView(getLine());
            }
        }
        mPopupWindow = getPopWin(view);
    }

    @Override
    public void onClick(View v) {
        if (mTvCancel == v) {
            mPopupWindow.dismiss();
        }
    }

    public interface ContentListener {
        void onContentListener(int position, String content, PopupWindow mPopupWindow);
    }

    public TextView getTextView(final int position) {
        TextView textView = new TextView(mActivity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(10, 30, 10, 30);
        textView.setBackgroundResource(R.drawable.bg_textview);
        textView.setLayoutParams(layoutParams);
        textView.setTextColor(mActivity.getResources().getColor(R.color.color_333333));
        textView.setTextSize(14);
        final String content = mContent[position];
        textView.setText(content);
        if (mContentListener != null) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    mContentListener.onContentListener(position, content , mPopupWindow);
                }
            });
        }
        return textView;
    }

    private View getLine() {
        View view = new View(mActivity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(mActivity.getResources().getColor(R.color.color_DDDDDD));
        return view;
    }

}
