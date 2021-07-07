package com.anantheswar.adoremusique.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.anantheswar.adoremusique.R;
import com.anantheswar.adoremusique.activities.ImagePicker;
import com.anantheswar.adoremusique.activities.MainActivity;
import com.anantheswar.adoremusique.activities.NowPlayingActivity;
import com.anantheswar.adoremusique.activities.PlaylistDetailActivity;
import com.anantheswar.adoremusique.activities.SearchActivity;
import com.anantheswar.adoremusique.activities.SettingsActivity;
import com.anantheswar.adoremusique.fragments.AlbumDetailFragment;
import com.anantheswar.adoremusique.fragments.ArtistDetailFragment;
import com.anantheswar.adoremusique.nowplaying.AdoreStyle1;
import com.anantheswar.adoremusique.nowplaying.AdoreStyle2;
import com.anantheswar.adoremusique.nowplaying.AdoreStyle3;
import com.anantheswar.adoremusique.nowplaying.AdoreStyle4;
import com.anantheswar.adoremusique.nowplaying.AdoreStyle5;
import com.anantheswar.adoremusique.nowplaying.AdoreStyle6;

import java.util.ArrayList;

public class NavigationUtils {

    @TargetApi(21)
    public static void navigateToAlbum(Activity context, long albumID, Pair<View, String> transitionViews) {

        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        transaction.setCustomAnimations(R.anim.activity_fade_in,
                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
        fragment = AlbumDetailFragment.newInstance(albumID, false, null);

        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null).commit();

    }

    @TargetApi(21)
    public static void navigateToArtist(Activity context, long artistID, Pair<View, String> transitionViews) {

        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        transaction.setCustomAnimations(R.anim.activity_fade_in,
                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
        fragment = ArtistDetailFragment.newInstance(artistID, false, null);

        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null).commit();

    }

    public static void goToArtist(Context context, long artistId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_ARTIST);
        intent.putExtra(Constants.ARTIST_ID, artistId);
        context.startActivity(intent);
    }

    public static void goToAlbum(Context context, long albumId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_ALBUM);
        intent.putExtra(Constants.ALBUM_ID, albumId);
        context.startActivity(intent);
    }

    public static void goToLyrics(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_LYRICS);
        context.startActivity(intent);
    }

    public static void navigateToNowplaying(Activity context, boolean withAnimations) {

        final Intent intent = new Intent(context, NowPlayingActivity.class);
        context.startActivity(intent);
    }

    public static Intent getNowPlayingIntent(Context context) {

        final Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_NOWPLAYING);
        return intent;
    }

    public static void navigateToSettings(Activity context) {
        final Intent intent = new Intent(context, SettingsActivity.class);
        intent.setAction(Constants.NAVIGATE_SETTINGS);
        context.startActivity(intent);
    }

    public static void navigateToSearch(Activity context) {
        final Intent intent = new Intent(context, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(Constants.NAVIGATE_SEARCH);
        context.startActivity(intent);

    }


    @TargetApi(21)
    public static void navigateToPlaylistDetail(Activity context, String action, long firstAlbumID, String playlistName, int foregroundcolor, long playlistID, ArrayList<Pair> transitionViews) {
        final Intent intent = new Intent(context, PlaylistDetailActivity.class);
        intent.setAction(action);
        intent.putExtra(Constants.PLAYLIST_ID, playlistID);
        intent.putExtra(Constants.PLAYLIST_FOREGROUND_COLOR, foregroundcolor);
        intent.putExtra(Constants.ALBUM_ID, firstAlbumID);
        intent.putExtra(Constants.PLAYLIST_NAME, playlistName);
        intent.putExtra(Constants.ACTIVITY_TRANSITION, transitionViews != null);

        if (transitionViews != null && TimberUtils.isLollipop()) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, transitionViews.get(0), transitionViews.get(1), transitionViews.get(2));
            context.startActivityForResult(intent, Constants.ACTION_DELETE_PLAYLIST, options.toBundle());
        } else {
            context.startActivityForResult(intent, Constants.ACTION_DELETE_PLAYLIST);
        }
    }

    public static void navigateToEqualizer(Activity context) {
        try {
            // The google MusicFX apps need to be started using startActivityForResult
            context.startActivityForResult(TimberUtils.createEffectsIntent(), 666);
        } catch (final ActivityNotFoundException notFound) {
            Toast.makeText(context, "Equalizer not found", Toast.LENGTH_SHORT).show();
        }
    }

    public static Intent getNavigateToStyleSelectorIntent(Activity context, String what) {
        final Intent intent = new Intent(context, SettingsActivity.class);
        intent.setAction(Constants.SETTINGS_STYLE_SELECTOR);
        intent.putExtra(Constants.SETTINGS_STYLE_SELECTOR_WHAT, what);
        return intent;
    }

    public static Intent test(Activity context) {
        final Intent intent = new Intent(context, ImagePicker.class);
        intent.setAction(Constants.TEST);
        return intent;
    }

    public static Fragment getFragmentForNowplayingID(String fragmentID) {
        switch (fragmentID) {
            case Constants.TIMBER1:
                return new AdoreStyle1();
            case Constants.TIMBER2:
                return new AdoreStyle2();
            case Constants.TIMBER3:
                return new AdoreStyle3();
            case Constants.TIMBER4:
                return new AdoreStyle4();
            case Constants.TIMBER5:
                return new AdoreStyle5();
            case Constants.TIMBER6:
                return new AdoreStyle6();
            default:
                return new AdoreStyle1();
        }

    }

    public static int getIntForCurrentNowplaying(String nowPlaying) {
        switch (nowPlaying) {
            case Constants.TIMBER1:
                return 0;
            case Constants.TIMBER2:
                return 1;
            case Constants.TIMBER3:
                return 2;
            case Constants.TIMBER4:
                return 3;
            case Constants.TIMBER5:
                return 4;
            case Constants.TIMBER6:
                return 5;
            default:
                return 3;
        }

    }

}
