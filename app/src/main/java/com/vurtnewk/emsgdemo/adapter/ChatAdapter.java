package com.vurtnewk.emsgdemo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.vurtnewk.emsg.beans.MessageInfoEntity;

import java.util.List;

/**
 * @author VurtneWk
 * @time created on 2016/3/21.16:32
 */
public class ChatAdapter extends BaseAdapter {

    private List<MessageInfoEntity> mList;

    public ChatAdapter(Context ctx, List<MessageInfoEntity> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        //自己发的是1,别人发的是2
        MessageInfoEntity messageInfoEntity = mList.get(position);
        if(messageInfoEntity.getMsg_state()){
            return 1;
        }else{
            return 2;
        }
    }
}
