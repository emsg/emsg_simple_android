package com.vurtnewk.emsgdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.adapter.UserAdapter;
import com.vurtnewk.emsgdemo.base.BaseActivity;
import com.vurtnewk.emsgdemo.constants.UrlConstants;
import com.vurtnewk.emsgdemo.entity.SearchUserEntity;
import com.vurtnewk.emsgdemo.entity.UserInfo;
import com.vurtnewk.emsgdemo.utils.http.HttpClient;
import com.vurtnewk.emsgdemo.utils.http.HttpListener;
import com.vurtnewk.emsgdemo.utils.http.HttpRequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VurtneWk
 * @time created on 2016/3/21.19:39
 */
public class SearchUserActivity extends BaseActivity {

    ListView mLvContacts;
    Toolbar mToolbar;
    private Button mBtnSearch;
    private EditText mEtSearch;
    private List<UserInfo> mList = new ArrayList<>();
    UserAdapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        mLvContacts = (ListView) findViewById(R.id.mLvContacts);
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBtnSearch = (Button) findViewById(R.id.mBtnSearch);
        mEtSearch = (EditText) findViewById(R.id.mEtSearch);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnSearch();
            }
        });
        mUserAdapter = new UserAdapter(mContext,mList);
        mLvContacts.setAdapter(mUserAdapter);
        mLvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo userInfo = mList.get(position);
                startActivity(new Intent(mContext,OtherUserInfoActivity.class).putExtra("userId",userInfo.getId()));
            }
        });
    }

    private void mBtnSearch() {
        HttpRequestParams httpRequestParams = new HttpRequestParams(UrlConstants.USER_SERVICE, UrlConstants.USER_METHOD_FIND_USER);
        httpRequestParams.put("nickname",mEtSearch.getText().toString());
        HttpClient.post(httpRequestParams, new HttpListener() {
            @Override
            public void onSuccess(String result) {
                SearchUserEntity searchUserEntity = new Gson().fromJson(result, SearchUserEntity.class);
                List<UserInfo> user_list = searchUserEntity.getEntity().getUser_list();
                mList.clear();
                mList.addAll(user_list);
                mUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {

            }

            @Override
            public void onStart() {

            }
        });
    }


}
