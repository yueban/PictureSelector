package com.yueban.qiniu_lib;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

/**
 * @author yueban
 * @date 2019/1/25
 * @email fbzhh007@gmail.com
 */
public class UploadUtil {
    private static final String TAG = "UploadUtil";

    public static void upload(final String uploadUrl, final String path, final String mimeType, final UploadCallback uploadCallback) {
        AsyncRun.runInBack(new Runnable() {
            @Override
            public void run() {
                AsyncRun.runInMain(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadCallback != null) {
                            uploadCallback.onStart();
                        }
                    }
                });
                OkHttpClient okHttpClient = OkHttpUtil.getClient();

                File file = new File(path);
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("files", FileNameUtil.getFileName(path),
                                RequestBody.create(MediaType.parse(mimeType), file));

                RequestBody requestBody = builder.build();

                Request request = new Request.Builder()
                        .url(uploadUrl)
                        .post(requestBody)
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                        Log.e(TAG, "onFailure: " + e);
                        AsyncRun.runInMain(new Runnable() {
                            @Override
                            public void run() {
                                if (uploadCallback != null) {
                                    uploadCallback.onFailed(e);
                                }
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull final Response response) {
                        Log.e(TAG, "成功" + response);
                        String result;
                        if (response.body() == null) {
                            result = "";
                        } else {
                            try {
                                result = response.body().string();
                            } catch (IOException e) {
                                result = "";
                                e.printStackTrace();
                            }
                        }
                        final String finalResult = result;
                        AsyncRun.runInMain(new Runnable() {
                            @Override
                            public void run() {
                                if (uploadCallback != null) {
                                    if (!TextUtils.isEmpty(finalResult)) {
                                        uploadCallback.onSuccess(response.code(), finalResult);
                                    } else {
                                        uploadCallback.onFailed(null);
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
