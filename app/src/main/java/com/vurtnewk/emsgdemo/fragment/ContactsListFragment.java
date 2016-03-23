package com.vurtnewk.emsgdemo.fragment;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.activities.OldChatActivity;
import com.vurtnewk.emsgdemo.activities.SearchUserActivity;
import com.vurtnewk.emsgdemo.adapter.UserAdapter;
import com.vurtnewk.emsgdemo.base.BaseFragment;
import com.vurtnewk.emsgdemo.constants.UrlConstants;
import com.vurtnewk.emsgdemo.entity.ContactListEntity;
import com.vurtnewk.emsgdemo.entity.UserInfo;
import com.vurtnewk.emsgdemo.ui.LoadingView;
import com.vurtnewk.emsgdemo.utils.VToast;
import com.vurtnewk.emsgdemo.utils.http.HttpClient;
import com.vurtnewk.emsgdemo.utils.http.HttpListener;
import com.vurtnewk.emsgdemo.utils.http.HttpRequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VurtneWk
 * @time created on 2016/3/18.17:14
 */
public class ContactsListFragment extends BaseFragment implements View.OnClickListener {

    Toolbar mToolbar;
    ListView mLvContacts;
    TextView mToolbarTitle;
    TextView mTvAddUser;
    private List<UserInfo> mList = new ArrayList<>();
    UserAdapter mUserAdapter;
    TextView mTvRefresh;
    LoadingView mLoadingView;

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_contacts_list, null);
        mToolbar = (Toolbar) view.findViewById(R.id.mToolbar);
        mLvContacts = (ListView) view.findViewById(R.id.mLvContacts);
        mToolbarTitle = (TextView) view.findViewById(R.id.mToolbarTitle);
        mTvAddUser = (TextView) view.findViewById(R.id.mTvAddUser);
        mToolbar.setNavigationIcon(null);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mTvRefresh = (TextView) view.findViewById(R.id.mTvRefresh);
        mLoadingView = (LoadingView) view.findViewById(R.id.mLoadingView);
        mToolbarTitle.setText("联系人列表");
        mTvAddUser.setOnClickListener(this);
        mTvRefresh.setOnClickListener(this);
        return view;
    }

    @Override
    protected void initData() {
        list();
        mLvContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                VToast.showShortToast(mContext, mList.get(position).getNickname());
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvAddUser:
                startActivity(new Intent(getActivity(), SearchUserActivity.class));
                break;
            case R.id.mTvRefresh:
                list();
                break;
        }
    }

    private void list() {
        HttpRequestParams httpRequestParams = new HttpRequestParams(UrlConstants.USER_SERVICE, UrlConstants.USER_METHOD_CONTACT);
        httpRequestParams.put("action", "list");
        HttpClient.post(httpRequestParams, new HttpListener() {
            @Override
            public void onSuccess(String result) {
                mLoadingView.hide();
                ContactListEntity mContactListEntity = new Gson().fromJson(result, ContactListEntity.class);
                refreshUI(mContactListEntity);
            }

            @Override
            public void onFailure(String error) {
                mLoadingView.hide();
            }

            @Override
            public void onStart() {
                mLoadingView.show();

            }
        });
    }

    private void refreshUI(ContactListEntity mContactListEntity) {
        mList.clear();
        mList.addAll(mContactListEntity.getEntity().contacts);
        mUserAdapter = new UserAdapter(mContext, mList);
        mLvContacts.setAdapter(mUserAdapter);
        mLvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(mContext, ChatActivity.class);
//                startActivity(intent);
//                ChatActivity.startChart(mContext,1,mList.get(position).getId());
//                startActivity(new Intent(mContext, OldChatActivity.class).putExtra("id", mList.get(position).getId()
//                ).putExtra("user",mList.get(position)));

                Intent intent = new Intent(mContext, OldChatActivity.class);
                intent.putExtra("id", mList.get(position).getId());
                intent.putExtra("name", mList.get(position).getNickname());
                intent.putExtra("headurl", mList.get(position).getIcon());
                mContext.startActivity(intent);
            }
        });
    }

}
