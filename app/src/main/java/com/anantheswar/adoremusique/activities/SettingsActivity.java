/*
 This is the Settings activity, Opens when you click on settings and also calls SettingsFragment.java that cointains the
 xml.preferences.xml layout
 */

package com.anantheswar.adoremusique.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.anantheswar.adoremusique.R;
import com.anantheswar.adoremusique.fragments.SettingsFragment;
import com.anantheswar.adoremusique.subfragments.StyleSelectorFragment;
import com.anantheswar.adoremusique.utils.Constants;
import com.anantheswar.adoremusique.utils.PreferencesUtility;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Objects;

public class SettingsActivity extends BaseThemedActivity implements ColorChooserDialog.ColorCallback, ATEActivityThemeCustomizer {

    String action;
    public static FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(!PreferencesUtility.fullScreen()){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else
        {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (PreferencesUtility.getInstance(this).getTheme().equals("dark"))
            setTheme(R.style.AppThemeNormalDark);
        else if (PreferencesUtility.getInstance(this).getTheme().equals("black"))
            setTheme(R.style.AppThemeNormalBlack);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        action = getIntent().getAction();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FrameLayout frameLayout = findViewById(R.id.settings_layout);
        if(Objects.equals(getATEKey(), "light_theme")){
            frameLayout.setBackgroundResource(R.drawable.gradient_settings);
        } else {
            frameLayout.setBackgroundResource(R.color.colorPrimaryDarkBlack);
        }



        if (action.equals(Constants.SETTINGS_STYLE_SELECTOR)) {
            getSupportActionBar().setTitle(R.string.now_playing_style);
            String what = getIntent().getExtras().getString(Constants.SETTINGS_STYLE_SELECTOR_WHAT);
            Fragment fragment = StyleSelectorFragment.newInstance(what);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
            Bundle params = new Bundle();
            mFirebaseAnalytics.logEvent("Player_Styles_Fragment", params);

        } else {
            getSupportActionBar().setTitle(R.string.settings);
            PreferenceFragment fragment = new SettingsFragment();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @StyleRes
    @Override
    public int getActivityTheme() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false) ?
                R.style.AppThemeDark : R.style.AppThemeLight;
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        final Config config = ATE.config(this, getATEKey());
        switch (dialog.getTitle()) {
            case R.string.primary_color:
                config.primaryColor(selectedColor);
                break;
            case R.string.accent_color:
                config.accentColor(selectedColor);
                break;
        }
        config.commit();
        recreate(); // recreation needed to reach the checkboxes in the preferences layout
    }

    @Override
    public void onBackPressed() {
    finish();
    }

}
