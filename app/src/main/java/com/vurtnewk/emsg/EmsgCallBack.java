
package com.vurtnewk.emsg;

public abstract class EmsgCallBack {
    /* 消息发送成功的回调 */
    public abstract void onSuccess();

    /* 消息发送失败的回调 */
    public abstract void onError(TypeError mErrorType);

    /* 发送中状态的回调 */
    public void onProgress(int current, int max) {

    }

    public enum TypeError {
        /* 超时 */TIMEOUT, /* 网络异常 */NETERROR, /* 连接通道异常 */SOCKETERROR, /* 认证错误 */AUTHERROR, /* 文件上传失败 */FILEUPLOADERROR, /* 连接异常关闭 */SESSIONCLOSED
    }

    long mCallBackTime;
}
