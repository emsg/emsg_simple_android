
package com.vurtnewk.emsg.client.asynctask.qiniu;

import android.os.AsyncTask;

import com.vurtnewk.emsg.client.asynctask.IDownLoadTask;
import com.vurtnewk.emsg.client.asynctask.TaskCallBack;
import com.vurtnewk.emsg.client.utils.DownloadUtil;


class DownloadTask implements IDownLoadTask {

    public void download(String key, String filename, TaskCallBack mTaskCallBack) {
        this.download(key, filename, false, mTaskCallBack);
    }

    /**
     * @param key       文件链接
     * @param filename  文件名
     * @param thumbnail 是否缩略图
     * @param callback  下载回调函数
     */
    public void download(String key, final String filename, boolean thumbnail,
                         final TaskCallBack callback) {
        String param = thumbnail ? "?imageView2/2/w/200/h/200" : "";
        String url = "http://emsg.qiniudn.com/" + key + param;
        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    DownloadUtil.get(params[0], params[1]);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    callback.onSuccess(filename);
                } else {
                    callback.onFailure();
                }
            }

        }.execute(url, filename);

    }

}
