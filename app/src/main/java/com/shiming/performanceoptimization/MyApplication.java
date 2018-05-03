package com.shiming.performanceoptimization;

import android.app.Application;

import com.shiming.performanceoptimization.electric_quantity_optimization.CrashHandler;

/**
 * author： Created by shiming on 2018/5/3 14:48
 * mailbox：lamshiming@sina.com
 */

public class MyApplication  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 程序会重新启动，如果点击电量优化，App崩溃了，请给与全部权限，
         * 还要在开发者模式里面给与位置信息模拟的设置，如果崩溃了，
         * 你也可以发现app会自动的重新启动，这是AlarmManager的应用，注意看MyApplication里面的代码，tks
         */
       CrashHandler.getInstance().initCrashHandler(this);
    }
}
