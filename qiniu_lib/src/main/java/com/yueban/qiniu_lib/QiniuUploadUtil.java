package com.yueban.qiniu_lib;

import android.util.Log;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.utils.AsyncRun;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * @author yueban
 * @date 2019/1/25
 * @email fbzhh007@gmail.com
 */
public class QiniuUploadUtil {
    private UploadManager uploadManager;

    private QiniuUploadUtil() {
        this.uploadManager = new UploadManager();
    }

    /**
     * @param type 1:img 2:video
     */
    public void uploadFile(final String uploadFilePath, final int type, final QiniuUploadCallback qiniuUploadCallback) {
        if (qiniuUploadCallback != null) {
            qiniuUploadCallback.onStart();
        }
        if (uploadFilePath == null) {
            if (qiniuUploadCallback != null) {
                qiniuUploadCallback.onFailed(null, "文件名不能为空");
            }
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final OkHttpClient httpClient = new OkHttpClient();
                Request req = new Request.Builder().url(QiniuUploadConfig.getUrl(type)).method("GET", null).build();

                Response resp = null;
                try {
                    resp = httpClient.newCall(req).execute();
                    JSONObject jsonObject = new JSONObject(resp.body().string());
                    String uploadToken = jsonObject.getString("uptoken");
                    String domain = jsonObject.getString("domain");

                    upload(uploadFilePath, uploadToken, domain, qiniuUploadCallback);
                } catch (final Exception e) {
                    AsyncRun.run(new Runnable() {
                        @Override
                        public void run() {
                            if (qiniuUploadCallback != null) {
                                qiniuUploadCallback.onFailed(e, "上传失败: 获取上传服务器信息失败");
                            }
                        }
                    });
                } finally {
                    if (resp != null) {
                        resp.body().close();
                    }
                }
            }
        }).start();
    }

    private void upload(final String uploadFilePath, final String uploadToken, final String domain, final QiniuUploadCallback qiniuUploadCallback) {
        File uploadFile = new File(uploadFilePath);
        UploadOptions uploadOptions = new UploadOptions(null, null, false,
                new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {

                    }
                }, null);
        this.uploadManager.put(uploadFile, null, uploadToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo respInfo,
                                         JSONObject jsonData) {
                        if (respInfo.isOK()) {
                            try {
                                String fileKey = jsonData.getString("key");
                                final String url = domain + "/" + fileKey;
                                if (qiniuUploadCallback != null) {
                                    qiniuUploadCallback.onSuccess();
                                }
                            } catch (JSONException e) {
                                if (qiniuUploadCallback != null) {
                                    qiniuUploadCallback.onFailed(e, "上传失败: 上传结果解析失败");
                                }
                                Log.e(QiniuUploadConfig.LOG_TAG, e.getMessage());
                            }
                        } else {
                            if (qiniuUploadCallback != null) {
                                qiniuUploadCallback.onFailed(new Exception(respInfo.error), "上传失败");
                            }
                            Log.e(QiniuUploadConfig.LOG_TAG, respInfo.toString());
                        }
                    }

                }, uploadOptions);
    }

    public static final class Holder {
        public static final QiniuUploadUtil INSTANCE = new QiniuUploadUtil();
    }
}
