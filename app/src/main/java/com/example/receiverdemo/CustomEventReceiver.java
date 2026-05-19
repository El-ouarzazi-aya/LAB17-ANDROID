package com.example.receiverdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CustomEventReceiver extends BroadcastReceiver {

    public static final String ACTION_CUSTOM = "com.example.receiverdemo.CUSTOM_EVENT";
    private static final String TAG = "CustomEventReceiver";
    private static EventCallback eventCallback;

    public interface EventCallback {
        void onCustomEventReceived(String sender, String payload, long timestamp);
    }

    public static void setEventCallback(EventCallback cb) {
        eventCallback = cb;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !ACTION_CUSTOM.equals(intent.getAction())) return;

        String sender  = intent.getStringExtra("sender");
        String payload = intent.getStringExtra("payload");
        long   ts      = intent.getLongExtra("timestamp", System.currentTimeMillis());

        Log.d(TAG, "Événement reçu de " + sender + " : " + payload);

        if (eventCallback != null) {
            eventCallback.onCustomEventReceived(sender, payload, ts);
        }
    }
}