package com.shiming.performanceoptimization.electric_quantity_optimization;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shiming.performanceoptimization.MainActivity;
import com.shiming.performanceoptimization.R;

import org.w3c.dom.Text;

/**
 * 电量优化，的注意事项
 * 把148和 151 行两行代码注释掉，你可以发现定位监听还是在运行中的 ,给你冬天来个暖手宝，
 *
 */
// TODO: 2018/5/3 给与权限，在开发者设置下，给与位置模拟，因为这里写了个获取位置的Demo，tks！！！ 麻烦了·······
public class ElectricQuantityOptimizationActivity extends AppCompatActivity {

    private LocationManager mLocationManager;
    private LocationListener mLlistener;
    private String mProvider;
    private TextView mTv_location;
    private static int mCount=0;
    private TextView mTv_alarmManager;
    private AlarmManager mAm;
    private PendingIntent mPi;
    private PowerManager.WakeLock mTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric_quantity_optimization);
        mTv_location = findViewById(R.id.tv_location_des);

        mTv_alarmManager = findViewById(R.id.tv_alarm_manager);

        demoOne();
        //AlarmManager Demo
        /**
         * AlarmManager 也是比较耗电的，通常情况下需要保证两次唤醒操作的时间间隔不要太短了，在不需要使用唤醒功能的情况下，尽早的取消唤醒功能，否则应用会一直消耗电量
         */
        alarmManager();

        findViewById(R.id.tv_crash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 int i=1/0;
            }
        });


        //WakeLock 是为了保持设备的唤醒状态的API，组织用户长时间不用，仍然需要组织设备进入休眠的状态，比如用户在看电影的时候
        findViewById(R.id.tv_wakelock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wakeLockDemo();
            }
        });

        mTv_alarmManager.setText("WakeLock 是为了保持设备的唤醒状态的API，组织用户长时间不用，仍然需要组织设备进入休眠的状态，比如用户在看电影的时候");

    }

    /**
     * 使用wakelock 时，需要及时的释放锁，比如播放视屏的时候WakeLock保持屏幕的常亮，在暂停的时候就应该释放锁，而不是等到停止播放才释放
     */
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

    /**
     * AlarmManager 是SDK提供的一个唤醒的APi，是系统级别的服务，可以在特定的时刻广播一个指定的Intent，这个pendingIntent可以用来启动Activity、Service、BroadcastReceiver， app，在后台也会启动
     */
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


    @SuppressLint("MissingPermission")
    private void demoOne() {
        //1、（BroadCastReceiver）为了减少应用耗损的电量，我们在代码中尽量避免使用无用的操作代码，当应用退到后台了，一切页面的刷新都是没有意义的，并且浪费电，比如有个监听网络状态的广播并执行一些动作，弹窗或者是Toast，那么app需要在后台的时候，禁用掉这个功能，

        //2、数据传输    蓝牙传输，Wi-Fi传输  移动网络传输  后台数据的管理：根据业务需求，接口尽量避免无效数据的传输  数据传输的频度问题：通过经验值或者是数据统计的方法确定好数据传输的频度，避免冗余重复的数据传输，数据传输过程中要压缩数据的大小，合并网络请求，避免轮询

        //3、位置服务 正确的使用位置复位，是应用耗电的一个关键，
        /**
         * 需要注意以下的三点：
         * 1、有没有及时注销位置监听器：和广播差不多
         * 2、位置更新监听频率的设定；更加业务需求设置一个合理的更新频率值，
         *          * minTime:用来指定间更新通知的最小的时间间隔，单位是毫秒，看日志这里是1s更新的
         *          * minDistance:用来指定位置更新通知的最小的距离，单位是米
         * 3、Android提供了三种定位
         * GPS定位，通过GPS实现定位，精度最高，通常在10米（火星坐标），但是GPS定位在时间和电量上消耗也是最高的
         * 网络定位，通过移动通信的基站信号差异来计算出手机所在的位置，精度比GPS差好多
         * 被动定位，最省电的定位服务，如果使用被动定位服务。说明它想知道位置更新信息但有不主动获取，等待手机中其他应用或者是服务或者是系统组件发出定位请求，并和这些组件的监听器一起接收位置的信息，实际的开发中，一般使用的是第三方的地图，高德，腾讯，百度，他们做了很好的封装，同时在地图上的表现上更加的优化
         */
        // TODO: 2018/5/3 下面获取的location 为null  http://www.jb51.net/article/33228.htm
//        String serviceString = Context.LOCATION_SERVICE;// 获取的是位置服务
//        // 调用getSystemService()方法来获取LocationManager对象
//        LocationManager locationManager = (LocationManager) getSystemService(serviceString);
//        String provider = LocationManager.GPS_PROVIDER;// 指定LocationManager的定位方法
//        @SuppressLint("MissingPermission") // 调用getLastKnownLocation()方法获取当前的位置信息
//        Location location = locationManager.getLastKnownLocation(provider);
//
//        double lat = location.getLatitude();//获取纬度
//        double lng = location.getLongitude();//获取经度
//
//        System.out.println("shiming   lat+"+lat);
//        System.out.println("shiming   lng+"+lng);

        // TODO: 2018/5/3 https://stackoverflow.com/questions/41255966/caused-by-java-lang-securityexception-com-example-geofences-from-uid-10049-not
        // TODO: 2018/5/3 这里需要注意给与权限，并且还要对开发者选项哪里进行一个位置权限的模拟的设置

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

    }

    public boolean isProviderEnabled(Context ctx, String provider) {
        LocationManager manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(provider);
    }

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

    @Override
    protected void onPause() {
        mLocationManager.removeUpdates(mLlistener);
        super.onPause();
    }
}
