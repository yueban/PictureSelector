package com.yueban.qiniu_lib;

/**
 * @author yueban
 * @date 2019/1/25
 * @email fbzhh007@gmail.com
 */
public class QiniuUploadConfig {
    public static final int TYPE_IMG = 1;
    public static final int TYPE_VIDEO = 2;

    final static String LOG_TAG = "Qiniu";

    private final static String REMOTE_SERVICE_SERVER = "http://chat.unicornsocialmedia.cn/upload";
    private final static String IMAGE_PATH = "/image";
    private final static String VIDEO_PATH = "/video";

    static String getUrl(int type) {
        String path = type == TYPE_IMG ? IMAGE_PATH : VIDEO_PATH;
        return QiniuUploadConfig.REMOTE_SERVICE_SERVER + path;
    }
}
