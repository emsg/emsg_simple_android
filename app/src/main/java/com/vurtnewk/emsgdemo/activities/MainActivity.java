package com.vurtnewk.emsgdemo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.vurtnewk.emsg.EmsgCallBack;
import com.vurtnewk.emsg.EmsgClient;
import com.vurtnewk.emsg.EmsgConstants;
import com.vurtnewk.emsg.beans.EmsMessage;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.base.BaseActivity;
import com.vurtnewk.emsgdemo.base.BaseApplication;
import com.vurtnewk.emsgdemo.entity.UserInfo;
import com.vurtnewk.emsgdemo.eventbus.RefreshMsgSessionEvent;
import com.vurtnewk.emsgdemo.fragment.ContactsListFragment;
import com.vurtnewk.emsgdemo.fragment.MessageListFragment;
import com.vurtnewk.emsgdemo.fragment.MyCenterFragment;
import com.vurtnewk.emsgdemo.utils.ACache;
import com.vurtnewk.emsgdemo.utils.VLog;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {


    @Bind(R.id.mViewContent)
    FrameLayout mViewContent;
    @Bind(android.R.id.tabcontent)
    FrameLayout tabcontent;
    @Bind(android.R.id.tabhost)
    FragmentTabHost mTabHost;

    static Class<?> mFragmentArray[] = {MessageListFragment.class,
            ContactsListFragment.class, MyCenterFragment.class};
    static int mImageViewArray[] = {R.drawable.tab_main_msg,
            R.drawable.tab_main_contacts, R.drawable.tab_main_mycenter};
    String mTextViewArray[] = {"消息", "联系人", "我的"};
    private Button mBtnTipsBut[] = new Button[mFragmentArray.length];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        loginEmsg();
        registerMessageReciver();
    }

    private void initView() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.mViewContent);
        for (int i = 0; i < mFragmentArray.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextViewArray[i])// 添加tabspec
                    .setIndicator(getTabItemView(i));// 添加图标
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);// 添加界面
            mTabHost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(R.color.color_tab_main);
        }
        mTabHost.getTabWidget().setDividerDrawable(android.R.color.transparent);
    }

    private View getTabItemView(int index) {
        View view = getLayoutInflater().inflate(R.layout.tab_item_main, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_item);
        mBtnTipsBut[index] = (Button) view.findViewById(R.id.mBtnTips);
        TextView mTvTabTitle = (TextView) view.findViewById(R.id.mTvTabTitle);
        imageView.setImageResource(mImageViewArray[index]);
        mTvTabTitle.setText(mTextViewArray[index]);
        return view;
    }

    public void loginEmsg() {
        final EmsgClient mEmsgClient = BaseApplication.getInstance().getEmsgClient();
        final UserInfo user = BaseApplication.getInstance().getUserInfo();
        if (user != null) {
            if (mEmsgClient != null) {
                if (mEmsgClient.isClose()) {
                    final String domain = ACache.get(this).getAsString(EmsgClient.EMSG_INFO_DOMAIN);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void registerMessageReciver() {
        IntentFilter mIntentFiter = new IntentFilter();
        // 注册即时消息接收广播
        mIntentFiter.addAction(EmsgConstants.MSG_ACTION_RECDATA);
        // 接收离线消息广播
        mIntentFiter.addAction(EmsgConstants.MSG_ACTION_RECOFFLINEDATA);
        // 接收消息服务开启广播即session连接成功
        mIntentFiter.addAction(EmsgConstants.MSG_ACTION_SESSONOPENED);
        // 对应的上下文对象
        registerReceiver(receiver, mIntentFiter);

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context mContext, Intent mIntent) {
            EmsMessage message = (EmsMessage) mIntent.getParcelableExtra("message");
            if (message == null)
                return;
            mEventBus.post(new RefreshMsgSessionEvent());
        }
    };
}
