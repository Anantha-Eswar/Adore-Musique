package com.anantheswar.adoremusique.subfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anantheswar.adoremusique.R;

public class ArtistTagFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "pageNumber";

    public static ArtistTagFragment newInstance(int pageNumber) {
        ArtistTagFragment fragment = new ArtistTagFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_artist_tag, container, false);
        return rootView;
    }

}
