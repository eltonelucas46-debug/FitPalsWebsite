package com.fitpals.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FitPalsMessagingService
        extends FirebaseMessagingService {

    private static final String TAG = "FitPalsFCM";

    private static final String CHANNEL_ID =
            "fitpals_notifications";

    private static final String CHANNEL_NAME =
            "FitPals Notifications";

    @Override
    public void onCreate() {

        super.onCreate();

        createNotificationChannel();
    }

    @Override
    public void onMessageReceived(
            RemoteMessage remoteMessage
    ) {

        Log.d(
                TAG,
                "Message received from: " +
                        remoteMessage.getFrom()
        );

        String title = "FitPals";

        String body = "You have a new FitPals notification.";

        if (remoteMessage.getNotification() != null) {

            if (remoteMessage.getNotification().getTitle() != null) {

                title =
                        remoteMessage
                                .getNotification()
                                .getTitle();
            }

            if (remoteMessage.getNotification().getBody() != null) {

                body =
                        remoteMessage
                                .getNotification()
                                .getBody();
            }
        }

        if (remoteMessage.getData().containsKey("title")) {

            title =
                    remoteMessage
                            .getData()
                            .get("title");
        }

        if (remoteMessage.getData().containsKey("body")) {

            body =
                    remoteMessage
                            .getData()
                            .get("body");
        }

        showNotification(title, body);
    }

    @Override
    public void onNewToken(String token) {

        super.onNewToken(token);

        Log.d(
                TAG,
                "New FCM token: " + token
        );

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
    }

    private void showNotification(
            String title,
            String body
    ) {

        Intent intent =
                new Intent(
                        this,
                        MainActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        this,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                R.drawable.ic_notification
                        )
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(body)
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setAutoCancel(true)
                        .setContentIntent(
                                pendingIntent
                        );

        NotificationManager manager =
                (NotificationManager)
                        getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (manager != null) {

            manager.notify(
                    (int) System.currentTimeMillis(),
                    builder.build()
            );
        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager
                                    .IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "FitPals messages, reminders and updates"
            );

            NotificationManager manager =
                    getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {

                manager.createNotificationChannel(
                        channel
                );
            }
        }
    }
}
