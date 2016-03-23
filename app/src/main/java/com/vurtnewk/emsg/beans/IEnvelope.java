package com.vurtnewk.emsg.beans;

public interface IEnvelope {

	public String getId();
	
	public void setId(String id);
	
	public String getFrom();
	
	public void setFrom(String from);
	
	public String getTo();
	
	public void setTo(String to);
	
	public Integer getType();
	
	public void setType(Integer type);
	
	public Integer getAck();
	
	public void setAck(Integer ack);
	
	public String getCt();
	
	public void setCt(String ct);
	
	public String getPwd();
	
	public void setPwd(String pwd);
	
	public String getGid();
	
	public void setGid(String gid);
	
	@Override
	public String toString();
	public static final int TYPE_CHAT_REC = 1;
	public static final int TYPE_SOCKET_KILL = 0;
	public static final int TYPE_MESSAGE_SERVER = 3;
	
}
