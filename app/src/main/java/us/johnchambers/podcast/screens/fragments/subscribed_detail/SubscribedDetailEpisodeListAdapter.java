package us.johnchambers.podcast.screens.fragments.subscribed_detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.database.PodcastTable;

/**
 * Created by johnchambers on 8/19/17.
 */

public class SubscribedDetailEpisodeListAdapter extends ArrayAdapter<EpisodeTable> {


    private Context _context;

    public SubscribedDetailEpisodeListAdapter(Context context) {
        super(context,
                R.layout.row_subscribed_detail,
                R.id.subscribed_detail_episode_list_view,
                new ArrayList<EpisodeTable>());
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        EpisodeTable episode = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_subscribed_detail, parent, false);
        }

        TextView date = (TextView) convertView.findViewById(R.id.row_subscribed_episode_detail_date);
        TextView title = (TextView) convertView.findViewById(R.id.row_subscribed_detail_episode_title);
        ImageView status = (ImageView) convertView.findViewById(R.id.row_subscribed_detail_episode_status);
        String d = episode.getPubDate();
        date.setText(d.toString());
        String t = episode.getTitle();
        title.setText(t.toString());

        if (episode.getDownloadedToDeviceBoolean()) {
            status.setImageDrawable(_context.getDrawable(R.drawable.ic_play));
        }
        else {
            status.setImageDrawable(_context.getDrawable(R.drawable.ic_download));
        }

        return convertView;
    }

}