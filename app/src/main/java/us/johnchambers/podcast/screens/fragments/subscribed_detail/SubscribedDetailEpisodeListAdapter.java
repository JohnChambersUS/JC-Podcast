package us.johnchambers.podcast.screens.fragments.subscribed_detail;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.EpisodeTable;

/**
 * Created by johnchambers on 8/19/17.
 */

public class SubscribedDetailEpisodeListAdapter extends ArrayAdapter<EpisodeTable> {


    private Context _context;
    private ViewGroup _parentView;

    public SubscribedDetailEpisodeListAdapter(Context context) {
        super(context,
                R.layout.row_subscribed_detail,
                R.id.subscribed_detail_episode_list_view,
                new ArrayList<EpisodeTable>());
        _context = context;
    }

    public EpisodeTable headerListGetItem(int pos) {
        return getItem(pos - 1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        _parentView = parent;
        EpisodeTable episode = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_subscribed_detail, parent, false);
        }

        TextView date = (TextView) convertView.findViewById(R.id.row_subscribed_episode_detail_date);
        TextView title = (TextView) convertView.findViewById(R.id.row_subscribed_detail_episode_title);

        String d = episode.getPubDate();
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

        String t = episode.getTitle();

        title.setText(t.toString());

        setProgress(convertView, episode);

        return convertView;
    }

    private void setProgress(View convertView, EpisodeTable episodeInfo) {

        GradientDrawable left = new GradientDrawable();
        left.setShape(GradientDrawable.RECTANGLE);
        left.setColor(_context.getResources().getColor(R.color.semiLightBackground));

        GradientDrawable right = new GradientDrawable();
        right.setShape(GradientDrawable.RECTANGLE);
        right.setColor(_context.getResources().getColor(R.color.lightBackground));

        GradientDrawable[] ar = new GradientDrawable[] {left, right};
        LayerDrawable layer = new LayerDrawable(ar);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)_context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        long playPoint = episodeInfo.getPlayPointAsLong();
        long length = episodeInfo.getLengthAsLong();

        float ratio = 0;
        if (playPoint != 0) {
            if (playPoint >= length) {
                ratio = 1;
            } else {
                ratio = (((float)playPoint) / length);
            }
        }

        int size = Math.round(width * ratio);

        layer.setLayerInset(1, size, 0, 0, 0);

        convertView.setBackground(layer);
    }


}
