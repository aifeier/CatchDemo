package com.ai.cwf.libexceptioncapture;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created at 陈 on 2017/5/23.
 * android日志捕捉
 * 6.0以上需要在activity中开启文件读写权限才能使用
 * log文件每启动一次生产一个，保存在./android/data/package name/cache/logcat
 *
 * @author chenwanfeng
 * @email 237142681@qq.com
 */

/*
        *日志等级：*:v,*:d,*:w,*:e,*:f,*:s
        *
        *显示当前mPID程序的 E和W等级的日志.
        *
        *
    cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
    cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
    cmds = "logcat -s way";//打印标签过滤信息
    cmds = "logcat *:e *:i | grep \"(" + mPID + ")\""; //会打印i，e，w，不会打印d
*/

public class LogcatUtils {
    private static LogcatUtils instance;
    /*自动删除文件的延时，7天前修改的文件会自动删除*/
    private final long oldLogDelayed = 1000 * 60 * 60 * 24 * 7;
    private Context context;
    private boolean saveLog = false;
    private Thread catchThread = null;
    private BufferedReader bufferedReader;
    private FileOutputStream fileOutputStream;
    private File parentFile;
    private File saveFile;

    /*context 传的是Application，保证只需要一次初始化*/
    public static LogcatUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (LogcatUtils.class) {
                if (instance == null)
                    instance = new LogcatUtils(context.getApplicationContext());
            }
        }
        return instance;
    }

    public LogcatUtils(Context context) {
        this.context = context;
        parentFile = new File(context.getExternalCacheDir(), "logcat");
        autoDeleteOldLogs();
    }

    public void init() {
        if (catchThread == null) {
            saveFile = new File(parentFile, "Log_" + TimeUtils.getSimpleDate() + ".log");
            catchThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    catchLog();
                }
            });
            catchThread.start();
        }
    }

    private void catchLog() {
        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (!parentFile.exists())
                    parentFile.mkdirs();
                if (!saveFile.exists())
                    saveFile.createNewFile();
                Process process;
                String run = "logcat  | grep \"(" + android.os.Process.myPid() + ")\"";
                process = Runtime.getRuntime().exec(run);   //捕获日志
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                fileOutputStream = new FileOutputStream(saveFile, true);
                String str = "";
                while (saveLog && (str = bufferedReader.readLine()) != null) {
                    fileOutputStream.write((TimeUtils.getSimpleDate() + ": " + str + "\n").getBytes());
                }
            } else {
                if (saveLog) {
                    Looper.prepare();
                    Toast.makeText(context, "缺少文件写入权限，不能保存日志信息。", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            catchLog();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileOutputStream = null;
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bufferedReader = null;
            }
        }
    }

    public void start() {
        init();
        this.saveLog = true;
    }

    public void stop() {
        this.saveLog = false;
        if (catchThread != null) {
            catchThread.interrupt();
        }
        catchThread = null;
    }

    private void autoDeleteOldLogs() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            return;
        }
        if (parentFile == null)
            return;
        if (!parentFile.exists())
            return;
        long currentTime = System.currentTimeMillis();
        for (File f : parentFile.listFiles()) {
            if (currentTime - f.lastModified() >= oldLogDelayed) {
                f.delete();
            }
        }
    }

    /*主动删除文件*/
    public void deleteAllLogs() {
        /*判断有没有读取权限*/
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            return;
        }
        if (parentFile == null)
            return;
        if (!parentFile.exists())
            return;
        for (File f : parentFile.listFiles()) {
            f.delete();
        }
        /*删除文件后，在运行的log信息会不能保存，需要重新开始*/
        stop();
        start();
    }
}
