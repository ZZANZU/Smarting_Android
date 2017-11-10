package com.tistory.dayglo.smarting_android;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "zzanzu";

    ExpandableRelativeLayout doorbellLayout, temperatureLayout, trashLayout;
    ImageView visitorImage;

    private static final String ispressedUrl = "http://13.59.174.162:7579/ispressed";
    private static final String temperatureUrl = "http:/13.59.174.162:7579/temperature";

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

        requestData(ispressedUrl, callbackAfterGettingPressed);

    }

    public void onClickDoorbell(View view) {
        doorbellLayout.toggle();

        requestData(ispressedUrl, callbackAfterGettingPressed);
    }

    public void onClickTemperature(View view) {
        temperatureLayout.toggle();
    }

    public void onClickTrashCan(View view) {
        trashLayout.toggle();
    }

    // TODO 2017-11-09 따로 파일 만들어서 import해와서 쓰기
    private void requestData(String httpUrl, Callback callBack) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(httpUrl).newBuilder();
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callBack);
    }

    public Callback callbackAfterGettingPressed = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onFailure: callback fail");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                final String responseData = response.body().string();
                Log.d(TAG, responseData);

                // Run view-related code back on the main thread
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.visit_time_textview);
                        tv.setText(responseData);
                    }
                });
            }

        }
    };
}
