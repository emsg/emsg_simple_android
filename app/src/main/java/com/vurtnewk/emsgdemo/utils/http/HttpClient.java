package com.vurtnewk.emsgdemo.utils.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.vurtnewk.emsgdemo.constants.UrlConstants;
import com.vurtnewk.emsgdemo.utils.VLog;

import cz.msebera.android.httpclient.Header;

/**
 * @author VurtneWk
 * @time created on 2016/3/17.10:57
 */
public class HttpClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, final HttpListener httpListener) {
        VLog.d("HttpClient:" , params.toString());
        client.post(getAbsoluteUrl(url), params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                httpListener.onStart();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                VLog.d("HttpClient:onFailure->" , responseString);
                httpListener.onFailure(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                VLog.d("HttpClient:onSuccess->" , responseString);
                httpListener.onSuccess(responseString);
            }
        });
    }
    public static void postFile(String url, RequestParams params, final HttpListener httpListener) {
        VLog.d("HttpClient:" , params.toString());
        client.post(url, params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                httpListener.onStart();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                VLog.d("HttpClient:onFailure->" , responseString);
                httpListener.onFailure(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                VLog.d("HttpClient:onSuccess->" , responseString);
                httpListener.onSuccess(responseString);
            }
        });
    }

    public static void post(RequestParams params, final HttpListener httpListener) {
        post("", params, httpListener);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return UrlConstants.BASE_URL + relativeUrl;
    }

}
