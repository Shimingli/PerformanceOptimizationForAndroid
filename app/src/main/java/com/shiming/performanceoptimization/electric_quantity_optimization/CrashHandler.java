package com.shiming.performanceoptimization.electric_quantity_optimization;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.shiming.performanceoptimization.MainActivity;
import com.shiming.performanceoptimization.MyApplication;

/**
 * author： Created by shiming on 2018/5/3 14:28
 * mailbox：lamshiming@sina.com
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static CrashHandler mAppCrashHandler;

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private MyApplication mAppContext;

    public void initCrashHandler(MyApplication application) {
        this.mAppContext = application;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler getInstance() {
        if (mAppCrashHandler == null) {
            mAppCrashHandler = new CrashHandler();
        }
        return mAppCrashHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        AlarmManager mgr = (AlarmManager) mAppContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mAppContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("crash", true);
        System.out.println("shiming -----》重启应用了哦");
        PendingIntent restartIntent = PendingIntent.getActivity(mAppContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, restartIntent); // 1秒钟后重启应用
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        System.gc();
    }
}
