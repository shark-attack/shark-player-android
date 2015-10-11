package com.sharkattack.audio;

import com.sharkattack.data.Asset;
import com.sharkattack.data.IPlaylistLoader;
import com.sharkattack.data.OnPlaylistListener;
import com.sharkattack.data.PlaylistLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Media Playlist
 */
public class Playlist {

    /** playlist.xml data */
    protected List<Asset> pls = new ArrayList<Asset>();

    /** current index */
    protected int index = -1;

    /** allow looping */
    public boolean allowLooping = false;

    /** playlist.xml loader */
    protected IPlaylistLoader loader;

    /** playlist.xml listener */
    protected OnPlaylistListener listener = null;

    /**
     * constructor
     */
    public Playlist() {
    }

    /**
     * get total duration of playlist.xml
     * @return duration
     */
    public int getDuration() {
        int ttl = 0;
        for (Asset a : pls) {
            ttl += a.duration;
        }
        return ttl;
    }

    /**
     * constructor
     * @param pls
     */
    public void setData(ArrayList<Asset> pls) {
        index = -1;
        this.pls = pls;
    }

    /**
     * constructor
     * @param loader
     */
    public void setLoader(IPlaylistLoader loader) {
        this.loader = loader;
        this.loader.setOnPlaylistListener(new OnPlaylistListener() {
            @Override
            public void onPlaylistLoaded(List<Asset> a) {
                pls = a;
                listener.onPlaylistLoaded(a);
            }

            @Override
            public void onPlaylistFailure(String url) {
                listener.onPlaylistFailure(url);
            }

            @Override
            public void onPlaylistAssetChange(Asset a){}
        });
    }

    /**
     * set the metadata completion listener
     * @param l metadata listener
     */
    public void setOnPlaylistListener( OnPlaylistListener l) {
        listener = l;
    }


    /**
     * load playlist.xml
     * @param url
     */
    public void load(String url) {
        // if no loader, get default
        if (loader == null) {
            setLoader(new PlaylistLoader());
        }
        this.loader.load(url);
    }

    /**
     * get current item
     * @return asset
     */
    public Asset current() {
        if (index >= pls.size()) {
            return null;
        } else if (index < 0) {
            return null;
        }
        return pls.get(index);
    }

    /**
     * get current index
     * @return index
     */
    public int currentIndex() {
        return index;
    }

    /**
     * get next item
     * @return asset
     */
    public Asset next() {
        index++;
        if (index >= pls.size())  {
            if (allowLooping) {
                index = 0;
            } else {
                index = pls.size();
            }
        }

        listener.onPlaylistAssetChange(current());
        return current();
    }

    /**
     * get previous item
     * @return asset
     */
    public Asset previous() {
        index--;
        if (index < 0) {
            if(allowLooping) {
                index = pls.size()-1;
            } else {
                index = -1;
            }
        }

        listener.onPlaylistAssetChange(current());
        return current();
    }

    /**
     * get previous item
     * @return asset
     */
    public Asset getAssetAt(int i) {
        if (i < 0) {
            return null;
        } else if (i >= pls.size()) {
            return null;
        } else {
            return pls.get(i);
        }
    }

    /**
     * set asset index
     * @return asset
     */
    public Asset setAssetIndex(int i) {
        if (i == index) {
            return current();
        }
        if (getAssetAt(i) != null) {
            index = i;
            listener.onPlaylistAssetChange(current());
            return current();
        } else {
            return null;
        }
    }

    /**
     * set asset
     * @return asset
     */
    public void setAsset(Asset a) {
        int i = getIndexOfAsset(a);
        if (i != -1) {
            setAssetIndex(i);
        }
    }

    /**
     * get index of asset
     * @param a
     * @return index of asset
     */
    public int getIndexOfAsset(Asset a) {
        for (int c = 0; c < pls.size(); c++ ) {
            if (pls.get(c).media.equals(a.media)) {
                setAssetIndex(c);
                return c;
            }
        }
        return -1;
    }

}
