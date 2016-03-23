package com.vurtnewk.emsg.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Gson工具类
 * 
 * @author tiger
 *
 */
public class JsonUtil {
	
	/**
	 * 从json格式字符串中反序列化成一个JSON对象
	 * 
	 * @param json
	 * @return
	 */
	public static JsonObject parse(String json) {
		try {
			return new JsonParser().parse(json).getAsJsonObject();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 将key对应的值当做一个JsonObject对象取出来
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static JsonObject getAsJsonObject(JsonObject json, String key) {
		try {
			return json.get(key).getAsJsonObject();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 将key对应的值当做一个JsonArray对象取出来
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static JsonArray getAsJsonArray(JsonObject json, String key) {
		return json.get(key).getAsJsonArray();
	}
	
	/**
	 * 将key对应的值当做一个String对象取出来
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static String getAsString(JsonObject json, String key) {
		try {
			return json.get(key).getAsString();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 将key对应的值当做一个整数取出来
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static int getAsInt(JsonObject json, String key) {
		return json.get(key).getAsInt();
	}
	
	/**
	 * 将key对应的值当做一个整数取出来，如不存在，返回默认值
	 * 
	 * @param json
	 * @param key
	 * @param def 默认值
	 * @return
	 */
	public static int getAsInt(JsonObject json, String key, int def) {
		try {
			return json.get(key).getAsInt();
		} catch (Exception e) {
			return def;
		}
	}

	/**
	 * 将key对应的值当做一个boolean取出来
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static boolean getAsBoolean(JsonObject json, String key) {
		return json.get(key).getAsBoolean();
	}
	
	/**
	 * 将key对应的值当做一个boolean取出来，如不存在，返回默认值
	 * 
	 * @param json
	 * @param key
	 * @param def 默认值
	 * @return
	 */
	public static boolean getAsBoolean(JsonObject json, String key, boolean def) {
		try {
			return json.get(key).getAsBoolean();
		} catch (Exception e) {
			return def;
		}
	}

}
