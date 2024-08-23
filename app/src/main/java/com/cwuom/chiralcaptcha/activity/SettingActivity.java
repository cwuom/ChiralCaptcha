package com.cwuom.chiralcaptcha.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.cwuom.chiralcaptcha.BuildConfig;
import com.cwuom.chiralcaptcha.R;
import com.cwuom.chiralcaptcha.statics.Constants;
import com.google.android.material.color.DynamicColors;

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
        }
    }
}