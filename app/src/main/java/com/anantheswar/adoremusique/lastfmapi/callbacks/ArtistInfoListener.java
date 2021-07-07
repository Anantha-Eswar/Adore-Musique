package com.anantheswar.adoremusique.lastfmapi.callbacks;

import com.anantheswar.adoremusique.lastfmapi.models.LastfmArtist;

public interface ArtistInfoListener {

    void artistInfoSucess(LastfmArtist artist);

    void artistInfoFailed();

}
