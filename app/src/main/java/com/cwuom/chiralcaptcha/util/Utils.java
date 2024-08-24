package com.cwuom.chiralcaptcha.util;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.VibratorManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Room;

import com.cwuom.chiralcaptcha.InitDataBase.InitHistoryDataBase;
import com.cwuom.chiralcaptcha.R;
import com.cwuom.chiralcaptcha.custom.CSnackbar;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    private static Handler sHandler;

    @SuppressLint("LambdaLast")
    public static void postDelayed(@NonNull Runnable r, long ms) {
        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper());
        }
        sHandler.postDelayed(r, ms);
    }

    public static void post(@NonNull Runnable r) {
        postDelayed(r, 0L);
    }

    public static void runOnUiThread(@NonNull Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            post(r);
        }
    }

    public static InitHistoryDataBase baseRoomDatabase;
    public static InitHistoryDataBase getInstance(Context context) {
        if (baseRoomDatabase == null) {
            baseRoomDatabase = Room.databaseBuilder(context, InitHistoryDataBase.class,
                    "history_database.db").allowMainThreadQueries().build();
        }
        return baseRoomDatabase;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void performVibrateClick(Context context) {
        VibratorManager vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        vibratorManager.getDefaultVibrator().vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void performVibrateHeavyClick(Context context) {
        VibratorManager vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        vibratorManager.getDefaultVibrator().vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK));
    }

    public static String parseCTime(long milliseconds) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = new Date(milliseconds);
        return sdf.format(date);
    }

    public static void snackbar(Context ctx, String text){
        runOnUiThread(() -> {
            CSnackbar CSnackbar = new CSnackbar(ctx);
            CSnackbar.show(text, Toast.LENGTH_SHORT);
        });

    }

}
