package com.tistory.dayglo.smarting_android;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

public class MainActivity extends AppCompatActivity {
    ExpandableRelativeLayout doorbellLayout, temperatureLayout, trashLayout;
    ImageView visitorImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_main);

        // TODO 2017-11-09 ButterKnife 적용
        doorbellLayout = (ExpandableRelativeLayout) findViewById(R.id.doorbell_layout);
        temperatureLayout = (ExpandableRelativeLayout) findViewById(R.id.temperature_layout);
        trashLayout = (ExpandableRelativeLayout) findViewById(R.id.trash_layout);

        visitorImage = (ImageView) findViewById(R.id.visitor_photo);


    }

    public void onClickDoorbell(View view) {
        doorbellLayout.toggle();
    }

    public void onClickTemperature(View view) {
        temperatureLayout.toggle();
    }

    public void onClickTrashCan(View view) {
        trashLayout.toggle();
    }
}
