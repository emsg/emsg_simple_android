package com.vurtnewk.emsg.client.upload;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vurtnewk.emsg.Define;
import com.vurtnewk.emsg.HttpUtils;
import com.vurtnewk.emsg.util.JsonUtil;
import com.vurtnewk.emsg.util.PreferencesUtils;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TokenGenerator {
    static String ACCESS_KEY = "eau6doiUgYfcnZiqzWl_xs6amhn5VZ5jC4IPpDwd";
    static String SECRET_KEY = "QsSEMBIDMMv3SlVtIrZzLCIDQKjB5N71FdLX8Lwj";

    public static String uploadToken(String bucketName) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = SECRET_KEY.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            PutPolicy putPolicy = new PutPolicy();
            putPolicy.setScope(bucketName);
            putPolicy.setDeadline(System.currentTimeMillis() / 1000 + 24 * 60
                    * 60);

            byte[] encodePutpolicy = Base64.encode(new Gson().toJson(putPolicy)
                    .getBytes("utf-8"), Base64.URL_SAFE | Base64.NO_WRAP);
            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(encodePutpolicy);

            String encodedSign = Base64.encodeToString(rawHmac, Base64.URL_SAFE
                    | Base64.NO_WRAP);

            return ACCESS_KEY + ":" + encodedSign + ":"
                    + new String(encodePutpolicy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getAccesskey(Context context, String appkey) {
        String finalAccessKey = null;
        finalAccessKey = PreferencesUtils.getString(context, "acckey");
        long effectTime = PreferencesUtils.getLong(context, "efftime");
        if (TextUtils.isEmpty(finalAccessKey) || isTimeOverEffectTime(effectTime)) {
            try {
                finalAccessKey = getAccessKeyFromPost(context, appkey);
            } catch (Exception e) {
            }
        }
        return finalAccessKey;
    }

    public static boolean isTimeOverEffectTime(long effectTime) {
        return System.currentTimeMillis() - effectTime > 24 * 60 * 60 * 1000;
    }

    public static String getAccessKeyFromPost(Context context, String appkey) {
        String upLoadJson = getJsonData(appkey);
        Map<String, String> mHashMap = new HashMap<String, String>();
        mHashMap.put("body", upLoadJson);
        String getDataFromHttpPost = HttpUtils.http(Define.TOKEN_HOST
                , mHashMap);
        if (getDataFromHttpPost == null) return null;
        JsonObject mJson = JsonUtil.parse(getDataFromHttpPost);
        if (JsonUtil.getAsBoolean(mJson, "success")) {
            String accessKey = JsonUtil.getAsString(mJson, "entity");
            if (!TextUtils.isEmpty(accessKey)) {
                PreferencesUtils.putLong(context, "efftime",
                        System.currentTimeMillis());
                PreferencesUtils.putString(context, "acckey", accessKey);
                return accessKey;
            }
        }
        return null;
    }

    private static String getJsonData(String appkey) {
        JsonObject mJsObj = new JsonObject();
        mJsObj.addProperty("appkey", appkey == null ? "" : appkey);
        mJsObj.addProperty("domain", "");
        return mJsObj.toString();
    }

    public static String downloadToken(String url) {
        return "";
    }

}
