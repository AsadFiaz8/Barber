package com.bjbarber.barber;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SaudeBlazaActivity extends AppCompatActivity {

    WebView webView;
    private Bundle webViewBundle;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saude_blaza);

        getSupportActionBar().setTitle("Saude Blaza");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = findViewById(R.id.webViewSaudeBlaza);
        progressBar = findViewById(R.id.progressBar);

        clearCookiesAndCache();
        webView.clearCache(true);

        if (webViewBundle == null) {
            webView.clearCache(true);
            Toast.makeText(SaudeBlazaActivity.this, "Loading...", Toast.LENGTH_LONG).show();
            webView.loadUrl("https://pt.oriflame.com/beautyedit?store=ORINATURA");
        } else {
            webView.restoreState(webViewBundle);
        }

        webView.setWebViewClient(new SaudeBlazaActivity.myWebClient());
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });
    }

    public void clearCookiesAndCache() {
        android.webkit.CookieManager cookieManager = CookieManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                // a callback which is executed when the cookies have been removed
                @Override
                public void onReceiveValue(Boolean aBoolean) {

                }
            });
        } else cookieManager.removeAllCookie();

    }

    public class myWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            Toast.makeText(SaudeBlazaActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            //super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            progressBar.setVisibility(View.GONE);
        }

        // To handle "Back" key press event for WebView to go back to previous screen.
    }
}
