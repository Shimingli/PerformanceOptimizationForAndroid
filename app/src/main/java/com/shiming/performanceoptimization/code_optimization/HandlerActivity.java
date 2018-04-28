package com.shiming.performanceoptimization.code_optimization;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shiming.performanceoptimization.R;

import java.lang.ref.WeakReference;
/**
 * author： Created by shiming on 2018/4/28 10:52
 * mailbox：lamshiming@sina.com
 */
public class HandlerActivity extends AppCompatActivity {


    /*-------------old ide 已经告诉我们这里可能内存泄露-------------------*/
    @SuppressLint("HandlerLeak")
    private final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);
        /**
         * Handler是和Looper以及MessageQueue一起工作的，在安卓中，一个 应用启动了，系统会默认创建一个主线程服务的Looper对象
         * ，该Looper对象处理主线程的所有的Message消息，他的生命周期贯穿整个应用。在主线程中使用的Handler的都会默认的绑定到
         * 这个looper的对象，咋主线程中创建handler的时候，它会立即关联主线程Looper对象的MessageQueue，这时发送到的MessageQueue
         * 中的Message对象都会持有这个Handler的对象的引用，这样Looper处理消息时Handler的handlerMessage的方法，因此，如果Message
         * 还没有处理完成，那么handler的对象不会立即被垃圾回收
         */
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // TODO: 2018/4/28  用户即使退出了应用的话，这里也是会执行的 ，通过日记的观察
//                //这里有可能用户退出了Activity
//                System.out.println("shiming mHandler --todo");
//            }
//        },5000);


        //如何避免，有两点的可以尝试
        /**
         * 1、在子线程中使用Handler，但是Handler不能再子线程中使用，需要开发者自己创建一个Looper对象，实现难，方法怪
         *
         * 2、将handler声明为静态的内部类，静态内部类不会持有外部类的引用，因此，也不会引起内存泄露，
         */
        InnerHandler innerHandler = new InnerHandler(this);
        innerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //这里这要 退出了 就不会执行了
                System.out.println("shiming innerHandler --todo");

            }
        },5000);
    }
    public class InnerHandler extends Handler{
        //弱应用，在另外一个地方会讲到
        private final WeakReference<HandlerActivity> mActivityWeakReference;

        public InnerHandler(HandlerActivity activity){
            mActivityWeakReference=new WeakReference<HandlerActivity>(activity);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("shiming Handler Activity onDestroy");
    }
}
