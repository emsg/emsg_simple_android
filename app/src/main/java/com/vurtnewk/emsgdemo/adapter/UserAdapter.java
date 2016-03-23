package com.vurtnewk.emsgdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.entity.UserInfo;

import java.util.List;

/**
 * @author VurtneWk
 * @time created on 2016/3/21.22:17
 */
public class UserAdapter extends BaseAdapter {

    private final List<UserInfo> mList;
    private final Context mContext;

    public UserAdapter(Context context, List<UserInfo> list) {
        this.mList = list;
        this.mContext = context;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_search_user, null);
        TextView tv = (TextView) convertView.findViewById(R.id.mTvNickName);
        ImageView mIvAvatar = (ImageView) convertView.findViewById(R.id.mIvAvatar);
        tv.setText(mList.get(position).getNickname());
        ImageLoader.getInstance().displayImage(mList.get(position).getIcon(), mIvAvatar);


        return convertView;
    }
}
