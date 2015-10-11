package com.sharkattack.data;

import java.util.List;

/**
 * Playlist Listener
 */
public interface OnPlaylistListener {
    public abstract void onPlaylistLoaded(List<Asset> a);
    public abstract void onPlaylistFailure(String url);
    public abstract void onPlaylistAssetChange(Asset a);
}
