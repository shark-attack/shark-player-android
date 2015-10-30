package com.sharkattack.views;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sharkattack.R;
import com.sharkattack.data.Asset;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Playlist Adapter
 */
public class PlaylistAdapter extends ArrayAdapter<Asset> {
    protected List<Asset> playlist;
    protected Context context;

    public PlaylistAdapter(List<Asset> playlist, Context ctx) {
        super(ctx, R.layout.playlist_item, playlist);
        this.playlist = playlist;
        this.context = ctx;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.playlist_item, parent, false);
        }

        Asset a = playlist.get(position);
        TextView songtitle = (TextView) convertView.findViewById(R.id.songtitle);
        TextView artist = (TextView) convertView.findViewById(R.id.artist);
        TextView src = (TextView) convertView.findViewById(R.id.source);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView duration = (TextView) convertView.findViewById(R.id.duration);

        Typeface boldTF = Typeface.createFromAsset(context.getAssets(), "fonts/Lato/Lato-Black.ttf");
        Typeface lightTF = Typeface.createFromAsset(context.getAssets(), "fonts/Lato/Lato-LightItalic.ttf");
        Typeface normalTF = Typeface.createFromAsset(context.getAssets(), "fonts/Lato/Lato-Regular.ttf");

        if (a.title != "" && a.artist != "") {
            songtitle.setText(a.title);
            songtitle.setTypeface(lightTF);

            artist.setText(a.artist);
            artist.setTypeface(boldTF);
            src.setText(Html.fromHtml("<a href='" + a.page + "'>" + a.sourcelabel + "</a>"));
        } else {
            artist.setText("Shark Attack");
            songtitle.setText(a.label);
            songtitle.setTypeface(lightTF);
            src.setText("Shark Attack");
        }

        src.setTypeface(normalTF);

        date.setText( (a.date.get(Calendar.MONTH) +1) + "/" + a.date.get(Calendar.DATE));

        Date dur = new Date(a.duration *1000 - a.timeElapsed *1000);
        SimpleDateFormat df = new SimpleDateFormat("m:ss");
        duration.setText(df.format(dur));
        duration.setTypeface(boldTF);

        /*Button btn = (Button) convertView.findViewById(R.id.play_button);
        btn.setOnClickListener(new OnPlaylistItemClickListener(a) {
            @Override
            public void onClick(View view) {
                PlaylistView pview = (PlaylistView) view.getParent().getParent();
                OnAssetSelectedListener pviewl = pview.getOnAssetSelectedListener();
                if (pviewl != null) {
                    pviewl.onAssetSelected(getAsset());
                }
            }
        });   */

        return convertView;
    }
}
