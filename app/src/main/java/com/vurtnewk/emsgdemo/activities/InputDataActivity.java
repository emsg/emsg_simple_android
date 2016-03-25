package com.vurtnewk.emsgdemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.base.BaseActivity;
import com.vurtnewk.emsgdemo.utils.VToast;

/**
 * @author VurtneWk
 * @time created on 2016/3/25.14:41
 */
public class InputDataActivity extends BaseActivity {

    private EditText mEtContent;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);
        mEtContent = (EditText) findViewById(R.id.mEtContent);
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbarTitle = (TextView) findViewById(R.id.mToolbarTitle);

        String title = getIntent().getStringExtra("title");
        mToolbarTitle.setText(title);
        String hint = getIntent().getStringExtra("hint");
        mEtContent.setHint(hint);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.inflateMenu(R.menu.menu_confirm);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mMenuConfirm:
                        String content = mEtContent.getText().toString();
                        if (!TextUtils.isEmpty(content)) {
                            Intent intent = new Intent();
                            intent.putExtra("data", content);
                            InputDataActivity.this.setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            VToast.showShortToast(mContext, "内容不能为空");
                        }
                        break;
                }
                return false;
            }
        });
    }

    public static void startAction(Activity activity, String title, String hint, int requestCode) {
        Intent intent = new Intent(activity, InputDataActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("hint", hint);
        activity.startActivityForResult(intent, requestCode);
    }

}
