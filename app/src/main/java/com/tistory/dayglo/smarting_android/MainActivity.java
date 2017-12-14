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

import org.json.JSONException;
import org.json.JSONObject;
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
        trashLayout = (ExpandableRelativeLayout) findViewById(R.id.trash_layout);

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
        trashLayout.toggle();

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
                // TODO: 2017-12-13 코드 정리 
                String responseData = response.body().string(); // {date: "", visitor:""}
                String visitor = null;
                String visitTime = null;

                try {
                    // String 타입의 responseData를 JSON 타입으로 변환(굳이?)
                    JSONObject jsonObject = new JSONObject(responseData);
                    // JSON key 데이터 추출
                    visitor = jsonObject.getString("visitor");
                    visitTime = jsonObject.getString("date");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Run view-related code back on the main thread
                final String finalVisitTime = visitTime;
                final String finalVisitor = visitor;

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String dateData = finalVisitTime.replaceAll("\"","");

                        Log.d(TAG, "dateData : " + dateData);

                        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", java.util.Locale.getDefault());

                        try {
                            Date originalDate = originalFormat.parse(dateData);

                            String newDate = newFormat.format(originalDate);

                            TextView visitTimeTextView = (TextView) findViewById(R.id.visit_time_textview);
                            TextView visitorTextView = (TextView) findViewById(R.id.visitor_name_textivew);
                            visitTimeTextView.setText(newDate);
                            visitorTextView.setText(finalVisitor);

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
                String responseData = response.body().string();
                String emptiedTime = null;
                int height = 0;

                try {
                    // String 타입의 responseData를 JSON 타입으로 변환(굳이?)
                    JSONObject jsonObject = new JSONObject(responseData);
                    // JSON key 데이터 추출
                    emptiedTime = jsonObject.getString("time");
                    height = jsonObject.getInt("percentage");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                final int height = Math.round(Float.valueOf(responseData));
//                Log.d(TAG, String.valueOf(height));

                // Run view-related code back on the main thread
                final int finalHeight = height;
                final String finalEmptiedTime = emptiedTime;

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "emptied time : " + finalEmptiedTime);

                        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                        SimpleDateFormat newFormat = new SimpleDateFormat("MM월 dd일 HH시 mm분", java.util.Locale.getDefault());

                        try {
                            Date originalTime = originalFormat.parse(finalEmptiedTime);

                            String newTime = newFormat.format(originalTime);

                            TextView emptiedTimeTextView = (TextView) findViewById(R.id.trash_emptied_time_textview);
                            emptiedTimeTextView.setText(newTime);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        ImageView trashIcon = (ImageView) findViewById(R.id.trash_icon);
                        ImageView trashBar = (ImageView) findViewById(R.id.trash_bar);
                        TextView trashHeightTextView = (TextView) findViewById(R.id.trash_percentage_textview);

                        if(finalHeight < 25) {
                            trashIcon.setImageResource(R.drawable.trash_icon_1);
                            trashBar.setImageResource(R.drawable.trash_bar_1);
                            trashHeightTextView.setText(String.valueOf(finalHeight));
                        } else if(25 <= finalHeight && finalHeight < 50) {
                            trashIcon.setImageResource(R.drawable.trash_icon_2);
                            trashBar.setImageResource(R.drawable.trash_bar_2);
                            trashHeightTextView.setText(String.valueOf(finalHeight));
                        } else if(50 <= finalHeight && finalHeight < 75) {
                            trashIcon.setImageResource(R.drawable.trash_icon_3);
                            trashBar.setImageResource(R.drawable.trash_bar_3);
                            trashHeightTextView.setText(String.valueOf(finalHeight));
                        } else if(75 <= finalHeight && finalHeight < 96) {
                            trashIcon.setImageResource(R.drawable.trash_icon_4);
                            trashBar.setImageResource(R.drawable.trash_bar_4);
                            trashHeightTextView.setText(String.valueOf(finalHeight));
                        } else if(96 <= finalHeight && finalHeight <= 100){
                            trashIcon.setImageResource(R.drawable.trash_icon_5);
                            trashBar.setImageResource(R.drawable.trash_bar_5);
                            trashHeightTextView.setText(String.valueOf(finalHeight));
                        } else {
                            trashIcon.setImageResource(R.drawable.trash_icon_1);
                            trashBar.setImageResource(R.drawable.trash_bar_1);
                            trashHeightTextView.setText("??");
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
