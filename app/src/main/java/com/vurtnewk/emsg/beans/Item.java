package com.vurtnewk.emsg.beans;

public class Item {

	private String id = null;
	private String node = null;
	private String cb = null;
	private String title = null;
	private String summary = null;
	private Long ct = null;
	private Long et = null;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getCb() {
		return cb;
	}
	public void setCb(String cb) {
		this.cb = cb;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public Long getCt() {
		return ct;
	}
	public void setCt(Long ct) {
		this.ct = ct;
	}
	public Long getEt() {
		return et;
	}
	public void setEt(Long et) {
		this.et = et;
	}
	@Override
	public String toString() {
		return "Item [id=" + id + ", node=" + node + ", cb=" + cb + ", title="
				+ title + ", summary=" + summary + ", ct=" + ct + ", et=" + et
				+ "]";
	}
	
}
