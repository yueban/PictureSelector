package com.luck.picture.lib.tools;

import android.webkit.JavascriptInterface;

/**
 * @author yueban
 * @date 2019/1/25
 * @email fbzhh007@gmail.com
 */
public class JSObject {
    private OnJsCallAndroid onJsCallAndroid;


    public JSObject(OnJsCallAndroid onJsCallAndroid) {
        this.onJsCallAndroid = onJsCallAndroid;
    }

    @JavascriptInterface
    public void chooseMedia() {
        onJsCallAndroid.chooseMedia();
    }

    public interface OnJsCallAndroid {
        void chooseMedia();
    }
}