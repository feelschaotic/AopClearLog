package com.feelschaotic.aptdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        initView();
        testLog1();
        testLog2("testLog2");
    }

    private void initView() {
        Log.d(TAG, "initView");
    }

    private void testLog2(String msg) {
        Log.e(TAG, msg);
    }

    private void testLog1() {
        Log.d(TAG, "testLog1");
    }
}
