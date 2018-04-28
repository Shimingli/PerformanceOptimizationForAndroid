package com.shiming.performanceoptimization;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * author： Created by shiming on 2018/4/28 10:52
 * mailbox：lamshiming@sina.com
 */

public  class MainActivity extends AppCompatActivity {

    private Button mBtn_codeOptimization;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn_codeOptimization = findViewById(R.id.btn_1);
//        Button viewById = findViewById(R.id.btn_2);
//        Button viewById = findViewById(R.id.btn_3);
//        Button viewById = findViewById(R.id.btn_4);
//        Button viewById = findViewById(R.id.btn_5);
       mBtn_codeOptimization.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this,CodeOptimizationActivity.class));
           }
       });
    }
}
