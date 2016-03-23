package com.vurtnewk.emsg.beans;


/*	
	{
    "envelope":{
      "id":"UUID，要求必须唯一"
      "from":"发送人JID",
      "to":"接收人JID",
      "type":"int型，含义是 消息类型",
      "ack":"int型，0 或空是不必响应，1 必须响应",
      "ct":"13位时间戳，由服务器来补充此值",
      "pwd":"只有当 type=0 时，即 opensession 时，才会使用此属性",
      "gid":"群ID，只在 type=2 时会用到此属性"
    },
    "vsn":"消息版本(预留属性)",
    "payload":{...}
}	
*/
/**
 * 信封:
 * 包含了消息递送的所有必要信息，
 * 根据不同的消息类型，会有不同的属性组合
 * 
 * @author liangc
 */
public class Envelope implements IEnvelope{

	private String id = null;
	private String from = null;
	private String to = null;
	private Integer type = null;
	private Integer ack = null;
	private String ct = null;
	private String pwd = null;
	private String gid = null;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getAck() {
		return ack;
	}
	public void setAck(Integer ack) {
		this.ack = ack;
	}
	public String getCt() {
		return ct;
	}
	public void setCt(String ct) {
		this.ct = ct;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	
	@Override
	public String toString() {
		return "Envelope [id=" + id + ", from=" + from + ", to=" + to
				+ ", type=" + type + ", ack=" + ack + ", ct=" + ct + ", pwd="
				+ pwd + ", gid=" + gid + "]";
	}
	
}
