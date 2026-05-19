package com.example.receiverdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class AirplaneModeReceiver extends BroadcastReceiver {

    private static final String TAG = "AirplaneModeReceiver";
    private static StatusCallback callback;

    public interface StatusCallback {
        void onAirplaneModeChanged(boolean enabled);
    }

    public static void setCallback(StatusCallback cb) {
        callback = cb;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) return;

        boolean airplaneEnabled = intent.getBooleanExtra("state", false);
        Log.d(TAG, "Mode avion changé : " + airplaneEnabled);

        new Handler(Looper.getMainLooper()).post(() -> {
            if (callback != null) {
                callback.onAirplaneModeChanged(airplaneEnabled);
            }
        });
    }
}