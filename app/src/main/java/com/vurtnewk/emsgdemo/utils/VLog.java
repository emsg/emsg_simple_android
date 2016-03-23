package com.vurtnewk.emsgdemo.utils;

import android.util.Log;

/**
* @author VurtneWk
* @time Created on 2016/3/16 14:43
* Log工具类,需要在Application中开启
*/
public class VLog {

	private static boolean DEBUG = false;
	
	public static void setDebug(boolean isDebug){
		DEBUG = isDebug;
	}

	public static void i(String tag, Object msg) {
		if (DEBUG) {
			Log.i(tag, msg.toString());
		}
	}

	public static void d(String tag, Object msg) {
		if (DEBUG) {
			Log.d(tag, msg.toString());
		}
	}

	public static void e(String tag, Object msg) {
		if (DEBUG) {
			Log.e(tag, msg.toString());
		}
	}

	public static void w(String tag, Object msg) {
		if (DEBUG) {
			Log.w(tag, msg.toString());
		}
	}

	public static void v(String tag, Object msg) {
		if (DEBUG) {
			Log.v(tag, msg.toString());
		}
	}
}
