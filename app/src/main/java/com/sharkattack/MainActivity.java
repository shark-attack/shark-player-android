package com.sharkattack;

import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sharkattack.audio.MediaEvent;
import com.sharkattack.audio.OnMediaListener;
import com.sharkattack.audio.PlaylistedAudioPlayer;
import com.sharkattack.data.Asset;
import com.sharkattack.data.OnShowDownloadListener;
import com.sharkattack.data.ShowDownloadEvent;
import com.sharkattack.data.ShowDownloader;
import com.sharkattack.data.OnPlaylistListener;
import com.sharkattack.views.PlaylistAdapter;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /** player */
    protected PlaylistedAudioPlayer player;

    /** show downloader */
    protected ShowDownloader show;

    /** adapter */
    protected PlaylistAdapter adapter;

    protected LinearLayout downloadStatusView;
    protected LinearLayout mediaControllerView;
    protected TextView downloadStatus;

    /**
     * initialize player and data
     */
    protected void initPlayer() {
        final AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        final AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    player.getPlayer().pause();
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    player.getPlayer().resume();
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    player.getPlayer().stop();
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    // Lower the volume
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    // Raise it back to normal
                }
            }
        };

        player = new PlaylistedAudioPlayer();
        final ImageButton playBtn = (ImageButton) findViewById(R.id.play_button);
        player.getPlayer().setOnMediaListener(new OnMediaListener() {
            @Override
            public void onMediaEvent(MediaEvent event) {
                switch (event.eventType) {
                    case MediaEvent.MEDIA_TIME_UPDATE:
                        if (player.current() != null) {
                            Asset a = adapter.getItem(player.currentIndex());
                            a.timeElapsed = event.playbackTime;
                            adapter.notifyDataSetChanged();
                        }
                        break;

                    case MediaEvent.MEDIA_PLAYING:
                        Log.v("Shark::" + this.getClass().toString(), "Media Playing");
                        playBtn.setBackgroundResource(R.drawable.pause_btn);
                        break;

                    case MediaEvent.MEDIA_PAUSE:
                        Log.v("Shark::" + this.getClass().toString(), "Media Pause");
                        playBtn.setBackgroundResource(R.drawable.play_btn);
                        break;

                    case MediaEvent.MEDIA_START:
                        Log.v("Shark::" + this.getClass().toString(), "Media Start");
                        playBtn.setBackgroundResource(R.drawable.buffering_btn);
                        break;

                    case MediaEvent.MEDIA_ERROR:
                        Log.v("Shark::" + this.getClass().toString(), "Media Error");
                        playBtn.setBackgroundResource(R.drawable.error_btn);
                        break;
                }
            }
        });

        player.setOnPlaylistListener(new OnPlaylistListener() {
            @Override
            public void onPlaylistLoaded(List<Asset> pls) {
                renderPlaylist(pls);
            }

            @Override
            public void onPlaylistFailure(String url) {
            }

            @Override
            public void onPlaylistAssetChange(Asset a) {
                ListView l = (ListView) findViewById(R.id.playlist);
                l.setItemChecked(player.getIndexOfAsset(a), true);
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.togglePlayback();
            }
        });

        File show = new File(Environment.getExternalStorageDirectory(), "sharkattack/currentshow/SAShow.json");
        player.load(show.getAbsolutePath());
    }

    /**
     * render playlist
     * @param pls
     */
    protected void renderPlaylist(List<Asset> pls) {
        adapter = new PlaylistAdapter(pls, this);
        final FragmentManager f = getFragmentManager();
        ListView l = (ListView) findViewById(R.id.playlist);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                player.setAssetIndex(i);
                adapterView.setSelection(i);
            }
        });
        l.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                player.setAssetIndex(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // set header label
        //TextView label = (TextView) findViewById(R.id.playlistLabel);
        //label.setText(pls.size() + " songs found at " + String.format("%d:%02d:%02d", player.getDuration()/3600, (player.getDuration()%3600)/60, (player.getDuration()%60)));

        l.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (player == null) {
            initPlayer();
        }

        downloadStatusView = (LinearLayout) findViewById(R.id.downloadstatus);
        mediaControllerView = (LinearLayout) findViewById(R.id.mediaController);
        downloadStatus = (TextView) findViewById(R.id.downloadstatustext);

        show = new ShowDownloader();
        show.setOnShowDownloadListener(new OnShowDownloadListener() {
            @Override
            public void onShowDownloadComplete(ShowDownloadEvent se) {
                Log.v("Shark::" + this.getClass().toString(), "Show Download Complete");
                File show = new File(Environment.getExternalStorageDirectory(), "sharkattack/currentshow/SAShow.json");
                player.load(show.getAbsolutePath());
                downloadStatusView.setVisibility(View.GONE);
                mediaControllerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onShowDownloadProgress(ShowDownloadEvent se) {
                Log.v("Shark::" + this.getClass().toString(), "Progress " + se.progress);
                if (se.mode == ShowDownloadEvent.DOWNLOADING) {
                    downloadStatus.setText("Fetching Show " + se.progress + "%");
                } else {
                    downloadStatus.setText("Unpacking Show");
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                break;

            case R.id.refresh_show:
                adapter.clear();
                downloadStatusView.setVisibility(View.VISIBLE);
                mediaControllerView.setVisibility(View.GONE);
                show.execute("http://bananastand:8080/api/shows?show=SAShow&download=true");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
