package us.johnchambers.podcast.fragments;


import android.support.v4.app.Fragment;

import us.johnchambers.podcast.objects.FragmentBackstackType;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class MyFragment extends Fragment {

    private FragmentBackstackType _fragmentBackstackType = FragmentBackstackType.BRANCH;

    public MyFragment() {}

    public void setBackstackType(FragmentBackstackType type) {
        _fragmentBackstackType = type;
    }

    public abstract FragmentBackstackType getBackstackType();

}
