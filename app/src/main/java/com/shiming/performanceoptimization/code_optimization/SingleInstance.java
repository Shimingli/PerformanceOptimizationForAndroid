package com.shiming.performanceoptimization.code_optimization;

import android.content.Context;

/**
 * author： Created by shiming on 2018/4/28 16:36
 * mailbox：lamshiming@sina.com
 */

class SingleInstance {
    private static SingleInstance sSingleInstance;
    private final Context mContext;

    private SingleInstance(Context context){
        mContext = context;
    }
//    因为每次调用实例都需要判断同步锁，很多项目包括很多人都是用这种的
//    双重判断校验的方法，这种的方法看似很完美的解决了效率的问题，但是它
//    在并发量不多，安全性不太高的情况下能完美的运行，但是，
//    在jvm编译的过程中会出现指令重排的优化过程，这就会导致singleton实际上
//    没有被初始化，就分配了内存空间，也就是说singleton！=null但是又没有被初始化，
//    这就会导致返回的singletonthird返回的是不完整的
    public static SingleInstance getSingleInstance(Context context){
        if (sSingleInstance==null){
            synchronized (SingleInstance.class){
                if (sSingleInstance==null)   {
                    // TODO: 2018/4/28 注意外面传入的conext对象是否，是哪个 
                    sSingleInstance= new SingleInstance(context);
                    //第二种是改动代码，使用application 中的context变量
                    sSingleInstance= new SingleInstance(context.getApplicationContext());
                    
                }
            }

        }
        return sSingleInstance;
    }
}
