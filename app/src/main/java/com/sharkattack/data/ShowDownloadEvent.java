package com.sharkattack.data;

/**
 * Show Download Event
 */
public class ShowDownloadEvent {

    public static final int DOWNLOAD_COMPLETE = 0;
    public static final int DOWNLOAD_PROGRESS = 1;

    public static final int UNZIPPING = 2;
    public static final int DOWNLOADING = 3;

    public int eventType;
    public int progress;
    public int mode;

    public ShowDownloadEvent(int type) {
        eventType = type;
    }
}
