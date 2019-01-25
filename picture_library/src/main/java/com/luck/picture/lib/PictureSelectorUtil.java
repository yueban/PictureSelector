package com.luck.picture.lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.JSObject;
import com.yueban.qiniu_lib.UploadCallback;
import com.yueban.qiniu_lib.UploadUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * @author yueban
 * @date 2019/1/25
 * @email fbzhh007@gmail.com
 */
public class PictureSelectorUtil {
    private final WeakReference<Activity> mActivityRef;
    private WeakReference<WebView> mWebViewRef;
    private WeakReference<Dialog> mLoadingDialogRef;
    private String mUploadUrl;

    private PictureSelectorUtil(Activity activity) {
        mActivityRef = new WeakReference<>(activity);
    }

    public static PictureSelectorUtil getInstance(Activity activity) {
        return new PictureSelectorUtil(activity);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public void integrateWithWebView(WebView webView) {
        mWebViewRef = new WeakReference<>(webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        webView.addJavascriptInterface(new JSObject(new JSObject.OnJsCallAndroid() {
            @Override
            public void chooseMedia(String type, String uploadUrl) {
                mUploadUrl = uploadUrl;
                if (mActivityRef.get() != null) {
                    gotoImageSelector(mActivityRef.get(), type);
                }
            }
        }), "JsTest");
    }

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    LocalMedia media = PictureSelector.obtainSingleResult(data);
                    final String type = PictureMimeType.isPictureType(media.getPictureType()) == PictureConfig.TYPE_IMAGE ? "img" : "video";
                    final String path = media.getFinalPath();
//                    final String content = media.toBase64();
//                    Log.i("type----->", type);
//                    Log.i("path----->", path);
//                    Log.i("content----->", content);

                    if (mUploadUrl == null) {
//                        Log.e("upload----->", "uploadUrl is null");
                        return;
                    }
                    //upload
                    UploadUtil.upload(mUploadUrl, path, media.getPictureType(), new UploadCallback() {
                        @Override
                        public void onStart() {
                            showLoadingDialog();
                        }

                        @Override
                        public void onSuccess(String result) {
                            Log.i("response--->", result);
                            dismissLoadingDialog();

                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                jsonObject.put("type", type);
                                jsonObject.put("path", path);
                                if (mWebViewRef != null && mWebViewRef.get() != null) {
                                    mWebViewRef.get().loadUrl("javascript:androidCallJSWithMedia(" + jsonObject + ")");
                                }
                            } catch (JSONException e) {
                                if (mActivityRef.get() != null) {
                                    Toast.makeText(mActivityRef.get(), "上传失败: 上传数据解析失败", Toast.LENGTH_SHORT).show();
                                }
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailed(Exception e, String message) {
                            dismissLoadingDialog();
                            if (!TextUtils.isEmpty(message)) {
                                if (mActivityRef.get() != null) {
                                    Toast.makeText(mActivityRef.get(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    break;
            }
        }
    }

    public void onDestroy() {
        mActivityRef.clear();
        mWebViewRef.clear();
        mLoadingDialogRef.clear();
    }

    private void gotoImageSelector(Activity activity, String type) {
        final int pictureMimeType;
        if ("image".equals(type)) {
            pictureMimeType = PictureMimeType.ofImage();
        } else if ("video".equals(type)) {
            pictureMimeType = PictureMimeType.ofVideo();
        } else if ("all".equals(type)) {
            pictureMimeType = PictureMimeType.ofAll();
        } else {
            return;
        }
        PictureSelector.create(activity)
                .openGallery(pictureMimeType)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .theme(R.style.picture_white_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .enablePreviewAudio(true) // 是否可播放
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(false)// 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                .enableCrop(true)// 是否裁剪
                .compress(false)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
//                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
//                .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .openClickSound(false)// 是否开启点击声音
//                .selectionMedia(selectList)// 是否传入已选图片
                //.isDragFrame(false)// 是否可拖动裁剪框(固定)
//                        .videoMaxSecond(15)
//                        .videoMinSecond(10)
                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 裁剪压缩质量 默认100
//                .minimumCompressSize(100)// 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(true) // 裁剪是否可旋转图片
                //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.videoSecond()//显示多少秒以内的视频
                //.recordVideoSecond()//录制视频秒数 默认60s
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private void showLoadingDialog() {
        if (mActivityRef.get() == null) {
            return;
        }
        if (mLoadingDialogRef == null || mLoadingDialogRef.get() == null) {
            Dialog loadingDialog = new AlertDialog.Builder(mActivityRef.get()).create();
            loadingDialog.setContentView(R.layout.dialog_loading);
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialogRef = new WeakReference<>(loadingDialog);
        }
        mLoadingDialogRef.get().show();
    }

    private void dismissLoadingDialog() {
        if (mActivityRef.get() == null) {
            return;
        }
        if (mLoadingDialogRef != null && mLoadingDialogRef.get() != null && mLoadingDialogRef.get().isShowing()) {
            mLoadingDialogRef.get().dismiss();
        }
    }
}
