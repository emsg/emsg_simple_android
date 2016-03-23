package com.vurtnewk.emsg.beans;

import java.util.HashMap;
import java.util.Map;

public class Entity {
	
	private String result = null;
	
	private String reason = null;
	/**
	 * 增加一个传递扩展属性的方式，应对系统消息
	 */
	public Map<String,String> attrs = new HashMap<String,String>();
	
	public Map<String, String> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, String> attrs) {
		this.attrs = attrs;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "Entity [result=" + result + ", reason=" + reason + "]";
	}
	
}
