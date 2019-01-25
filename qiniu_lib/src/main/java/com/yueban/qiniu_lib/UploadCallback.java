package com.yueban.qiniu_lib;

import okhttp3.ResponseBody;

/**
 * @author yueban
 * @date 2019/1/25
 * @email fbzhh007@gmail.com
 */
public interface UploadCallback {
    void onStart();

    void onSuccess(String result);

    void onFailed(Exception e, String message);
}
