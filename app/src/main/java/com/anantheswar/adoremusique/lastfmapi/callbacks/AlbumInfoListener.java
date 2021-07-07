package com.anantheswar.adoremusique.lastfmapi.callbacks;

import com.anantheswar.adoremusique.lastfmapi.models.LastfmAlbum;

public interface AlbumInfoListener {

    void albumInfoSuccess(LastfmAlbum album);

    void albumInfoFailed();

}
