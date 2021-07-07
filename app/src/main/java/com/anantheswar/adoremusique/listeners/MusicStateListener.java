package com.anantheswar.adoremusique.listeners;

/**
 * Listens for playback changes to send the the fragments bound to this activity
 */
public interface MusicStateListener {

    /**
     * Called when {@link com.anantheswar.adoremusique.MusicService#REFRESH} is invoked
     */
    void restartLoader();

    /**
     * Called when {@link com.anantheswar.adoremusique.MusicService#PLAYLIST_CHANGED} is invoked
     */
    void onPlaylistChanged();

    /**
     * Called when {@link com.anantheswar.adoremusique.MusicService#META_CHANGED} is invoked
     */
    void onMetaChanged();

}
