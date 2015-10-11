package com.sharkattack.data;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Playlist loader class
 * parses online JSON playlist.xml
 */
public class PlaylistLoader implements IPlaylistLoader {

    /** playlist.xml listener */
    protected OnPlaylistListener listener = null;

    /** currently loading playlist.xml URL */
    protected String currentURL = "";

    /** loaded playlist.xml */
    protected List<Asset> playlist = new ArrayList<Asset>();

    /**
     * constructor
     */
    public PlaylistLoader() {}


    /**
     * load playlist.xml
     * @param playlist
     *
     */
    public void load(String playlist) {
        try {
            FileInputStream stream = new FileInputStream(playlist);
            String jsonStr = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
            } catch (IOException e) {
                Log.e("Blastanova::" + this.getClass().toString(), e.toString());
            } finally {

                try {
                    stream.close();
                    onLoaded(jsonStr);
                } catch (IOException e) {
                    Log.e("Blastanova::" + this.getClass().toString(), e.toString());
                }
            }
        } catch(FileNotFoundException e) {
            Log.e("Blastanova::" + this.getClass().toString(), e.toString());
        }
    }

    /**
     * set the metadata completion listener
     * @param l metadata listener
     */
    public void setOnPlaylistListener( OnPlaylistListener l) {
        listener = l;
    }


    /**
     * load JSON playlist.xml
     *
     * @param result JSON result
     */
    private void onLoaded(String result){
        Asset A = new Asset();
        Log.v("Blastanova::" + this.getClass().toString(), "Playlist Loaded: " + currentURL);

        JSONArray plist = null;
        try {
            plist = new JSONArray(result);
        } catch (JSONException e) {
            Log.v("Blastanova::" + this.getClass().toString(), e.toString());
        }

        if (plist != null) {
            for (int i = 0; i < plist.length(); i++) {
                Asset a = new Asset();
                String jsondate = "";
                try {
                    JSONObject jsonasset = plist.getJSONObject(i);
                    try { a.album = jsonasset.getString("album");} catch (JSONException e) {}
                    //try { a.amazonPurchaseLink = jsonasset.getString("amazonPurchaseLink");} catch (JSONException e) {}
                    try { a.artist = jsonasset.getString("artist");} catch (JSONException e) {}
                    try { a.bitrate = jsonasset.getString("bitrate");} catch (JSONException e) {}
                    try { jsondate = jsonasset.getString("date");} catch (JSONException e) {}
                    try { a.description = jsonasset.getString("description");} catch (JSONException e) {}
                    try { a.duration = jsonasset.getInt("duration");} catch (JSONException e) {}
                    try { a.filename = jsonasset.getString("filename");} catch (JSONException e) {}
                    try { a.label = jsonasset.getString("label");} catch (JSONException e) {}
                    try { a.link = jsonasset.getString("link");} catch (JSONException e) {}
                    try { a.media = jsonasset.getString("media");} catch (JSONException e) {}
                    try { a.mediatype = jsonasset.getString("mediatype");} catch (JSONException e) {}
                    try { a.publisher = jsonasset.getString("publisher");} catch (JSONException e) {}
                    try { a.recordingDate = jsonasset.getString("recordingDate");} catch (JSONException e) {}
                    try { a.audioTranscodeFilename = jsonasset.getString("audioTranscodeFilename");} catch (JSONException e) {}
                    //try { a.source = jsonasset.getString("source");} catch (JSONException e) {}
                    try { a.sourcelabel = jsonasset.getString("sourcelabel");} catch (JSONException e) {}
                    try { a.title = jsonasset.getString("title");} catch (JSONException e) {}

                    if (a.audioTranscodeFilename != "") {
                        a.localMedia = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sharkattack" + File.separator + "currentshow" + File.separator + a.audioTranscodeFilename;
                    } else {
                        a.localMedia = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sharkattack" + File.separator + "currentshow" + File.separator + a.filename;
                    }

                } catch (JSONException e) {}

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Calendar assetDate = Calendar.getInstance();
                a.date = assetDate;
                playlist.add(a);
            }
        }

        listener.onPlaylistLoaded(playlist);
    }
}
