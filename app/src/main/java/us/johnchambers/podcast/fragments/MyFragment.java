package us.johnchambers.podcast.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.misc.FragmentBackstackType;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class MyFragment extends Fragment {


    public MyFragment() {}

    public abstract FragmentBackstackType getBackstackType();

}
