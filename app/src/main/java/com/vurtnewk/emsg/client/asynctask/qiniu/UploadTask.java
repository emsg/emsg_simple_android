package com.vurtnewk.emsg.client.asynctask.qiniu;

import android.content.Context;
import android.net.Uri;

import com.qiniu.auth.JSONObjectRet;
import com.qiniu.io.IO;
import com.qiniu.io.PutExtra;
import com.qiniu.utils.FileUri;
import com.qiniu.utils.QiniuException;
import com.vurtnewk.emsg.client.asynctask.IUpLoadTask;
import com.vurtnewk.emsg.client.asynctask.TaskCallBack;
import com.vurtnewk.emsg.client.upload.TokenGenerator;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

class UploadTask implements IUpLoadTask {

    private Context context;
    private String key;

    public UploadTask(Context context) {
        this.context = context;
    }

    /**
     * 上传uri文件
     *
     * @param uri
     * @param callback
     */
    public void upload(Uri uri, TaskCallBack callback) {
        File file = FileUri.getFile(context, uri);
        upload(file, callback);
    }

    /**
     * 上传文件
     *
     * @param file
     * @param callback
     */
    public void upload(File file, final TaskCallBack callback) {
        String uptoken = TokenGenerator.getAccesskey(context, "emsg");
        key = UUID.randomUUID().toString() + "_" + file.getName(); // 自动生成key
        PutExtra extra = new PutExtra();
        extra.params = new HashMap<String, String>();
        IO.putFile(uptoken, key, file, extra, new JSONObjectRet() {

            @Override
            public void onFailure(QiniuException obj) {
                callback.onFailure();
            }

            @Override
            public void onSuccess(JSONObject obj) {
                callback.onSuccess(key);
            }
        });
    }

    /**
     * 异步上传完成/失败后通知,在主线程中,可以更新界面
     * @author tiger
     */
    public interface UploadTaskCallback {
        /**
         * 上传成功后调用
         */
        public void onSuccess(String key);

        /**
         * 上传失败后调用
         */
        public void onFailure();
    }

}
