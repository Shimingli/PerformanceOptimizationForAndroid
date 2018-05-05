package com.shiming.performanceoptimization.network_optimization;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import com.shiming.performanceoptimization.R;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.shiming.performanceoptimization.network_optimization.NetworkType.TYPE_2G;
import static com.shiming.performanceoptimization.network_optimization.NetworkType.TYPE_3G;
import static com.shiming.performanceoptimization.network_optimization.NetworkType.TYPE_4G;
import static com.shiming.performanceoptimization.network_optimization.NetworkType.TYPE_UNKNOWN;
import static com.shiming.performanceoptimization.network_optimization.NetworkType.TYPE_WIFI;

/**
 * 网络优化
 * 移动端对额App几乎都是联网的，网络延迟等会对App的性能产生较大的影响，网络优化可以节约网络流量和电量
 */
public class NetWorkOptimizationActivity extends AppCompatActivity {

    private PingWorker mWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work_optimization);

        //----->1
        /*
        DNS域名的系统，主要的功能根据应用请求所用的域名URL去网络上面映射表中查相对应的IP地址，这个过程有可能会消耗上百毫秒，而且可能存在着DNS劫持的危险，可以替换为Ip直接连接的方式来代替域名访问的方法，从而达到更快的网络请求，但是使用Ip地址不够灵活，当后台变换了Ip地址的话，会出现访问不了，前段的App需要发包，解决方法是增加Ip地址动态更新的能力，或者是在IP地址访问失败了，切换到域名的访问
         */
        DNSdemo();


        //----->2
        /*
        合并网络请求，一次完整的Http请求，首先进行的是DNS查找，通过TCP三次握手，从而建立连接，如果是https请求的话，还要经过TLS握手成功后才可以进行连接，对于网络请求，减少接口，能够合并的网络请求就尽量合并


        SSL(Secure Sockets Layer 安全套接层),及其继任者传输层安全（Transport Layer Security，TLS）是为网络通信提供安全及数据完整性的一种安全协议。TLS与SSL在传输层对网络连接进行加密。

        HTTPS和HTTP的区别主要为以下四点：
一、https协议需要到ca申请证书，一般免费证书很少，需要交费。
二、http是超文本传输协议，信息是明文传输，https 则是具有安全性的ssl加密传输协议。
三、http和https使用的是完全不同的连接方式，用的端口也不一样，前者是80，后者是443。
四、http的连接很简单，是无状态的；HTTPS协议是由SSL+HTTP协议构建的可进行加密传输、身份认证的网络协议，比http协议安全。
         */

        //----->3
        /*
        预先获取数据能够将网络请求集中在一次，这样其他时间段手机就可以切换到空闲的时间，从而避免经常性的唤醒，从而节约用电
         */



        //----->4
       /*
       避免轮询：如果说每个一段时间需要向服务器发起主动的网络请求，其实不建议在app端做这样的操作，可以使用推送，如果说在不得已的情况下，也要避免使用Thread.sleep()函数来循环等待，建议使用系统的AlarmManager来实现定时轮询，AlarmManager 可以保证在系统休眠的时候，CPU也可以得到休息，在下一次需要发起网络球球的时候才唤醒
        */

        //----->5
        /*
        尽量避免网络请求失败时候，无限制的循环重试连接，在我第一篇简书博客有写过一个网络加载的框架 ：https://www.jianshu.com/p/141ee58eb143    中有提到过
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
         */

        //----->6
        /*
        离线缓存，对于图片或者文件，内存缓存+磁盘缓存+网络缓存，一般我们本地需要做的是二级缓存，当缓存中存在图片或者是文件，直接从缓存中读取，不会走网络，下载图片，在Android中使用LruCache实现内存缓存，DiskLruCache实现本地缓存
         */

        lruCacheDemo();

       //https://github.com/JakeWharton/DiskLruCache


        //----->7
        /*
        压缩数据的大小：可以对发送服务端数据进行gzip压缩，同时可以使用更优的数据传输格式，例如二进制的代替Json格式，这个比较牛逼，估计运用的很少，使用webp格式代替图片格式

         */
        //----->8
       /*
       不同的网络环境使用不同的超时策略，常见的网络格式有 2g、3g、4g、wifi,实时的更新当前的网络状态，通过监听来获取最新的网络类型，并动态调整网络超时的时间
        */
        netWorkDemo();
        //----->9 CDN
        /*
        CDN的全称是Content Delivery Network，即内容分发网络。其基本思路是尽可能避开互联网上有可能影响数据传输速度和稳定性的瓶颈和环节，使内容传输的更快、更稳定。通过在网络各处放置节点服务器所构成的在现有的互联网基础之上的一层智能虚拟网络，CDN系统能够实时地根据网络流量和各节点的连接、负载状况以及到用户的距离和响应时间等综合信息将用户的请求重新导向离用户最近的服务节点上。其目的是使用户可就近取得所需内容，解决 Internet网络拥挤的状况，提高用户访问网站的响应速度。
         */

    }

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

//        mLruCache = new LruCache<String, Bitmap>(CACHE_MAX_SIZE) {
//            @Override
//            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
//                if (oldValue != newValue)
//                    oldValue.recycle();
//            }
//        };
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
    /**
     * dns的Demo演示
     */
    private void DNSdemo() {
        /*

        PING ps_other.a.shifen.com (220.181.57.216) 56(84) bytes of data.64 bytes from 220.181.57.216: icmp_seq=1 ttl=55 time=41.4 ms--- ps_other.a.shifen.com ping statistics ---1 packets transmitted, 1 received, 0% packet loss, time 0msrtt min/avg/max/mdev = 41.435/41.435/41.435/0.000 ms
         */
        //220.181.57.216
        boolean ping = ping("wwww.baidu.com");
        System.out.println("shiming wwww.baidu.com baidu ping==="+ping);
        TextView pingTv = findViewById(R.id.ping_tv_demo);

        boolean ping1 = ping("wwww.baidu.com11");
        System.out.println("shiming wwww.baidu.com11  ping====="+ping1);
        pingTv.setText("wwww.baidu.com baidu ping的是正确的地址==="+ping+"\n\r"+"shiming wwww.baidu.com11  ping错误的地址====="+ping1+"-------->可以根据是否ping的通，来判断是否加载另外一个地址  nice");
        /*
        测试ping 一个不通的地址，如果ping不通的话，返回一个百度的地址
         */
        //耗时操作
//        final String s = ping4Domain("https://www.jianshu.com/");
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                System.out.println("shiming 返回的地址是 ---》"+s);
//            }
//        }, 5000);//


    }
    /**
     * 测试主域名返回ip地址
     * @param domain
     * @return ip地址，为空则ping不通
     */
    private String ping4Domain(final String domain) {
        String ip = "";
        try {
            ip = executeCommand(domain,PING_TIME_OUT);
        } catch (IOException e) {
            e.printStackTrace();
            ip = "IOException";
        } catch (InterruptedException e) {
            e.printStackTrace();
            ip = "InterruptedException";
        } catch (TimeoutException e) {
            e.printStackTrace();
            ip = "www.baidu.com";
        }
        return ip;
    }

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
    /**
     * 执行域名是否可通，且获取ping后的IP地址
     * @param command
     * @param timeout
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    private String executeCommand( final String command, final long timeout )
            throws IOException, InterruptedException, TimeoutException {
        Process process = Runtime.getRuntime().exec("ping -c 1 -w 100 " + command);
        PingWorker worker = new PingWorker(process);
        worker.start();
        try {
            worker.join(timeout);
            if (worker.exit != null) {
                return worker.ip;
            } else {
                //throw new TimeoutException();
                return "超时了，这里不想抛出异常";
            }
        } catch (InterruptedException ex) {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw ex;
        } finally {
            process.destroy();
        }
    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (null!=mWorker.process) {
//            mWorker.process.destroy();
//        }
    }
}
