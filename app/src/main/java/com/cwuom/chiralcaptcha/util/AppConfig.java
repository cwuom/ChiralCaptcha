package com.cwuom.chiralcaptcha.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.preference.PreferenceManager;

public class AppConfig {
    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean getLimitQuantity(Context context) {
        return getPrefs(context).getBoolean("limit_quantity", false);
    }

    public static boolean getDisableScoreCheck(Context context) {
        return getPrefs(context).getBoolean("disable_score_check", false);
    }

    public static boolean getHideQuantity(Context context) {
        return getPrefs(context).getBoolean("hide_quantity", false);
    }

    public static boolean getDynamicAnswerTime(Context context) {
        return getPrefs(context).getBoolean("dynamic_answer_time", true);
    }

    public static int getMinQuantity(Context context) {
        return Integer.parseInt(getPrefs(context).getString("min_quantity", "1"));
    }

    public static int getMaxQuantity(Context context) {
        return Integer.parseInt(getPrefs(context).getString("max_quantity", "15"));
    }

    public static int getAnswerTimeBase(Context context) {
        return Integer.parseInt(getPrefs(context).getString("answer_time_base", "600"));
    }

    public static int getPreloadCount(Context context) {
        return Integer.parseInt(getPrefs(context).getString("preload_count", "8"));
    }

    public static int getMaxStorageCount(Context context) {
        return Integer.parseInt(getPrefs(context).getString("max_storage_count", "250"));
    }

    public static int getMaxViewCount(Context context) {
        return Integer.parseInt(getPrefs(context).getString("max_view_count", "200"));
    }

    public static int getMolPoolIndex(Context context) {
        return Integer.parseInt(getPrefs(context).getString("mol_pool_index", "1"));
    }


    public static boolean isDarkTheme(Context context) {
        int flag = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return flag == Configuration.UI_MODE_NIGHT_YES;
    }
}
