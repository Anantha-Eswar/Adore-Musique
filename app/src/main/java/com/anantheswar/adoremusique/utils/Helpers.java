package com.anantheswar.adoremusique.utils;

import android.content.Context;
import android.preference.PreferenceManager;

public class Helpers {


    public static String getATEKey(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false) ?
                "dark_theme" : "light_theme";
    }



    }

