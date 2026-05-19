package com.example.receiverdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID   = "receiver_demo_channel";
    private static final String CHANNEL_NAME = "ReceiverDemo Alerts";
    private static final int    NOTIF_ID     = 1001;

    public static void sendBootNotification(Context context) {
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(ch);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("ReceiverDemo")
                .setContentText("Système démarré — BootReceiver opérationnel.")
                .setAutoCancel(true);

        nm.notify(NOTIF_ID, builder.build());
    }
}