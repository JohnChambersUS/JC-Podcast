package us.johnchambers.podcast.fragments;


import android.support.v4.app.Fragment;

import us.johnchambers.podcast.objects.FragmentBackstackType;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class MyFragment extends Fragment {


    public MyFragment() {}

    public abstract FragmentBackstackType getBackstackType();

}
