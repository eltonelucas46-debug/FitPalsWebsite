package com.fitpals.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends Activity {

    private WebView webView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressBar progressBar;

    private FrameLayout rootLayout;

    private ValueCallback<Uri[]> filePathCallback;

    private static final int FILE_CHOOSER_REQUEST = 1001;

    private static final int NOTIFICATION_PERMISSION_REQUEST = 2001;

    private static final String FITPALS_URL =
            "https://eltonelucas46-debug.github.io/FitPalsWebsite/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            boolean darkMode = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int flags = window.getDecorView().getSystemUiVisibility();
                if (!darkMode) {
                    flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                } else {
                    flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                }
                window.getDecorView().setSystemUiVisibility(flags);
            }
        }



        requestNotificationPermission();

        initializeFirebaseMessaging();

        showFitnessSplash();
    }

    // ============================================================
    // NOTIFICATION PERMISSION
    // ============================================================

    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            if (checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        NOTIFICATION_PERMISSION_REQUEST
                );
            }
        }
    }

    // ============================================================
    // FIREBASE MESSAGING
    // ============================================================

    private void initializeFirebaseMessaging() {

        FirebaseMessaging
                .getInstance()
                .getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        return;
                    }

                    String token = task.getResult();

                    getSharedPreferences(
                            "fitpals",
                            MODE_PRIVATE
                    )
                            .edit()
                            .putString(
                                    "fcm_token",
                                    token
                            )
                            .apply();
                });
    }

    // ============================================================
    // SPLASH
    // ============================================================


    private void showFitnessSplash() {

        FrameLayout splash = new FrameLayout(this);
        splash.setBackgroundColor(Color.WHITE);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);
        content.setPadding(dp(24), dp(24), dp(24), dp(24));

        FrameLayout.LayoutParams contentParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
        splash.addView(content, contentParams);

        // FitPals logo
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo);
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        logo.setAlpha(0f);
        logo.setScaleX(0.65f);
        logo.setScaleY(0.65f);

        LinearLayout.LayoutParams logoParams =
                new LinearLayout.LayoutParams(dp(155), dp(155));
        logoParams.bottomMargin = dp(12);
        content.addView(logo, logoParams);

        // "FitPals" letter-by-letter writing effect
        LinearLayout letters = new LinearLayout(this);
        letters.setOrientation(LinearLayout.HORIZONTAL);
        letters.setGravity(Gravity.CENTER);

        String title = "FitPals";

        TextView[] letterViews = new TextView[title.length()];

        for (int i = 0; i < title.length(); i++) {
            TextView letter = new TextView(this);
            letter.setText(String.valueOf(title.charAt(i)));
            letter.setTextSize(34);
            letter.setTextColor(Color.rgb(33, 150, 243));
            letter.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            letter.setGravity(Gravity.CENTER);
            letter.setAlpha(0f);
            letter.setTranslationY(dp(18));

            letters.addView(letter,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

            letterViews[i] = letter;
        }

        content.addView(letters);

        // Slogan
        TextView slogan = new TextView(this);
        slogan.setText("TRAIN • MOVE • GROW");
        slogan.setTextSize(12);
        slogan.setTextColor(Color.DKGRAY);
        slogan.setGravity(Gravity.CENTER);
        slogan.setLetterSpacing(0.18f);
        slogan.setAlpha(0f);
        slogan.setTranslationY(dp(10));

        LinearLayout.LayoutParams sloganParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        sloganParams.topMargin = dp(8);
        content.addView(slogan, sloganParams);

        // Small animated loading line
        View loadingLine = new View(this);
        loadingLine.setBackgroundColor(Color.rgb(33, 150, 243));
        loadingLine.setScaleX(0f);
        loadingLine.setAlpha(0f);

        LinearLayout.LayoutParams lineParams =
                new LinearLayout.LayoutParams(dp(110), dp(3));
        lineParams.topMargin = dp(24);
        content.addView(loadingLine, lineParams);

        setContentView(splash);

        // Logo entrance
        logo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(650)
                .setInterpolator(new android.view.animation.OvershootInterpolator())
                .start();

        // Write FitPals one letter at a time
        for (int i = 0; i < letterViews.length; i++) {

            final TextView letter = letterViews[i];

            letter.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(450 + (i * 90L))
                    .setDuration(280)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
        }

        // Slogan
        slogan.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(1050)
                .setDuration(500)
                .start();

        // Loading line
        loadingLine.animate()
                .alpha(1f)
                .scaleX(1f)
                .setStartDelay(1150)
                .setDuration(550)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

        // Open the FitPals website
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            splash.animate()
                    .alpha(0f)
                    .setDuration(350)
                    .withEndAction(this::createWebView)
                    .start();

        }, 1900);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    // WEBVIEW
    // ============================================================

    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView() {

        rootLayout =
                new FrameLayout(this);

        swipeRefreshLayout =
                new SwipeRefreshLayout(this);

        webView =
                new WebView(this);

        SwipeRefreshLayout.LayoutParams webParams =
                new SwipeRefreshLayout.LayoutParams(
                        SwipeRefreshLayout.LayoutParams.MATCH_PARENT,
                        SwipeRefreshLayout.LayoutParams.MATCH_PARENT
                );

        swipeRefreshLayout.addView(
                webView,
                webParams
        );

        FrameLayout.LayoutParams swipeParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        rootLayout.addView(
                swipeRefreshLayout,
                swipeParams
        );

        progressBar =
                new ProgressBar(
                        this,
                        null,
                        android.R.attr.progressBarStyleHorizontal
                );

        progressBar.setMax(
                100
        );

        FrameLayout.LayoutParams progressParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        5
                );

        progressParams.gravity =
                Gravity.TOP;

        rootLayout.addView(
                progressBar,
                progressParams
        );

        setContentView(
                rootLayout
        );

        configureWebView();

        setupWebViewClient();

        setupWebChromeClient();

        setupDownloads();

        swipeRefreshLayout
                .setOnRefreshListener(
                        () -> {

                            if (webView != null) {

                                webView.reload();
                            }
                        }
                );

        swipeRefreshLayout
                .setColorSchemeColors(
                        Color.BLACK
                );

        swipeRefreshLayout
                .setOnChildScrollUpCallback(
                        (parent, child) ->
                                webView.canScrollVertically(-1)
                );

        webView.loadUrl(
                FITPALS_URL
        );
    }

    // ============================================================
    // WEBVIEW CONFIGURATION
    // ============================================================

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {

        WebSettings settings =
                webView.getSettings();

        settings.setJavaScriptEnabled(
                true
        );

        settings.setDomStorageEnabled(
                true
        );

        settings.setDatabaseEnabled(
                true
        );

        settings.setAllowFileAccess(
                true
        );

        settings.setAllowContentAccess(
                true
        );

        settings.setSupportZoom(
                false
        );

        settings.setBuiltInZoomControls(
                false
        );

        settings.setDisplayZoomControls(
                false
        );

        settings.setLoadWithOverviewMode(
                false
        );

        settings.setUseWideViewPort(
                false
        );

        settings.setJavaScriptCanOpenWindowsAutomatically(
                true
        );

        settings.setMediaPlaybackRequiresUserGesture(
                false
        );

        CookieManager cookieManager =
                CookieManager.getInstance();

        cookieManager.setAcceptCookie(
                true
        );

        cookieManager.setAcceptThirdPartyCookies(
                webView,
                true
        );

        webView.setBackgroundColor(
                Color.WHITE
        );

        webView.setVerticalScrollBarEnabled(
                false
        );

        webView.setHorizontalScrollBarEnabled(
                false
        );

        webView.setOverScrollMode(
                View.OVER_SCROLL_NEVER
        );

        webView.setLayerType(
                View.LAYER_TYPE_HARDWARE,
                null
        );
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    private void setupWebViewClient() {

        webView.setWebViewClient(
                new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(
                            WebView view,
                            WebResourceRequest request
                    ) {

                        return handleUrl(
                                request.getUrl()
                        );
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(
                            WebView view,
                            String url
                    ) {

                        return handleUrl(
                                Uri.parse(url)
                        );
                    }

                    @Override
                    public void onPageFinished(
                            WebView view,
                            String url
                    ) {

                        super.onPageFinished(
                                view,
                                url
                        );

                        progressBar.setVisibility(
                                View.GONE
                        );

                        if (swipeRefreshLayout != null) {

                            swipeRefreshLayout
                                    .setRefreshing(false);
                        }
                    }

                    @Override
                    public void onReceivedError(
                            WebView view,
                            int errorCode,
                            String description,
                            String failingUrl
                    ) {

                        if (swipeRefreshLayout != null) {

                            swipeRefreshLayout
                                    .setRefreshing(false);
                        }

                        showOfflineScreen();
                    }
                }
        );
    }

    private boolean handleUrl(
            Uri uri
    ) {

        String scheme =
                uri.getScheme();

        if (scheme == null) {

            return false;
        }

        if (scheme.equals("http")
                || scheme.equals("https")) {

            String host =
                    uri.getHost();

            if (host != null
                    && host.contains(
                    "eltonelucas46-debug.github.io"
            )) {

                return false;
            }

            openExternal(uri);

            return true;
        }

        if (scheme.equals("tel")
                || scheme.equals("mailto")
                || scheme.equals("whatsapp")
                || scheme.equals("sms")) {

            openExternal(uri);

            return true;
        }

        return false;
    }

    private void openExternal(
            Uri uri
    ) {

        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            uri
                    );

            startActivity(intent);

        } catch (
                ActivityNotFoundException e
        ) {

            Toast.makeText(
                    this,
                    "No app available for this link",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ============================================================
    // FILE UPLOAD
    // ============================================================

    private void setupWebChromeClient() {

        webView.setWebChromeClient(
                new WebChromeClient() {

                    @Override
                    public void onProgressChanged(
                            WebView view,
                            int newProgress
                    ) {

                        progressBar.setVisibility(
                                View.VISIBLE
                        );

                        progressBar.setProgress(
                                newProgress
                        );

                        if (newProgress >= 100) {

                            progressBar.setVisibility(
                                    View.GONE
                            );
                        }
                    }

                    @Override
                    public boolean onShowFileChooser(
                            WebView webView,
                            ValueCallback<Uri[]> callback,
                            FileChooserParams params
                    ) {

                        if (filePathCallback != null) {

                            filePathCallback
                                    .onReceiveValue(
                                            null
                                    );
                        }

                        filePathCallback =
                                callback;

                        Intent intent =
                                params.createIntent();

                        try {

                            startActivityForResult(
                                    intent,
                                    FILE_CHOOSER_REQUEST
                            );

                        } catch (
                                ActivityNotFoundException e
                        ) {

                            filePathCallback = null;

                            Toast.makeText(
                                    MainActivity.this,
                                    "Unable to open file picker",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return false;
                        }

                        return true;
                    }
                }
        );
    }

    // ============================================================
    // DOWNLOADS
    // ============================================================

    private void setupDownloads() {

        webView.setDownloadListener(
                new DownloadListener() {

                    @Override
                    public void onDownloadStart(
                            String url,
                            String userAgent,
                            String contentDisposition,
                            String mimetype,
                            long contentLength
                    ) {

                        try {

                            Intent intent =
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(url)
                                    );

                            startActivity(
                                    intent
                            );

                        } catch (Exception e) {

                            Toast.makeText(
                                    MainActivity.this,
                                    "Unable to download file",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );
    }

    // ============================================================
    // OFFLINE SCREEN
    // ============================================================

    private void showOfflineScreen() {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setGravity(
                Gravity.CENTER
        );

        layout.setPadding(
                40,
                40,
                40,
                40
        );

        layout.setBackgroundColor(
                Color.WHITE
        );

        ImageView logo =
                new ImageView(this);

        logo.setImageResource(
                R.drawable.fitpals_icon
        );

        logo.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        int logoSize =
                (int) (
                        110 *
                        getResources()
                                .getDisplayMetrics()
                                .density
                );

        layout.addView(
                logo,
                new LinearLayout.LayoutParams(
                        logoSize,
                        logoSize
                )
        );

        TextView title =
                new TextView(this);

        title.setText(
                "You're offline"
        );

        title.setTextSize(
                26
        );

        title.setTextColor(
                Color.BLACK
        );

        title.setGravity(
                Gravity.CENTER
        );

        title.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        layout.addView(
                title
        );

        TextView message =
                new TextView(this);

        message.setText(
                "FitPals needs an internet connection.\n\n" +
                        "Check your connection and try again."
        );

        message.setTextSize(
                15
        );

        message.setTextColor(
                Color.DKGRAY
        );

        message.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams messageParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        messageParams.topMargin = 15;

        layout.addView(
                message,
                messageParams
        );

        Button retry =
                new Button(this);

        retry.setText(
                "RETRY"
        );

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        buttonParams.topMargin = 25;

        layout.addView(
                retry,
                buttonParams
        );

        retry.setOnClickListener(
                v -> {

                    setContentView(
                            rootLayout
                    );

                    webView.loadUrl(
                            FITPALS_URL
                    );
                }
        );

        setContentView(
                layout
        );
    }

    // ============================================================
    // FILE RESULT
    // ============================================================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode ==
                FILE_CHOOSER_REQUEST) {

            Uri[] results = null;

            if (resultCode == RESULT_OK
                    && data != null) {

                Uri dataUri =
                        data.getData();

                if (dataUri != null) {

                    results =
                            new Uri[]{
                                    dataUri
                            };
                }
            }

            if (filePathCallback != null) {

                filePathCallback
                        .onReceiveValue(
                                results
                        );

                filePathCallback = null;
            }
        }
    }

    // ============================================================
    // BACK BUTTON
    // ============================================================

    @Override
    public void onBackPressed() {

        if (webView != null
                && webView.canGoBack()) {

            webView.goBack();

        } else {

            super.onBackPressed();
        }
    }

    // ============================================================
    // CLEANUP
    // ============================================================

    @Override
    protected void onDestroy() {

        if (webView != null) {

            webView.stopLoading();

            webView.destroy();

            webView = null;
        }

        super.onDestroy();
    }
}
