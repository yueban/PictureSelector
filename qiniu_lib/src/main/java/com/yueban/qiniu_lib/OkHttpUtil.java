package com.yueban.qiniu_lib;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author yueban
 * @date 2019/1/25
 * @email fbzhh007@gmail.com
 */
class OkHttpUtil {
    private static OkHttpClient mOkHttpClient;

    static OkHttpClient getClient() {
        if (mOkHttpClient == null) {
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                mOkHttpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
            } else {
                mOkHttpClient = new OkHttpClient();
            }
        }
        return mOkHttpClient;
    }
}
