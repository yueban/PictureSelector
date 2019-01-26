package com.yueban.qiniu_lib;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * @author yueban
 * @date 2019/1/25
 * @email fbzhh007@gmail.com
 */
class OkHttpUtil {
    private static OkHttpClient mOkHttpClient;

    static OkHttpClient getClient() {
        if (mOkHttpClient == null) {
            final OkHttpClient.Builder builder;
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                builder = new OkHttpClient.Builder().addInterceptor(logging);
            } else {
                builder = new OkHttpClient.Builder();
            }
            mOkHttpClient = builder
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
        }
        return mOkHttpClient;
    }
}
