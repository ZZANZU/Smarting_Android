package com.tistory.dayglo.smarting_android;

import android.content.DialogInterface;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private static final String temperatureUrl = "http://13.59.174.162:7579/temperature";
    private static final String trashUrl = "http://13.59.174.162:7579/trash";
    private static final String openDoorUrl = "http://192.168.2.51:3000/unlock";
    private static final String closeDoorUrl = "http://192.168.2.51:3000/lock";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_main);

        // TODO 2017-11-09 ButterKnife 적용
        doorbellLayout = (ExpandableRelativeLayout) findViewById(R.id.doorbell_layout);
        temperatureLayout = (ExpandableRelativeLayout) findViewById(R.id.temperature_layout);

        visitorImage = (ImageView) findViewById(R.id.visitor_photo);

        requestData(ispressedUrl, callbackAfterGettingPressed);
        requestData(temperatureUrl, callbackAfterGettingTemperature);
        requestData(trashUrl, callbackAfterGettingTrash);

    }

    public void onClickDoorbell(View view) {
        doorbellLayout.toggle();

        requestData(ispressedUrl, callbackAfterGettingPressed);
    }

    public void onClickTemperature(View view) {
        temperatureLayout.toggle();

        requestData(temperatureUrl, callbackAfterGettingTemperature);
    }

    public void onClickTrashCan(View view) {
        requestData(trashUrl, callbackAfterGettingTrash);
    }

    Call openDoor(String httpUrl, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(httpUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
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

    // doorbell 콜백
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
                // TODO: 2017-12-12 JSON형태의 responseData 변수를 parsing해서 visitor 텍스트뷰와 visiting time 텍스트뷰에 setText하기

                // Run view-related code back on the main thread
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String dateData = responseData.replaceAll("\"","");
//                        String dateData = responseData;
                        Log.d(TAG, "dateData : " + dateData);

                        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", java.util.Locale.getDefault());

                        try {
                            Date originalDate = originalFormat.parse(dateData);

                            String newDate = newFormat.format(originalDate);

                            TextView tv = (TextView) findViewById(R.id.visit_time_textview);
                            tv.setText(newDate);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }

        }
    };

    // temperature 콜백
    public Callback callbackAfterGettingTemperature = new Callback() {
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
                        TextView tv = (TextView) findViewById(R.id.temperature_textview);
                        tv.setText(responseData);
                    }
                });
            }

        }
    };

    // trash can 콜백
    public Callback callbackAfterGettingTrash = new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onFailure: trash callback fail");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                final String responseData = response.body().string();
                final int height = Math.round(Float.valueOf(responseData));
                Log.d(TAG, String.valueOf(height));

                // Run view-related code back on the main thread
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView trashIcon = (ImageView) findViewById(R.id.trash_icon);
                        ImageView trashBar = (ImageView) findViewById(R.id.trash_bar);
                        TextView trashHeight = (TextView) findViewById(R.id.trash_percentage_textview);

                        if(height < 25) {
                            trashIcon.setImageResource(R.drawable.trash_icon_1);
                            trashBar.setImageResource(R.drawable.trash_bar_1);
                            trashHeight.setText(String.valueOf(height));
                        } else if(25 <= height && height < 50) {
                            trashIcon.setImageResource(R.drawable.trash_icon_2);
                            trashBar.setImageResource(R.drawable.trash_bar_2);
                            trashHeight.setText(String.valueOf(height));
                        } else if(50 <= height && height < 75) {
                            trashIcon.setImageResource(R.drawable.trash_icon_3);
                            trashBar.setImageResource(R.drawable.trash_bar_3);
                            trashHeight.setText(String.valueOf(height));
                        } else if(75 <= height && height < 96) {
                            trashIcon.setImageResource(R.drawable.trash_icon_4);
                            trashBar.setImageResource(R.drawable.trash_bar_4);
                            trashHeight.setText(String.valueOf(height));
                        } else if(96 <= height && height <= 100){
                            trashIcon.setImageResource(R.drawable.trash_icon_5);
                            trashBar.setImageResource(R.drawable.trash_bar_5);
                            trashHeight.setText(String.valueOf(height));
                        } else {
                            trashIcon.setImageResource(R.drawable.trash_icon_1);
                            trashBar.setImageResource(R.drawable.trash_bar_1);
                            trashHeight.setText("??");
                        }


                    }
                });
            }
        }
    };

    // door opening 콜백
    public Callback callbackAfterOpeningDoor = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d(TAG, "onResponse: door opening button pressed");
        }
    };

    public void onClickVisitorImage(View view) {
        final WebViewDialog webViewDialog = new WebViewDialog(this);

        webViewDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // TODO 2017-11-14 로딩할 때 ProgressBar 추가
            }
        });

        webViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        webViewDialog.show();
    }

    // door opening button
    public void onClickOpen(View view) throws IOException {
        openDoor(openDoorUrl, callbackAfterOpeningDoor);
    }

    public void onclickClose(View view) {
        openDoor(closeDoorUrl, callbackAfterOpeningDoor);
    }
}
