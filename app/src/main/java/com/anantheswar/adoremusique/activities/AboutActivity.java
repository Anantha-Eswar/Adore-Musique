package com.anantheswar.adoremusique.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.anantheswar.adoremusique.R;
import com.anantheswar.adoremusique.utils.PreferencesUtility;
import com.google.firebase.analytics.FirebaseAnalytics;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!PreferencesUtility.fullScreen()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_about);

    }


    public void facebook(View view) {
        Uri uri = Uri.parse("https://www.facebook.com/AnanthPgrmr?href=Adore_Musique");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        Bundle params = new Bundle();
        MainActivity.mFirebaseAnalytics.logEvent("Facebook_Opened", params);

    }

    public void privacyPolicy(View view) {
        Uri uri = Uri.parse("https://www.sites.google.com/view/adoremusique");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        Bundle params = new Bundle();
        MainActivity.mFirebaseAnalytics.logEvent("PrivacyPolicy_Opened", params);

    }

    public void email(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ananth.androiddeveloper@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Adore Musique");
        try {
            startActivity(Intent.createChooser(i, getString(R.string.send_email)));
        } catch (android.content.ActivityNotFoundException ex) {
            showMessage(getString(R.string.no_email_app));
        }
        Bundle params = new Bundle();
        MainActivity.mFirebaseAnalytics.logEvent("Email_Opened", params);
    }

    private void showMessage(String msg) {
        ViewGroup container = findViewById(R.id.snackbar_layout);
        Snackbar.make(container, msg, Snackbar.LENGTH_LONG).show();
    }

    public static void showRateDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.rate_this_app)
                .setMessage(R.string.rate_this_app_message)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (context != null) {
                            String link = "market://details?id=";
                            try {
                                // play market available
                                context.getPackageManager()
                                        .getPackageInfo("com.android.vending", 0);
                                // not available
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                                // should use browser
                                link = "https://play.google.com/store/apps/details?id=";
                            }
                            // starts external action
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(link + context.getPackageName())));
                            Bundle params = new Bundle();
                            MainActivity.mFirebaseAnalytics.logEvent("RateApp_Rated", params);
                        }
                    }

                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle params = new Bundle();
                        MainActivity.mFirebaseAnalytics.logEvent("RateApp_Cancelled", params);
                    }
                });
        builder.show();
    }

    public void messageEveryone(View view) {
        String link = "https://play.google.com/store/apps/details?id=com.anantheswar.messageeveryone";
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(link)));
        Bundle params = new Bundle();
        MainActivity.mFirebaseAnalytics.logEvent("MessageEveryone_Opened", params);
    }


    public void translations(View view) {
        Intent intent = new Intent(AboutActivity.this,Translations.class);
        startActivity(intent);
    }
}
