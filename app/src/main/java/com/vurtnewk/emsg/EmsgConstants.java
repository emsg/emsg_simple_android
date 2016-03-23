
package com.vurtnewk.emsg;

public class EmsgConstants {

    // 保存离线消息的数
    static final int OFFLINE_MSG_COUNT = 10;
    //接收离线消息的action
    public static final String MSG_ACTION_RECDATA = "action.emsg.reciverdata";
    //接收在线消息的action
    public static final String MSG_ACTION_RECOFFLINEDATA = "action.msg.reciverofflinedata";
    //接收消息通道打开的action
    public static final String MSG_ACTION_SESSONOPENED = "action.msg.sessonopened";
    //消息类型为音频
    public static final String MSG_TYPE_FILEAUDIO = "audio";
    //消息类型为图片
    public static final String MSG_TYPE_FILEIMG = "image";
    //消息类型为文本
    public static final String MSG_TYPE_FILETEXT = "text";

    static final int MSG_FILETYPE_IMAGE = 1;
    static final int MSG_FILETYPE_AUDIO = 2;
    static final int MSG_ERRORCODE_INIT = 101;
    static final int MSG_ERRORCODE_NET = 102;
    static final int MSG_ERRORCODE_INTRRU = 103;
    static final int MSG_ERRORCODE_OTHER = 106;
    static final int MSG_ERRORCODE_APPKEY = 104;
    static final int MSG_ERRORCODE_LOGIN = 105;
}
