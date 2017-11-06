package com.tistory.dayglo.smarting_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 2017-11-06.
 */

public class IntroActivity extends AppCompatActivity {
    private Handler mHandler;

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 1500);
    }
}
