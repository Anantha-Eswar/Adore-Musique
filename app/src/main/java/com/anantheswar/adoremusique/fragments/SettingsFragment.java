package com.anantheswar.adoremusique.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.View;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.prefs.ATECheckBoxPreference;
import com.afollestad.appthemeengine.prefs.ATEColorPreference;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.anantheswar.adoremusique.R;
import com.anantheswar.adoremusique.activities.SettingsActivity;
import com.anantheswar.adoremusique.lastfmapi.LastFmClient;
import com.anantheswar.adoremusique.utils.Constants;
import com.anantheswar.adoremusique.utils.NavigationUtils;
import com.anantheswar.adoremusique.utils.PreferencesUtility;
import com.google.firebase.analytics.FirebaseAnalytics;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String NOW_PLAYING_SELECTOR = "now_playing_selector";
    private static final String LOCKSCREEN = "show_albumart_lockscreen";
    private static final String XPOSED = "toggle_xposed_trackselector";
    private static final String SELECT_PHOTO = "select_photo";
    private static final String FULLSCREEN = "fullscreen";


    private static final String KEY_ABOUT = "preference_about";
    private static final String KEY_SOURCE = "preference_source";
    private static final String KEY_THEME = "theme_preference";
    private static final String TOGGLE_ANIMATIONS = "toggle_animations";
    private static final String TOGGLE_SYSTEM_ANIMATIONS = "toggle_system_animations";
    private static final String KEY_START_PAGE = "start_page_preference";

    public Preference nowPlayingSelector, lockscreen, xposed, selectphoto, fullscreen, lastFMlogin ;

    private SwitchPreference toggleAnimations;
    private ListPreference themePreference, startPagePreference;
    private PreferencesUtility mPreferences;
    private String mAteKey;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        mPreferences = PreferencesUtility.getInstance(getActivity());


        lockscreen = findPreference(LOCKSCREEN);
        nowPlayingSelector = findPreference(NOW_PLAYING_SELECTOR);
        selectphoto = findPreference(SELECT_PHOTO);
        fullscreen = findPreference(FULLSCREEN);

        xposed = findPreference(XPOSED);

        //        themePreference = (ListPreference) findPreference(KEY_THEME);
        startPagePreference = (ListPreference) findPreference(KEY_START_PAGE);

        nowPlayingSelector.setIntent(NavigationUtils.getNavigateToStyleSelectorIntent(getActivity(), Constants.SETTINGS_STYLE_SELECTOR_NOWPLAYING));
        selectphoto.setIntent(NavigationUtils.test(getActivity()));
        setPreferenceClickListeners();


    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
    }

    private void setPreferenceClickListeners() {

//        themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
//                return true;
//            }
//        });

        //Settings -> Start Page (Cointains 4 options to select anyone)
        startPagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                switch ((String) newValue) {
                    case "last_opened":
                        mPreferences.setLastOpenedAsStartPagePreference(true);
                        Bundle params = new Bundle();
                        params.putString("Last_Opened", "Last_Opened");
                        SettingsActivity.mFirebaseAnalytics.logEvent("Initial_Page_Dialog", params);
                        break;
                    case "songs":
                        mPreferences.setLastOpenedAsStartPagePreference(false);
                        mPreferences.setStartPageIndex(0);
                        Bundle params_1 = new Bundle();
                        params_1.putString("Songs", "Songs");
                        SettingsActivity.mFirebaseAnalytics.logEvent("Initial_Page_Dialog", params_1);
                        break;
                    case "albums":
                        mPreferences.setLastOpenedAsStartPagePreference(false);
                        mPreferences.setStartPageIndex(1);
                        Bundle params_2 = new Bundle();
                        params_2.putString("Albums", "Albums");
                        SettingsActivity.mFirebaseAnalytics.logEvent("Initial_Page_Dialog", params_2);
                        break;
                    case "artists":
                        mPreferences.setLastOpenedAsStartPagePreference(false);
                        mPreferences.setStartPageIndex(2);
                        Bundle params_3 = new Bundle();
                        params_3.putString("Artists", "Artists");
                        SettingsActivity.mFirebaseAnalytics.logEvent("Initial_Page_Dialog", params_3);
                        break;
                }
                return true;
            }
        });



        lockscreen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Bundle extras = new Bundle();
                extras.putBoolean("lockscreen",(boolean)newValue);
                mPreferences.updateService(extras);
                Bundle params = new Bundle();
                SettingsActivity.mFirebaseAnalytics.logEvent("Album_Art_Checkbox", params);
                return true;
            }
        });

        fullscreen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                PreferencesUtility.setFullScreen((Boolean) newValue);
                getActivity().recreate();
                Bundle params = new Bundle();
                SettingsActivity.mFirebaseAnalytics.logEvent("Fullscreen_Checkbox", params);
                return true;
            }
        });

        /*xposed.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Bundle extras = new Bundle();
                extras.putBoolean("xtrack",(boolean)newValue);
                mPreferences.updateService(extras);
                Bundle params = new Bundle();
                SettingsActivity.mFirebaseAnalytics.logEvent("Xposed_Queue_Checkbox", params);
                return true;
            }
        });*/

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        invalidateSettings();
        ATE.apply(view, mAteKey);
    }

    public void invalidateSettings() {
        mAteKey = ((SettingsActivity) getActivity()).getATEKey();

        //Enable this if you want Primary Color Option
        /*ATEColorPreference primaryColorPref = (ATEColorPreference) findPreference("primary_color");
        primaryColorPref.setColor(Config.primaryColor(getActivity(), mAteKey), Color.BLACK);
        primaryColorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new ColorChooserDialog.Builder((SettingsActivity) getActivity(), R.string.primary_color)
                        .preselect(Config.primaryColor(getActivity(), mAteKey))
                        .show();
                return true;
            }
        });*/

        ATEColorPreference accentColorPref = (ATEColorPreference) findPreference("accent_color");
        accentColorPref.setColor(Config.accentColor(getActivity(), mAteKey), Color.BLACK);
        accentColorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new ColorChooserDialog.Builder((SettingsActivity) getActivity(), R.string.accent_color)
                        .preselect(Config.accentColor(getActivity(), mAteKey))
                        .show();
                Bundle params = new Bundle();
                SettingsActivity.mFirebaseAnalytics.logEvent("Accent_Color_Dialog", params);
                return true;
            }
        });


        findPreference("dark_theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Marks both theme configs as changed so MainActivity restarts itself on return
                Config.markChanged(getActivity(), "light_theme");
                Config.markChanged(getActivity(), "dark_theme");
                // The dark_theme preference value gets saved by Android in the default PreferenceManager.
                // It's used in getATEKey() of both the Activities.
                getActivity().recreate();
                Bundle params = new Bundle();
                SettingsActivity.mFirebaseAnalytics.logEvent("Dark_Theme_Checkbox", params);
                return true;
            }
        });

        final ATECheckBoxPreference statusBarPref = (ATECheckBoxPreference) findPreference("colored_status_bar");
        final ATECheckBoxPreference navBarPref = (ATECheckBoxPreference) findPreference("colored_nav_bar");

        /*statusBarPref.setChecked(Config.coloredStatusBar(getActivity(), mAteKey));
        statusBarPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ATE.config(getActivity(), mAteKey)
                        .coloredStatusBar((Boolean) newValue)
                        .apply(getActivity());
                return true;
            }
        });*/


        /*navBarPref.setChecked(Config.coloredNavigationBar(getActivity(), mAteKey));
        navBarPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ATE.config(getActivity(), mAteKey)
                        .coloredNavigationBar((Boolean) newValue)
                        .apply(getActivity());
                return true;
            }
        });*/

    }
}
