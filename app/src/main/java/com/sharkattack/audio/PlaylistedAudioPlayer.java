package com.sharkattack.audio;

import com.sharkattack.data.Asset;

/**
 * Audioplayer/Playlist combo
 */
public class PlaylistedAudioPlayer extends Playlist {

    /**
     * player
     */
    protected AudioPlayer player;

    /**
     * constructor
     */
    public PlaylistedAudioPlayer() {
        super();
        player = new AudioPlayer();
        player.setOnMediaListener( new OnMediaListener() {
            @Override
            public void onMediaEvent(MediaEvent event) {
                switch (event.eventType) {
                    case MediaEvent.MEDIA_COMPLETE:
                        next();
                        break;

                    case MediaEvent.MEDIA_TIME_UPDATE:
                        break;

                    case MediaEvent.MEDIA_ERROR:
                        next();
                        break;
                }
            }
        });
    }

    /**
     * get audio player
     * @return
     */
    public AudioPlayer getPlayer() {
        return player;
    }


    /**
     * toggle playback
     * @return is playing
     */
    public boolean togglePlayback() {
        if (player.isIdle()) {
            next();
            return true;
        } else {
            return player.togglePlayback();
        }
    }

    /**
     * next
     * @return asset
     */
    @Override
    public Asset next() {
        Asset a = super.next();
        if (a != null) {
            player.play(a.localMedia);
        }
        return a;
    }


    /**
     * previous
     * @return asset
     */
    @Override
    public Asset previous() {
        Asset a = super.previous();
        if (a != null) {
            player.play(a.localMedia);
        }
        return a;
    }

    /**
     * set asset index
     * @return asset
     */
    @Override
    public Asset setAssetIndex(int i) {
        Asset a = super.setAssetIndex(i);
        if (a != null) {
            player.play(a.localMedia);
        }
        return a;
    }
}
