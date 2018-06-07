package com.shiming.performanceoptimization.layout_Optimization;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shiming.performanceoptimization.R;
/**
 * author： Created by shiming on 2018/5/3 10:52
 * mailbox：lamshiming@sina.com
 * des:如果创建的层级结构比较复杂，View树嵌套的层次比较深，那么将会使得页面的响应的时间变长，导致运行的时候越来越慢
 */

public class LayoutOptimizationActivity extends AppCompatActivity {

    private Button mBtnViewStub;
    private ViewStub mViewStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_optimization);

        findViewById(R.id.btn_go_to_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LayoutOptimizationActivity.this,MergeLayoutActivity.class));
            }
        });
        /*
        在安卓中经常会使用到相同的布局，比如说title，最佳的实践的方法就是把相同的布局抽取出来，独立成一个xml文件，需要使用到的时候，就把这个布局include进来，不仅减少了代码量，而且修改这个相同的布局，只需要修改一个地方即可
         */

        mBtnViewStub = findViewById(R.id.btn_view_stub);
        /*
        ViewStub 是一种不可见的并且大小为0的试图，它可以延迟到运行时才填充inflate 布局资源，当Viewstub设为可见或者是inflate的时候，就会填充布局资源，这个布局和普通的试图就基本上没有任何区别，比如说，加载网络失败，或者是一个比较消耗性能的功能，需要用户去点击才可以加载，参考我的开源的项目WritingPen
         */
        mViewStub = findViewById(R.id.view_stub);

        mBtnViewStub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=mViewStub.getParent()){
                    /*
                    android:inflatedId 的值是Java代码中调用ViewStub的 inflate()或者是serVisibility方法返回的Id，这个id就是被填充的View的Id
                     */
                    /**
                     * ViewStub.inflate() 的方法和 setVisibility 方法是差不多，因为 setVisibility方法会（看源码）走这个inflate的方法
                     */
                    View inflate = mViewStub.inflate();
//                    mViewStub.setVisibility(View.VISIBLE);
                    // TODO: 2018/6/7 如果设置了      android:inflatedId="@+id/view_stub_inflateid" 在ViewStub ，那么ViewStub的layout的根布局找出来的id 会为null ，如果没有设置 ，就可以找出来，但是前提以一定要 inflate 或者是 设置可见 
                    View viewById = findViewById(R.id.view_stub_layout);
                    System.out.println("shiming viewStub layout"+viewById);// shiming viewStub layoutnull
                    //inflate--->android.support.v7.widget.AppCompatImageView{de7e3a2 V.ED..... ......I. 0,0-0,0 #7f07003e app:id/find_view_stub}
                    System.out.println("shiming inflate--->"+inflate);
                    final View find_view_stub = findViewById(R.id.view_stub_inflateid);
                    System.out.println("shiming  find_view_stub----"+find_view_stub);
//
//
                   // View iamgeivew11 = find_view_stub.findViewById(R.id.imageview);
                    //himing ---- iamgeivew11null
                    // TODO: 2018/5/4 為啥為null  原因是布局文件中根布局只有View，没有ViewGroup
                    //System.out.println("shiming ---- iamgeivew11"+iamgeivew11);

                }else{
                    View viewById = findViewById(R.id.view_stub_layout);
                    System.out.println("shiming viewStub layout 已经inflate了"+viewById);
                    Toast.makeText(LayoutOptimizationActivity.this,"已经inflate了",Toast.LENGTH_LONG).show();
                   // final View viewById = findViewById(R.id.view_stub_inflateid);
                    View iamgeivew = findViewById(R.id.imageview);
                    //已经inflate了android.support.v7.widget.AppCompatImageView{4637833 V.ED..... ........ 348,294-732,678 #7f07003e app:id/find_view_stub}
                   // System.out.println("shiming l----已经inflate了"+viewById);//
                    System.out.println("shiming l----已经inflate了iamgeivew"+iamgeivew);//已经inflate了iamgeivew==null
                   // View iamgeivew11 = viewById.findViewById(R.id.imageview);
                    //已经inflate了 iamgeivew11null
                   // System.out.println("shiming l----已经inflate了 iamgeivew11"+iamgeivew11);
                }
            }
        });
        // TODO: 2018/5/23  ViewStub 中的第二种的加载的方式
//        //commLv2 ViewStub 中的第二种的加载的方式
//        mViewStub = findViewById(R.id.view_stub);
//        // 成员变量commLv2为空则代表未加载 commLv2 的id为ViewStub中的根布局的id
//        View commLv2=findViewById(R.id.my_title_parent_id);
//        if ( commLv2 == null ) {
//            // 加载评论列表布局, 并且获取评论ListView,inflate函数直接返回ListView对象
//            commLv2 = (View)mViewStub.inflate();
//        } else {
//            // ViewStub已经加载
//        }

        /*
        merge 标签 在某些场景下可以减少布局的层次,由于所有的Activity的根布局都是fragment  DecorView PhoneWindow 事件的传递，包括设置setContentView 等的方法---> 我会写一篇文章独立解释安卓事件的源码解析，会更加清楚的介绍这个类，所以，当独立的一个布局文件最外层是FrameLayout的时候，并且和这个布局不需要设置 background 或者 padding的时候，可以使用<merge>标签来代替FrameLayout布局。另外一种的情况可以使用《merge》便签的情况是当前布局作为另外一个布局的子布局
         */


     /*
       尽量使用CompoundDrawable,如果存在相邻的ImageView和TextView 的话
      */

        /*
         使用Lint 检查代码，和布局是否可以存在优化的地方，我会写个简单的经常遇见过的问题，同时完成一篇文档，加以说明
         */
    }
}
