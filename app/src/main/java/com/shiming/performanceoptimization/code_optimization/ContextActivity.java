package com.shiming.performanceoptimization.code_optimization;

import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shiming.performanceoptimization.R;
/**
 * author： Created by shiming on 2018/4/28 10:52
 * mailbox：lamshiming@sina.com
 */
public class ContextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context);

        //Context的种类
        //Application 全局唯一的Context实例
        Application application = getApplication();
        Context applicationContext = application.getApplicationContext();
        //不同的Activity，得到这个Context，是独立的，不会进行复用
        Context baseContext = this.getBaseContext();

        MyBroadcaseRecriver myBroadcaseRecriver = new MyBroadcaseRecriver();

        //ContentProvider 中的Context

        /**
         *如果创建单利必须需要使用到context对象
         */
        //这样不会内存泄露，不用改动单利类中代码
        SingleInstance.getSingleInstance(getApplication().getApplicationContext());


    }
}
