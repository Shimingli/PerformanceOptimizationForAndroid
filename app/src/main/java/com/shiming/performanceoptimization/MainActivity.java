package com.shiming.performanceoptimization;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.shiming.performanceoptimization.code_optimization.CodeOptimizationActivity;
import com.shiming.performanceoptimization.electric_quantity_optimization.ElectricQuantityOptimizationActivity;
import com.shiming.performanceoptimization.image_optimization.ImageOptimizationActivity;
import com.shiming.performanceoptimization.layout_Optimization.LayoutOptimizationActivity;


/**
 * author： Created by shiming on 2018/4/28 10:52
 * mailbox：lamshiming@sina.com
 */

public  class MainActivity extends AppCompatActivity {

    private Button mBtn_codeOptimization;
    private Button mBtn_imageOptimization;
    private Button mBtn_electricQuantityOptimization;
    private Button mBtn_layoutOptimization;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn_codeOptimization = findViewById(R.id.btn_1);
        mBtn_imageOptimization = findViewById(R.id.btn_2);
        mBtn_electricQuantityOptimization = findViewById(R.id.btn_3);
        mBtn_layoutOptimization = findViewById(R.id.btn_4);
//        Button viewById = findViewById(R.id.btn_5);
       mBtn_codeOptimization.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this,CodeOptimizationActivity.class));
           }
       });
        mBtn_imageOptimization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ImageOptimizationActivity.class));
            }
        });
        mBtn_electricQuantityOptimization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(MainActivity.this,ElectricQuantityOptimizationActivity.class));
            }
        });
        mBtn_layoutOptimization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LayoutOptimizationActivity.class));
            }
        });
    }
}
