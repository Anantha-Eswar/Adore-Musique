package com.anantheswar.adoremusique.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.anantheswar.adoremusique.MusicPlayer;
import com.anantheswar.adoremusique.R;
import com.anantheswar.adoremusique.fragments.AlbumDetailFragment;
import com.anantheswar.adoremusique.fragments.ArtistDetailFragment;
import com.anantheswar.adoremusique.fragments.FoldersFragment;
import com.anantheswar.adoremusique.fragments.MainFragment;
import com.anantheswar.adoremusique.fragments.PlaylistFragment;
import com.anantheswar.adoremusique.fragments.QueueFragment;
import com.anantheswar.adoremusique.permissions.Nammu;
import com.anantheswar.adoremusique.permissions.PermissionCallback;
import com.anantheswar.adoremusique.slidinguppanel.SlidingUpPanelLayout;
import com.anantheswar.adoremusique.subfragments.LyricsFragment;
import com.anantheswar.adoremusique.utils.Constants;
import com.anantheswar.adoremusique.utils.NavigationUtils;
import com.anantheswar.adoremusique.utils.PreferencesUtility;
import com.anantheswar.adoremusique.utils.TimberUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements ATEActivityThemeCustomizer, RewardedVideoAdListener {

    private SlidingUpPanelLayout panelLayout;
    private NavigationView navigationView;
    @SuppressLint("StaticFieldLeak")
    public static ImageView albumart;
    private String action;
    private Map<String, Runnable> navigationMap = new HashMap<String, Runnable>();
    private Handler navDrawerRunnable = new Handler();
    private Runnable runnable;
    private DrawerLayout mDrawerLayout;
    private boolean isDarkTheme;
    static public RewardedVideoAd mRewardedVideoAd;
    static public InterstitialAd mInterstitialAd;
    public static FirebaseAnalytics mFirebaseAnalytics;
    //private AdView mAdView;

    //Ads ID
    /*String REWARDED = "ca-app-pub-2658405203684206/4607606577";
    String INTERSTITIAL_UNIT = "ca-app-pub-2658405203684206/7837218180";
    String INTERSTITIAL_APP = "ca-app-pub-2658405203684206~2167121976";*/

    //Test Ad IDs
    String REWARDED = "ca-app-pub-3940256099942544/5224354917";
    String INTERSTITIAL_UNIT = "ca-app-pub-3940256099942544/1033173712";
    String INTERSTITIAL_APP = "ca-app-pub-3940256099942544~3347511713";

    public static String UNLOCK_STYLE = "undefined";

    private Runnable navigateLibrary = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
            Fragment fragment = new MainFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();

        }
    };

    private Runnable navigatePlaylist = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_playlists).setChecked(true);
            Fragment fragment = new PlaylistFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();

        }
    };

    private Runnable navigateFolder = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_folders).setChecked(true);
            Fragment fragment = new FoldersFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();

        }
    };

    private Runnable navigateQueue = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_queue).setChecked(true);
            Fragment fragment = new QueueFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();

        }
    };

    private Runnable navigateAlbum = new Runnable() {
        public void run() {
            long albumID = getIntent().getExtras().getLong(Constants.ALBUM_ID);
            Fragment fragment = AlbumDetailFragment.newInstance(albumID, false, null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateArtist = new Runnable() {
        public void run() {
            long artistID = getIntent().getExtras().getLong(Constants.ARTIST_ID);
            Fragment fragment = ArtistDetailFragment.newInstance(artistID, false, null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateLyrics = new Runnable() {
        public void run() {
            Fragment fragment = new LyricsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateNowplaying = new Runnable() {
        public void run() {
            navigateLibrary.run();
            startActivity(new Intent(MainActivity.this, NowPlayingActivity.class));
        }
    };

    private final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadEverything();
        }

        @Override
        public void permissionRefused() {
            finish();
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ThemeBaseLight_Launcher);
        action = getIntent().getAction();
        isDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationMap.put(Constants.NAVIGATE_LIBRARY, navigateLibrary);
        navigationMap.put(Constants.NAVIGATE_PLAYLIST, navigatePlaylist);
        navigationMap.put(Constants.NAVIGATE_QUEUE, navigateQueue);
        navigationMap.put(Constants.NAVIGATE_NOWPLAYING, navigateNowplaying);
        navigationMap.put(Constants.NAVIGATE_ALBUM, navigateAlbum);
        navigationMap.put(Constants.NAVIGATE_ARTIST, navigateArtist);
        navigationMap.put(Constants.NAVIGATE_LYRICS, navigateLyrics);
        MobileAds.initialize(this, INTERSTITIAL_APP);
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        panelLayout = findViewById(R.id.sliding_layout);

        navigationView = findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header);

        albumart = header.findViewById(R.id.album_art);

        setPanelSlideListeners(panelLayout);

        navDrawerRunnable.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupDrawerContent(navigationView);
                setupNavigationIcons(navigationView);
            }
        }, 700);

        if (TimberUtils.isMarshmallow()) {
            checkPermissionAndThenLoad();
            //checkWritePermissions();
        } else {
            loadEverything();
        }

        addBackstackListener();

        if (Intent.ACTION_VIEW.equals(action)) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.clearQueue();
                    MusicPlayer.openFile(getIntent().getData().getPath());
                    MusicPlayer.playOrPause();
                    navigateNowplaying.run();
                }
            }, 350);
        }

        if (!panelLayout.isPanelHidden() && MusicPlayer.getTrackName() == null) {
            panelLayout.hidePanel();
        }
        AnimationDrawable animationDrawable = (AnimationDrawable) mDrawerLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10000);
        animationDrawable.setExitFadeDuration(10000);
        animationDrawable.start();
        setImage();

        /*mAdView = findViewById(R.id.adView1);

        ca-app-pub-2658405203684206/4826366877
        AdRequest adRequest = new AdRequest.Builder().build();
        final Button button = findViewById(R.id.adbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setVisibility(View.GONE);
                mAdView.setVisibility(View.INVISIBLE);
                Bundle params = new Bundle();
                params.putString("Banner", "Custom_Close_Button");
                mFirebaseAnalytics.logEvent("Advertisement", params);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mAdView.setVisibility(View.VISIBLE);
                        button.setVisibility(View.VISIBLE);
                    }
                }, 10000);
            }
        });
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            Bundle params = new Bundle();

            @Override
            public void onAdLoaded() {
                button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdLeftApplication() {
                params.putString("Banner", "onAdLeft");
                mFirebaseAnalytics.logEvent("Advertisement", params);

            }

            @Override
            public void onAdOpened() {
                params.putString("Banner", "onAdOpened");
                mFirebaseAnalytics.logEvent("Advertisement", params);
            }
        });*/

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(INTERSTITIAL_UNIT);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            Bundle params = new Bundle();


            @Override
            public void onAdLeftApplication() {
                params.putString("Interstitial", "onAdLeftApplication");
                mFirebaseAnalytics.logEvent("Advertisement", params);
                switch (UNLOCK_STYLE) {
                    case "First_Style":
                        PreferencesUtility.setFirstUnlockCount(1);
                        firstStyle();
                        showAdInFewSeconds();
                        break;
                    case "Second_Style":
                        PreferencesUtility.setSecondUnlockCount(1);
                        secondStyle();
                        showAdInFewSeconds();
                        break;
                    case "Fifth_Style":
                        PreferencesUtility.setFifthUnlockCount(1);
                        fifthStyle();
                        showAdInFewSeconds();
                        break;
                    case "Sixth_Style":
                        PreferencesUtility.setSixthUnlockCount(1);
                        sixthStyle();
                        showAdInFewSeconds();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                params.putString("Interstitial", "onAdClosed");
                mFirebaseAnalytics.logEvent("Advertisement", params);
            }
        });
    }

    private void showAdInFewSeconds() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (mInterstitialAd.isLoaded()) {

                    {
                        mInterstitialAd.show();
                    }
                }
                //handler.postDelayed(this, 60000); //Displays the ad for every specified milliseconds
            }
        }, 1000 * 18);
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(REWARDED,
                new AdRequest.Builder().build());

    }

    private void loadEverything() {
        Runnable navigation = navigationMap.get(action);
        if (navigation != null) {
            navigation.run();
        } else {
            navigateLibrary.run();
        }

        new initQuickControls().execute("");
    }

    private void checkPermissionAndThenLoad() {
        //check for permission
        if (Nammu.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadEverything();
        } else {
            if (Nammu.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(panelLayout, R.string.check_permission,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.okay, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Nammu.askForPermission(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
                            }
                        }).show();
            } else {
                Nammu.askForPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (isNavigatingMain()) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                } else super.onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (panelLayout.isPanelExpanded()) {
            panelLayout.collapsePanel();
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (navigationView.getMenu().findItem(R.id.nav_library).isChecked()) {
                super.onBackPressed();
            } else {
                navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
                Fragment fragment = new MainFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
            }
        }
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        updatePosition(menuItem);
                        return true;

                    }
                });
    }

    private void setupNavigationIcons(NavigationView navigationView) {

        //material-icon-lib currently doesn't work with navigationview of design support library 22.2.0+
        //set icons manually for now
        //https://github.com/code-mc/material-icon-lib/issues/15

        if (!isDarkTheme) {
            navigationView.setBackgroundResource(R.drawable.navigation_background);
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note);
            navigationView.getMenu().findItem(R.id.nav_folders).setIcon(R.drawable.ic_folder_open_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music);
            navigationView.getMenu().findItem(R.id.sponsorship).setIcon(R.drawable.free_tips);
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings);
            navigationView.getMenu().findItem(R.id.action_rate).setIcon(R.drawable.rate);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information);
        } else {
            mDrawerLayout.setBackgroundColor(Color.BLACK);
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music_white);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play_white);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note_white);
            navigationView.getMenu().findItem(R.id.nav_folders).setIcon(R.drawable.ic_folder_open_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music_white);
            navigationView.getMenu().findItem(R.id.sponsorship).setIcon(R.drawable.free_tips_white);
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings_white);
            navigationView.getMenu().findItem(R.id.action_rate).setIcon(R.drawable.rate_white);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information_white);
        }

    }

    private void updatePosition(final MenuItem menuItem) {
        runnable = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_library:
                runnable = navigateLibrary;
                break;
            case R.id.nav_playlists:
                runnable = navigatePlaylist;
                break;
            case R.id.nav_folders:
                runnable = navigateFolder;
                break;
            case R.id.nav_nowplaying:
                NavigationUtils.navigateToNowplaying(MainActivity.this, false);
                break;
            case R.id.nav_queue:
                runnable = navigateQueue;
                break;
            case R.id.sponsorship:
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Toast.makeText(this, R.string.please_wait, Toast.LENGTH_SHORT).show();
                }
                Bundle params = new Bundle();
                mFirebaseAnalytics.logEvent("Sponsorship", params);
                break;
            case R.id.nav_settings:
                NavigationUtils.navigateToSettings(MainActivity.this);
                break;
            case R.id.action_rate:
                AboutActivity.showRateDialog(MainActivity.this);
                break;
            case R.id.nav_about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
        }

        if (runnable != null) {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, 350);
        }
    }

    // Sets song details to Navigation Header and also enable setDetailsToHeader() method on onMetaChanged() to display.
    /*public void setDetailsToHeader() {

        //It displays the current song or default image on the Navigation header
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .build());

    }*/

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
        //setDetailsToHeader();

        if (panelLayout.isPanelHidden() && MusicPlayer.getTrackName() != null) {
            panelLayout.showPanel();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isNavigatingMain() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return (currentFragment instanceof MainFragment || currentFragment instanceof QueueFragment
                || currentFragment instanceof PlaylistFragment || currentFragment instanceof FoldersFragment);
    }

    private void addBackstackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                getSupportFragmentManager().findFragmentById(R.id.fragment_container).onResume();
            }
        });
    }


    @Override
    public int getActivityTheme() {
        return isDarkTheme ? R.style.AppThemeNormalDark : R.style.AppThemeNormalLight;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportFragmentManager().findFragmentById(R.id.fragment_container).onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Bundle params = new Bundle();
        params.putString("Rewarded_Video", "AdLoaded");
        mFirebaseAnalytics.logEvent("Advertisement", params);
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Bundle params = new Bundle();
        params.putString("Rewarded_Video", "AdOpened");
        mFirebaseAnalytics.logEvent("Advertisement", params);
    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        Bundle params = new Bundle();
        params.putString("Rewarded_Video", "AdClosed");
        mFirebaseAnalytics.logEvent("Advertisement", params);
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Bundle params = new Bundle();
        params.putString("Rewarded_Video", "onRewarded");
        mFirebaseAnalytics.logEvent("Advertisement", params);
        switch (UNLOCK_STYLE) {
            case "First_Style":
                firstStyle();
                break;
            case "Second_Style":
                secondStyle();
                break;
            case "Fifth_Style":
                fifthStyle();
                break;
            case "Sixth_Style":
                sixthStyle();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Bundle params = new Bundle();
        params.putString("Rewarded_Video", "AdLeftApplication");
        mFirebaseAnalytics.logEvent("Advertisement", params);
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Bundle params = new Bundle();
        params.putString("Rewarded_Video", "AdFailedToLoad");
        mFirebaseAnalytics.logEvent("Advertisement", params);
    }

    @Override
    public void onRewardedVideoCompleted() {
        Bundle params = new Bundle();
        params.putString("Rewarded_Video", "VideoCompleted");
        mFirebaseAnalytics.logEvent("Advertisement", params);
    }

    public static void setImage() {

        try {
            String path = PreferencesUtility.getImagePath();
            if (path.equals("")) {
                albumart.setBackgroundResource(R.drawable.app_icon);

            } else {
                Uri uri = Uri.parse(path);
                albumart.setImageURI(uri);
            }

        } catch (Exception ignored) {
        }
    }

    private void firstStyle() {
        Bundle params = new Bundle();
        UNLOCK_STYLE = "undefined";
        int i = PreferencesUtility.firstUnlockCount();
        PreferencesUtility.setFirstUnlockCount(--i);
        if (PreferencesUtility.firstUnlockCount() == 0) {
            PreferencesUtility.setFirstUnlocked(true);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, R.string.first_style, Toast.LENGTH_LONG).show();
                }
            }, 1000 * 3);
            params.putString("Player_Style_Unlocked", "First_Style_Unlocked");
            mFirebaseAnalytics.logEvent("WatchedOrClicked", params);

        }
    }

    private void secondStyle() {
        Bundle params = new Bundle();
        UNLOCK_STYLE = "undefined";
        int i = PreferencesUtility.secondUnlockCount();
        PreferencesUtility.setSecondUnlockCount(--i);
        if (PreferencesUtility.secondUnlockCount() == 0) {
            PreferencesUtility.setSecondUnlocked(true);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, R.string.second_style, Toast.LENGTH_LONG).show();
                }
            }, 1000 * 3);
            params.putString("Player_Style_Unlocked", "Second_Style_Unlocked");
            mFirebaseAnalytics.logEvent("WatchedOrClicked", params);
        }
    }

    private void fifthStyle() {
        Bundle params = new Bundle();
        UNLOCK_STYLE = "undefined";
        int i = PreferencesUtility.fifthUnlockCount();
        PreferencesUtility.setFifthUnlockCount(--i);
        if (PreferencesUtility.fifthUnlockCount() == 0) {
            PreferencesUtility.setFifthUnlocked(true);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, R.string.fifth_style, Toast.LENGTH_LONG).show();
                }
            }, 1000 * 3);
            params.putString("Player_Style_Unlocked", "Fifth_Style_Unlocked");
            mFirebaseAnalytics.logEvent("WatchedOrClicked", params);
        }
    }

    private void sixthStyle() {
        Bundle params = new Bundle();
        UNLOCK_STYLE = "undefined";
        int i = PreferencesUtility.sixthUnlockCount();
        PreferencesUtility.setSixthUnlockCount(--i);
        if (PreferencesUtility.sixthUnlockCount() == 0) {
            PreferencesUtility.setSixthUnlocked(true);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, R.string.sixth_style, Toast.LENGTH_LONG).show();
                }
            }, 1000 * 3);
            params.putString("Player_Style_Unlocked", "Sixth_Style_Unlocked");
            mFirebaseAnalytics.logEvent("WatchedOrClicked", params);
        }
    }

}