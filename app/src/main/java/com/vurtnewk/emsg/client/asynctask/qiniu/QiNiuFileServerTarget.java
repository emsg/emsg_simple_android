package com.vurtnewk.emsg.client.asynctask.qiniu;

import android.content.Context;

import com.vurtnewk.emsg.client.asynctask.AbsFileServerTarget;


public class QiNiuFileServerTarget extends AbsFileServerTarget {

    private final String FILEHOST = "http://fileserver.qiuyouzone.com/fileserver/upload/";

    public QiNiuFileServerTarget(Context mContext) {
        super(mContext);
    }

    @Override
    public void setDownLoadTask() {
        this.mDownLoadTask = new DownloadTask();
    }

    @Override
    public void setUpLoadTask() {
        this.mUpLoadTask = new UploadTask(mContext);
    }

    @Override
    public String getImageUrlPath(String content) {
        return FILEHOST + content + "?" + "imageView2/2/w/200/h/200";
    }

    @Override
    public String getAudioUrlPath(String content) {
        return FILEHOST + content;
    }


}
