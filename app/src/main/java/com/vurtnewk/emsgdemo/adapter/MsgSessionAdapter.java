package com.vurtnewk.emsgdemo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vurtnewk.emsg.beans.MsgSessionEntity;
import com.vurtnewk.emsg.db.MessageInfoDaoImpl;
import com.vurtnewk.emsg.util.SmileUtils;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.activities.NotifyActivity;
import com.vurtnewk.emsgdemo.activities.OldChatActivity;
import com.vurtnewk.emsgdemo.base.BaseApplication;
import com.vurtnewk.emsgdemo.entity.MsgAttr;
import com.vurtnewk.emsgdemo.entity.UserInfo;

import java.util.Arrays;
import java.util.List;

/**
 * @author VurtneWk
 * @time created on 2016/3/22.11:13
 */
public class MsgSessionAdapter extends BaseAdapter {

    public List<MsgSessionEntity> mList;
    public Context context;

    public MsgSessionAdapter(Context ctx, List<MsgSessionEntity> list) {
        mList = list;
        context = ctx;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //时间关系..没做viewholder等处理
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_msg_session, null);
        TextView mTvNickName = (TextView) convertView.findViewById(R.id.mTvNickName);
        TextView mTvNickContent = (TextView) convertView.findViewById(R.id.mTvNickContent);
        ImageView mIvAvatar = (ImageView) convertView.findViewById(R.id.mIvAvatar);
        final MsgSessionEntity msgSessionEntity = mList.get(position);
        mTvNickName.setText(msgSessionEntity.getNickname());
        ImageLoader.getInstance().displayImage(msgSessionEntity.getHeadurl(), mIvAvatar);

        // 设置内容
        Spannable span = SmileUtils.getSmiledText(context, msgSessionEntity.getMsg_content());
        mTvNickContent.setText(span, TextView.BufferType.SPANNABLE);
        String msg_type = msgSessionEntity.getType();
        switch (msg_type) {
            case "1":

//                    else if (msgSessionEntity.getMsg_type() != null && !"".equals(msgSessionEntity.getMsg_type())) {
//                        convertView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(context, OldChatActivity.class);
//                                intent.putExtra("id", msgSessionEntity.getJid().split("@")[0]);
//                                intent.putExtra("name", msgSessionEntity.getNickname());
//                                intent.putExtra("headurl", msgSessionEntity.getHeadurl());
//                                context.startActivity(intent);
//                            }
//                        });
//                    }
//        }else{
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OldChatActivity.class);
                        intent.putExtra("id", msgSessionEntity.getJid().split("@")[0]);
                        intent.putExtra("name", msgSessionEntity.getNickname());
                        intent.putExtra("headurl", msgSessionEntity.getHeadurl());
                        context.startActivity(intent);
                    }
                });
//        }
                break;
            case "100":
                String attr = msgSessionEntity.getAttr();//根据Attr进行不同处理
                final MsgAttr msgAttr = new Gson().fromJson(attr, MsgAttr.class);
                if (msgAttr != null) {
                    if ("add".equals(msgAttr.action)) {
                        mTvNickName.setText(msgAttr.contact_nickname);
                        mTvNickContent.setText("请求添加好友");
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //TODO
                                context.startActivity(new Intent(context, NotifyActivity.class).putExtra("msgAttr", msgAttr));
                            }
                        });
                    } else if ("accept".equals(msgAttr.action)) {
                        mTvNickName.setText(msgAttr.contact_nickname);
                        mTvNickContent.setText("接受好友请求");
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //TODO
                                context.startActivity(new Intent(context, NotifyActivity.class).putExtra("msgAttr", msgAttr));
                            }
                        });
                    }
                    break;

                }
        }
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("提示")
                        .setMessage("删除回话")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MessageInfoDaoImpl mMsgDao = new MessageInfoDaoImpl(context);
                                UserInfo userinfo = BaseApplication.getInstance().getUserInfo();
                                String str[] = {userinfo.getId(), msgSessionEntity.getJid().split("@")[0]};
                                Arrays.sort(str);
                                String sid = str[0] + str[1];
                                mMsgDao.DeleteMsgSession(null, sid, msgSessionEntity.getType());
                                mMsgDao.DeleteMsgInfo(null, sid, msgSessionEntity.getType());
                                mList.remove(position);
                                notifyDataSetChanged();
                            }
                        }).show();
                return true;
            }
        });
        return convertView;
    }
}
