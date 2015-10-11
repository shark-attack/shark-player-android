package com.sharkattack.views;

import android.view.View;
import com.sharkattack.data.Asset;

/**
 * Custom Click Listener for Playlist Views
 */
public class OnPlaylistItemClickListener implements View.OnClickListener {
    protected Asset asset;

    /**
     * constructor
     * @param asset
     */
    public OnPlaylistItemClickListener(Asset asset) {
        this.asset = asset;
    }

    /**
     * on click
     * @param v
     */
    public void onClick(View v) {
        System.out.println("position " + asset.title + " clicked.");
    }

    /**
     * get asset clicked
     * @return
     */
    public Asset getAsset() {
        return asset;
    }
}
