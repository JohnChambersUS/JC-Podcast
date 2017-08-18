package us.johnchambers.podcast.screens.fragments.search;

/**
 * Created by johnchambers on 7/15/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import us.johnchambers.podcast.R;

//import us.johnchambers.podcast.R;
//import us.johnchambers.jcpodcast.misc.MyViewManager;
//import us.johnchambers.jcpodcast.objects.SearchRow;

public class SearchDisplayAdapter extends ArrayAdapter<SearchRow> {

    private Context _context;

    public SearchDisplayAdapter(Context context) {
        super(context,
                R.layout.row_search_result,
                R.id.searchResultListView,
                new ArrayList<SearchRow>());
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SearchRow sr = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_search_result, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.searchResultRowImage);
        TextView desc = (TextView) convertView.findViewById(R.id.searchResultRowTitle);
        TextView url = (TextView) convertView.findViewById(R.id.searchResultRowFeedUrl);

        image.setImageBitmap(sr.getImage());
        desc.setText(sr.getTitle());
        url.setText(sr.getFeedUrl());

        return convertView;
    }
}

