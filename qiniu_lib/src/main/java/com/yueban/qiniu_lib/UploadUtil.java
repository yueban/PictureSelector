package com.yueban.qiniu_lib;

import android.os.Handler;
import android.os.Looper;
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
    public static final int TYPE_IMG = 1;
    public static final int TYPE_VIDEO = 2;

    private static final String uploadUrl = "http://chat.unicornsocialmedia.cn/upload/image";
    private static final String TAG = "UploadUtil";

    public static void upload(String path, int type, String mimeType, final UploadCallback uploadCallback) {
        if (uploadCallback != null) {
            uploadCallback.onStart();
        }
        OkHttpClient mOkHttpClent = new OkHttpClient();
        File file = new File(path);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(type == TYPE_IMG ? "img" : "video", FileNameUtil.getFileName(path),
                        RequestBody.create(MediaType.parse(mimeType), file));

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build();
        Call call = mOkHttpClent.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.e(TAG, "onFailure: " + e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadCallback != null) {
                            uploadCallback.onFailed(e, "上传失败");
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "成功" + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadCallback != null) {
                            uploadCallback.onSuccess();
                        }
                    }
                });
            }
        });
    }

    public static void runOnUiThread(Runnable r) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(r);
    }


}
