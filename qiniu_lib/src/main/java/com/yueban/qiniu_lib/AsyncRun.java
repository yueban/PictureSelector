package com.yueban.qiniu_lib;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author yueban
 * @date 2019/1/26
 * @email fbzhh007@gmail.com
 */
public final class AsyncRun {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void runInMain(Runnable r) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(r);
    }

    public static void runInBack(Runnable r) {
        executor.execute(r);
    }
}
