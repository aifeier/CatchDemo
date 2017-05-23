package com.ai.cwf.libexceptioncapture;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created at 陈 on 2017/5/23.
 *
 * @author chenwanfeng
 * @email 237142681@qq.com
 */

public class CatchExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static CatchExceptionHandler instance;
    private Application application;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public static CatchExceptionHandler getInstance(Context context) {
        if (instance == null) {
            synchronized (CatchExceptionHandler.class) {
                if (instance == null)
                    instance = new CatchExceptionHandler(context);
            }
        }
        return instance;
    }

    public CatchExceptionHandler(Context context) {
        application = (Application) context.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!handException(t, e) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, e);
        }
    }

    private boolean handException(Thread t, Throwable e) {
        if (e == null && t == null)
            return false;
        for (StackTraceElement s : t.getStackTrace()) {
            Log.e("StackTraceElement", s.getClassName() + ":" + s.getFileName() + ":" + s.getMethodName());
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Log.e("test", writer.toString());
        System.exit(-1);
        return true;
    }
}
