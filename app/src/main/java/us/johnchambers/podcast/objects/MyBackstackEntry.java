package us.johnchambers.podcast.objects;

/**
 * Created by johnchambers on 7/20/17.
 */

public class MyBackstackEntry {

    private FragmentBackstackType _fragmentBackstackType;
    private String _fragmentTag;
    private String _title;

    public MyBackstackEntry(FragmentBackstackType fragmentBackstackType, String fragmentTag, String title) {
        _fragmentBackstackType = fragmentBackstackType;
        _fragmentTag = fragmentTag;
        _title = title;
    }

    public FragmentBackstackType getFragmentBackstackType() {
        return _fragmentBackstackType;
    }

    public String getFragmentTag() {
        return _fragmentTag;
    }

    public String getTitle() { return _title;}
}
