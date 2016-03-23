package com.vurtnewk.emsgdemo.utils.http;

/**
 * @author VurtneWk
 * @time created on 2016/3/17.15:27
 */
public interface HttpListener {

    /**
     * @param result 返回的字符串
     * @brief 网络请求成功的回调方法
     */
    void onSuccess(String result);

    /**
     * @param error 返回的字符串
     * @brief 网络请求成功的回调方法
     */
    void onFailure(String error);

    /**
     * 网络请求开始的回调方法
     */
    void onStart();

}
