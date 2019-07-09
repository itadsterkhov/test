package com.itad.autorepaircloud.utils;

import com.itad.autorepaircloud.utils.holders.AuthHolder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

public class HttpRequestHelper {
    private final OkHttpClient client = new OkHttpClient();
    private final AuthHolder authHolder = AuthHolder.getInstance();
    private static HttpRequestHelper instance = new HttpRequestHelper();

    private HttpRequestHelper() {
    }

    public static HttpRequestHelper getInstance() {
        return instance;
    }

    private void checkAuth(){
        if (!authHolder.tokeInNotNull()) {
            throw new IllegalArgumentException("Token must be not null");
        }
    }

    private RequestBody createRequestBody(String contentType, String postData){
        return RequestBody.create(MediaType.parse(contentType), postData);
    }

    public void sendPost(String contentType, String postData, String url, Callback callback){
        checkAuth();
        Request request = new Request.Builder()
                .url(url + (url.contains("&") ? "&" : "?") + "pubtoken=" + authHolder.getToken())
                .addHeader("Content-Type", contentType)
                .post(createRequestBody(contentType, postData))
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void sendPostWithoutToken(String contentType, String postData, String url, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", contentType)
                .post(createRequestBody(contentType, postData))
                .build();
        client.newCall(request).enqueue(callback);
    }
}
