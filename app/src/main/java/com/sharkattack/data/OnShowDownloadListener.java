package com.sharkattack.data;

/**
 * Show Download Listener
 */
public interface OnShowDownloadListener {
    public abstract void onShowDownloadComplete(ShowDownloadEvent se);
    public abstract void onShowDownloadProgress(ShowDownloadEvent se);
}
