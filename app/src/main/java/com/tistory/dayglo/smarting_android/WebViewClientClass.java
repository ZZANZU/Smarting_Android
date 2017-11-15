package com.tistory.dayglo.smarting_android;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by user on 2017-11-14.
 */

class WebViewClientClass extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
    }
}
