package com.anantheswar.adoremusique;

import android.support.multidex.MultiDexApplication;

import com.afollestad.appthemeengine.ATE;
import com.anantheswar.adoremusique.permissions.Nammu;
import com.anantheswar.adoremusique.utils.PreferencesUtility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.L;

import java.io.IOException;
import java.io.InputStream;

public class AdoreMusiqueApp extends MultiDexApplication {


    private static AdoreMusiqueApp mInstance;

    public static synchronized AdoreMusiqueApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).imageDownloader(new BaseImageDownloader(this) {
            PreferencesUtility prefs = PreferencesUtility.getInstance(AdoreMusiqueApp.this);

            @Override
            protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
                if (prefs.loadArtistAndAlbumImages())
                    return super.getStreamFromNetwork(imageUri, extra);
                throw new IOException();
            }
        }).build();

        ImageLoader.getInstance().init(localImageLoaderConfiguration);
        L.writeLogs(false);
        L.disableLogging();
        L.writeDebugLogs(false);
        Nammu.init(this);

        if (!ATE.config(this, "light_theme").isConfigured()) {
            ATE.config(this, "light_theme")
                    .activityTheme(R.style.AppThemeLight)
                    .primaryColorRes(R.color.colorPrimaryLightDefault)
                    .accentColorRes(R.color.colorAccentLightDefault)
                    .coloredNavigationBar(false)
                    .usingMaterialDialogs(true)
                    .commit();
        }
        if (!ATE.config(this, "dark_theme").isConfigured()) {
            ATE.config(this, "dark_theme")
                    .activityTheme(R.style.AppThemeDark)
                    .primaryColorRes(R.color.colorPrimaryDarkDefault)
                    .accentColorRes(R.color.colorAccentDarkDefault)
                    .coloredNavigationBar(false)
                    .usingMaterialDialogs(true)
                    .commit();
        }
        if (!ATE.config(this, "light_theme_notoolbar").isConfigured()) {
            ATE.config(this, "light_theme_notoolbar")
                    .activityTheme(R.style.AppThemeLight)
                    .coloredActionBar(false)
                    .primaryColorRes(R.color.colorPrimaryLightDefault)
                    .accentColorRes(R.color.colorAccentLightDefault)
                    .coloredNavigationBar(false)
                    .usingMaterialDialogs(true)
                    .commit();
        }
        if (!ATE.config(this, "dark_theme_notoolbar").isConfigured()) {
            ATE.config(this, "dark_theme_notoolbar")
                    .activityTheme(R.style.AppThemeDark)
                    .coloredActionBar(false)
                    .primaryColorRes(R.color.colorPrimaryDarkDefault)
                    .accentColorRes(R.color.colorAccentDarkDefault)
                    .coloredNavigationBar(true)
                    .usingMaterialDialogs(true)
                    .commit();
        }

    }


}
