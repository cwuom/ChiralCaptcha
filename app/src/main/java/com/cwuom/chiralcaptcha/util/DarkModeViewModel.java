package com.cwuom.chiralcaptcha.util;

import android.annotation.SuppressLint;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DarkModeViewModel extends ViewModel {

    private final MutableLiveData<Boolean> darkModeState = new MutableLiveData<>();
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private ComponentCallbacks2 componentCallback;

    public LiveData<Boolean> getDarkModeState() {
        return darkModeState;
    }

    public void observeDarkModeChanges(Context context) {
        this.context = context.getApplicationContext();
        componentCallback = new ComponentCallbacks2() {
            @Override
            public void onConfigurationChanged(@NonNull Configuration newConfig) {
                updateDarkModeState(newConfig);
            }

            @Override
            public void onLowMemory() {
                // no need to handle low memory here
            }

            @Override
            public void onTrimMemory(int level) {
                // no need to handle trim memory here
            }
        };

        // Register the callback to listen for configuration changes
        this.context.registerComponentCallbacks(componentCallback);
        // Update the initial state
        updateDarkModeState(this.context.getResources().getConfiguration());
    }

    private void updateDarkModeState(Configuration config) {
        boolean isDarkMode = (config.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        darkModeState.postValue(isDarkMode);
    }

    public void unobserveDarkModeChanges() {
        if (context != null && componentCallback != null) {
            context.unregisterComponentCallbacks(componentCallback);
        }
    }
}