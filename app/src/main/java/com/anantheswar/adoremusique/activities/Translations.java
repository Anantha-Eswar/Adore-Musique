package com.anantheswar.adoremusique.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.anantheswar.adoremusique.R;

public class Translations extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translations);
        Bundle params = new Bundle();
        MainActivity.mFirebaseAnalytics.logEvent("Translations_Activity", params);
    }

    private void showMessage(String msg) {
        ViewGroup container = findViewById(R.id.snackbar_layout);
        Snackbar.make(container, msg, Snackbar.LENGTH_LONG).show();
    }

    public void id1(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"anthonioustony@hotmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "From Adore Musique");
        try {
            startActivity(Intent.createChooser(i, getString(R.string.send_email)));
        } catch (android.content.ActivityNotFoundException ex) {
            showMessage(getString(R.string.no_email_app));
        }
        Bundle params = new Bundle();
        params.putString("ID","anthonioustony@hotmail.com");
        MainActivity.mFirebaseAnalytics.logEvent("Translations", params);
    }

    public void id2(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"noiseshifterz01@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "From Adore Musique");
        try {
            startActivity(Intent.createChooser(i, getString(R.string.send_email)));
        } catch (android.content.ActivityNotFoundException ex) {
            showMessage(getString(R.string.no_email_app));
        }
        Bundle params = new Bundle();
        params.putString("ID","noiseshifterz01@gmail.com");
        MainActivity.mFirebaseAnalytics.logEvent("Translations", params);
    }

}
