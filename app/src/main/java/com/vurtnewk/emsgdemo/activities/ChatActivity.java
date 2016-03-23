package com.vurtnewk.emsgdemo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vurtnewk.emsg.beans.MessageInfoEntity;
import com.vurtnewk.emsg.db.MessageInfoDaoImpl;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.adapter.ChatAdapter;
import com.vurtnewk.emsgdemo.base.BaseActivity;
import com.vurtnewk.emsgdemo.base.BaseApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author VurtneWk
 * @time created on 2016/3/21.16:09
 * 新的聊天界面..待完成的
 */
public class ChatActivity extends BaseActivity {

    private ImageView mIvVoice;
    private EditText mEtContent;
    private ImageView mIvOther;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private ListView mLvChat;

    private int chatType = 1;
    private String id;
    private MessageInfoDaoImpl messageInfoDao;
    private String sid;

    private List<MessageInfoEntity> mList = new ArrayList<>();
    private ChatAdapter mChatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        initData();
    }

    public static final void startChart(Context ctx, int chatType, String id) {
        Intent intent = new Intent(ctx, ChatActivity.class);
        intent.putExtra("chatType", chatType);
        intent.putExtra("id", id);
        ctx.startActivity(intent);
    }


    private void initView() {
        mIvVoice = (ImageView) findViewById(R.id.mIvVoice);
        mEtContent = (EditText) findViewById(R.id.mEtContent);
        mIvOther = (ImageView) findViewById(R.id.mIvOther);
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mLvChat = (ListView) findViewById(R.id.mLvChat);
        mToolbarTitle = (TextView) findViewById(R.id.mToolbarTitle);
        mToolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        chatType = getIntent().getIntExtra("chatType", 1);
        id = getIntent().getStringExtra("id");
        //获取SID
        String[] str = {id, BaseApplication.getInstance().getUserInfo().getId()};
        Arrays.sort(str);
        sid = str[0] + str[1];

        mChatAdapter = new ChatAdapter(mContext,mList);
        mLvChat.setAdapter(mChatAdapter);

        searchHistory();
    }

    /**
     * 查询历史记录
     * 没有做线程等处理的 .后期优化
     */
    private void searchHistory() {
        messageInfoDao = new MessageInfoDaoImpl(this);
        List<Map<String, String>> listmessageinfo = messageInfoDao.listMessageInfo(null, sid, chatType + "", mList.size(), 10);
        //更新为已读
        messageInfoDao.updateMsgReadOrNo(sid);
        if (listmessageinfo != null && !listmessageinfo.isEmpty()) {
            for (int i = 0; i < listmessageinfo.size(); i++) {
                MessageInfoEntity entity = new MessageInfoEntity();
                entity.setMsg_content(listmessageinfo.get(i).get("msg_content"));
                entity.setMsg_time(listmessageinfo.get(i).get("msg_time"));
                entity.setMyheadurl(listmessageinfo.get(i).get("myheadurl"));
                entity.setMyjid(listmessageinfo.get(i).get("myjid"));
                entity.setMynickname(listmessageinfo.get(i).get("mynickname"));
                entity.setHeadurl(listmessageinfo.get(i).get("headurl"));
                entity.setJid(listmessageinfo.get(i).get("jid"));
                entity.setMsg_type(listmessageinfo.get(i).get("msg_type"));
                entity.setNickname(listmessageinfo.get(i).get("nickname"));
                entity.setSid(listmessageinfo.get(i).get("sid"));
                entity.setMsg_imageUrlId(listmessageinfo.get(i).get("msg_imageUrlId"));
                entity.setMsg_GeoLat(listmessageinfo.get(i).get("msg_GeoLat"));
                entity.setMsg_GeoLng(listmessageinfo.get(i).get("msg_GeoLng"));
                entity.setVoice_time(listmessageinfo.get(i).get("voice_time"));
                entity.setAttr(listmessageinfo.get(i).get("msg_attr"));
                if (listmessageinfo.get(i).get("msg_state").equals("true")) {
                    entity.setMsg_state(true);
                } else {
                    entity.setMsg_state(false);
                }
                mList.add(entity);
            }
            mChatAdapter.notifyDataSetChanged();
            mLvChat.setSelection(mLvChat.getCount() - 1);
        }
    }
}