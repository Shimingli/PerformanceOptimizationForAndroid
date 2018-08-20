package com.shiming.performanceoptimization.code_optimization;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.view.View;

import com.shiming.performanceoptimization.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * author： Created by shiming on 2018/4/28 11:07
 * mailbox：lamshiming@sina.com
 *  代码优化
 * des: 不要做多余的工作，尽量避免次数过多的内存的分配，（需要对api有一定的熟悉）
 */

public  class CodeOptimizationActivity extends AppCompatActivity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_optimization_demo);

        /**
         * 建议最佳的做法是可能使用ArrayList作为首选，只要你需要使用额外的功能的时候，或者当程序性能由于经常从表的中间进行
         * 插入和删除而变差的时候，才会去选择LinkedList
         */
        //数据结构的选择，对于ArrayList，插入的操作特别高昂，并且其代价将随着列表的尺寸的增加而增加
        ArrayList list=new ArrayList();
        //需要执行大量的随机的访问，这个不是一个好的选择，如果是使用迭代器在列表中插入新的数据，使用这个，比较低廉（插入和移除的代价比较低廉）
        LinkedList linkedList=new LinkedList();
        //HashMap性能上于HashTable相当，因为HashMap和HashTable在底层的存储和查找机制是一样的，但是TreeMap通常比HashMap要慢
        HashMap<String,String> hashMap=new HashMap<>();
        /**
         * HashSet总体上的性能比TreeSet好，特别实在添加和查询元素的时候，而这两个操作也是最重要的操作。TreeSet存在的唯一的原因是它
         * 可以维持元素的排序的状态，所以当需要一个排好序的Set，才使用TreeSet。因为其内部的结果欧支持排序，并且因为迭代是我们更有可能
         * 执行的操作，所以，用TreeSet迭代通常比用HashSet要快
         */
        HashSet<String>  hashSet=new HashSet<>();

        TreeSet<String> treeSet=new TreeSet<>();



        //是Android特有的稀疏数组的实现，他是Integer和Object的为例进行的一个映射
        //用于代替 HsahMap<Integer,<E>>,提高性能
        SparseArray<String> sparseArray=new SparseArray<>();
        sparseArray.append(1,"shiming1");
        sparseArray.append(2,"shiming2");
        sparseArray.append(3,"shiming3");
        sparseArray.append(1,"shiming4");

        //原型模式,这里有使用到了的，原型模式内存中复制数据的，不会调用到类的构造的方法，而且访问的权限对原型模式无效
        // 在单利模式中构造的方法访问权限是private的，但是原型模式直接无视构造方法，所以单利模式和原型模式是冲突的
        /**
         * 优点： 1、性能提高。 2、逃避构造函数的约束。
         *  缺点：
         1、配备克隆方法需要对类的功能进行通盘考虑，这对于全新的类不是很难，但对于已有的类不一定很容易，特别当一个类引用不支持串行化的间接对象，或者引用含有循环结构的时候。
          2、必须实现 Cloneable 接口。
         */
        SparseArray<String> clone = sparseArray.clone();

        //ContainerHelpers.binarySearch(mKeys, mSize, key);\

        int[] ints={1,2,3,4,5};
        int i = binarySearch(ints, ints.length, 10);

        System.out.println("shiming" +i);

        //SpareArray 家族有以下的四类
        //用于替换    HashMap<Integer,boolean>
        SparseBooleanArray sparseBooleanArray=new SparseBooleanArray();
        sparseBooleanArray.append(1,false);
        //用于替换    HashMap<Integer,Interger>
        SparseIntArray SparseIntArray=new SparseIntArray();
        SparseIntArray.append(1,1);
        //用于替换    HashMap<Integer,boolean>

        @SuppressLint({"NewApi", "LocalSuppress"})
        SparseLongArray SparseLongArray=new SparseLongArray();
        SparseLongArray.append(1,1111000L);
        //用于替换    HashMap<Integer,boolean>
        SparseArray<String> SparseArray11=new SparseArray<String>();
        SparseArray11.append(1,"dd");

        /**
         * SparseArray
         * 线程不安全（多线程中需要注意）
         * 由于要进行二分查找，（可以是有序的），SparseArray会对插入的数据按照Key的大小顺序插入
         * SparseArray对删除操作做了优化，它并不会理解删除这个元素，而是通过设置标记位（DELETED）的方法，后面尝试重用
         */

        //handler Demo 演示
        findViewById(R.id.btn_handler_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CodeOptimizationActivity.this,HandlerActivity.class));
            }
        });

        //Context Demo 演示
        findViewById(R.id.btn_context_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CodeOptimizationActivity.this,ContextActivity.class));
            }
        });
        //java 四种引用方式和引用队列的解释
        findViewById(R.id.btn_four_ways_of_reference_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CodeOptimizationActivity.this,JavaFourWaysOfReferenceActivity.class));
            }
        });
        //1、不要重复的创建相同的对象，对象的创建都是需要内存分配的，对象的销毁需要垃圾回收，这些都在一定程度上影响程序的性能

        //2、对常量使用static final修饰，对于基本类型和String类型的常量，建议使用常量static final 修饰，因为final类型的常量会在静态dex文件的域初始化部分，这时对基本数据类型和String类型常量的调用不会涉及类的初始化，而是直接调用字面量

        /**
         * JIT是”Just In Time Compiler”的缩写，就是”即时编译技术”，与Dalvik虚拟机相关,JIT是在2.2版本提出的，目的是为了提高Android的运行速度，一直存活到4.4版本，因为在4.4之后的ROM中，就不存在Dalvik虚拟机了。
         * 编译打包APK文件:1、Java编译器将应用中所有Java文件编译为class文件,2、dx工具将应用编译输出的类文件转换为Dalvik字节码，即dex文件
         * Google在2.2版本添加了JIT编译器，当App运行时，每当遇到一个新类，JIT编译器就会对这个类进行编译，经过编译后的代码，会被优化成相当精简的原生型指令码（即native code），这样在下次执行到相同逻辑的时候，速度就会更快。
         * dex字节码翻译成本地机器码是发生在应用程序的运行过程中的，并且应用程序每一次重新运行的时候，都要做重做这个翻译工作，所以这个工作并不是一劳永逸，每次重新打开App，都需要JIT编译，Dalvik虚拟机从Android一出生一直活到4.4版本，而JIT在Android刚发布的时候并不存在，在2.2之后才被添加到Dalvik中。
         * AOT是”Ahead Of Time”的缩写，指的就是ART(Anroid RunTime)这种运行方式。
         * JIT是运行时编译，这样可以对执行次数频繁的dex代码进行编译和优化，减少以后使用时的翻译时间，虽然可以加快Dalvik运行速度，但是还是有弊病，那就是将dex翻译为本地机器码也要占用时间，所以Google在4.4之后推出了ART，用来替换Dalvik。
         * ART的策略与Dalvik不同，在ART 环境中，应用在第一次安装的时候，字节码就会预先编译成机器码，使其成为真正的本地应用。之后打开App的时候，不需要额外的翻译工作，直接使用本地机器码运行，因此运行速度提高。
         *
         * 当然ART与Dalvik相比，还是有缺点的。
         *
         * ART需要应用程序在安装时，就把程序代码转换成机器语言，所以这会消耗掉更多的存储空间，但消耗掉空间的增幅通常不会超过应用代码包大小的20%
         * 由于有了一个转码的过程，所以应用安装时间难免会延长
         *
         * 但是这些与更流畅的Android体验相比而言，不值一提。
         */
        //3、避免内部的get set方法的调用，get set的作用是对以外屏蔽具体的变量定义，从而达到更好的封装性，如果在类的内部调用get set的方法访问变量的话，会降低访问的速度，根据在安卓的官方的文档，在没有jit编译器时，直接访问变量的速度是调用get方法的3倍，在jit编译器，直接访问变量是调用get方法的7倍，当然使用了ProGuard的话，perGuard会对get set 进行内联的操作，从而达到直接访问的效果，



    }

    /**
     * 二分查找也称折半查找（Binary Search），它是一种效率较高的查找方法。
     * 但是，折半查找要求线性表必须采用顺序存储结构，而且表中元素按关键字有序排列。
     * @param array
     * @param size
     * @param value
     * @return
     */
    //二分查找
    static int binarySearch(int[] array, int size, int value) {
        int lo = 0;
        int hi = size - 1;

        while (lo <= hi) {
            /**
             * >>>与>>唯一的不同是它无论原来的最左边是什么数，统统都用0填充。
             * —比如你的例子，byte是8位的，-1表示为byte型是11111111(补码表示法）
             * b>>>4就是无符号右移4位，即00001111，这样结果就是15。
             * 这里相当移动一位，除以二
             */
            final int mid = (lo + hi) >>> 1;
            final int midVal = array[mid];

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        //按位取反（~）运算符 ,没有找到，这个数据
        return ~lo;  // value not present
    }
}
