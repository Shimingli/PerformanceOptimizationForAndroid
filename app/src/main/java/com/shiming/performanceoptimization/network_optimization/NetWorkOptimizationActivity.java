package com.shiming.performanceoptimization.network_optimization;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        /*
        DNS域名的系统，主要的功能根据应用请求所用的域名URL去网络上面映射表中查相对应的IP地址，这个过程有可能会消耗上百毫秒，而且可能存在着DNS劫持的危险，可以替换为Ip直接连接的方式来代替域名访问的方法，从而达到更快的网络请求，但是使用Ip地址不够灵活，当后台变换了Ip地址的话，会出现访问不了，前段的App需要发包，解决方法是增加Ip地址动态更新的能力，或者是在IP地址访问失败了，切换到域名的访问
         */
        DNSdemo();

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
