package us.johnchambers.podcast.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.misc.MyFileManager;

/**
 * Created by johnchambers on 8/13/17.
 */

public class SubscribedAdapter extends ArrayAdapter<PodcastTable> {

    private Context _context;


    public SubscribedAdapter(Context context) {
        super(context,
                R.layout.row_subscribed,
                R.id.subscribedListView,
                new ArrayList<PodcastTable>());
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PodcastTable currPodcast = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_subscribed, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.subscribedRowImage);
        Bitmap pcImage = MyFileManager.getInstance().getPodcastImage(currPodcast.getPid());
        //todo put handling if image is null


        image.setImageBitmap(pcImage);


        return convertView;
    }

}
