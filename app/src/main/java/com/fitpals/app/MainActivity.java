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

        final int BLUE = Color.rgb(33, 150, 243);
        final int DARK = Color.rgb(18, 25, 35);
        final int WHITE = Color.WHITE;

        FrameLayout splash = new FrameLayout(this);
        splash.setBackgroundColor(DARK);

        // ============================================================
        // MAIN CONTENT
        // ============================================================

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);
        content.setPadding(dp(20), dp(20), dp(20), dp(20));

        FrameLayout.LayoutParams contentParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        splash.addView(content, contentParams);

        // ============================================================
        // TOP BRANDING
        // ============================================================

        TextView brandTop = new TextView(this);
        brandTop.setText("FITPALS™");
        brandTop.setTextSize(13);
        brandTop.setTextColor(Color.LTGRAY);
        brandTop.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        brandTop.setGravity(Gravity.CENTER);
        brandTop.setLetterSpacing(0.28f);
        brandTop.setAlpha(0f);

        content.addView(
                brandTop,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        // ============================================================
        // LOGO
        // ============================================================

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo);
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        logo.setAlpha(0f);
        logo.setScaleX(0.55f);
        logo.setScaleY(0.55f);

        LinearLayout.LayoutParams logoParams =
                new LinearLayout.LayoutParams(dp(145), dp(145));

        logoParams.topMargin = dp(8);
        logoParams.bottomMargin = dp(4);

        content.addView(logo, logoParams);

        // ============================================================
        // FITPALS TITLE
        // ============================================================

        TextView title = new TextView(this);
        title.setText("FitPals");
        title.setTextSize(36);
        title.setTextColor(BLUE);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        title.setAlpha(0f);
        title.setScaleX(0.85f);
        title.setScaleY(0.85f);

        content.addView(
                title,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        // ============================================================
        // FITNESS SCENE
        // ============================================================

        LinearLayout scene = new LinearLayout(this);
        scene.setOrientation(LinearLayout.HORIZONTAL);
        scene.setGravity(Gravity.CENTER);
        scene.setAlpha(0f);

        LinearLayout.LayoutParams sceneParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(125)
                );

        sceneParams.topMargin = dp(14);
        sceneParams.bottomMargin = dp(8);

        content.addView(scene, sceneParams);

        // Gym structure
        LinearLayout gym = new LinearLayout(this);
        gym.setOrientation(LinearLayout.VERTICAL);
        gym.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams gymParams =
                new LinearLayout.LayoutParams(dp(70), dp(105));

        gymParams.rightMargin = dp(8);

        scene.addView(gym, gymParams);

        TextView gymTop = new TextView(this);
        gymTop.setText("GYM");
        gymTop.setTextSize(14);
        gymTop.setTextColor(WHITE);
        gymTop.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        gymTop.setGravity(Gravity.CENTER);

        android.graphics.drawable.GradientDrawable gymBox =
                new android.graphics.drawable.GradientDrawable();

        gymBox.setColor(Color.rgb(35, 45, 58));
        gymBox.setCornerRadius(dp(8));

        gymTop.setBackground(gymBox);

        gym.addView(
                gymTop,
                new LinearLayout.LayoutParams(
                        dp(64),
                        dp(34)
                )
        );

        // Gym pillars
        LinearLayout gymPillars = new LinearLayout(this);
        gymPillars.setOrientation(LinearLayout.HORIZONTAL);
        gymPillars.setGravity(Gravity.CENTER);

        TextView pillar1 = new TextView(this);
        TextView pillar2 = new TextView(this);

        pillar1.setBackgroundColor(BLUE);
        pillar2.setBackgroundColor(BLUE);

        gymPillars.addView(
                pillar1,
                new LinearLayout.LayoutParams(dp(5), dp(55))
        );

        LinearLayout.LayoutParams pillarGap =
                new LinearLayout.LayoutParams(dp(5), dp(55));

        pillarGap.leftMargin = dp(38);

        gymPillars.addView(pillar2, pillarGap);

        gym.addView(gymPillars);

        // Dumbbell
        ImageView dumbbell = new ImageView(this);
        dumbbell.setImageResource(R.drawable.ic_dumbbell);
        dumbbell.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        dumbbell.setAlpha(0f);
        dumbbell.setScaleX(0.8f);
        dumbbell.setScaleY(0.8f);

        LinearLayout.LayoutParams dumbbellParams =
                new LinearLayout.LayoutParams(dp(90), dp(90));

        dumbbellParams.leftMargin = dp(4);
        dumbbellParams.rightMargin = dp(4);

        scene.addView(dumbbell, dumbbellParams);

        // Running athlete
        TextView runner = new TextView(this);
        runner.setText("🏃");
        runner.setTextSize(48);
        runner.setGravity(Gravity.CENTER);
        runner.setAlpha(0f);
        runner.setTranslationX(dp(-30));

        LinearLayout.LayoutParams runnerParams =
                new LinearLayout.LayoutParams(dp(85), dp(90));

        runnerParams.leftMargin = dp(4);

        scene.addView(runner, runnerParams);

        // ============================================================
        // MOTIVATIONAL TEXT
        // ============================================================

        TextView message = new TextView(this);
        message.setText("TRAIN • MOVE • GROW");
        message.setTextSize(13);
        message.setTextColor(Color.LTGRAY);
        message.setGravity(Gravity.CENTER);
        message.setLetterSpacing(0.18f);
        message.setAlpha(0f);

        LinearLayout.LayoutParams messageParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        messageParams.topMargin = dp(5);

        content.addView(message, messageParams);

        TextView status = new TextView(this);
        status.setText("Preparing your fitness experience...");
        status.setTextSize(11);
        status.setTextColor(Color.GRAY);
        status.setGravity(Gravity.CENTER);
        status.setAlpha(0f);

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        statusParams.topMargin = dp(10);

        content.addView(status, statusParams);

        // ============================================================
        // PROGRESS BAR
        // ============================================================

        android.widget.ProgressBar progress =
                new android.widget.ProgressBar(
                        this,
                        null,
                        android.R.attr.progressBarStyleHorizontal
                );

        progress.setMax(100);
        progress.setProgress(0);
        progress.setAlpha(0f);

        LinearLayout.LayoutParams progressParams =
                new LinearLayout.LayoutParams(
                        dp(230),
                        dp(5)
                );

        progressParams.topMargin = dp(16);

        content.addView(progress, progressParams);

        // ============================================================
        // PERCENTAGE
        // ============================================================

        TextView percentage = new TextView(this);
        percentage.setText("0%");
        percentage.setTextSize(11);
        percentage.setTextColor(Color.GRAY);
        percentage.setGravity(Gravity.CENTER);
        percentage.setAlpha(0f);

        LinearLayout.LayoutParams percentageParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        percentageParams.topMargin = dp(5);

        content.addView(percentage, percentageParams);

        // ============================================================
        // SET SPLASH SCREEN
        // ============================================================

        setContentView(splash);

        // ============================================================
        // BRAND / LOGO ANIMATION
        // ============================================================

        brandTop.animate()
                .alpha(1f)
                .setDuration(500)
                .start();

        logo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(250)
                .setDuration(850)
                .setInterpolator(
                        new android.view.animation.OvershootInterpolator()
                )
                .start();

        title.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(800)
                .setDuration(650)
                .setInterpolator(
                        new android.view.animation.DecelerateInterpolator()
                )
                .start();

        // ============================================================
        // FITNESS SCENE ANIMATION
        // ============================================================

        scene.animate()
                .alpha(1f)
                .setStartDelay(1800)
                .setDuration(700)
                .start();

        dumbbell.animate()
                .alpha(1f)
                .setStartDelay(2300)
                .setDuration(450)
                .start();

        android.animation.ObjectAnimator dumbbellBounce =
                android.animation.ObjectAnimator.ofFloat(
                        dumbbell,
                        "translationY",
                        dp(8),
                        dp(-8)
                );

        dumbbellBounce.setDuration(650);
        dumbbellBounce.setRepeatMode(
                android.animation.ValueAnimator.REVERSE
        );
        dumbbellBounce.setRepeatCount(
                android.animation.ValueAnimator.INFINITE
        );
        dumbbellBounce.setStartDelay(2800);
        dumbbellBounce.start();

        runner.animate()
                .alpha(1f)
                .translationX(0f)
                .setStartDelay(6500)
                .setDuration(900)
                .start();

        android.animation.ObjectAnimator runnerMove =
                android.animation.ObjectAnimator.ofFloat(
                        runner,
                        "translationX",
                        dp(-8),
                        dp(8)
                );

        runnerMove.setDuration(500);
        runnerMove.setRepeatMode(
                android.animation.ValueAnimator.REVERSE
        );
        runnerMove.setRepeatCount(8);
        runnerMove.setStartDelay(7400);
        runnerMove.start();

        // ============================================================
        // TEXT ANIMATION
        // ============================================================

        message.animate()
                .alpha(1f)
                .setStartDelay(5000)
                .setDuration(700)
                .start();

        status.animate()
                .alpha(1f)
                .setStartDelay(8000)
                .setDuration(600)
                .start();

        progress.animate()
                .alpha(1f)
                .setStartDelay(1800)
                .setDuration(500)
                .start();

        percentage.animate()
                .alpha(1f)
                .setStartDelay(1800)
                .setDuration(500)
                .start();

        // ============================================================
        // 20-SECOND PROGRESS ANIMATION
        // ============================================================

        android.animation.ValueAnimator progressAnimator =
                android.animation.ValueAnimator.ofInt(0, 100);

        progressAnimator.setDuration(19000);
        progressAnimator.setStartDelay(1000);

        progressAnimator.addUpdateListener(animation -> {

            int value = (Integer) animation.getAnimatedValue();

            progress.setProgress(value);
            percentage.setText(value + "%");

            if (value < 25) {
                status.setText("Preparing your fitness experience...");
            } else if (value < 50) {
                status.setText("Building your training environment...");
            } else if (value < 75) {
                status.setText("Getting you ready to move...");
            } else if (value < 95) {
                status.setText("Almost ready...");
            } else {
                status.setText("Welcome to FitPals!");
            }
        });

        progressAnimator.start();

        // ============================================================
        // FINISH SPLASH AFTER APPROXIMATELY 20 SECONDS
        // ============================================================

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            splash.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction(this::createWebView)
                    .start();

        }, 20000);
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
