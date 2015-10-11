package com.sharkattack.data;

/**
 * Playlist Loader Interface
 */
public interface IPlaylistLoader {

    /**
     * load playlist.xml
     * @param url
     */
    public void load(String url);

    /**
     * set the metadata completion listener
     * @param l metadata listener
     */
    public void setOnPlaylistListener(OnPlaylistListener l);
}
