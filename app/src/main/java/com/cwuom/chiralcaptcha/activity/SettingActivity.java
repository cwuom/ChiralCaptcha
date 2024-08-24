package com.cwuom.chiralcaptcha.activity;

import static com.cwuom.chiralcaptcha.util.Utils.snackbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.cwuom.chiralcaptcha.BuildConfig;
import com.cwuom.chiralcaptcha.R;
import com.cwuom.chiralcaptcha.statics.Constants;
import com.cwuom.chiralcaptcha.util.AppConfig;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference version = findPreference("version");
            Preference telegram = findPreference("telegram");
            Preference github = findPreference("github");
            Preference qq = findPreference("qq");
            Preference clear_sequential_index = findPreference("clear_sequential_index");
            Preference mol_pool_index = findPreference("mol_pool_index");
            Preference passed_count = findPreference("passed_count");
            Preference not_passed_count = findPreference("not_passed_count");
            Preference average_duration = findPreference("average_duration");
            Preference clear_stats = findPreference("clear_stats");
            Objects.requireNonNull(version).setSummary(BuildConfig.VERSION_NAME);

            assert github != null;
            github.setOnPreferenceClickListener(preference -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GITHUB_LINK));
                startActivity(browserIntent);
                return false;
            });


            assert telegram != null;
            telegram.setOnPreferenceClickListener(preference -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TELEGRAM_LINK));
                startActivity(browserIntent);
                return false;
            });

            assert qq != null;
            qq.setOnPreferenceClickListener(preference -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.QQ_LINK));
                startActivity(browserIntent);
                return false;
            });

            assert clear_sequential_index != null;
            clear_sequential_index.setOnPreferenceClickListener(preference -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("确认清空顺序作答记录吗")
                        .setMessage("此操作不可逆。")
                        .setNeutralButton("手滑了..", null)
                        .setPositiveButton("确认清空", (dialog1, which) -> AppConfig.clearFileIndex(requireContext()))
                        .show();
                return false;
            });

            assert mol_pool_index != null;
            mol_pool_index.setOnPreferenceChangeListener((preference, newValue) -> {
                AppConfig.clearFileIndex(requireContext());
                return true;
            });

            assert passed_count != null;
            passed_count.setSummary(String.valueOf(AppConfig.getPassedCount(requireContext())));
            assert not_passed_count != null;
            not_passed_count.setSummary(String.valueOf(AppConfig.getNotPassedCount(requireContext())));
            assert average_duration != null;
            average_duration.setSummary(AppConfig.getFormattedAverageDuration(requireContext()));

            assert clear_stats != null;
            clear_stats.setOnPreferenceClickListener(preference -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("确认清空统计数据吗")
                        .setMessage("此操作不可逆。")
                        .setNeutralButton("手滑了..", null)
                        .setPositiveButton("确认清空", (dialog1, which) -> {
                            AppConfig.clearStatistics(requireContext());
                            passed_count.setSummary("0");
                            not_passed_count.setSummary("0");
                            average_duration.setSummary("00:00:00");
                        })
                        .show();
                return false;
            });
        }
    }
}