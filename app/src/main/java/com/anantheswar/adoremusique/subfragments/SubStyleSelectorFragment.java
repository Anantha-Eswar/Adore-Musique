package com.anantheswar.adoremusique.subfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.anantheswar.adoremusique.R;
import com.anantheswar.adoremusique.activities.MainActivity;
import com.anantheswar.adoremusique.utils.Constants;
import com.anantheswar.adoremusique.utils.NavigationUtils;
import com.anantheswar.adoremusique.utils.PreferencesUtility;

import static com.anantheswar.adoremusique.activities.SettingsActivity.mFirebaseAnalytics;

public class SubStyleSelectorFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "pageNumber";
    private static final String WHAT = "what";
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private LinearLayout currentStyle;
    private View foreground;
    private ImageView styleImage, imgLock;

    String STYLE_NUM = "undefined";
    String content;

    public static SubStyleSelectorFragment newInstance(int pageNumber, String what) {
        SubStyleSelectorFragment fragment = new SubStyleSelectorFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_NUMBER, pageNumber);
        bundle.putString(WHAT, what);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_style_selector_pager, container, false);

        TextView styleName = rootView.findViewById(R.id.style_name);
        styleName.setText(String.valueOf(getArguments().getInt(ARG_PAGE_NUMBER) + 1));
        preferences = getActivity().getSharedPreferences(Constants.FRAGMENT_ID, Context.MODE_PRIVATE);
        styleImage = rootView.findViewById(R.id.style_image);
        imgLock = rootView.findViewById(R.id.img_lock);

        styleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (getArguments().getInt(ARG_PAGE_NUMBER)) {
                    case 0:
                        if (isFirstUnlocked()) {
                            setPreferences();
                        } else {
                            STYLE_NUM = "First_Style";
                            int count = PreferencesUtility.firstUnlockCount();
                            content = getResources().getString(R.string.player_style_rewarded)+"\n(" + count+ " times left)";
                            showUnlockDialog();
                        }
                        break;
                    case 1:
                        if (isSecondUnlocked()) {
                            setPreferences();
                        } else {
                            STYLE_NUM = "Second_Style";
                            int count = PreferencesUtility.secondUnlockCount();
                            content = getResources().getString(R.string.player_style_rewarded)+"\n(" + count+ " times left)";
                            showUnlockDialog();
                        }
                        break;
                    case 4:
                        if (isFifthUnlocked()) {
                            setPreferences();
                        } else {
                            STYLE_NUM = "Fifth_Style";
                            int count = PreferencesUtility.fifthUnlockCount();
                            content = getResources().getString(R.string.player_style_rewarded)+"\n(" + count+ " times left)";
                            showUnlockDialog();
                        }
                        break;
                    case 5:
                        if (isSixthUnlocked()) {
                            setPreferences();
                        } else {
                            STYLE_NUM = "Sixth_Style";
                            int count = PreferencesUtility.sixthUnlockCount();
                            content = getResources().getString(R.string.player_style_rewarded)+"\n(" + count+ " times left)";
                            showUnlockDialog();
                        }

                        break;
                    default:
                        setPreferences();
                        break;
                }
            }
        });

        switch (getArguments().getInt(ARG_PAGE_NUMBER)) {
            case 0:
                styleImage.setImageResource(R.drawable.timber_1_nowplaying_x);
                break;
            case 1:
                styleImage.setImageResource(R.drawable.timber_2_nowplaying_x);
                break;
            case 2:
                styleImage.setImageResource(R.drawable.timber_3_nowplaying_x);
                break;
            case 3:
                styleImage.setImageResource(R.drawable.timber_4_nowplaying_x);
                break;
            case 4:
                styleImage.setImageResource(R.drawable.timber_5_nowplaying_x);
                break;
            case 5:
                styleImage.setImageResource(R.drawable.timber_6_nowplaying_x);
                break;
        }

        currentStyle = rootView.findViewById(R.id.currentStyle);
        foreground = rootView.findViewById(R.id.foreground);

        setCurrentStyle();

        return rootView;
    }

    private boolean isFirstUnlocked() {
        return getActivity() != null && PreferencesUtility.getInstance(getActivity()).firstUnlocked();
    }

    private boolean isSecondUnlocked() {
        return getActivity() != null && PreferencesUtility.getInstance(getActivity()).secondUnlocked();
    }

    private boolean isFifthUnlocked() {
        return getActivity() != null && PreferencesUtility.getInstance(getActivity()).fifthUnlocked();
    }

    private boolean isSixthUnlocked() {
        return getActivity() != null && PreferencesUtility.getInstance(getActivity()).sixthUnlocked();
    }



    @Override
    public void onResume() {
        super.onResume();
        /*updateLockedStatus();*/
    }

    //Sets Lock icon on the selected Styles
    /*private void updateLockedStatus() {
        if (getArguments().getInt(ARG_PAGE_NUMBER) >= 4 && !isFifthUnlocked()) {
            imgLock.setVisibility(View.VISIBLE);
            foreground.setVisibility(View.VISIBLE);
        }
        else {
            imgLock.setVisibility(View.GONE);
            foreground.setVisibility(View.GONE);
        }
    }*/

    private void showUnlockDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.unlock)
                .content(content)
                .positiveText(R.string.watch_video)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (STYLE_NUM) {
                            case "First_Style":
                                if(MainActivity.mRewardedVideoAd.isLoaded()){
                                    MainActivity.mRewardedVideoAd.show();
                                    MainActivity.UNLOCK_STYLE = "First_Style";
                                    STYLE_NUM = "undefined";
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(),R.string.rewarded_failed,Toast.LENGTH_LONG).show();
                                    Bundle params = new Bundle();
                                    params.putString("First_Style","Not loaded");
                                    mFirebaseAnalytics.logEvent("Showing_Unlock_Dialog", params);
                                }

                                break;
                            case "Second_Style":
                                if(MainActivity.mRewardedVideoAd.isLoaded()) {
                                    MainActivity.mRewardedVideoAd.show();
                                    MainActivity.UNLOCK_STYLE = "Second_Style";
                                    STYLE_NUM = "undefined";
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(),R.string.rewarded_failed,Toast.LENGTH_LONG).show();
                                    Bundle params = new Bundle();
                                    params.putString("Second_Style","Not loaded");
                                    mFirebaseAnalytics.logEvent("Showing_Unlock_Dialog", params);
                                }
                                break;
                            case "Fifth_Style":
                                if(MainActivity.mRewardedVideoAd.isLoaded()) {
                                    MainActivity.mRewardedVideoAd.show();
                                    MainActivity.UNLOCK_STYLE = "Fifth_Style";
                                    STYLE_NUM = "undefined";
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(),R.string.rewarded_failed,Toast.LENGTH_LONG).show();
                                    Bundle params = new Bundle();
                                    params.putString("Fifth_Style","Not loaded");
                                    mFirebaseAnalytics.logEvent("Showing_Unlock_Dialog", params);
                                }
                                break;
                            case "Sixth_Style":
                                if (MainActivity.mRewardedVideoAd.isLoaded()) {
                                    MainActivity.mRewardedVideoAd.show();
                                    MainActivity.UNLOCK_STYLE = "Sixth_Style";
                                    STYLE_NUM = "undefined";
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(),R.string.rewarded_failed,Toast.LENGTH_LONG).show();
                                    Bundle params = new Bundle();
                                    params.putString("Sixth_Style","Not loaded");
                                    mFirebaseAnalytics.logEvent("Showing_Unlock_Dialog", params);
                                }
                                break;
                        }
                    }
                })
                .show();
        Bundle params = new Bundle();
        mFirebaseAnalytics.logEvent("Showing_Unlock_Dialog", params);
    }

    private void showUnlockDialogInterstitial() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.unlock)
                .content(content)
                .positiveText(R.string.show_ad)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (STYLE_NUM) {
                            case "First_Style":
                                if(MainActivity.mInterstitialAd.isLoaded()){
                                    MainActivity.mInterstitialAd.show();
                                    MainActivity.UNLOCK_STYLE = "First_Style";
                                    STYLE_NUM = "undefined";
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(),R.string.please_wait,Toast.LENGTH_LONG).show();
                                    Bundle params = new Bundle();
                                    params.putString("First_Style","Not loaded");
                                    mFirebaseAnalytics.logEvent("Showing_Unlock_Dialog", params);
                                }

                                break;
                            case "Second_Style":
                                if(MainActivity.mInterstitialAd.isLoaded()) {
                                    MainActivity.mInterstitialAd.show();
                                    MainActivity.UNLOCK_STYLE = "Second_Style";
                                    STYLE_NUM = "undefined";
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(),R.string.please_wait,Toast.LENGTH_LONG).show();
                                    Bundle params = new Bundle();
                                    params.putString("Second_Style","Not loaded");
                                    mFirebaseAnalytics.logEvent("Showing_Unlock_Dialog", params);
                                }
                                break;
                            case "Fifth_Style":
                                if(MainActivity.mInterstitialAd.isLoaded()) {
                                    MainActivity.mInterstitialAd.show();
                                    MainActivity.UNLOCK_STYLE = "Fifth_Style";
                                    STYLE_NUM = "undefined";
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(),R.string.please_wait,Toast.LENGTH_LONG).show();
                                    Bundle params = new Bundle();
                                    params.putString("Fifth_Style","Not loaded");
                                    mFirebaseAnalytics.logEvent("Showing_Unlock_Dialog", params);
                                }
                                break;
                            case "Sixth_Style":
                                if (MainActivity.mInterstitialAd.isLoaded()) {
                                    MainActivity.mInterstitialAd.show();
                                    MainActivity.UNLOCK_STYLE = "Sixth_Style";
                                    STYLE_NUM = "undefined";
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(),R.string.please_wait,Toast.LENGTH_LONG).show();
                                    Bundle params = new Bundle();
                                    params.putString("Sixth_Style","Not loaded");
                                    mFirebaseAnalytics.logEvent("Showing_Unlock_Dialog", params);
                                }
                                break;
                        }
                    }
                })
                .show();
        Bundle params = new Bundle();
        mFirebaseAnalytics.logEvent("Showing_Unlock_Dialog", params);
    }

    //Sets the selected icon (Tick) on the given Style
    public void setCurrentStyle() {
        String fragmentID = preferences.getString(Constants.NOWPLAYING_FRAGMENT_ID, Constants.TIMBER4);

        if (getArguments().getInt(ARG_PAGE_NUMBER) == NavigationUtils.getIntForCurrentNowplaying(fragmentID)) {
            currentStyle.setVisibility(View.VISIBLE);
            foreground.setVisibility(View.VISIBLE);
        } else {
            currentStyle.setVisibility(View.GONE);
            foreground.setVisibility(View.GONE);
        }

    }

    private void setPreferences() {

        if (getArguments().getString(WHAT).equals(Constants.SETTINGS_STYLE_SELECTOR_NOWPLAYING)) {
            editor = getActivity().getSharedPreferences(Constants.FRAGMENT_ID, Context.MODE_PRIVATE).edit();
            editor.putString(Constants.NOWPLAYING_FRAGMENT_ID, getStyleForPageNumber());
            editor.apply();
            if (getActivity() != null)
                PreferencesUtility.getInstance(getActivity()).setNowPlayingThemeChanged(true);
            setCurrentStyle();
            ((StyleSelectorFragment) getParentFragment()).updateCurrentStyle();
        }
    }

    private String getStyleForPageNumber() {
        switch (getArguments().getInt(ARG_PAGE_NUMBER)) {
            case 0:
                return Constants.TIMBER1;
            case 1:
                return Constants.TIMBER2;
            case 2:
                return Constants.TIMBER3;
            case 3:
                return Constants.TIMBER4;
            case 4:
                return Constants.TIMBER5;
            case 5:
                return Constants.TIMBER6;
            default:
                return Constants.TIMBER3;
        }
    }


}
