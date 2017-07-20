package us.johnchambers.podcast.objects;

import us.johnchambers.podcast.misc.FragmentBackstackType;

/**
 * Created by johnchambers on 7/20/17.
 */

public class MyBackstackEntry {

    private FragmentBackstackType _fragmentBackstackType;
    private String _fragmentTag;

    public MyBackstackEntry(FragmentBackstackType fragmentBackstackType, String fragmentTag) {
        _fragmentBackstackType = fragmentBackstackType;
        _fragmentTag = fragmentTag;
    }

    public FragmentBackstackType getFragmentBackstackType() {
        return _fragmentBackstackType;
    }

    public String getFragmentTag() {
        return _fragmentTag;
    }
}
