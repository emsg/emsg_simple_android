package com.vurtnewk.emsg.beans;

/**
 * Created by Administrator on 2015/9/29.
 * 聊天记录  实体类
 * <p/>
 * + "id integer primary key autoincrement,"
 * + "sid varchar(255),"             //消息会话id（我的球友号+对方球友号）
 * + "type varchar(255),"            //消息类型   （好友请求/聊天消息/系统消息）
 * + "msg_state boolean,"            //判断是消息来源（true接收 false发送）
 * + "myqiuyouno varchar(255),"      //我的球友号
 * + "qiuyouno varchar(255),"        //对方球友号
 * + "from_jid varchar(255),"        //接收方jid
 * + "to_jid varchar(255),"          //发送方jid
 * + "from_nickname varchar(255),"   //接收方昵称
 * + "to_nickname varchar(255),"     //发送方昵称
 * + "from_headurl varchar(255),"    //接收方头像
 * + "to_headurl varchar(255),"      //发送方头像
 * + "msg_content varchar(255),"     //消息内容
 * + "msg_type varchar(255),"        //聊天消息类型 (text image voice)
 * + "msg_readorno varchar(255),"    //0为未读 1为已读
 * + "msg_time varchar(255),"        //消息接收时间
 * + "msg_GeoLat varchar(255),"      //经度
 * + "msg_GeoLng varchar(255),"      //纬度
 * + "msg_imageUrlId varchar(255))"; //图片url
 */
public class MessageInfoEntity {
    private String id;
    private String sid;
    private String type;
    //    private String myqiuyouno;
//    private String qiuyouno;
    private String myjid;
    private String jid;
    private String mynickname;
    private String nickname;
    private String myheadurl;
    private String headurl;
    private String msg_content;
    private String msg_type;
    private String msg_readorno;
    private String msg_time;
    private String voice_time;
    private String msg_GeoLat;
    private String msg_GeoLng;
    private String msg_imageUrlId;
    private String attr;

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getVoice_time() {
        return voice_time;
    }

    public void setVoice_time(String voice_time) {
        this.voice_time = voice_time;
    }

    private Boolean msg_state;//判断是消息来源（true接收 false发送）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    public String getMyqiuyouno() {
//        return myqiuyouno;
//    }
//
//    public void setMyqiuyouno(String myqiuyouno) {
//        this.myqiuyouno = myqiuyouno;
//    }

//    public String getQiuyouno() {
//        return qiuyouno;
//    }

//    public void setQiuyouno(String qiuyouno) {
//        this.qiuyouno = qiuyouno;
//    }

    public String getMyjid() {
        return myjid;
    }

    public void setMyjid(String myjid) {
        this.myjid = myjid;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getMynickname() {
        return mynickname;
    }

    public void setMynickname(String mynickname) {
        this.mynickname = mynickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMyheadurl() {
        return myheadurl;
    }

    public void setMyheadurl(String myheadurl) {
        this.myheadurl = myheadurl;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    public String getMsg_readorno() {
        return msg_readorno;
    }

    public void setMsg_readorno(String msg_readorno) {
        this.msg_readorno = msg_readorno;
    }

    public String getMsg_time() {
        return msg_time;
    }

    public void setMsg_time(String msg_time) {
        this.msg_time = msg_time;
    }

    public String getMsg_GeoLat() {
        return msg_GeoLat;
    }

    public void setMsg_GeoLat(String msg_GeoLat) {
        this.msg_GeoLat = msg_GeoLat;
    }

    public String getMsg_GeoLng() {
        return msg_GeoLng;
    }

    public void setMsg_GeoLng(String msg_GeoLng) {
        this.msg_GeoLng = msg_GeoLng;
    }

    public String getMsg_imageUrlId() {
        return msg_imageUrlId;
    }

    public void setMsg_imageUrlId(String msg_imageUrlId) {
        this.msg_imageUrlId = msg_imageUrlId;
    }

    public Boolean getMsg_state() {
        return msg_state;
    }

    public void setMsg_state(Boolean msg_state) {
        this.msg_state = msg_state;
    }

    @Override
    public String toString() {
        return "MessageInfoEntity{" +
                "id='" + id + '\'' +
                ", sid='" + sid + '\'' +
                ", type='" + type + '\'' +
//                ", myqiuyouno='" + myqiuyouno + '\'' +
//                ", qiuyouno='" + qiuyouno + '\'' +
                ", myjid='" + myjid + '\'' +
                ", jid='" + jid + '\'' +
                ", mynickname='" + mynickname + '\'' +
                ", nickname='" + nickname + '\'' +
                ", myheadurl='" + myheadurl + '\'' +
                ", headurl='" + headurl + '\'' +
                ", msg_content='" + msg_content + '\'' +
                ", msg_type='" + msg_type + '\'' +
                ", msg_readorno='" + msg_readorno + '\'' +
                ", msg_time='" + msg_time + '\'' +
                ", msg_GeoLat='" + msg_GeoLat + '\'' +
                ", msg_GeoLng='" + msg_GeoLng + '\'' +
                ", msg_imageUrlId='" + msg_imageUrlId + '\'' +
                ", msg_state=" + msg_state +
                '}';
    }
}
