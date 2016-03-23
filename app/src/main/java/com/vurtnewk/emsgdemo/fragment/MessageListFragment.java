package com.vurtnewk.emsgdemo.fragment;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.vurtnewk.emsg.EmsgClient;
import com.vurtnewk.emsg.beans.MsgSessionEntity;
import com.vurtnewk.emsg.db.MessageInfoDaoImpl;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.adapter.MsgSessionAdapter;
import com.vurtnewk.emsgdemo.base.BaseApplication;
import com.vurtnewk.emsgdemo.base.BaseFragment;
import com.vurtnewk.emsgdemo.eventbus.RefreshMsgSessionEvent;
import com.vurtnewk.emsgdemo.utils.ACache;
import com.vurtnewk.emsgdemo.utils.VLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.Subscribe;

/**
 * @author VurtneWk
 * @time created on 2016/3/18.17:13
 */
public class MessageListFragment extends BaseFragment implements View.OnClickListener {

    private static final String TYPE = "1,100";//要查询的类型
    private Toolbar mToolbar;
    private ListView mLvMsg;
    private List<MsgSessionEntity> mList = new ArrayList<>();
    private MessageInfoDaoImpl mMsgDao;
    private MsgSessionAdapter mMsgSessionAdapter;
    private TextView mTvRefreshData;
    TextView mToolbarTitle;


    @Override
    protected View initView(LayoutInflater inflater) {
        View inflate = inflater.inflate(R.layout.fragment_message_list, null);
        mToolbar = (Toolbar) inflate.findViewById(R.id.mToolbar);
        mLvMsg = (ListView) inflate.findViewById(R.id.mLvMsg);
        mTvRefreshData = (TextView) inflate.findViewById(R.id.mTvRefreshData);
        mToolbarTitle = (TextView) inflate.findViewById(R.id.mToolbarTitle);
        mToolbarTitle.setText("消息");
        mToolbar.setNavigationIcon(null);
        mEventBus.register(this);
        return inflate;
    }

    @Override
    protected void initData() {
        mMsgSessionAdapter = new MsgSessionAdapter(getActivity(), mList);
        mLvMsg.setAdapter(mMsgSessionAdapter);
        mTvRefreshData.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        searchSession();
    }

    private void searchSession() {
        mMsgDao = new MessageInfoDaoImpl(mContext);
        List<Map<String, String>> mapList = mMsgDao.SearchMsgSession(null,
                BaseApplication.getInstance().getUserInfo().getId()
                        + "@" + ACache.get(getActivity()).getAsString(EmsgClient.EMSG_INFO_DOMAIN), TYPE);
        mList.clear();
        int noRead = 0;//这个计算出来是所有的未读数
        if (mapList != null && !mapList.isEmpty()) {//TODO 这里回头在封装..
            for (int i = 0; i < mapList.size(); i++) {
                MsgSessionEntity entity = new MsgSessionEntity();
                entity.setMsg_lasttime(mapList.get(i).get("msg_lasttime"));
                String nickName = mapList.get(i).get("nickname").toString();
                entity.setNickname(nickName);
                if ("image".equals(mapList.get(i).get("msg_type"))) {
                    entity.setMsg_content("[图片]");
                } else if ("audio".equals(mapList.get(i).get("msg_type"))) {
                    entity.setMsg_content("[语音]");
                } else {
                    entity.setMsg_content(mapList.get(i).get("msg_content"));
                }
                entity.setMsg_type(mapList.get(i).get("msg_type"));
                entity.setType(mapList.get(i).get("type"));
                entity.setJid(mapList.get(i).get("jid"));
                entity.setMyjid(mapList.get(i).get("myjid"));
                entity.setMsg_noread_num(mapList.get(i).get("msg_noread_num"));
                entity.setSid(mapList.get(i).get("sid"));
                entity.setHeadurl(mapList.get(i).get("headurl"));
                entity.setAttr(mapList.get(i).get("msg_attr"));
                mList.add(entity);
                if (entity.getMsg_noread_num() != null && !"".equals(entity.getMsg_noread_num())) {
                    noRead += Integer.parseInt(entity.getMsg_noread_num());
                }
            }
        }
        VLog.i(TAG, "mList:" + mList.size());
        mMsgSessionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvRefreshData:
                searchSession();
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(RefreshMsgSessionEvent event) {
        searchSession();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }


}
