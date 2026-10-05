package com.niftitech.invoicing;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {
    private static final String APP_URL = "https://niftitech-invoice.netlify.app/";
    private static final String APP_HOST = "niftitech-invoice.netlify.app";
    private WebView web;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        web = findViewById(R.id.webview);
        swipe = findViewById(R.id.swipe);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);      // keeps login/session data
        s.setDatabaseEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setBuiltInZoomControls(false);
        CookieManager.getInstance().setAcceptCookie(true);

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                if (APP_HOST.equals(u.getHost())) return false;
                startActivity(new Intent(Intent.ACTION_VIEW, u)); // external links -> browser
                return true;
            }
            @Override
            public void onPageFinished(WebView v, String url) { swipe.setRefreshing(false); }
            @Override
            public void onReceivedError(WebView v, WebResourceRequest r, WebResourceError e) {
                if (r.isForMainFrame()) {
                    swipe.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "No connection. Pull down to retry.", Toast.LENGTH_LONG).show();
                }
            }
        });

        web.setDownloadListener((url, ua, cd, mime, len) -> {
            DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
            req.setMimeType(mime);
            req.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url));
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, cd, mime));
            ((DownloadManager) getSystemService(DOWNLOAD_SERVICE)).enqueue(req);
            Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show();
        });

        swipe.setOnRefreshListener(() -> web.reload());
        // Only allow pull-to-refresh when scrolled to top
        web.setOnScrollChangeListener((v, x, y, ox, oy) -> swipe.setEnabled(y == 0));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (web.canGoBack()) web.goBack(); else finish();
            }
        });

        if (savedInstanceState != null) web.restoreState(savedInstanceState);
        else web.loadUrl(APP_URL);
    }

    @Override
    protected void onSaveInstanceState(Bundle out) { super.onSaveInstanceState(out); web.saveState(out); }
}
