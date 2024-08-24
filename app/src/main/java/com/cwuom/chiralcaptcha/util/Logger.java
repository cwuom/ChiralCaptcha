package com.cwuom.chiralcaptcha.util;

import androidx.annotation.NonNull;



public class Logger {

    private Logger() {
        throw new IllegalStateException("no instances for you!");
    }

    private static final String TAG = "ChiralCaptcha";

    public static void e(@NonNull String msg) {
        android.util.Log.e(TAG, msg);
    }

    public static void e(@NonNull Throwable e) {
        android.util.Log.e(TAG, e.toString(), e);
    }

    public static void e(@NonNull String tag, @NonNull String msg) {
        android.util.Log.e(TAG+"<"+tag+">", msg);
    }
    public static void e(@NonNull String msg, @NonNull Throwable e) {
        android.util.Log.e(TAG, msg, e);
    }
    public static void e(@NonNull String tag, @NonNull String msg, @NonNull Throwable e) {
        android.util.Log.e(TAG+"<"+tag+">", msg, e);
    }

    public static void w(@NonNull String msg) {
        android.util.Log.w(TAG, msg);
    }
    public static void w(@NonNull String msg, @NonNull Throwable e) {
        android.util.Log.w(TAG, msg, e);
    }
    public static void w(@NonNull Throwable e) {
        android.util.Log.w(TAG, e.toString(), e);
    }


    public static void i(@NonNull String msg) {
        android.util.Log.i(TAG, msg);
    }
    public static void i(@NonNull String tag, @NonNull String msg) {
        android.util.Log.i(TAG+"<"+tag+">", msg);
    }
    public static void i(@NonNull Throwable e) {
        android.util.Log.i(TAG, e.toString(), e);
    }
    public static void i(@NonNull String msg, @NonNull Throwable e) {
        android.util.Log.i(TAG, msg, e);
    }



    public static void d(@NonNull String msg) {
        android.util.Log.d(TAG, msg);
    }
    public static void d(@NonNull Throwable e) {
        android.util.Log.d(TAG, e.toString(), e);
    }
    public static void d(@NonNull String msg, @NonNull Throwable e) {
        android.util.Log.d(TAG, msg, e);
    }

    public static void d(@NonNull String tag, @NonNull String msg) {
        android.util.Log.d(TAG+"<"+tag+">", msg);
    }

    public static void v(@NonNull String msg) {
        android.util.Log.v(TAG, msg);
    }


    @NonNull
    public static String getStackTraceString(@NonNull Throwable th) {
        return android.util.Log.getStackTraceString(th);
    }
}
