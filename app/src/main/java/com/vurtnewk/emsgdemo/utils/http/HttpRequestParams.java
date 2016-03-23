package com.vurtnewk.emsgdemo.utils.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.vurtnewk.emsgdemo.base.BaseApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class HttpRequestParams extends RequestParams {

    private static final String PARAMS_NAME = "body";
    //! 请求的JSON对象
    private JSONObject requestJsonExpression;
    //! 请求的具体参数对象
    private JSONObject paramsJsonExpression;

    public HttpRequestParams(String service, String method) {
        requestJsonExpression = new JSONObject();
        paramsJsonExpression = new JSONObject();
        addDefaultParams(requestJsonExpression);
        try {
            requestJsonExpression.put("service", service);
            requestJsonExpression.put("method", method);
            requestJsonExpression.put("params", paramsJsonExpression);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.put(PARAMS_NAME, requestJsonExpression.toString());
    }

    /**
     * @param requestJsonExpression
     * @brief 增加默认的参数
     */
    private void addDefaultParams(JSONObject requestJsonExpression) {

//        //默认添加TOKEN
        if (!TextUtils.isEmpty(BaseApplication.getInstance().getToken()))
            addTopLevelParams("token", BaseApplication.getInstance().getToken());
//
//        //默认添加渠道号
//        if (!TextUtils.isEmpty(JdApplication.getInstance().getChannel()))
//            addTopLevelParams("channel", JdApplication.getInstance().getChannel());
//
//        //默认添加APP版本号
//        if (!TextUtils.isEmpty(JdApplication.getInstance().getVersionCode()))
//            addTopLevelParams("version", JdApplication.getInstance().getVersionCode());
//
//        //默认添加APP版本号
//        if (!TextUtils.isEmpty(JdApplication.getInstance().getIMEI()))
//            addTopLevelParams("imei", JdApplication.getInstance().getIMEI());

        //默认添加UUID
        addTopLevelParams("sn", UUID.randomUUID().toString());
    }


    /**
     * @param key
     * @param value
     * @brief 增加与service 和Method 同等级参数
     */
    public void addTopLevelParams(String key, String value) {
        try {
            if (requestJsonExpression != null) {
                //默认添加TOKEN
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    requestJsonExpression.put(key, value);
                    super.put(PARAMS_NAME, requestJsonExpression.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * @brief 删除请求中token
     */
    public void removeToken() {
        try {
            if (requestJsonExpression != null) {
                //默认添加TOKEN
                Object token = requestJsonExpression.remove("token");
                super.put(PARAMS_NAME, requestJsonExpression.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(String key, String value) {
        try {
            if (value != null)
                paramsJsonExpression.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //提交 post／get 请求，参数名都为 body
        super.put(PARAMS_NAME, requestJsonExpression.toString());
    }

    @Override
    public void put(String key, Object value) {
        try {
            if (value != null) {
                String s = new Gson().toJson(value);
                JSONObject jsonObject = new JSONObject(s);
                paramsJsonExpression.put(key, jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //提交 post／get 请求，参数名都为 body
        super.put(PARAMS_NAME, requestJsonExpression.toString());
    }

    public void put(String key, JSONArray value) {
        try {
            if (value != null) {
                paramsJsonExpression.put(key, value);
                super.put(PARAMS_NAME, requestJsonExpression.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, JSONObject value) {
        try {
            if (value != null) {
                paramsJsonExpression.put(key, value);
                super.put(PARAMS_NAME, requestJsonExpression.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(String key, int value) {
        try {
            if (value != -1)
                paramsJsonExpression.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //提交 post／get 请求，参数名都为 body
        super.put(PARAMS_NAME, requestJsonExpression.toString());
    }

    /**
     * @return
     * @brief 获得请求的JSON串
     */
    public String getRequestJsonExpression() {
        return requestJsonExpression.toString();
    }
}
