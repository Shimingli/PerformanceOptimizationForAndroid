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
