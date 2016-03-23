package com.vurtnewk.emsg.beans;

/**
 * 消息会话  实体类
 * Created by Administrator on 2015/9/29.
 * <p/>
 * +"id integer primary key autoincrement,"
 * + "sid varchar(255),"             //消息会话id（我的球友号+对方球友号）
 * + "type varchar(255),"            //消息类型  （好友请求/聊天消息/系统消息）
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
 * + "msg_lasttime varchar(255),"    //最后一条消息接收时间
 * + "msg_noread_num varchar(255))"; //消息未读数
 */
public class MsgSessionEntity {
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
    private String msg_lasttime;
    private String msg_noread_num;
    private String sex;
    private String age;
    private String isfriend;
    private String attr;

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(String isfriend) {
        this.isfriend = isfriend;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

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
//
//    public String getQiuyouno() {
//        return qiuyouno;
//    }
//
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

    public String getMsg_lasttime() {
        return msg_lasttime;
    }

    public void setMsg_lasttime(String msg_lasttime) {
        this.msg_lasttime = msg_lasttime;
    }

    public String getMsg_noread_num() {
        return msg_noread_num;
    }

    public void setMsg_noread_num(String msg_noread_num) {
        this.msg_noread_num = msg_noread_num;
    }

    @Override
    public String toString() {
        return "MsgSessionEntity{" +
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
                ", msg_lasttime='" + msg_lasttime + '\'' +
                ", msg_noread_num='" + msg_noread_num + '\'' +
                '}';
    }
}
