package com.vurtnewk.emsg.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EMSGDBHelper extends SQLiteOpenHelper {

    public EMSGDBHelper(Context context, int version) {
        super(context, "EMSG_DB", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建一个消息列表
        String message_info_table = "create table message_info_table("
                + "id integer primary key autoincrement,"
                + "sid varchar(255),"             //消息会话id
                + "type varchar(255),"            //消息类型   （好友请求/聊天消息/系统消息）
                + "msg_state boolean,"            //判断是消息来源（true接收 false发送）
                + "myjid varchar(255),"        //我的jid
                + "jid varchar(255),"          //对方jid
                + "mynickname varchar(255),"   //我的昵称
                + "nickname varchar(255),"     //对方昵称
                + "myheadurl varchar(255),"    //我的头像
                + "headurl varchar(255),"      //对方头像
                + "msg_content varchar(255),"     //消息内容
                + "msg_type varchar(255),"        //聊天消息类型 (text image voice)
                + "msg_readorno varchar(255),"    //0为未读 1为已读
                + "msg_time varchar(255),"        //消息接收时间
                + "voice_time varchar(255),"
                + "msg_GeoLat varchar(255),"      //经度
                + "msg_GeoLng varchar(255),"      //纬度
                + "msg_imageUrlId varchar(255),"//图片url
                + "msg_attr varchar(255),"//attr
                + "msg_id varchar(255))";
        //创建一个会话列表
        String message_session_table = "create table message_session_table("
                + "id integer primary key autoincrement,"
                + "sid varchar(255),"             //消息会话id
                + "type varchar(255),"            //消息类型  （好友请求/聊天消息/系统消息）
                + "myjid varchar(255),"        //我的jid
                + "jid varchar(255),"          //对方jid
                + "mynickname varchar(255),"   //我的昵称
                + "nickname varchar(255),"     //对方昵称
                + "myheadurl varchar(255),"    //我的头像
                + "headurl varchar(255),"      //对方头像
                + "msg_content varchar(255),"     //消息内容
                + "msg_type varchar(255),"        //聊天消息类型 (text image voice)
                + "msg_lasttime varchar(255),"    //最后一条消息接收时间
                + "msg_noread_num varchar(255)," //消息未读数
//                + "sex varchar(255)," //性别
//                + "age varchar(255),"//年龄
                + "msg_attr varchar(255),"//年龄
                + "isfriend varchar(255))"; //是否是好友关系
        sqLiteDatabase.execSQL(message_info_table);
        sqLiteDatabase.execSQL(message_session_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }
}
