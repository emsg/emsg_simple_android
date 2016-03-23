package com.vurtnewk.emsg.service;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vurtnewk.emsg.HttpUtils;
import com.vurtnewk.emsg.MyLogger;
import com.vurtnewk.emsg.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * 黑名单操作
 * @author liangc
 */
public class BlackList {
	
	
	static MyLogger logger = new MyLogger(BlackList.class);
	
	private String INF_URL = "http://192.168.1.11:4280";
	private final static String INF_SERVICE = "emsg_black_list";
	private final static String INF_METHOD_APPEND = "append";
	private final static String INF_METHOD_REMOVE = "remove";
	private final static String INF_METHOD_FETCH = "fetch";
	
	public BlackList(){}
	
	public BlackList(String INF_URL){
		setInfUrl(INF_URL);
	}
	public void setInfUrl(String INF_URL){
		this.INF_URL = INF_URL;
	}
	
	private String buildArgs(String method,Map<String,String> args){
		return buildArgs(INF_SERVICE,method,args);
	}
	private String buildArgs(String service,String method,Map<String,String> args){
		JsonObject json = new JsonObject();
		json.addProperty("sn", UUID.randomUUID().toString());
		json.addProperty("service", service);
		json.addProperty("method", method);
		json.add("params", new Gson().toJsonTree(args));
		return json.toString();
	}
	
	/**
	 * 添加黑名单；<br>
	 * 例如要将 test.com 域的 usera 添加到 userb 的黑名单中
	 * @param from_jid = usera@test.com
	 * @param to_jid = userb@test.com
	 * @param pwd = 登录 emsg 时用的 pwd，应该是个 token
	 * @throws Exception 添加失败时，会返回异常
	 */
	public void append(String from_jid,String to_jid,String pwd) throws Exception {
		Map<String,String> params = new HashMap<String,String>();
		params.put("pwd", pwd);
		params.put("from", from_jid);
		params.put("to", to_jid);
		String body = buildArgs(INF_METHOD_APPEND,params);
		logger.debug("blacklist_append_body="+body);
		Map<String,String> form = new HashMap<String,String>();
		form.put("body", body);
		String rtn = HttpUtils.http(INF_URL, form);
		JsonObject json = JsonUtil.parse(rtn);
		logger.debug(json.toString());
		if(!JsonUtil.getAsBoolean(json, "success")){
			throw new Exception(JsonUtil.getAsString(json, "entity"));
		}
	} 
	/**
	 * 移除黑名单；<br>
	 * 例如要将 test.com 域的 usera 在 userb 的黑名单中移出
	 * @param from_jid = usera@test.com
	 * @param to_jid = userb@test.com
	 * @param pwd = 登录 emsg 时用的 pwd，应该是个 token
	 * @throws Exception 删除失败时，会返回异常
	 */
	public void remove(String from_jid,String to_jid,String pwd) throws Exception {
		Map<String,String> params = new HashMap<String,String>();
		params.put("pwd", pwd);
		params.put("from", from_jid);
		params.put("to", to_jid);
		String body = buildArgs(INF_METHOD_REMOVE,params);
		logger.debug("blacklist_remove_body="+body);
		Map<String,String> form = new HashMap<String,String>();
		form.put("body", body);
		String rtn = HttpUtils.http(INF_URL, form);
		JsonObject json = JsonUtil.parse(rtn);
		logger.debug(json.toString());
		if(!JsonUtil.getAsBoolean(json, "success")){
			throw new Exception(JsonUtil.getAsString(json, "entity"));
		}
	}
	
	public List<Map<String,String>> fetch(String to_jid,String pwd) throws Exception {
		Map<String,String> params = new HashMap<String,String>();
		params.put("pwd", pwd);
		params.put("to", to_jid);
		String body = buildArgs(INF_METHOD_FETCH,params);
		logger.debug("blacklist_fetch_body="+body);
		Map<String,String> form = new HashMap<String,String>();
		form.put("body", body);
		String rtn = HttpUtils.http(INF_URL, form);
		JsonObject json = JsonUtil.parse(rtn);
		logger.debug(json.toString());
		if(!JsonUtil.getAsBoolean(json, "success")){
			throw new Exception(JsonUtil.getAsString(json, "entity"));
		}else{
			JsonArray jsonArray = JsonUtil.getAsJsonArray(json, "entity");
			Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
			return new Gson().fromJson(jsonArray, type);
		}
	}
	
	public static void main(String[] args) throws Exception {
		BlackList b = new BlackList();
		System.out.println(b.fetch("userb@test.com", "123123"));
	}
	
}
