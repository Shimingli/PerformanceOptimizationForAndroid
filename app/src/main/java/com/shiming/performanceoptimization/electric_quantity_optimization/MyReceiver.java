package com.shiming.performanceoptimization.electric_quantity_optimization;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;



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
