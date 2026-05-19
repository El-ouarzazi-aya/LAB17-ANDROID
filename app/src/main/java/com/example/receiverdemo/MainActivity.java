package com.example.receiverdemo;

import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements AirplaneModeReceiver.StatusCallback,
        CustomEventReceiver.EventCallback {

    // Receivers
    private AirplaneModeReceiver airplaneReceiver;
    private CustomEventReceiver  customReceiver;
    private boolean airplaneRegistered = false;
    private boolean customRegistered   = false;

    // UI
    private TextView   tvSystemTitle;
    private CardView   cardAirplane, cardCustom;
    private TextView   tvAirplaneStatus, tvCustomStatus;
    private LinearLayout logContainer;
    private ScrollView   logScroll;

    private final List<String> logEntries = new ArrayList<>();
    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUI();

        airplaneReceiver = new AirplaneModeReceiver();
        customReceiver   = new CustomEventReceiver();

        AirplaneModeReceiver.setCallback(this);
        CustomEventReceiver.setEventCallback(this);

        addLog("SYSTEM", "Application initialisée");
    }

    // ─── Interface AirplaneModeReceiver.StatusCallback ──────────────────────
    @Override
    public void onAirplaneModeChanged(boolean enabled) {
        runOnUiThread(() -> {
            String state = enabled ? "ACTIVÉ" : "DÉSACTIVÉ";
            tvAirplaneStatus.setText("Mode avion : " + state);
            cardAirplane.setCardBackgroundColor(
                    enabled ? Color.parseColor("#1a0a00") : Color.parseColor("#001a0a"));
            addLog("AIRPLANE", "Mode avion → " + state);
        });
    }

    // ─── Interface CustomEventReceiver.EventCallback ─────────────────────────
    @Override
    public void onCustomEventReceived(String sender, String payload, long timestamp) {
        runOnUiThread(() -> {
            tvCustomStatus.setText("Dernier event : " + payload);
            addLog("CUSTOM", sender + " → \"" + payload + "\"");
        });
    }

    // ─── Actions boutons ──────────────────────────────────────────────────────
    private void toggleAirplaneReceiver() {
        if (!airplaneRegistered) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            registerReceiver(airplaneReceiver, filter);
            airplaneRegistered = true;
            addLog("REG", "AirplaneModeReceiver enregistré");
            Toast.makeText(this, "Receiver avion actif", Toast.LENGTH_SHORT).show();
        } else {
            unregisterReceiver(airplaneReceiver);
            airplaneRegistered = false;
            tvAirplaneStatus.setText("Mode avion : en attente…");
            cardAirplane.setCardBackgroundColor(Color.parseColor("#0d0d0d"));
            addLog("UNREG", "AirplaneModeReceiver retiré");
            Toast.makeText(this, "Receiver avion arrêté", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleCustomReceiver() {
        if (!customRegistered) {
            IntentFilter filter = new IntentFilter(CustomEventReceiver.ACTION_CUSTOM);
            registerReceiver(customReceiver, filter);
            customRegistered = true;
            addLog("REG", "CustomEventReceiver enregistré");
        } else {
            unregisterReceiver(customReceiver);
            customRegistered = false;
            tvCustomStatus.setText("Aucun événement");
            addLog("UNREG", "CustomEventReceiver retiré");
        }
    }

    private void fireCustomBroadcast() {
        if (!customRegistered) {
            Toast.makeText(this, "Activez d'abord le receiver custom", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(CustomEventReceiver.ACTION_CUSTOM);
        intent.putExtra("sender",    "MainActivity");
        intent.putExtra("payload",   "Ping #" + (logEntries.size() + 1));
        intent.putExtra("timestamp", System.currentTimeMillis());
        sendBroadcast(intent);
        addLog("SEND", "Custom broadcast émis");
    }

    // ─── Log ──────────────────────────────────────────────────────────────────
    private void addLog(String tag, String message) {
        String entry = "[" + SDF.format(new Date()) + "] [" + tag + "] " + message;
        logEntries.add(0, entry);

        TextView tv = new TextView(this);
        tv.setText(entry);
        tv.setTextColor(tagColor(tag));
        tv.setTextSize(11f);
        tv.setTypeface(android.graphics.Typeface.MONOSPACE);
        tv.setPadding(0, 2, 0, 2);

        logContainer.addView(tv, 0);
        logScroll.post(() -> logScroll.scrollTo(0, 0));
    }

    private int tagColor(String tag) {
        switch (tag) {
            case "AIRPLANE": return Color.parseColor("#00e5ff");
            case "CUSTOM":   return Color.parseColor("#69ff47");
            case "SEND":     return Color.parseColor("#ffde00");
            case "SYSTEM":   return Color.parseColor("#bb86fc");
            case "REG":      return Color.parseColor("#00e5ff");
            case "UNREG":    return Color.parseColor("#ff6b6b");
            default:         return Color.LTGRAY;
        }
    }

    // ─── Build UI programmatique ──────────────────────────────────────────────
    private void buildUI() {
        ScrollView root = new ScrollView(this);
        root.setBackgroundColor(Color.parseColor("#050510"));

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(32, 48, 32, 48);

        // ── Titre
        tvSystemTitle = new TextView(this);
        tvSystemTitle.setText("⬡  RECEIVER OS  ⬡");
        tvSystemTitle.setTextSize(26f);
        tvSystemTitle.setTextColor(Color.parseColor("#00e5ff"));
        tvSystemTitle.setTypeface(android.graphics.Typeface.MONOSPACE,
                android.graphics.Typeface.BOLD);
        tvSystemTitle.setGravity(android.view.Gravity.CENTER);
        tvSystemTitle.setPadding(0, 0, 0, 8);
        page.addView(tvSystemTitle);

        TextView tvSub = new TextView(this);
        tvSub.setText("android broadcast monitor v2.0");
        tvSub.setTextSize(11f);
        tvSub.setTextColor(Color.parseColor("#445566"));
        tvSub.setTypeface(android.graphics.Typeface.MONOSPACE);
        tvSub.setGravity(android.view.Gravity.CENTER);
        tvSub.setPadding(0, 0, 0, 40);
        page.addView(tvSub);

        // ── Card Airplane
        cardAirplane = makeCard("#0d0d0d", "#00e5ff");
        LinearLayout colAir = new LinearLayout(this);
        colAir.setOrientation(LinearLayout.VERTICAL);
        colAir.setPadding(28, 28, 28, 28);

        TextView titleAir = sectionTitle("✈  AIRPLANE RECEIVER", "#00e5ff");
        tvAirplaneStatus = statusLine("Mode avion : en attente…", "#445566");

        android.widget.Button btnAir = neonButton("[ ENREGISTRER ]", "#00e5ff");
        btnAir.setOnClickListener(v -> toggleAirplaneReceiver());

        colAir.addView(titleAir);
        colAir.addView(tvAirplaneStatus);
        colAir.addView(gap(12));
        colAir.addView(btnAir);
        cardAirplane.addView(colAir);
        page.addView(cardAirplane);
        page.addView(gap(20));

        // ── Card Custom
        cardCustom = makeCard("#0d0d0d", "#69ff47");
        LinearLayout colCust = new LinearLayout(this);
        colCust.setOrientation(LinearLayout.VERTICAL);
        colCust.setPadding(28, 28, 28, 28);

        TextView titleCust = sectionTitle("⚡  CUSTOM RECEIVER", "#69ff47");
        tvCustomStatus = statusLine("Aucun événement", "#445566");

        LinearLayout rowBtns = new LinearLayout(this);
        rowBtns.setOrientation(LinearLayout.HORIZONTAL);

        android.widget.Button btnTogCust = neonButton("[ ON/OFF ]", "#69ff47");
        btnTogCust.setOnClickListener(v -> toggleCustomReceiver());

        android.widget.Button btnFire = neonButton("[ ÉMETTRE ]", "#ffde00");
        btnFire.setOnClickListener(v -> fireCustomBroadcast());

        LinearLayout.LayoutParams half = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        half.setMargins(0, 0, 8, 0);
        btnTogCust.setLayoutParams(half);

        LinearLayout.LayoutParams half2 = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnFire.setLayoutParams(half2);

        rowBtns.addView(btnTogCust);
        rowBtns.addView(btnFire);

        colCust.addView(titleCust);
        colCust.addView(tvCustomStatus);
        colCust.addView(gap(12));
        colCust.addView(rowBtns);
        cardCustom.addView(colCust);
        page.addView(cardCustom);
        page.addView(gap(20));

        // ── Log terminal
        CardView cardLog = makeCard("#020208", "#bb86fc");
        LinearLayout colLog = new LinearLayout(this);
        colLog.setOrientation(LinearLayout.VERTICAL);
        colLog.setPadding(28, 28, 28, 28);

        TextView titleLog = sectionTitle("▸  SYSTEM LOG", "#bb86fc");
        colLog.addView(titleLog);
        colLog.addView(gap(8));

        logScroll = new ScrollView(this);
        logScroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 420));
        logScroll.setBackgroundColor(Color.parseColor("#010106"));

        logContainer = new LinearLayout(this);
        logContainer.setOrientation(LinearLayout.VERTICAL);
        logContainer.setPadding(16, 16, 16, 16);

        logScroll.addView(logContainer);
        colLog.addView(logScroll);
        cardLog.addView(colLog);
        page.addView(cardLog);

        root.addView(page);
        setContentView(root);
    }

    // ─── Helpers UI ───────────────────────────────────────────────────────────
    private CardView makeCard(String bg, String border) {
        CardView cv = new CardView(this);
        cv.setRadius(4f);
        cv.setCardElevation(12f);
        cv.setCardBackgroundColor(Color.parseColor(bg));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cv.setLayoutParams(lp);
        return cv;
    }

    private TextView sectionTitle(String text, String color) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(14f);
        tv.setTextColor(Color.parseColor(color));
        tv.setTypeface(android.graphics.Typeface.MONOSPACE,
                android.graphics.Typeface.BOLD);
        tv.setPadding(0, 0, 0, 12);
        return tv;
    }

    private TextView statusLine(String text, String color) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(13f);
        tv.setTextColor(Color.parseColor(color));
        tv.setTypeface(android.graphics.Typeface.MONOSPACE);
        return tv;
    }

    private android.widget.Button neonButton(String label, String color) {
        android.widget.Button btn = new android.widget.Button(this);
        btn.setText(label);
        btn.setTextColor(Color.parseColor(color));
        btn.setTypeface(android.graphics.Typeface.MONOSPACE,
                android.graphics.Typeface.BOLD);
        btn.setTextSize(12f);
        btn.setBackgroundColor(Color.parseColor("#0a0a1a"));
        btn.setPadding(16, 10, 16, 10);
        return btn;
    }

    private View gap(int dp) {
        View v = new View(this);
        float px = dp * getResources().getDisplayMetrics().density;
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) px));
        return v;
    }

    // ─── Cycle de vie ─────────────────────────────────────────────────────────
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (airplaneRegistered) unregisterReceiver(airplaneReceiver);
        if (customRegistered)   unregisterReceiver(customReceiver);
        AirplaneModeReceiver.setCallback(null);
        CustomEventReceiver.setEventCallback(null);
    }
}