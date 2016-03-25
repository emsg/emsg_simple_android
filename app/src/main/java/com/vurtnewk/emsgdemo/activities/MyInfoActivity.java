package com.vurtnewk.emsgdemo.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.base.BaseActivity;
import com.vurtnewk.emsgdemo.base.BaseApplication;
import com.vurtnewk.emsgdemo.constants.SettingsConstants;
import com.vurtnewk.emsgdemo.constants.UrlConstants;
import com.vurtnewk.emsgdemo.entity.SimpleData;
import com.vurtnewk.emsgdemo.entity.UserInfo;
import com.vurtnewk.emsgdemo.ui.LoadingView;
import com.vurtnewk.emsgdemo.ui.VBasePopWin;
import com.vurtnewk.emsgdemo.utils.ACache;
import com.vurtnewk.emsgdemo.utils.CameraUtils;
import com.vurtnewk.emsgdemo.utils.PicUtils;
import com.vurtnewk.emsgdemo.utils.RequestWindow;
import com.vurtnewk.emsgdemo.utils.VLog;
import com.vurtnewk.emsgdemo.utils.http.HttpClient;
import com.vurtnewk.emsgdemo.utils.http.HttpListener;
import com.vurtnewk.emsgdemo.utils.http.HttpRequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;

/**
 * @author VurtneWk
 * @time created on 2016/3/21.10:50
 * 我的个人资料
 * TODO 文件上传的是copy来的 待修改 太乱
 */
public class MyInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST_CODE = 26;
    private static final int ALBUM_REQUEST_CODE = 20;
    private static final int REQUEST_CODE_NICKNAME = 5;
    private static final int REQUEST_CODE_EMAIL = 11;

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private View mLlPhoto;
    private ImageView mIvAvatar;
    private ImageLoader mImageLoader;
    TextView mTvEmsg;
    TextView mTvNickName;
    TextView mTvGender;
    TextView mTvBirthday;
    TextView mTvEmail;
    private UserInfo user;
    LoadingView mLoadingView;
    View mLlNickName;
    View mLlGender;
    View mLlBirthday;
    View mLlEmail;

    VBasePopWin mVBasePopWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        initView();
        initData();
    }

    private void initData() {
        mImageLoader = ImageLoader.getInstance();
        user = (UserInfo) ACache.get(mContext).getAsObject(SettingsConstants.CACHE_USER);
        if (user != null) {
            mLlPhoto.setOnClickListener(this);
            mImageLoader.displayImage(user.getIcon(), mIvAvatar);
            mTvEmsg.setText(user.getId());
            mTvNickName.setText(user.getNickname());
            mTvGender.setText(user.getGender());
            mTvBirthday.setText(user.getBirthday());
            mTvEmail.setText(user.getEmail());
        }
        mVBasePopWin = new VBasePopWin(this, new String[]{"男", "女"}, new VBasePopWin.ContentListener() {
            @Override
            public void onContentListener(int position, String content, PopupWindow mPopupWindow) {
                updateUser("gender", content);
            }
        }, false);

        mLlBirthday.setOnClickListener(this);
        mLlGender.setOnClickListener(this);
        mLlNickName.setOnClickListener(this);
        mLlEmail.setOnClickListener(this);
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbarTitle = (TextView) findViewById(R.id.mToolbarTitle);
        mToolbarTitle.setText(R.string.personalInfo);
        mToolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLlPhoto = findViewById(R.id.mLlPhoto);
        mIvAvatar = (ImageView) findViewById(R.id.mIvAvatar);
        mTvEmsg = (TextView) findViewById(R.id.mTvEmsg);
        mTvNickName = (TextView) findViewById(R.id.mTvNickName);
        mTvGender = (TextView) findViewById(R.id.mTvGender);
        mTvBirthday = (TextView) findViewById(R.id.mTvBirthday);
        mTvEmail = (TextView) findViewById(R.id.mTvEmail);
        mLoadingView = (LoadingView) findViewById(R.id.mLoadingView);
        mLlNickName = findViewById(R.id.mLlNickName);
        mLlGender = findViewById(R.id.mLlGender);
        mLlBirthday = findViewById(R.id.mLlBirthday);
        mLlEmail = findViewById(R.id.mLlEmail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mLlPhoto:
                RequestWindow.showpopupWindow(this, null);
                break;
            case R.id.mLlGender:
                mVBasePopWin.show();
                break;
            case R.id.mLlBirthday:
                mLlBirthday();
                break;
            case R.id.mLlNickName:
                InputDataActivity.startAction(this, "修改昵称", mTvNickName.getText().toString(), REQUEST_CODE_NICKNAME);
                break;
            case R.id.mLlEmail:
                InputDataActivity.startAction(this, "修改邮箱", mTvEmail.getText().toString(), REQUEST_CODE_EMAIL);
                break;
        }
    }

    private void mLlBirthday() {
        new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                updateUser("birthday", year + "-" + monthOfYear + "-" + dayOfMonth);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VLog.i(TAG, "requestCode:" + requestCode + (data != null));
        switch (requestCode) {
            case ALBUM_REQUEST_CODE:
                updateUserPhoto("file", data, null);
                break;
            case CAMERA_REQUEST_CODE:
                if (resultCode == -1 && RequestWindow.mUploadFilePath != null) {
                    new CompressPicTask().execute();
                }
                break;
            case REQUEST_CODE_NICKNAME:
                if (resultCode == RESULT_OK && data != null) {
                    updateUser("nickname", data.getStringExtra("data"));
                }
                break;
            case REQUEST_CODE_EMAIL:
                if (resultCode == RESULT_OK && data != null) {
                    updateUser("email", data.getStringExtra("data"));
                }
                break;
            default:
                break;
        }
    }

    private class CompressPicTask extends AsyncTask<Void, Void, File> {

        @Override
        protected File doInBackground(Void... params) {
            // 如果相机拍照时候屏幕被反转，需要转换照片的角度，异步线程处理
            CameraUtils.saveBitmap(RequestWindow.mUploadFilePath);
            return RequestWindow.mUploadFilePath;
        }

        @Override
        protected void onPostExecute(File file) {
            String path = file.getAbsolutePath();
            updateUserPhoto("file", null, path);
        }
    }

    public void updateUserPhoto(final String fields, final Intent data, final String filePath) {
        //先进行压缩
        if (data != null || !TextUtils.isEmpty(filePath)) {
            // 压缩图片
            boolean isCompress = PicUtils.compressToLocal(data, filePath, MyInfoActivity.this, 600, 600, "upload.jpg");
            if (isCompress) {// 通知主线程上传已压缩文件
                final File f = new File(SettingsConstants.upLoadFileDir.getAbsolutePath(), "/upload.jpg");
                uploadFile(fields, f);
            } else {
//                showError("压缩文件失败");
            }
        }
    }

    public void uploadFile(String field, final File... files) {
        if (files == null || field == null)
            return;
        if (files != null) {
            try {
                RequestParams uploadParams = new RequestParams();
                uploadParams.put(field, files);
                uploadParams.put("appid", "test");
                uploadParams.put("appkey", "83bf20e2b20141e098fa6b721f693163");
                if (files[0].getAbsolutePath().toString().contains("amr")) {//判断语音
                    uploadParams.put("file_type", "audio");
                } else {
                    uploadParams.put("file_type", "image");
                }
                uploadParams.setForceMultipartEntityContentType(true);
                HttpClient.postFile(UrlConstants.BASE_FILE_URL_UPLOAD, uploadParams, new HttpListener() {
                    @Override
                    public void onSuccess(String result) {
                        VLog.i(TAG, "result:" + result);
                        JSONObject o = null;
                        try {
                            o = new JSONObject(result);
                            JSONObject entity = o.optJSONObject("entity");
                            String photoId = entity.optString("id");
                            if (!TextUtils.isEmpty(photoId)) {
                                set_icon(UrlConstants.BASE_FILE_URL_GET + photoId);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        VLog.e(TAG, "error:" + error);
                    }

                    @Override
                    public void onStart() {
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUser(final String key, final String value) {
        HttpRequestParams httpRequestParams = new HttpRequestParams(UrlConstants.USER_SERVICE, UrlConstants.USER_METHOD_UPDATE_USER_INFO);
        httpRequestParams.put(key, value);
        HttpClient.post(httpRequestParams, new HttpListener() {
            @Override
            public void onSuccess(String result) {
                mLoadingView.hide();
                SimpleData simpleData = new Gson().fromJson(result, SimpleData.class);
                if (simpleData.isSuccess()) {
                    switch (key) {
                        case "nickname":
                            user.setNickname(value);
                            mTvNickName.setText(value);
                            break;
                        case "gender":
                            user.setGender(value);
                            mTvGender.setText(value);
                            break;
                        case "birthday":
                            user.setBirthday(value);
                            mTvBirthday.setText(value);
                            break;
                        case "email":
                            mTvEmail.setText(value);
                            user.setEmail(value);
                            break;
                    }
                    BaseApplication.getInstance().setUser(user);
                    ACache.get(BaseApplication.getAppContext()).put(SettingsConstants.CACHE_USER, user);
                } else {

                }
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

    private void set_icon(final String url) {
        HttpRequestParams httpRequestParams = new HttpRequestParams(UrlConstants.USER_SERVICE, UrlConstants.USER_METHOD_SET_ICON);
        httpRequestParams.put("icon_url", url);
        HttpClient.post(httpRequestParams, new HttpListener() {
            @Override
            public void onSuccess(String result) {
                ImageLoader.getInstance().displayImage(url, mIvAvatar);
                user.setIcon(url);
                BaseApplication.getInstance().setUser(user);
                ACache.get(mContext).put(SettingsConstants.CACHE_USER, user);
                mLoadingView.hide();
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

}
