package com.vurtnewk.emsgdemo.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.vurtnewk.emsgdemo.R;

import java.io.File;
import java.util.Date;

/**
 * @author 蒋洪波
 * @file RequestWindow.java
 * @brief PopupWindow工具，管理大部分PopupWindow的生成
 * @date 2015-6-10
 * Copyright (c) 2015, 北京球友圈网络科技有限责任公司
 * All rights reserved.
 */
public class RequestWindow {

    /**
     * @author 蒋洪波
     * @brief 分享的回调方法
     * @data 2015年3月31日
     */
    public interface ShareCallBack {
        public void callBack(String platform);
    }

    /**
     * @author 搜索回调
     * @author 蒋洪波
     * @data 2015年4月20日
     */
    public interface SearchCallBack {
        public void callBack(String keyWords);
    }

    /**
     * @param context
     * @param alpha
     * @brief 设置当前Window的透明度
     */
    private static void setAlpha(final Activity context, float alpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = alpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }

    private static PopupWindow mPopupWindow;
    private static final int PICTURE = 20;
    protected static final int CAMERA_REQUEST_CODE = 26;
    //! 本地文件夹名称
    private static final String localTempImgDir = "yueqiu";
    //! 弹出界面所属的上下文
    private static Activity context;
    //! 上传文件名称,由外部直接访问获得，判空处理
    public static String localTempImgFileName;
    //! 上传文件地址
    public static File mUploadFilePath;
    //! 分享回调方法
    private static ShareCallBack callBack;
    //! 搜索回调
    private static SearchCallBack searchCallBack;


    public static PopupWindow showpopupWindow(final Activity context, View tv_title) {
        RequestWindow.context = context;
        View popupwindowView = View.inflate(context, R.layout.fa_dongtai_popupwindow, null);
        setupPopopwindow(popupwindowView);

        popupwindowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            }
        });
        mPopupWindow = new PopupWindow(popupwindowView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(R.style.popwin_anim_style_01);
        mPopupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        setAlpha(context, 0.6f);
        mPopupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setAlpha(context, 1f);
            }
        });
        return mPopupWindow;
    }

    private static void setupPopopwindow(View popupwindowView) {
        Button btn_paizhao = (Button) popupwindowView.findViewById(R.id.btn_paizhao);
        Button btn_xuanze = (Button) popupwindowView.findViewById(R.id.btn_xuanze);
        Button btn_quxiao = (Button) popupwindowView.findViewById(R.id.btn_quxiao);

        btn_paizhao.setOnClickListener(new popuListener());
        btn_xuanze.setOnClickListener(new popuListener());
        btn_quxiao.setOnClickListener(new popuListener());
    }


    public static class popuListener implements View.OnClickListener {

        boolean dismiss = true;

        public popuListener() {
        }

        public popuListener(boolean isDismiss) {
            this.dismiss = isDismiss;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_paizhao:
                    getCamera();
                    break;
                case R.id.btn_xuanze:
                    // 调用系统相册
                    getLocal();
                    break;
                case R.id.btn_quxiao:
                    cancelPic();
                    break;
            }
            if (dismiss) {
                if (mPopupWindow != null && mPopupWindow.isShowing())
                    mPopupWindow.dismiss();
            }
        }

        /**
         * @brief 取消选择图片
         */
        private static void cancelPic() {
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        }

        /**
         * @brief 获得相册图片
         */
        private static void getLocal() {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            context.startActivityForResult(i, PICTURE);
        }

        /**
         * @brief 拍照选择
         */
        private void getCamera() {

            // 先验证手机是否有sdcard
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                try {
                    File dir = new File(Environment.getExternalStorageDirectory() + "/" + localTempImgDir);
                    if (!dir.exists())// 不存在则创建
                        dir.mkdirs();

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    localTempImgFileName = new Long(new Date().getTime()).toString();
                    mUploadFilePath = new File(dir, localTempImgFileName);
                    Uri u = Uri.fromFile(mUploadFilePath);
                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                    context.startActivityForResult(intent, CAMERA_REQUEST_CODE);

                } catch (ActivityNotFoundException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(context, "没有找到储存目录", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "没有储存卡", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * @param uri
     * @return
     * @brief 将URI转为本地地址
     */
    public static String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


}
