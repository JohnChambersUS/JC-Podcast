package us.johnchambers.podcast.screens.fragments.subscribe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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

        String d = ser.getDateAsString();
        String[] dSplit1 = d.split(" ");
        String[] dSplit2 = dSplit1[0].split("-");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        cal.set(Calendar.YEAR, Integer.parseInt(dSplit2[0]));
        cal.set(Calendar.DAY_OF_YEAR, Integer.parseInt(dSplit2[1]));
        SimpleDateFormat format1;
        if ((year - 2000) == cal.get(Calendar.YEAR)) {
            format1 = new SimpleDateFormat("MMM dd");
        }
        else {
            format1 = new SimpleDateFormat("MMM dd, 20yy");
        }
        String formattedDate = format1.format(cal.getTime());
        date.setText(formattedDate);

        title.setText(ser.getTitle());

        return convertView;
    }
}

