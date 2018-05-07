# PerformanceOptimizationForAndroid
安卓的性能优化
#### 写在前面的话，前段时间写了一篇文章 [23种设计模式](https://www.jianshu.com/p/4e01479b6a2c)，写的不详细，因为如果要写的很详细，估计一年半载都写不完，完全都是按照自己理解，每个设计模式就画了一个简单的图，同时完成了一个小Demo，哪知道这篇文章成了我在简书点赞最高的一篇文章，实在有点受宠若惊，谢谢各位大佬点赞！！！

#### Demo下载的地址
![F0xJ.png](https://upload-images.jianshu.io/upload_images/5363507-bfcb14dc69cb04ce.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### 这篇文章分为五个部分代码优化、图片优化、布局优化、网络优化、电量优化，尽量每个方法都写了小的Demo！
![image.png](https://upload-images.jianshu.io/upload_images/5363507-151e1acaa8105b43.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 代码优化：不要做多余的工作，尽量避免次数过多的内存的分配，（需要对api有一定的熟悉）
数据集合的使用：建议最佳的做法是可能使用ArrayList作为首选，只要你需要使用额外的功能的时候，或者当程序性能由于经常从表的中间进行插入和删除而变差的时候，才会去选择LinkedList。HashMap性能上于HashTable相当，因为HashMap和HashTable在底层的存储和查找机制是一样的，但是TreeMap通常比HashMap要慢。HashSet总体上的性能比TreeSet好，特别实在添加和查询元素的时候，而这两个操作也是最重要的操作。TreeSet存在的唯一的原因是它可以维持元素的排序的状态，所以当需要一个排好序的Set，才使用TreeSet。因为其内部的结果欧支持排序，并且因为迭代是我们更有可能执行的操作，所以，用TreeSet迭代通常比用HashSet要快。

```
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
```

SparseArray是Android特有的稀疏数组的实现，他是Integer和Object的为例进行的一个映射用于代替 HsahMap<Integer,<E>>,提高性能。

 * SparseArray
  * 线程不安全（多线程中需要注意）
   * 由于要进行二分查找，（可以是有序的），SparseArray会对插入的数据按照Key的大小顺序插入
   * SparseArray对删除操作做了优化，它并不会立刻删除这个元素，而是通过设置标记位（DELETED）的方法，后面尝试重用。


内部核心的实现（二分查找）
```
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
```
SpareArray 家族有以下的四类
```
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
```

SpareArray中的设计模式：原型模式:这里有使用到了的，原型模式内存中复制数据的，不会调用到类的构造的方法，而且访问的权限对原型模式无效

* 优点： 1、性能提高。 2、逃避构造函数的约束。
 *  缺点：
         1、配备克隆方法需要对类的功能进行通盘考虑，这对于全新的类不是很难，但对于已有的类不一定很容易，特别当一个类引用不支持串行化的间接对象，或者引用含有循环结构的时候。
          2、必须实现 Cloneable 接口。
```
    SparseArray<String> clone = sparseArray.clone();
```

Handler正确的使用姿势（☺☺☺）
下面的代码是很多人都会这样写，这样会造成内存泄漏
原因：Handler是和Looper以及MessageQueue一起工作的，在安卓中，一个 应用启动了，系统会默认创建一个主线程服务的Looper对象 ，该Looper对象处理主线程的所有的Message消息，他的生命周期贯穿整个应用。在主线程中使用的Handler的都会默认的绑定到这个looper的对象，咋主线程中创建handler的时候，它会立即关联主线程Looper对象的MessageQueue，这时发送到的MessageQueue 中的Message对象都会持有这个Handler的对象的引用，这样Looper处理消息时Handler的handlerMessage的方法，因此，如果Message还没有处理完成，那么handler的对象不会立即被垃圾回收
```
   /*-------------old ide 已经告诉我们这里可能内存泄露-------------------*/
    @SuppressLint("HandlerLeak")
    private final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // TODO: 2018/4/28  用户即使退出了应用的话，这里也是会执行的 ，通过日记的观察
//                //这里有可能用户退出了Activity
//                System.out.println("shiming mHandler --todo");
//            }
//        },5000);
```
如何避免，有两点的可以尝试
* 1、在子线程中使用Handler，但是Handler不能再子线程中使用，需要开发者自己创建一个Looper对象，实现难，方法怪
 * 2、将handler声明为静态的内部类，静态内部类不会持有外部类的引用，因此，也不会引起内存泄露，

```

    InnerHandler innerHandler = new InnerHandler(this);
        innerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //这里这要 退出了 就不会执行了
                System.out.println("shiming innerHandler --todo");

            }
        },5000);
  public class InnerHandler extends Handler{
        //弱应用，在另外一个地方会讲到
        private final WeakReference<HandlerActivity> mActivityWeakReference;

        public InnerHandler(HandlerActivity activity){
            mActivityWeakReference=new WeakReference<HandlerActivity>(activity);
        }
    }
```
Context正确的姿势
```
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
```
 * 单例模式，如果不得不传入Context，由于单例一直存在会导致Activity或者是Service的单例引用，从而不会被垃圾回收， Activity中的关联的View和数据结构也不会被释放，正确的方式应该使用Application中的Context

```
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
```

 * java 四种引用方式和引用队列的解释
 和java一样，Android也是基于垃圾回收（GC）机制实现内存的自动的回收，垃圾回收的算法“标记-清除（Mark-Sweep）” “标记压缩（Mark-Compact）“复制算法（Copying）以及引用计数算法（Reference-Counting），安卓的虚拟机（Dalvik还是Art），都是使用标记清除算法。 在Android中，内存泄露是指不再使用的对象依然占有内存，或者是他们占用的内存没有得到释放， 从而导致内存空间不断的减少，由于可用的空间比较少，发生内存泄露会使得内存更加的紧张，甚至最终由于内存耗尽而发生的OOM，导致应用的崩溃。

```  /**
         * 和java一样，Android也是基于垃圾回收（GC）机制实现内存的自动的回收，垃圾回收的算法“标记-清除（Mark-Sweep）”
         * “标记压缩（Mark-Compact）“复制算法（Copying）以及引用计数算法（Reference-Counting），安卓的虚拟机（Dalvik还是Art），
         * 都是使用标记清除算法”
         *

        mTextView1.setText(des1);

        /**
         * 在Android中，内存泄露是指不再使用的对象依然占有内存，或者是他们占用的内存没有得到释放，
         * 从而导致内存空间不断的减少，由于可用的空间比较少，发生内存泄露会使得内存更加的紧张，
         * 甚至最终由于内存耗尽而发生的OOM，导致应用的崩溃
         */

        mTextView2.setText(des2);
```
* 强引用：Java中里面最广泛的使用的一种，也是对象默认的引用类型，如果又一个对象具有强引用，那么垃圾回收器是不会对它进行回收操作的，当内存的空间不足的时候，Java虚拟机将会抛OutOfMemoryError错误，这时应用将会被终止运行
* 软引用：一个对象如果只有一个软引用，那么当内存空间充足是，垃圾回收器不会对他进行回收操作，只有当内存空间不足的时候，这个对象才会被回收，软引用可以用来实现内存敏感的高速缓存，如果配合引用队列（ReferenceQueue使用，当软引用指向对象被垃圾回收器回收后，java会把这个软引用加入到与之关联的引用队列中）
* 弱引用：弱引用是比软引用更弱的一种的引用的类型，只有弱引用指向的对象的生命周期更短，当垃圾回收器扫描到只有具有弱引用的对象的时候，不敢当前空间是否不足，都会对弱引用对象进行回收，当然弱引用也可以和一个队列配合着使用
* 引用队列：ReferenceQueue一般是作为WeakReference SoftReference 的构造的函数参数传入的，在WeakReference 或者是 softReference 的指向的对象被垃圾回收后，ReferenceQueue就是用来保存这个已经被回收的Reference
```

        String des3="强引用：Java中里面最广泛的使用的一种，也是对象默认的引用类型，如果又一个对象具有强引用，那么垃圾回收器是不会对它进行回收操作的，当内存的空间不足的时候，Java虚拟机将会抛出OutOfMemoryError错误，这时应用将会被终止运行";

        mTextView3.setText(des3);
        String des4="软引用：一个对象如果只有一个软引用，那么当内存空间充足是，垃圾回收器不会对他进行回收操作，只有当内存空间不足的时候，这个对象才会被回收，软引用可以用来实现内存敏感的高速缓存，如果配合引用队列（ReferenceQueue使用，当软引用指向对象被垃圾回收器回收后，java会把这个软引用加入到与之关联的引用队列中）";

        Object obj=new Object();

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
        PhantomReference pr = new PhantomReference<Object>(obj1, queue);


        String des7="引用队列：ReferenceQueue一般是作为WeakReference SoftReference 的构造的函数参数传入的，在WeakReference 或者是 softReference 的指向的对象被垃圾回收后，ReferenceQueue就是用来保存这个已经被回收的Reference";
        mTextView7.setText(des7);

```

* 将HashMap封装成一个线程安全的集合，并且使用软引用的方式防止OOM（内存不足）。由于在ListView中会加载大量的图片.那么为了有效的防止OOM导致程序终止的情况

```
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
```
一个简单的Demo
```
/**
 * author： Created by shiming on 2018/5/2 14:50
 * mailbox：lamshiming@sina.com
 */
public class EmployeeCache {
    static private EmployeeCache cache;// 一个Cache实例
    private Hashtable<String, EmployeeRef> employeeRefs;// 用于Chche内容的存储
    private ReferenceQueue<Employee> q;// 垃圾Reference的队列

     // 继承SoftReference，使得每一个实例都具有可识别的标识。
    // 并且该标识与其在HashMap内的key相同。
    public class EmployeeRef extends SoftReference<Employee> {
        private String _key = "";
        public EmployeeRef(Employee em, ReferenceQueue<Employee> q) {
            super(em, q);
            _key = em.getID();
        }
    }

    // 构建一个缓存器实例
    private EmployeeCache() {
        employeeRefs = new Hashtable<String, EmployeeRef>();
        q = new ReferenceQueue<Employee>();
    }

    // 取得缓存器实例
    public static EmployeeCache getInstance() {
        if (cache == null) {
            cache = new EmployeeCache();
        }
        return cache;
    }

    // 以软引用的方式对一个Employee对象的实例进行引用并保存该引用
    private void cacheEmployee(Employee em) {
        cleanCache();// 清除垃圾引用
        EmployeeRef ref = new EmployeeRef(em, q);
        employeeRefs.put(em.getID(), ref);
    }

    // 依据所指定的ID号，重新获取相应Employee对象的实例
    public Employee getEmployee(String ID) {
        Employee em = null;
       // 缓存中是否有该Employee实例的软引用，如果有，从软引用中取得。
        if (employeeRefs.containsKey(ID)) {
            EmployeeRef ref = (EmployeeRef) employeeRefs.get(ID);
            em = (Employee) ref.get();
        }
       // 如果没有软引用，或者从软引用中得到的实例是null，重新构建一个实例，
       // 并保存对这个新建实例的软引用
        if (em == null) {
            em = new Employee(ID);
            System.out.println("Retrieve From EmployeeInfoCenter. ID=" + ID);
            this.cacheEmployee(em);
        }
        return em;
    }

    // 清除那些所软引用的Employee对象已经被回收的EmployeeRef对象
    private void cleanCache() {
        EmployeeRef ref = null;
        while ((ref = (EmployeeRef) q.poll()) != null) {
            employeeRefs.remove(ref._key);
        }
    }

    // 清除Cache内的全部内容
    public void clearCache() {
        cleanCache();
        employeeRefs.clear();
        //告诉垃圾收集器打算进行垃圾收集，而垃圾收集器进不进行收集是不确定的
        System.gc();
        //强制调用已经失去引用的对象的finalize方法
        System.runFinalization();
    }

    /**
     * 当垃圾收集器认为没有指向对象实例的引用时，会在销毁该对象之前调用finalize()方法。
     * 该方法最常见的作用是确保释放实例占用的全部资源。java并不保证定时为对象实例调用该方法，
     * 甚至不保证方法会被调用，所以该方法不应该用于正常内存处理。
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}


/**
 * author： Created by shiming on 2018/5/2 14:49
 * mailbox：lamshiming@sina.com
 */

public class Employee {
    private String id;// 雇员的标识号码
    private String name;// 雇员姓名
    private String department;// 该雇员所在部门
    private String Phone;// 该雇员联系电话
    private int salary;// 该雇员薪资
    private String origin;// 该雇员信息的来源

    // 构造方法
    public Employee(String id) {
        this.id = id;
        getDataFromlnfoCenter();
    }
    // 到数据库中取得雇员信息
    private void getDataFromlnfoCenter() {
// 和数据库建立连接井查询该雇员的信息，将查询结果赋值
// 给name，department，plone，salary等变量
// 同时将origin赋值为"From DataBase"
    }

    public String getID() {
        return id;
    }
}
```
其他需要注意到的地方：

* 1、不要重复的创建相同的对象，对象的创建都是需要内存分配的，对象的销毁需要垃圾回收，这些都在一定程度上影响程序的性能
* 2、对常量使用static final修饰，对于基本类型和String类型的常量，建议使用常量static final 修饰，因为final类型的常量会在静态dex文件的域初始化部分，这时对基本数据类型和String类型常量的调用不会涉及类的初始化，而是直接调用字面量
* 3、避免内部的get set方法的调用，get set的作用是对以外屏蔽具体的变量定义，从而达到更好的封装性，如果在类的内部调用get set的方法访问变量的话，会降低访问的速度，根据在安卓的官方的文档，在没有jit编译器时，直接访问变量的速度是调用get方法的3倍，在jit编译器，直接访问变量是调用get方法的7倍，当然使用了ProGuard的话，perGuard会对get set 进行内联的操作，从而达到直接访问的效果

关于JIT：
 * JIT是”Just In Time Compiler”的缩写，就是”即时编译技术”，与Dalvik虚拟机相关,JIT是在2.2版本提出的，目的是为了提高Android的运行速度，一直存活到4.4版本，因为在4.4之后的ROM中，就不存在Dalvik虚拟机了。 
 * 编译打包APK文件:1、Java编译器将应用中所有Java文件编译为class文件,2、dx工具将应用编译输出的类文件转换为Dalvik字节码，即dex文件
 * Google在2.2版本添加了JIT编译器，当App运行时，每当遇到一个新类，JIT编译器就会对这个类进行编译，经过编译后的代码，会被优化成相当精简的原生型指令码（即native code），这样在下次执行到相同逻辑的时候，速度就会更快。
* dex字节码翻译成本地机器码是发生在应用程序的运行过程中的，并且应用程序每一次重新运行的时候，都要做重做这个翻译工作，所以这个工作并不是一劳永逸，每次重新打开App，都需要JIT编译，Dalvik虚拟机从Android一出生一直活到4.4版本，而JIT在Android刚发布的时候并不存在，在2.2之后才被添加到Dalvik中。
 * AOT是”Ahead Of Time”的缩写，指的就是ART(Anroid RunTime)这种运行方式。
* JIT是运行时编译，这样可以对执行次数频繁的dex代码进行编译和优化，减少以后使用时的翻译时间，虽然可以加快Dalvik运行速度，但是还是有弊病，那就是将dex翻译为本地机器码也要占用时间，所以Google在4.4之后推出了ART，用来替换Dalvik。
 * ART的策略与Dalvik不同，在ART 环境中，应用在第一次安装的时候，字节码就会预先编译成机器码，使其成为真正的本地应用。之后打开App的时候，不需要额外的翻译工作，直接使用本地机器码运行，因此运行速度提高。
  * 当然ART与Dalvik相比，还是有缺点的。
     * ART需要应用程序在安装时，就把程序代码转换成机器语言，所以这会消耗掉更多的存储空间，但消耗掉空间的增幅通常不会超过应用代码包大小的20%
     * 由于有了一个转码的过程，所以应用安装时间难免会延长
      * 但是这些与更流畅的Android体验相比而言，不值一提。


#### 图片优化
四种图片格式
  * JPEG
     *   是一种广泛使用的有损压缩图像标准格式，它不支持透明和多帧动画，一般摄影的作品是JEPG格式的，通过控制压缩比，可以调整图片的大小

  * PNG
     * 是一种无损压缩的图片格式，他支持完整的透明通道，从图片处理的领域来讲，JEPG只有RGB三个通道，而PNG有ARGB四个通道，因此PNG图片占用空间一般比较大，会无形的增加app的大小，在做app瘦身时一般都要对PNG图片进行梳理以减小其占用的体积
     
* GIF
         * 是一种古老的图片的格式，诞生于1987年，随着初代互联网流行开来，他的特别是支持多帧动画，表情图，
  
 * Webp
   * google于2010年发布，支持有损和无损、支持完整的透明通道、也支持多帧动画，目前主流的APP都已经使用了Webp，淘宝，微信，即保证了图片的大小和质量


 * 在安卓应用开发中能够使用编解码格式的只有三种 JEPG PNG WEBP
```
   /**
     * 在安卓应用开发中能够使用编解码格式的只有三种 JEPG PNG WEBP
     */
    public enum CompressFormat {
        JPEG    (0),
        PNG     (1),
        WEBP    (2);//安卓4.0后开始支持

        CompressFormat(int nativeInt) {
            this.nativeInt = nativeInt;
        }
        final int nativeInt;
    }
```
推荐几种图片处理网站
* 无损压缩ImageOptin，在不牺牲图片质量的前提下，即减下来PNG图片占用的空间，又提高了图片的加载速度  https://imageoptim.com/api

* 有损压缩ImageAlpha，图片大小得到极大的缩小，如果需要使用的话，一定要ui设计师看能否使用  https://pngmini.com/

* 有损压缩TinyPNG 比较知名的png压缩的工具，也需要ui设计师看能够使用不  https://tinypng.com/

* PNG/JPEG 转化为 wepb  ：智图  ：http://zhitu.isux.us/


如果ui设计师工作量不饱和的话，可以推荐， 尽量使用 .9.png 点9图   小黑点表示 可拉伸区域，黑边表示纵向显示内容的范围

####布局优化：如果创建的层级结构比较复杂，View树嵌套的层次比较深，那么将会使得页面的响应的时间变长，导致运行的时候越来越慢

* merge标签(对安卓的事件传递要达到源码级的熟悉才可以理解) 在某些场景下可以减少布局的层次,由于所有的Activity的根布局都是FrameLayout    Window   PhoneWindow  DecorView   事件的传递，包括设置setContentView 等的方法---> 我会写一篇文章独立解释安卓事件的源码解析，会更加清楚的介绍这个类，(对安卓的事件传递要达到源码级的熟悉才可以理解)todo<-----所以，当独立的一个布局文件最外层是FrameLayout的时候，并且和这个布局不需要设置 background 或者 padding的时候，可以使用<merge>标签来代替FrameLayout布局。另外一种的情况可以使用《merge》便签的情况是当前布局作为另外一个布局的子布局

```
  <include android:layout_height="50dp"
      android:layout_width="match_parent"
      layout="@layout/layout_include_merge"
      />


<?xml version="1.0" encoding="utf-8"?>
<merge xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <TextView android:gravity="center"
        android:text="merge 标签 在某些场景下可以减少布局的层次,由于所有的"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</merge>
```

* 在安卓中经常会使用到相同的布局，比如说title，最佳的实践的方法就是把相同的布局抽取出来，独立成一个xml文件，需要使用到的时候，就把这个布局include进来，不仅减少了代码量，而且修改这个相同的布局，只需要修改一个地方即可.

*   ViewStub 是一种不可见的并且大小为0的试图，它可以延迟到运行时才填充inflate 布局资源，当Viewstub设为可见或者是inflate的时候，就会填充布局资源，这个布局和普通的试图就基本上没有任何区别，比如说，加载网络失败，或者是一个比较消耗性能的功能，需要用户去点击才可以加载，参考我的开源的项目 [WritingPen](https://www.jianshu.com/p/6746d68ef2c3)


注意事项：如果这个根布局是个View，比如说是个ImagView，那么找出来的id为null，得必须注意这一点
```
   <ViewStub
        android:padding="10dp"
        android:background="@color/colorPrimary"
        android:layout_gravity="center"
        android:inflatedId="@+id/find_view_stub"
        android:id="@+id/view_stub"
        android:layout="@layout/view_stub_imageview"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />



<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:padding="10dp"
    android:src="@drawable/ic_launcher_background"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <TextView
        android:text="如果这个根布局是个View，比如说是个ImagView，那么找出来的id为null，得必须注意这一点"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
    <!--如果这个根布局是个View，比如说是个ImagView，那么找出来的id为null，得必须注意这一点-->
    <ImageView
        android:layout_marginTop="20dp"
        android:id="@+id/imageview"
        android:padding="10dp"
        android:src="@drawable/ic_launcher_background"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</FrameLayout>
```

调用todo: 2018/5/4 為啥為null  原因是布局文件中根布局只有View，没有ViewGroup，ViewStub.inflate() 的方法和 setVisibility 方法是差不多，因为 setVisibility方法会（看源码）走这个inflate的方法

 ```
 if (null!=mViewStub.getParent()){
                    /*
                    android:inflatedId 的值是Java代码中调用ViewStub的 inflate()或者是serVisibility方法返回的Id，这个id就是被填充的View的Id
                     */
                    /**
                     * ViewStub.inflate() 的方法和 setVisibility 方法是差不多，因为 setVisibility方法会（看源码）走这个inflate的方法
                     */
//                    View inflate = mViewStub.inflate();
                    mViewStub.setVisibility(View.VISIBLE);
                    //inflate--->android.support.v7.widget.AppCompatImageView{de7e3a2 V.ED..... ......I. 0,0-0,0 #7f07003e app:id/find_view_stub}
//                    System.out.println("shiming inflate--->"+inflate);
                    final View find_view_stub = findViewById(R.id.find_view_stub);
                    System.out.println("shiming ----"+find_view_stub);


                    View iamgeivew11 = find_view_stub.findViewById(R.id.imageview);
                    //himing ---- iamgeivew11null
                    // TODO: 2018/5/4 為啥為null  原因是布局文件中根布局只有View，没有ViewGroup
                    System.out.println("shiming ---- iamgeivew11"+iamgeivew11);

                }else{
                    Toast.makeText(LayoutOptimizationActivity.this,"已经inflate了",Toast.LENGTH_LONG).show();
                    final View viewById = findViewById(R.id.find_view_stub);
                    View iamgeivew = findViewById(R.id.imageview);
                    //已经inflate了android.support.v7.widget.AppCompatImageView{4637833 V.ED..... ........ 348,294-732,678 #7f07003e app:id/find_view_stub}
                    System.out.println("shiming l----已经inflate了"+viewById);//
                    System.out.println("shiming l----已经inflate了iamgeivew"+iamgeivew);//已经inflate了iamgeivew==null
                    View iamgeivew11 = viewById.findViewById(R.id.imageview);
                    //已经inflate了 iamgeivew11null
                    System.out.println("shiming l----已经inflate了 iamgeivew11"+iamgeivew11);
                }
            }
```

* 尽量使用CompoundDrawable,如果存在相邻的ImageView和TextView 的话

```
<LinearLayout
        android:layout_width="match_parent"
        android:layout_height="150dp">
    <TextView
        android:text="我是文字"
        android:drawableBottom="@mipmap/ic_launcher_round"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
   <TextView
       android:text="我是title2"
       android:drawableEnd="@mipmap/ic_launcher_round"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"
       android:drawableRight="@mipmap/ic_launcher_round" />
    <TextView
        android:text="我是文字33"
        android:drawableLeft="@mipmap/ic_launcher_round"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:drawableStart="@mipmap/ic_launcher_round" />

    <TextView
        android:drawableTop="@mipmap/ic_launcher_round"
        android:text="我是文字3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
    </LinearLayout>

```
![image.png](https://upload-images.jianshu.io/upload_images/5363507-796b8e15dd7eda3b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

* 使用Lint 检查代码，和布局是否可以存在优化的地方，我会写个简单的经常遇见过的问题，同时完成一篇文档，加以说明,地址[Lint的使用（安卓性能提升必备掌握的工具）](https://www.jianshu.com/p/d6068542b00b)

#### 网络优化. 移动端对额App几乎都是联网的，网络延迟等会对App的性能产生较大的影响，网络优化可以节约网络流量和电量
 *  DNS域名的系统，主要的功能根据应用请求所用的域名URL去网络上面映射表中查相对应的IP地址，这个过程有可能会消耗上百毫秒，而且可能存在着DNS劫持的危险，可以替换为Ip直接连接的方式来代替域名访问的方法，从而达到更快的网络请求，但是使用Ip地址不够灵活，当后台变换了Ip地址的话，会出现访问不了，前段的App需要发包，解决方法是增加Ip地址动态更新的能力，或者是在IP地址访问失败了，切换到域名的访问.

Demo--->ping 一个地址，不正确的话，切换到备用的地址
```
  boolean ping = ping("wwww.baidu.com");

 /**
     * 测试主域名是否可用
     *
     * @param ip
     * @return
     */
    private final int PING_TIME_OUT = 1000; // ping 超时时间
    private boolean ping(String ip) {
        try {
            Integer status = executeCommandIp( ip, PING_TIME_OUT );
            if ( status != null && status == 0 ) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }
   /**
     * 执行域名是否可通
     * @param command
     * @param timeout
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    private int executeCommandIp( final String command, final long timeout )
            throws IOException, InterruptedException, TimeoutException {
        Process process = Runtime.getRuntime().exec(
                "ping -c 1 -w 100 " + command);
        mWorker = new PingWorker(process);
        mWorker.start();
        try {
            mWorker.join(timeout);
            if (mWorker.exit != null) {
                return mWorker.exit;
            } else {
                //throw new TimeoutException();
                return -1;
            }
        } catch (InterruptedException ex) {
            mWorker.interrupt();
            Thread.currentThread().interrupt();
            throw ex;
        } finally {
            process.destroy();
        }
    }
```
PingWorker 类
```
 class PingWorker extends Thread {
        private final Process process;
        private Integer exit;
        private String ip;

        public PingWorker(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            try {
                exit = process.waitFor();
                if (exit == 0) {
                    BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String str = new String();
                    StringBuffer ipInfo = new StringBuffer();

                    //读出所有信息并显示
                    while((str=buf.readLine())!=null) {
                        ipInfo.append(str);
                    }
                    /*
                    PING sni1st.dtwscache.ourwebcdn.com (14.215.228.4) 56(84) bytes of data.64 bytes from 14.215.228.4: icmp_seq=1 ttl=57 time=16.6 ms--- sni1st.dtwscache.ourwebcdn.com ping statistics ---1 packets transmitted, 1 received, 0% packet loss, time 0msrtt min/avg/max/mdev = 16.656/16.656/16.656/0.000 ms
                     */
                    System.out.println("shiming ipInfo----->"+ipInfo);
                    Pattern mPattern = Pattern.compile("\\((.*?)\\)");
                    Matcher matcher = mPattern.matcher(ipInfo.toString());
                    if ( matcher.find() ) {
                        ip = matcher.group( 1 );
                    }
                }
                else {
                    ip = " process.waitFor()==="+exit;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                ip="java.io.IOException: Stream closed";
                return;
            }
            catch (InterruptedException e) {
                ip="java.io.InterruptedException: Stream closed";
                return;
            }
        }
    }
```

* 合并网络请求，一次完整的Http请求，首先进行的是DNS查找，通过TCP三次握手，从而建立连接，如果是https请求的话，还要经过TLS握手成功后才可以进行连接，对于网络请求，减少接口，能够合并的网络请求就尽量合并
    * SSL(Secure Sockets Layer 安全套接层),及其继任者传输层安全（Transport Layer Security，TLS）是为网络通信提供安全及数据完整性的一种安全协议。TLS与SSL在传输层对网络连接进行加密。

 HTTPS和HTTP的区别主要为以下四点：
   * 一、https协议需要到ca申请证书，一般免费证书很少，需要交费。
* 二、http是超文本传输协议，信息是明文传输，https 则是具有安全性的ssl加密传输协议。
* 三、http和https使用的是完全不同的连接方式，用的端口也不一样，前者是80，后者是443。
* 四、http的连接很简单，是无状态的；HTTPS协议是由SSL+HTTP协议构建的可进行加密传输、身份认证的网络协议，比http协议安全。


*   预先获取数据能够将网络请求集中在一次，这样其他时间段手机就可以切换到空闲的时间，从而避免经常性的唤醒，从而节约用电

*    避免轮询：如果说每个一段时间需要向服务器发起主动的网络请求，其实不建议在app端做这样的操作，可以使用推送，如果说在不得已的情况下，也要避免使用Thread.sleep()函数来循环等待，建议使用系统的AlarmManager来实现定时轮询，AlarmManager 可以保证在系统休眠的时候，CPU也可以得到休息，在下一次需要发起网络球球的时候才唤醒

*    尽量避免网络请求失败时候，无限制的循环重试连接，在我第一篇简书博客有写过一个网络加载的框架 ：https://www.jianshu.com/p/141ee58eb143    中有提到过
```

  //基于Rxjava 和 RxAndroid Retorfit
          o.subscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenHandler(1, 5))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        s.onBegin();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
```

*  离线缓存，对于图片或者文件，内存缓存+磁盘缓存+网络缓存，一般我们本地需要做的是二级缓存，当缓存中存在图片或者是文件，直接从缓存中读取，不会走网络，下载图片，在Android中使用LruCache实现内存缓存，[DiskLruCache](https://github.com/JakeWharton/DiskLruCache)实现本地缓存

```
   /**
     * 图片缓存的核心类
     */
    private LruCache<String, Bitmap> mLruCache;
    // 缓存大小
    private static final int CACHE_MAX_SIZE = 1024;

    /**
     * LRU（Least recently used，最近最少使用）算法根据数据的历史访问记录来进行淘汰数据，其核心思想是“如果数据最近被访问过，那么将来被访问的几率也更高”。
     */
    private void lruCacheDemo() {
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //设置LruCache缓存的大小，一般为当前进程可用容量的1/8。
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            //重写sizeOf方法，计算出要缓存的每张图片的大小
            //这个方法要特别注意，跟我们实例化 LruCache 的 maxSize 要呼应，怎么做到呼应呢，比如 maxSize 的大小为缓存的个数，这里就是 return 1就 ok，如果是内存的大小，如果5M，这个就不能是个数 了，这是应该是每个缓存 value 的 size 大小，如果是 Bitmap，这应该是 bitmap.getByteCount();
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
            ////这里用户可以重写它，实现数据和内存回收操作
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (oldValue != newValue) {
                    oldValue.recycle();
                }
            }
        };
    }
    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }
    /**
     * 往LruCache中添加一张图片
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null) {
            if (bitmap != null)
                mLruCache.put(key, bitmap);
        }
    }
```


*    压缩数据的大小：可以对发送服务端数据进行gzip压缩，同时可以使用更优的数据传输格式，例如二进制的代替Json格式，这个比较牛逼，估计运用的很少，使用webp格式代替图片格式


 * 不同的网络环境使用不同的超时策略，常见的网络格式有 2g、3g、4g、wifi,实时的更新当前的网络状态，通过监听来获取最新的网络类型，并动态调整网络超时的时间
```        
 private void netWorkDemo() {
        TextView netWork = findViewById(R.id.net_work);
        boolean networkConnected = NetworkUtils.isNetworkConnected(this);
        int networkType = NetworkUtils.getNetworkType(this);

        System.out.println("shiming 是否联网了"+networkConnected);
        switch (networkType){
            case TYPE_UNKNOWN:
                System.out.println("shiming 联网的类型---无网络连接");
                netWork.setText("是否联网了---》"+networkConnected+" 联网的类型---无网络连接");
                break;
            case TYPE_2G:
                System.out.println("shiming 联网的类型---2G");
                netWork.setText("是否联网了---》"+networkConnected+" 联网的类型---2G");
                break;
            case TYPE_3G:
                System.out.println("shiming 联网的类型---TYPE_3G");
                netWork.setText("是否联网了---》"+networkConnected+" 联网的类型---TYPE_3G");
                break;
            case TYPE_4G:
                System.out.println("shiming 联网的类型---TYPE_4G");
                netWork.setText("是否联网了---》"+networkConnected+" 联网的类型---TYPE_4G");
                break;
            case TYPE_WIFI:
                System.out.println("shiming 联网的类型---TYPE_WIFI");
                netWork.setText("是否联网了---》"+networkConnected+" 联网的类型---TYPE_WIFI");
                break;
        }
    }
```
NetworkUtils 类
```
package com.shiming.performanceoptimization.network_optimization;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;



/**
 * author： Created by shiming on 2018/4/28 10:52
 * mailbox：lamshiming@sina.com
 * des:网络连接工具类
 */

public class NetworkUtils {
    private static final String SUBTYPE_TD_SCDMA = "SCDMA";
    private static final String SUBTYPE_WCDMA = "WCDMA";
    private static final String SUBTYPE_CDMA2000 = "CDMA2000";

    /**
     * 判断是否已连接到网络.
     *
     * @param context Context
     * @return 是否已连接到网络
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前网络类型
     *
     * @param context Context
     * @return 当前网络类型(Unknown, 2G, 3G, 4G, WIFI)
     */
    public static int getNetworkType(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetworkType.TYPE_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        return NetworkType.TYPE_2G;

                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        return NetworkType.TYPE_3G;

                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        return NetworkType.TYPE_4G;

                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        String subtypeName = info.getSubtypeName();
                        if (SUBTYPE_TD_SCDMA.equalsIgnoreCase(subtypeName) ||
                                SUBTYPE_WCDMA.equalsIgnoreCase(subtypeName) ||
                                SUBTYPE_CDMA2000.equalsIgnoreCase(subtypeName)) {
                            return NetworkType.TYPE_3G;
                        } else {
                            return NetworkType.TYPE_UNKNOWN;
                        }
                }
            }
        }
        return NetworkType.TYPE_UNKNOWN;
    }
}

```
NetworkType类
```
package com.shiming.performanceoptimization.network_optimization;

/**
 * author： Created by shiming on 2018/4/28 10:52
 * mailbox：lamshiming@sina.com
 * des:网络连接类型常量
 */

public class NetworkType {
    /**
     * 无网络连接
     */
    public static final int TYPE_UNKNOWN = -1;
    /**
     * 2G
     */
    public static final int TYPE_2G = 0;
    /**
     * 3G
     */
    public static final int TYPE_3G = 1;
    /**
     * 4G
     */
    public static final int TYPE_4G = 2;
    /**
     * WIFI
     */
    public static final int TYPE_WIFI = 3;
}
```

* CDN的全称是Content Delivery Network，即内容分发网络。其基本思路是尽可能避开互联网上有可能影响数据传输速度和稳定性的瓶颈和环节，使内容传输的更快、更稳定。通过在网络各处放置节点服务器所构成的在现有的互联网基础之上的一层智能虚拟网络，CDN系统能够实时地根据网络流量和各节点的连接、负载状况以及到用户的距离和响应时间等综合信息将用户的请求重新导向离用户最近的服务节点上。其目的是使用户可就近取得所需内容，解决 Internet网络拥挤的状况，提高用户访问网站的响应速度。

####电量优化

* 1、（BroadCastReceiver）为了减少应用耗损的电量，我们在代码中尽量避免使用无用的操作代码，当应用退到后台了，一切页面的刷新都是没有意义的，并且浪费电，比如有个监听网络状态的广播并执行一些动作，弹窗或者是Toast，那么app需要在后台的时候，禁用掉这个功能，

* 2、数据传输    蓝牙传输，Wi-Fi传输  移动网络传输  后台数据的管理：根据业务需求，接口尽量避免无效数据的传输  数据传输的频度问题：通过经验值或者是数据统计的方法确定好数据传输的频度，避免冗余重复的数据传输，数据传输过程中要压缩数据的大小，合并网络请求，避免轮询

*  3、位置服务 正确的使用位置复位，是应用耗电的一个关键     

 * 需要注意以下的三点：
      * 1、有没有及时注销位置监听器：和广播差不多
     * 2、位置更新监听频率的设定；更加业务需求设置一个合理的更新频率值，
          * minTime:用来指定间更新通知的最小的时间间隔，单位是毫秒，看日志这里是1s更新的
          *  minDistance:用来指定位置更新通知的最小的距离，单位是米

    * 3、Android提供了三种定位
         * GPS定位，通过GPS实现定位，精度最高，通常在10米（火星坐标），但是GPS定位在时间和电量上消耗也是最高的
         * 网络定位，通过移动通信的基站信号差异来计算出手机所在的位置，精度比GPS差好多
         * 被动定位，最省电的定位服务，如果使用被动定位服务。说明它想知道位置更新信息但有不主动获取，等待手机中其他应用或者是服务或者是系统组件发出定位请求，并和这些组件的监听器一起接收位置的信息，实际的开发中，一般使用的是第三方的地图，高德，腾讯，百度，他们做了很好的封装，同时在地图上的表现上更加的优化
```
        /**
         *   //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
         *         criteria.setAccuracy(Criteria.ACCURACY_FINE);
         *         //设置是否要求速度
         *         criteria.setSpeedRequired(false);
         *         // 设置是否允许运营商收费
         *         criteria.setCostAllowed(false);
         *         //设置是否需要方位信息
         *         criteria.setBearingRequired(false);
         *         //设置是否需要海拔信息
         *         criteria.setAltitudeRequired(false);
         *         // 设置对电源的需求
         *         criteria.setPowerRequirement(Criteria.POWER_LOW);
         */
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String serviceName = Context.LOCATION_SERVICE;
        mLocationManager = (LocationManager) getSystemService(serviceName);
//        locationManager.setTestProviderEnabled("gps", true);
        // TODO: 2018/5/3 IllegalArgumentException 'Provider "gps" unknown"   https://www.cnblogs.com/ok-lanyan/archive/2011/10/12/2208378.html
        mLocationManager.addTestProvider(LocationManager.GPS_PROVIDER,
                "requiresNetwork" == "", "requiresSatellite" == "", "requiresCell" == "", "hasMonetaryCost" == "",
                "supportsAltitude" == "", "supportsSpeed" == "",
                "supportsBearing" == "", android.location.Criteria.POWER_LOW,
                android.location.Criteria.ACCURACY_FINE);
        mProvider = mLocationManager.getBestProvider(criteria, true);

        //获取纬度
        //获取经度
        mLlistener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // thread is not runable, msg ignore, state:TIMED_WAITING, 这里的线程有可能ANR
                if (location != null) {
                    double lat = location.getLatitude();//获取纬度
                    double lng = location.getLongitude();//获取经度
                    System.out.println("shiming   lat+" + lat);
                    System.out.println("shiming   lng+" + lng);
                    String name = Thread.currentThread().getName();
                    mCount++;
                    System.out.println("当前线程的位置name---"+name+"i==="+mCount);
                    mTv_location.setText("位置信息是2s变动的，可以设置,我是第"+mCount+"次变动的--->"+"\n\r"+"lat===="+lat+"      lng----->"+lng);
                }
                if (mLocationManager!=null) {
                    mLocationManager.removeUpdates(this);
                    if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 1000, mLlistener);
                    }else {
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1000, mLlistener);
                    }
//                     TODO: 2018/5/3  这里在报错了，我把他注释掉
//                mLocationManager.setTestProviderEnabled(mProvider, false);//   java.lang.IllegalArgumentException: Provider "network" unknown
                }
            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {

            }
        };
        /**
         * minTime:用来指定间更新通知的最小的时间间隔，单位是毫秒，看日志这里是1s更新的
         * minDistance:用来指定位置更新通知的最小的距离，单位是米
         */
        mLocationManager.requestLocationUpdates(mProvider, 2000, (float) 1000.0, mLlistener);

```
在OnDestroy 变量手动值为null，我在测试过程中，只有在值为null的时候这个位置监听才会停止，有兴趣的小伙伴，可以好好看看值为null，底层会做什么操作
```
/**
     * 记得需要销毁这个监听,
     * todo 如果不手动置为null的话，其实您可以通过日记发现，这个监听还是一直在走的，所以说这里手动值为null的好处
     */
    protected void onDestroy() {
        if (mAm!=null){
            mAm.cancel(mPi);
            mAm=null;//变为null
        }
        if (null!=mTag){
            mTag.release();
            //释放唤醒锁锁
            mTag=null;
        }
        mLocationManager.removeUpdates(mLlistener);
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLlistener);
            mLocationManager = null;//不用分配空间
        }
        if (mLlistener != null) {
            mLlistener = null;
        }
        //   mLocationManager.setTestProviderEnabled(mProvider, false);
        super.onDestroy();
    }
```

* WakeLock 是为了保持设备的唤醒状态的API，组织用户长时间不用，仍然需要组织设备进入休眠的状态，比如用户在看电影的时候。使用wakelock 时，需要及时的释放锁，比如播放视屏的时候WakeLock保持屏幕的常亮，在暂停的时候就应该释放锁，而不是等到停止播放才释放。

```
 @SuppressLint("WakelockTimeout")
    private void wakeLockDemo() {
//        PowerManager.PARTIAL_WAKE_LOCK;//保持CPU正常运转，但屏幕和键盘灯有可能是关闭的
//        PowerManager.SCREEN_DIM_WAKE_LOCK://保持CPU正常运转，允许屏幕点亮但可能是置灰的，键盘灯可能是关闭的
//        PowerManager.SCREEN_BRIGHT_WAKE_LOCK;//保持CPU正常的运转，允许屏幕高亮显示，键盘灯可能是关闭的
//        PowerManager.FULL_WAKE_LOCK;//保持CPU正常运转，保持屏幕高亮显示，键盘灯也保持连读
//        PowerManager.ACQUIRE_CAUSES_WAKEUP;//强制屏幕和键盘灯亮起，这种锁针对必须通知用户的操作
//        PowerManager.ON_AFTER_RELEASE;//当WakeLock被释放了，继续保持屏幕和键盘灯开启一定的时间
        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        /**
         *   case PARTIAL_WAKE_LOCK:
         *             case SCREEN_DIM_WAKE_LOCK:
         *             case SCREEN_BRIGHT_WAKE_LOCK:
         *             case FULL_WAKE_LOCK:
         *             case PROXIMITY_SCREEN_OFF_WAKE_LOCK:
         *             case DOZE_WAKE_LOCK:
         *             case DRAW_WAKE_LOCK:
         */
        mTag = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Tag");
        if (null!= mTag){
            mTag.acquire();
        }

    }

```

* AlarmManager 也是比较耗电的，通常情况下需要保证两次唤醒操作的时间间隔不要太短了，在不需要使用唤醒功能的情况下，尽早的取消唤醒功能，否则应用会一直消耗电量 AlarmManager 是SDK提供的一个唤醒的APi，是系统级别的服务，可以在特定的时刻广播一个指定的Intent，这个pendingIntent可以用来启动Activity、Service、BroadcastReceiver， app，在后台也会启动

```
  private void alarmManager() {
        //创建Intent对象，action为ELITOR_CLOCK，附加信息为字符串“你该打酱油了”
        Intent intent = new Intent("action");
        intent.putExtra("msg","重启---App ---Le  -- 回到前台");
//        intent.setClass(ElectricQuantityOptimizationActivity.this,MainActivity.class);
        //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        //也就是发送了action 为"action"的intent
        mPi = PendingIntent.getBroadcast(this,0,intent,0);

       //AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
        mAm = (AlarmManager)getSystemService(ALARM_SERVICE);

       //设置闹钟从当前时间开始，每隔5s执行一次PendingIntent对象pi，注意第一个参数与第二个参数的关系
        // 5秒后通过PendingIntent pi对象发送广播
        assert mAm != null;
        /**
         * 频繁的报警对电池寿命不利。至于API 22，警报管理器将覆盖近期和高频报警请求，
         * 在未来至少延迟5秒的警报，并确保重复间隔至少为60秒,如果真的需要间隔很短的话，官方建议使用handler
         * 该方法用于设置重复闹钟，第一个参数表示闹钟类型，第二个参数表示闹钟首次执行时间，
         * 第三个参数表示闹钟两次执行的间隔时间，第三个参数表示闹钟响应动作。
         */
        mAm.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),1000, mPi);
        //该方法用于设置一次性闹钟，第一个参数表示闹钟类型，第二个参数表示闹钟执行时间，第三个参数表示闹钟响应动作。
        //am.set(AlarmManager.RTC_WAKEUP,100000,pi);
        /**
         * （1）int type： 闹钟的类型，常用的有5个值：AlarmManager.ELAPSED_REALTIME、 AlarmManager.ELAPSED_REALTIME_WAKEUP、AlarmManager.RTC、 AlarmManager.RTC_WAKEUP、AlarmManager.POWER_OFF_WAKEUP。AlarmManager.ELAPSED_REALTIME表示闹钟在手机睡眠状态下不可用，该状态下闹钟使用相对时间（相对于系统启动开始），状态值为3；
         * AlarmManager.ELAPSED_REALTIME_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用相对时间，状态值为2；
         *
         * AlarmManager.RTC表示闹钟在睡眠状态下不可用，该状态下闹钟使用绝对时间，即当前系统时间，状态值为1；
         *
         * AlarmManager.RTC_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用绝对时间，状态值为0；
         *
         * AlarmManager.POWER_OFF_WAKEUP表示闹钟在手机关机状态下也能正常进行提示功能，所以是5个状态中用的最多的状态之一，该状态下闹钟也是用绝对时间，状态值为4；不过本状态好像受SDK版本影响，某些版本并不支持；
         */
        //该方法也用于设置重复闹钟，与第二个方法相似，不过其两个闹钟执行的间隔时间不是固定的而已。
        //基本上相似,只不过这个方法优化了很多,省电
        // am.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),1000,pi);
    }


```

```
/**
 * author： Created by shiming on 2018/5/3 14:28
 * mailbox：lamshiming@sina.com
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String msg = intent.getStringExtra("msg");
        System.out.println("shiming "  +  msg) ;
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();

//        ElectricQuantityOptimizationActivity context1 = (ElectricQuantityOptimizationActivity) context;
//        context1.startActivity(intent);
    }
}
```

提供一个Demo，当app崩溃了，通过AlarmManager来重启App的功能

```
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

```
  * 程序会重新启动，如果点击电量优化，App崩溃了，请给与全部权限，还要在开发者模式里面给与位置信息模拟的设置，如果崩溃了， 你也可以发现app会自动的重新启动，这是AlarmManager的应用，注意看MyApplication里面的代码，tks
```
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

```
###以上就是个人总结的基本，总结的不太全面，同时也不太详细，如果可以的话，还请给个小星星，表示鼓励，谢谢了☺☺☺
###git地址[PerformanceOptimizationForAndroid](https://github.com/Shimingli/PerformanceOptimizationForAndroid)


     