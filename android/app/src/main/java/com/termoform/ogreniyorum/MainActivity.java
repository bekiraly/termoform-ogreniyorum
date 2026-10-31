package com.termoform.ogreniyorum;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

// Opens the online page, so page updates reach the app without a new APK.
// Without internet it falls back to the copy bundled in the APK.
public class MainActivity extends Activity {
    private static final String LOCAL = "file:///android_asset/index.html";
    private WebView web;
    private boolean fellBack = false;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        int bg = Color.parseColor("#12191e");
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);

        web = new WebView(this);
        web.setBackgroundColor(bg);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setCacheMode(online() ? WebSettings.LOAD_DEFAULT : WebSettings.LOAD_CACHE_ELSE_NETWORK);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView v, WebResourceRequest r, WebResourceError e) {
                if (r.isForMainFrame() && !fellBack) {
                    fellBack = true;
                    v.loadUrl(LOCAL);
                }
            }
        });
        setContentView(web);

        if (state != null) web.restoreState(state);
        else web.loadUrl(online() ? BuildConfig.WEB_URL : LOCAL);
    }

    private boolean online() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo n = cm == null ? null : cm.getActiveNetworkInfo();
        return n != null && n.isConnected();
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
