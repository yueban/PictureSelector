package com.yueban.qiniu_lib;

import okhttp3.OkHttpClient;

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
            mOkHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .build();
        }
        return mOkHttpClient;
    }
}
