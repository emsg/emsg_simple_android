
package com.vurtnewk.emsg;

/**
 * @author liangc
 */
public interface Define {

    int heartBeat = 50 * 1000;

    String KILL = "\01\02\03";
    /**
     *
     */
    String END_TAG = "\01";
    /**
     * 心跳
     */
    String HEART_BEAT = "\02";
    /**
     * 服务器KILL信号
     */
    String SERVER_KILL = "\03";
    /**
     * 心跳频率
     */
    int HEART_BEAT_FREQ = 1000 * 50;

    int MSG_TYPE_OPEN_SESSION = 0;
    int MSG_TYPE_CHAT = 1;
    int MSG_TYPE_GROUP_CHAT = 2;
    int MSG_TYPE_STATE = 3;
    int MSG_TYPE_SYSTEM = 4;
    int MSG_TYPE_P2P_SOUND = 5;
    int MSG_TYPE_P2P_VIDEO = 6;
    int MSG_TYPE_PUBSUB = 7;

    int ACK_DISABLE = 0;
    int ACK_ENABLE = 1;

    String VSN = "0.0.1";
    //    static String EMSG_HOST = "server.lcemsg.com";
//    static int EMSG_PORT = 4222;
    static String TOKEN_HOST = "http://server.lcemsg.com/uptoken/";

    static final String ACTION_HEATBEAT = "com.emsg.client";
}
