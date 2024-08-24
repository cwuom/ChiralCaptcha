package com.cwuom.chiralcaptcha.util;

import static com.cwuom.chiralcaptcha.util.Utils.snackbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.preference.PreferenceManager;

import com.cwuom.chiralcaptcha.statics.Constants;

public class AppConfig {
    private static final String TAG = "ChiralCaptcha-AppConfig";

    private AppConfig() {
        throw new IllegalStateException("no instances for you!");
    }

    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isLimitQuantity(Context context) {
        try {
            return getPrefs(context).getBoolean("limit_quantity", false);
        } catch (NumberFormatException e) {
            Logger.e(TAG, "Error getting limit_quantity", e);
            return false;
        }
    }

    public static boolean isDisableScoreCheck(Context context) {
        try {
            return getPrefs(context).getBoolean("disable_score_check", false);
        } catch (NumberFormatException e) {
            Logger.e(TAG, "Error getting disable_score_check", e);
            return false;
        }
    }

    public static boolean isHideQuantity(Context context) {
        try {
            return getPrefs(context).getBoolean("hide_quantity", false);
        } catch (NumberFormatException e) {
            Logger.e(TAG, "Error getting hide_quantity", e);
            return false;
        }
    }

    public static boolean isDynamicAnswerTime(Context context) {
        try {
            return getPrefs(context).getBoolean("dynamic_answer_time", true);
        } catch (NumberFormatException e) {
            Logger.e(TAG, "Error getting dynamic_answer_time", e);
            return true;
        }
    }

    public static boolean isSequentialLoadMode(Context context) {
        try {
            return getPrefs(context).getBoolean("sequential_load_mode", false);
        } catch (NumberFormatException e) {
            Logger.e(TAG, "Error getting isSequentialLoadMode", e);
            return false;
        }
    }

    public static int getMinQuantity(Context context) {
        try {
            return Integer.parseInt(getPrefs(context).getString("min_quantity", "1"));
        } catch (NumberFormatException e) {
            snackbar(context, "不合法的输入，请重新设置「最小生成数量」");
            Logger.e(TAG, "Error getting min_quantity", e);
            return 1;
        }
    }

    public static int getMaxQuantity(Context context) {
        try {
            return Integer.parseInt(getPrefs(context).getString("max_quantity", "15"));
        } catch (NumberFormatException e) {
            snackbar(context, "不合法的输入，请重新设置「最大生成数量」");
            Logger.e(TAG, "Error getting max_quantity", e);
            return 15;
        }
    }

    public static int getAnswerTimeBase(Context context) {
        try {
            return Integer.parseInt(getPrefs(context).getString("answer_time_base", "600"));
        } catch (NumberFormatException e) {
            snackbar(context, "不合法的输入，请重新设置「基础作答时间」");
            Logger.e(TAG, "Error getting answer_time_base", e);
            return 600;
        }
    }

    public static int getPreloadCount(Context context) {
        try {
            return Integer.parseInt(getPrefs(context).getString("preload_count", "8"));
        } catch (NumberFormatException e) {
            snackbar(context, "不合法的输入，请重新设置「预加载数」");
            Logger.e(TAG, "Error getting preload_count", e);
            return 8;
        }
    }

    public static int getMaxStorageCount(Context context) {
        try {
            return Integer.parseInt(getPrefs(context).getString("max_storage_count", "250"));
        } catch (NumberFormatException e) {
            snackbar(context, "不合法的输入，请重新设置「最大存储数」");
            Logger.e(TAG, "Error getting max_storage_count", e);
            return 250;
        }
    }

    public static int getMaxViewCount(Context context) {
        try {
            return Integer.parseInt(getPrefs(context).getString("max_view_count", "200"));
        } catch (NumberFormatException e) {
            snackbar(context, "不合法的输入，请重新设置「最大查看数」");
            Logger.e(TAG, "Error getting max_view_count", e);
            return 200;
        }
    }

    public static int getMolPoolIndex(Context context) {
        try {
            return Integer.parseInt(getPrefs(context).getString("mol_pool_index", "1"));
        } catch (NumberFormatException e) {
            Logger.e(TAG, "Error getting mol_pool_index", e);
            return 1;
        }
    }

    public static boolean isDarkTheme(Context context) {
        int flag = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return flag == Configuration.UI_MODE_NIGHT_YES;
    }

    public static int getFileIndex(Context context) {
        return getPrefs(context).getInt("file_index", 0);
    }

    public static void addFileIndex(Context context, int poolIndex) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        int maxFileIndex = Constants.MAX_MOLECULES[poolIndex - 1];
        int fileIndex = getFileIndex(context);
        if (fileIndex < maxFileIndex) {
            editor.putInt("file_index", fileIndex+1);
        }
        editor.apply();
    }

    public static void subFileIndex(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        int fileIndex = getFileIndex(context);
        if (fileIndex >= 1){
            editor.putInt("file_index", fileIndex-1);
            editor.apply();
        }

    }


    public static void clearFileIndex(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("file_index", 0);
        editor.apply();
    }

    public static void addPassedCount(Context context, long increment) {
        SharedPreferences prefs = getPrefs(context);
        long currentPassedCount = prefs.getLong("passed_count", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("passed_count", currentPassedCount + increment);
        editor.apply();
    }

    public static void addNotPassedCount(Context context, long increment) {
        SharedPreferences prefs = getPrefs(context);
        long currentNotPassedCount = prefs.getLong("not_passed_count", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("not_passed_count", currentNotPassedCount + increment);
        editor.apply();
    }

    public static void addAverageDuration(Context context, long durationMillis) {
        SharedPreferences prefs = getPrefs(context);
        long totalDuration = prefs.getLong("total_duration", 0);
        totalDuration += durationMillis;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("total_duration", totalDuration);
        editor.apply();
    }

    public static long getPassedCount(Context context) {
        SharedPreferences prefs = getPrefs(context);
        return prefs.getLong("passed_count", 0);
    }

    public static long getNotPassedCount(Context context) {
        SharedPreferences prefs = getPrefs(context);
        return prefs.getLong("not_passed_count", 0);
    }

    public static void clearStatistics(Context context) {
        SharedPreferences prefs = getPrefs(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("passed_count", 0);
        editor.putLong("not_passed_count", 0);
        editor.putLong("total_duration", 0);

        editor.apply();
    }


    @SuppressLint("DefaultLocale")
    public static String getFormattedAverageDuration(Context context) {
        SharedPreferences prefs = getPrefs(context);
        long totalDuration = prefs.getLong("total_duration", 0);
        long passedCount = prefs.getLong("passed_count", 0);

        if (passedCount == 0) {
            return "00:00:00";
        }

        long averageDuration = totalDuration / passedCount;
        int hours = (int) (averageDuration / 3600000);
        int minutes = (int) ((averageDuration % 3600000) / 60000);
        int seconds = (int) ((averageDuration % 60000) / 1000);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


}