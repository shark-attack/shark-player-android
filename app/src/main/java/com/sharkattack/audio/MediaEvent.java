package com.sharkattack.audio;

/**
 * Media Event
 */
public class MediaEvent {

    public static final int MEDIA_COMPLETE = 0;
    public static final int MEDIA_TIME_UPDATE = 1;
    public static final int MEDIA_START = 2;
    public static final int MEDIA_SEEK = 3;
    public static final int MEDIA_HALFWAY = 4;
    public static final int MEDIA_ERROR = 5;
    public static final int MEDIA_PAUSE = 6;
    public static final int MEDIA_PLAYING = 7;
    public static final int MEDIA_STOP = 8;

    public int eventType;
    public int playbackTime;
    public int duration;

    public MediaEvent(int type) {
        eventType = type;
    }
}
