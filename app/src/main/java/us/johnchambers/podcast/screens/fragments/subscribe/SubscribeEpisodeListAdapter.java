package us.johnchambers.podcast.screens.fragments.subscribe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import us.johnchambers.podcast.R;

/**
 * Created by johnchambers on 7/18/17.
 */


public class SubscribeEpisodeListAdapter extends ArrayAdapter<SubscribeEpisodeRow> {

    private Context _context;

    public SubscribeEpisodeListAdapter(Context context) {
        super(context,
                R.layout.row_subscribe_episode,
                R.id.subscribeEpisodeListView,
                new ArrayList<SubscribeEpisodeRow>());
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SubscribeEpisodeRow ser = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_subscribe_episode, parent, false);
        }

        TextView date = (TextView) convertView.findViewById(R.id.subscribeEpisodeRowDate);
        TextView title = (TextView) convertView.findViewById(R.id.subscribeEpisodeRowTitle);

        date.setText(ser.getDateAsString());
        title.setText(ser.getTitle());

        return convertView;
    }
}

