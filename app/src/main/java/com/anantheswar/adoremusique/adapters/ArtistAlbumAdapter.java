package com.anantheswar.adoremusique.adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anantheswar.adoremusique.R;
import com.anantheswar.adoremusique.models.Album;
import com.anantheswar.adoremusique.utils.ImageUtils;
import com.anantheswar.adoremusique.utils.NavigationUtils;
import com.anantheswar.adoremusique.utils.TimberUtils;

import java.util.List;

public class ArtistAlbumAdapter extends RecyclerView.Adapter<ArtistAlbumAdapter.ItemHolder> {

    private List<Album> arraylist;
    private Activity mContext;

    public ArtistAlbumAdapter(Activity context, List<Album> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist_album, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {

        Album localItem = arraylist.get(i);

        itemHolder.title.setText(localItem.title);
        String songCount = TimberUtils.makeLabel(mContext, R.plurals.Nsongs, localItem.songCount);
        itemHolder.details.setText(songCount);

        ImageUtils.loadAlbumArtIntoView(localItem.id, itemHolder.albumArt);

        if (TimberUtils.isLollipop())
            itemHolder.albumArt.setTransitionName("transition_album_art" + i);

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, details;
        protected ImageView albumArt;
        protected CardView rootView;

        public ItemHolder(View view) {
            super(view);
            this.rootView = view.findViewById(R.id.root_view);
            this.title = view.findViewById(R.id.album_title);
            this.details = view.findViewById(R.id.album_details);
            this.albumArt = view.findViewById(R.id.album_art);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavigationUtils.navigateToAlbum(mContext, arraylist.get(getAdapterPosition()).id,
                    new Pair<View, String>(albumArt, "transition_album_art" + getAdapterPosition()));
        }

    }
}




