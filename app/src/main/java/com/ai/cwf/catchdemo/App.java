package com.ai.cwf.catchdemo;

import android.app.Application;

import com.ai.cwf.libexceptioncapture.CatchExceptionHandler;
import com.ai.cwf.libexceptioncapture.LogcatUtils;

/**
 * Created at 陈 on 2017/5/17.
 *
 * @author chenwanfeng
 * @email 237142681@qq.com
 */

public class App extends Application {
    private static App instance;

    public static final App getInstance() {
        if (instance == null) {
            synchronized (App.class) {
                if (instance == null) {
                    instance = new App();
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LogcatUtils.getInstance(this).start();
        CatchExceptionHandler.getInstance(this);
    }

}
