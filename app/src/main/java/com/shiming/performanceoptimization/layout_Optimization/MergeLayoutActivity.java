package com.shiming.performanceoptimization.layout_Optimization;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shiming.performanceoptimization.R;

/**
 *   merge 标签 在某些场景下可以减少布局的层次,由于所有的Activity的根布局都是fragment  DecorView PhoneWindow 事件的传递，包括设置setContentView 等的方法---> 我会写一篇文章独立解释安卓事件的源码解析，会更加清楚的介绍这个类，所以，当独立的一个布局文件最外层是FrameLayout的时候，并且和这个布局不需要设置 background 或者 padding的时候，可以使用<merge>标签来代替FrameLayout布局。另外一种的情况可以使用《merge》便签的情况是当前布局作为另外一个布局的子布局
 */
public class MergeLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_layout);
    }
}
