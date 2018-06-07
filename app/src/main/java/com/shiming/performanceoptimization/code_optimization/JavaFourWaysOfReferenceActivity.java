package com.shiming.performanceoptimization.code_optimization;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shiming.performanceoptimization.R;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * java 的四种的应用方式
 */
public class JavaFourWaysOfReferenceActivity extends AppCompatActivity {

    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView5;
    private TextView mTextView6;
    private TextView mTextView7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_four_ways_of_reference);
        mTextView1 = findViewById(R.id.text1);
        mTextView2 = findViewById(R.id.text2);
        mTextView3 = findViewById(R.id.text3);
        mTextView4 = findViewById(R.id.text4);
        mTextView5 = findViewById(R.id.text5);
        mTextView6 = findViewById(R.id.text6);
        mTextView7 = findViewById(R.id.text7);

        /**
         * 和java一样，Android也是基于垃圾回收（GC）机制实现内存的自动的回收，垃圾回收的算法“标记-清除（Mark-Sweep）”
         * “标记压缩（Mark-Compact）“复制算法（Copying）以及引用计数算法（Reference-Counting），安卓的虚拟机（Dalvik还是Art），
         * 都是使用标记清除算法”
         */

        String des1="和java一样，Android也是基于垃圾回收（GC）机制实现内存的自动的回收，垃圾回收的算法“标记-清除（Mark-Sweep）”“标记压缩（Mark-Compact）“复制算法（Copying）以及引用计数算法（Reference-Counting），安卓的虚拟机（Dalvik还是Art），都是使用标记清除算法”";

        mTextView1.setText(des1);

        /**
         * 在Android中，内存泄露是指不再使用的对象依然占有内存，或者是他们占用的内存没有得到释放，
         * 从而导致内存空间不断的减少，由于可用的空间比较少，发生内存泄露会使得内存更加的紧张，
         * 甚至最终由于内存耗尽而发生的OOM，导致应用的崩溃
         */
        String des2="在Android中，内存泄露是指不再使用的对象依然占有内存，或者是他们占用的内存没有得到释放，从而导致内存空间不断的减少，由于可用的空间比较少，发生内存泄露会使得内存更加的紧张，甚至最终由于内存耗尽而发生的OOM，导致应用的崩溃";

        mTextView2.setText(des2);

        String des3="强引用：Java中里面最广泛的使用的一种，也是对象默认的引用类型，如果又一个对象具有强引用，那么垃圾回收器是不会对它进行回收操作的，当内存的空间不足的时候，Java虚拟机将会抛出OutOfMemoryError错误，这时应用将会被终止运行";
        mTextView3.setText(des3);


        String des4="软引用：一个对象如果只有一个软引用，那么当内存空间充足是，垃圾回收器不会对他进行回收操作，只有当内存空间不足的时候，这个对象才会被回收，软引用可以用来实现内存敏感的高速缓存，如果配合引用队列（ReferenceQueue使用，当软引用指向对象被垃圾回收器回收后，java会把这个软引用加入到与之关联的引用队列中）";

        Object obj=new Object();
        // TODO: 2018/5/23 SoftReference 软引用
        SoftReference<Object> sr = new SoftReference<>(obj);//这里使用了软引用...
        /*
         *在这个期间，有可能会出现内存不足的情况发生，那么GC就会直接把所有的软引用全部清除..并释放内存空间
         *如果内存空间足够的话，那么就GC就不会进行工作...
         *GC的工作取决于内存的大小，以及其内部的算法,,,,
         */
        if(sr!=null){
            //如果软引用还存在，那么直接就可以获取这个对象的相关数据...这样就实现了cache...
            obj = sr.get();

        }else{
            //如果已经不存在，表示GC已经将其回收，我们需要重新实例化对象，获取数据信息...
            obj = new Object();
            sr = new SoftReference<>(obj);
        }
        mTextView4.setText(des4);



        String des5="弱引用：弱引用是比软引用更弱的一种的引用的类型，只有弱引用指向的对象的生命周期更短，当垃圾回收器扫描到只有具有弱引用的对象的时候，不敢当前空间是否不足，都会对弱引用对象进行回收，当然弱引用也可以和一个队列配合着使用";

        Object obj1 = new Object();
        // TODO: 2018/5/23  WeakReference 弱引用
        WeakReference<Object> weakProductA = new WeakReference<>(obj1);
        mTextView5.setText(des5);


        String des6="虚引用：和软引用和弱引用不同，虚引用并不会对所指向的对象生命周期产生任何影响，也就是对象还是会按照它原来的方式别垃圾回收期回收，虚引用本质上只是有一个标记作用，主要用来跟踪对象被垃圾回收的活动，虚引用必须和引用队列配合使用，当对象被垃圾回收时，如果存在虚引用，那么Java虚拟机会将这个虚引用加入到与之关联的引用队列中";

        mTextView6.setText(des6);
        /**
         * 如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾回收器回收。
         * 虚引用主要用来跟踪对象被垃圾回收器回收的活动
         */
        // TODO: 2018/5/2 程序可以通过判断引用队列中是否已经加入了虚引用，
        // 来了解被引用的对象是否将要被垃圾回收。如果程序发现某个虚引用已经被加入到引用队列，
        // 那么就可以在所引用的对象的内存被回收之前采取必要的行动。
        ReferenceQueue queue = new ReferenceQueue ();
        // TODO: 2018/5/23 虚引用(PhantomReference) 
        PhantomReference pr = new PhantomReference<Object>(obj1, queue);


        String des7="引用队列：ReferenceQueue一般是作为WeakReference SoftReference 的构造的函数参数传入的，在WeakReference 或者是 softReference 的指向的对象被垃圾回收后，ReferenceQueue就是用来保存这个已经被回收的Reference";
        mTextView7.setText(des7);


    }

    /**
     * list中使用大量的bitmap，这种情况的话，我自己感觉使用的比较少
     */
    public class MemoryCache {
        //将HashMap封装成一个线程安全的集合，并且使用软引用的方式防止OOM（内存不足）...
        //由于在ListView中会加载大量的图片.那么为了有效的防止OOM导致程序终止的情况...
        private Map<String,SoftReference<Bitmap>> cache=Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());

        public Bitmap get(String id){
            if(!cache.containsKey(id))
                return null;
            SoftReference<Bitmap>ref=cache.get(id);
            return ref.get();
        }

        public void put(String id,Bitmap bitmap){
            cache.put(id, new SoftReference<Bitmap>(bitmap));
        }
        public void clear(){
            cache.clear();
        }
    }
}
