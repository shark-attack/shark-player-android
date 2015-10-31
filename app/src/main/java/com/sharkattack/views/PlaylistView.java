package com.sharkattack.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Playlist View Component
 */
public class PlaylistView extends ListView {

    /** playlist.xml item listener */
    protected OnAssetSelectedListener listener;

    /**
     * constructor
     * @param context
     */
    public PlaylistView(Context context) {
        super(context);
        setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    public PlaylistView(Context context, AttributeSet aSet) {
        super(context, aSet);
        setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    /**
     * set the media listener
     * @param l listener
     */
    public void setOnAssetSelectedListener(OnAssetSelectedListener l) {
        listener = l;
    }
    /**
     * set the media listener
     * @return listener
     */
    public OnAssetSelectedListener getOnAssetSelectedListener() {
        return listener;
    }
}
