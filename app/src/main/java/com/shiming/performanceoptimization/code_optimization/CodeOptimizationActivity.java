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
                startActivity(new Intent(CodeOptimizationActivity.this,HandlerActivity.class));
            }
        });

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
