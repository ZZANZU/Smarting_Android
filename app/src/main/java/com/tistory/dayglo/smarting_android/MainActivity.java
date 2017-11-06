package com.tistory.dayglo.smarting_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
        setContentView(R.layout.activity_main);
    }
}
