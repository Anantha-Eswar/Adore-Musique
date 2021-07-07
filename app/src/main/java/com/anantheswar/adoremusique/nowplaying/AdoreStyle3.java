package com.anantheswar.adoremusique.nowplaying;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anantheswar.adoremusique.R;

public class AdoreStyle3 extends BaseNowplayingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_timber3, container, false);

        setMusicStateListener();
        setSongDetails(rootView);

        initGestures(rootView.findViewById(R.id.album_art));

        return rootView;
    }

}
