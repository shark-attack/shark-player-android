package com.sharkattack.audio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Audio Player
 */
public class AudioPlayer {

    /** duration of audio */
    public int duration;

    /** media player */
    protected MediaPlayer mp;

    /** if media player is prepared*/
    protected Boolean isPrepared = false;

    /** is idle */
    protected Boolean isIdle = true;

    /* if paused */
    protected Boolean isPaused = false;

    /* is playing */
    protected Boolean isPlaying = false;

    /* if paused */
    protected Boolean isHalfwayReached = false;

    /** playback timer */
    protected Handler tick = new Handler();

    /** media listener */
    protected List<OnMediaListener> listeners = new ArrayList<OnMediaListener>();

    /** current media url */
    protected String currentMedia = "";

    /** current time */
    protected int playbackTime;

    /**
     * toggle playback
     */
    public boolean togglePlayback() {
        if (isPaused && mp != null && isPrepared) {
            mp.start();
            return true;
        } else if (mp != null && isPrepared) {
            mp.pause();
            isPaused = true;
            isPlaying = false;
            for (OnMediaListener listener : listeners) {
                MediaEvent me = new MediaEvent(MediaEvent.MEDIA_PAUSE);
                listener.onMediaEvent(me);
            }
            return false;
        }
        return false;
    }

    /**
     * is player idle
     * @return
     */
    public boolean isIdle() {
        return isIdle;
    }

    /**
     * is player ready
     * @return
     */
    public boolean isReady() {
        if (mp != null && isPrepared) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * get if player is currently playing
     * @return
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * play audio from a URL
     * @param url
     */
    public void play(String url) {
        isIdle = false;
        currentMedia = url;
        tick.removeCallbacks(updateTimeTask);
        if (mp != null) {
            mp.reset();
            mp.release();
        }
        try {
            mp = new MediaPlayer();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mpl) {
                    mpl.start();
                    Log.v("Blastanova::" + this.getClass().toString(), "Media Player Prepared");

                    mpl.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            if (i == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
                                Log.v("Blastanova::" + this.getClass().toString(), "Media Error" + i + " , " + i1);
                                handleError();
                            }
                            return true;
                        }
                    });

                    isPrepared = true;
                    tick.removeCallbacks(updateTimeTask);
                    tick.postDelayed(updateTimeTask, 250);

                    MediaEvent me = new MediaEvent(MediaEvent.MEDIA_START);
                    for (OnMediaListener listener : listeners) {
                        listener.onMediaEvent(me);
                    }
                }
            });

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    stop();
                    Log.v("Blastanova::" + this.getClass().toString(), "audio Complete: " + currentMedia);
                    MediaEvent me = new MediaEvent(MediaEvent.MEDIA_COMPLETE);

                    for (OnMediaListener listener : listeners) {
                        listener.onMediaEvent(me);
                    }
                }
            });

            mp.setDataSource(currentMedia);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Log.v("Blastanova::" + this.getClass().toString(), "Opening: " + currentMedia);
            mp.prepareAsync();
        } catch (IOException ex) {
            Log.v("Blastanova::" + this.getClass().toString(), ex.getMessage());
            Log.v("Blastanova::" + this.getClass().toString(), "Unable to open media: " + currentMedia);
            handleError();
            return;
        } catch (IllegalArgumentException ex) {
            Log.v("Blastanova::" + this.getClass().toString(), ex.getMessage());
            Log.v("Blastanova::" + this.getClass().toString(), "Unable to open media: " + currentMedia);
            handleError();
            return;
        }
    }

    /**
     * stop media player / cleanup
     */
    public void stop() {
        reset();
        isPlaying = false;
        isPrepared = false;
        isIdle = true;
        tick.removeCallbacks(updateTimeTask);

        for (OnMediaListener listener : listeners) {
            MediaEvent me = new MediaEvent(MediaEvent.MEDIA_STOP);
            listener.onMediaEvent(me);
        }
    }

    /**
     * reset playback statuses
     */
    protected void reset() {
        playbackTime = -1;
        isPrepared = false;
        isPaused = false;
        isPlaying = false;
        isIdle = true;
        isHalfwayReached = false;
        if (mp != null) {
            mp = null;
        }
    }

    /**
     * set the media listener
     * @param l listener
     */
    public void setOnMediaListener(OnMediaListener l) {
        listeners.add(l);
    }

    /**
     * timer task to update our current playhead time
     */
    protected Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
        if (isPrepared && mp != null) {
            MediaEvent me;
            int newtime = mp.getCurrentPosition() / 1000;
            if (newtime !=  playbackTime) {
                if (!isPlaying) {
                    for (OnMediaListener listener : listeners) {
                        me = new MediaEvent(MediaEvent.MEDIA_PLAYING);
                        listener.onMediaEvent(me);
                    }
                }
                playbackTime = newtime;
                for (OnMediaListener listener : listeners) {
                    me = new MediaEvent(MediaEvent.MEDIA_TIME_UPDATE);
                    me.playbackTime = playbackTime;
                    listener.onMediaEvent(me);
                }

                if (playbackTime > duration/2 && !isHalfwayReached) {
                    isHalfwayReached = true;
                    me = new MediaEvent(MediaEvent.MEDIA_HALFWAY);
                    me.playbackTime = playbackTime;

                    for (OnMediaListener listener : listeners) {
                        listener.onMediaEvent(me);
                    }
                }

                isPlaying = true;
                isPaused = false;
                isIdle = false;
            }
        }
        tick.postDelayed(this, 250);
        }
    };

    /**
     * handle any error in the same way
     */
    protected void handleError() {
        MediaEvent me = new MediaEvent(MediaEvent.MEDIA_ERROR);
        for (OnMediaListener listener : listeners) {
            listener.onMediaEvent(me);
        }
        stop();
    }
}
