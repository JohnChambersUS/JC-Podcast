package us.johnchambers.podcast.misc;

/**
 * Created by johnchambers on 7/20/17.
 */

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import java.util.Stack;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.fragments.MyFragment;
import us.johnchambers.podcast.objects.Docket;
import us.johnchambers.podcast.objects.FragmentBackstackType;
import us.johnchambers.podcast.screens.fragments.about.AboutFragment;
import us.johnchambers.podcast.screens.fragments.options.GlobalOptionsFragment;
import us.johnchambers.podcast.screens.fragments.options.PodcastOptionsFragment;
import us.johnchambers.podcast.screens.fragments.player.PlayerFragment;
import us.johnchambers.podcast.screens.fragments.playlist_generic.GenericPlaylistFragment;
import us.johnchambers.podcast.screens.fragments.playlist_latest.LatestPlaylistFragment;
import us.johnchambers.podcast.screens.fragments.playlist_manual.ManualPlaylistFragment;
import us.johnchambers.podcast.screens.fragments.search.SearchFragment;
import us.johnchambers.podcast.screens.fragments.subscribe.SubscribeFragment;
import us.johnchambers.podcast.screens.fragments.subscribed.SubscribedFragment;
import us.johnchambers.podcast.objects.MyBackstackEntry;
import us.johnchambers.podcast.screens.fragments.search.SearchRow;
import us.johnchambers.podcast.screens.fragments.subscribed_detail.SubscribedDetailFragment;
import us.johnchambers.podcast.screens.fragments.tag.tag_add_to_podcast_fragment.TagAddToPodcastFragment;
import us.johnchambers.podcast.screens.fragments.tag.tag_fragment.TagFragment;
import us.johnchambers.podcast.screens.fragments.tag.tag_podcast_list.TagPodcastListFragment;

public class MyFragmentManager {

    FragmentManager _fragmentManager;
    Toolbar _toolbar;

    private final String SEARCH_FRAGMENT = "SEARCH_FRAGMENT";
    private final String SUBSCRIBE_FRAGMENT = "SUBSCRIBE_FRAGMENT";
    private final String SUBSCRIBED_FRAGMENT = "SUBSCRIBED_FRAGMENT";
    private final String SUBSCRIBED_DETAIL_FRAGMENT = "SUBSCRIBED_DETAIL_FRAGMENT";
    private final String PLAYER_FRAGMENT = "PLAYER_FRAGMENT";
    private final String ABOUT_FRAGMENT = "ABOUT_FRAGMENT";
    private final String LATEST_PLAYLIST_FRAGMENT = "LATEST_PLAYLIST_FRAGMENT";
    private final String MANUAL_PLAYLIST_FRAGMENT = "MANUAL_PLAYLIST_FRAGMENT";
    private final String GLOBAL_OPTIONS_FRAGMENT = "GLOBAL_OPTIONS_FRAGMENT";
    private final String PODCAST_OPTIONS_FRAGMENT = "PODCAST_OPTIONS_FRAGMENT";
    private final String TAG_FRAGMENT = "TAG_FRAGMENT";
    private final String PODCAST_TAG_LIST_FRAGMENT = "PODCAST_TAG_LIST_FRAGMENT";
    private final String TAG_ADD_TO_PODCAST_FRAGMENT = "TAG_ADD_TO_PODCAST_FRAGMENT";
    private final String GENERIC_PLAYLIST_FRAGMENT = "GENERIC_PLAYLIST_FRAGMENT";


    private Stack _backstack = new Stack<MyBackstackEntry>();

    public MyFragmentManager(FragmentManager fragmentManager, Toolbar toolbar) {
        _fragmentManager = fragmentManager;
        _toolbar = toolbar;
    }

    private void addToBackstack(FragmentBackstackType fragmentBackstackType,
                                String fragmentName, String fragmentTitle) {
        if (fragmentBackstackType == FragmentBackstackType.ROOT) {
            while (_backstack.size() > 0) {
                MyBackstackEntry currEntry = (MyBackstackEntry) _backstack.pop();
                _fragmentManager.beginTransaction().
                    remove(_fragmentManager.findFragmentByTag(currEntry.getFragmentTag()))
                    .commit();
            }
        }
        _backstack.push(new MyBackstackEntry(fragmentBackstackType, fragmentName, fragmentTitle));
        updateAppBarTitle();
    }

    public void popBackstackEntry() {
        if (_backstack.size() > 0) {
            MyBackstackEntry topEntry = (MyBackstackEntry) _backstack.peek();
            _backstack.pop();
            _fragmentManager.beginTransaction().
                    remove(_fragmentManager.findFragmentByTag(topEntry.getFragmentTag()))
                    .commit();
        }
        updateAppBarTitle();
    }

    private void closeInfoFragments() {
        if (_backstack.size() > 0) {
            MyBackstackEntry topEntry = (MyBackstackEntry) _backstack.peek();
            if ((topEntry.getFragmentTag() == ABOUT_FRAGMENT) ||
                    (topEntry.getFragmentTag() == GLOBAL_OPTIONS_FRAGMENT)) {
                popBackstackEntry();
            }
        }
    }

    private void activateFragment(int containerViewId, MyFragment frag, String fragmentName, String fragmentTitle) {
        closeInfoFragments();
        _fragmentManager
                .beginTransaction()
                .add(containerViewId, frag, fragmentName)
                .commit();
        _fragmentManager.beginTransaction().show(frag).commit();
        addToBackstack(frag.getBackstackType(), fragmentName, fragmentTitle);
    }

    public void activateSearchFragment() {
        if (!alreadyOnTop(SEARCH_FRAGMENT)) {
            activateFragment(R.id.search_placeholder,
                    SearchFragment.newInstance(),
                    SEARCH_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.search_fragment_title));
        }
    }

    public void activateSubscribeFragment(SearchRow sr) {
        if (!alreadyOnTop(SUBSCRIBE_FRAGMENT)) {
            activateFragment(R.id.subscribe_placeholder,
                    SubscribeFragment.newInstance(sr),
                    SUBSCRIBE_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.subscribe_fragment_title));
        }
    }

    public void activateSubscribedFragment() {
        if (!alreadyOnTop(SUBSCRIBED_FRAGMENT)) {
            activateFragment(R.id.subscribed_placeholder,
                    SubscribedFragment.newInstance(),
                    SUBSCRIBED_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.subscribed_fragment_title));
        }
    }

    public void activateSubscribedFragment(String root) {
        if (!alreadyOnTop(SUBSCRIBED_FRAGMENT)) {
            MyFragment fragment = SubscribedFragment.newInstance();
            fragment.setBackstackType(FragmentBackstackType.ROOT);
            activateFragment(R.id.subscribed_placeholder,
                    fragment,
                    SUBSCRIBED_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.subscribed_fragment_title));
        }
    }

    public void refreshSubscribedFragment() {
        if (alreadyOnTop(SUBSCRIBED_FRAGMENT)) {
            popBackstackEntry();
            activateFragment(R.id.subscribed_placeholder,
                    SubscribedFragment.newInstance(),
                    SUBSCRIBED_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.subscribed_fragment_title));
        }
    }

    public void refreshManualPlaylistFragment() {
        if (alreadyOnTop(MANUAL_PLAYLIST_FRAGMENT)) {
            popBackstackEntry();
            activateFragment(R.id.manual_playlist_placeholder,
                    ManualPlaylistFragment.newInstance(),
                    MANUAL_PLAYLIST_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.manual_playlist_fragment_title));
        }
    }


    public void activateSubscribedDetailFragment(PodcastTable pt) {
        if (!alreadyOnTop(SUBSCRIBED_DETAIL_FRAGMENT)) {
            activateFragment(R.id.subscribed_detail_placeholder,
                    SubscribedDetailFragment.newInstance(pt),
                    SUBSCRIBED_DETAIL_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.subscribed_detail_fragment_title));
        }
    }

    public void activatePlayerFragment(Docket docket) {
        if (!alreadyOnTop(PLAYER_FRAGMENT)) {
            PlayerFragment p = PlayerFragment.newInstance(docket);
            activateFragment(R.id.player_placeholder,
                    p,
                    PLAYER_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.player_fragment_title));
        }
    }

    public void activateAboutFragment() {
        if (!alreadyOnTop(ABOUT_FRAGMENT)) {
            AboutFragment fragment = AboutFragment.newInstance();
            activateFragment(R.id.about_placeholder,
                    fragment,
                    ABOUT_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.about_fragment_title));
        }
    }

    public void activateLatestPlaylistFragment() {
        if (!alreadyOnTop(LATEST_PLAYLIST_FRAGMENT)) {
            LatestPlaylistFragment fragment = LatestPlaylistFragment.newInstance();
            activateFragment(R.id.latest_playlist_placeholder,
                    fragment,
                    LATEST_PLAYLIST_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.latest_playlist_fragment_title));
        }
    }

    public void activateManualPlaylistFragment() {
        if (!alreadyOnTop(MANUAL_PLAYLIST_FRAGMENT)) {
            ManualPlaylistFragment fragment = ManualPlaylistFragment.newInstance();
            activateFragment(R.id.manual_playlist_placeholder,
                    fragment,
                    MANUAL_PLAYLIST_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.manual_playlist_fragment_title));
        }
    }

    public void activateGlobalOptionsFragment() {
        if (!alreadyOnTop(GLOBAL_OPTIONS_FRAGMENT)) {
            GlobalOptionsFragment fragment = GlobalOptionsFragment.newInstance();
            activateFragment(R.id.global_options_placeholder,
                    fragment,
                    GLOBAL_OPTIONS_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.global__options_fragment_title));
        }
    }

    public void activatePoldcastOptionsFragment(String podcastId) {
        if (!alreadyOnTop(PODCAST_OPTIONS_FRAGMENT)) {
            PodcastOptionsFragment fragment = PodcastOptionsFragment.newInstance(podcastId);
            activateFragment(R.id.podcast_options_placeholder,
                    fragment,
                    PODCAST_OPTIONS_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.podcast_options_fragment_title));
        }
    }


    public void activateTagFragment() {
        if (!alreadyOnTop(TAG_FRAGMENT)) {
            TagFragment fragment = TagFragment.newInstance();
            activateFragment(R.id.root_placeholder,
                    fragment,
                    TAG_FRAGMENT,
                    _toolbar.getContext().getResources().getString(R.string.tag_title));
        }
    }


    public void activatePodcatsTagListFragment(String tag) {
        if (!alreadyOnTop(PODCAST_TAG_LIST_FRAGMENT)) {
            TagPodcastListFragment fragment = TagPodcastListFragment.newInstance();
            fragment.setTag(tag);
            activateFragment(R.id.podcast_tag_list_placeholder,
                    fragment,
                    PODCAST_TAG_LIST_FRAGMENT,
                    tag);
        }
    }

    public void activateTagAddToPodcastFragment(String pid) {
        if (!alreadyOnTop(TAG_ADD_TO_PODCAST_FRAGMENT)) {
            TagAddToPodcastFragment fragment = TagAddToPodcastFragment.newInstance();
            fragment.setPid(pid);
            activateFragment(R.id.tag_add_to_podcastplaceholder,
                    fragment,
                    TAG_ADD_TO_PODCAST_FRAGMENT,
                    "Tag The Podcast");
        }
    }

    public void activateGenericPlaylistFragment(String tag) {
        if (!alreadyOnTop(GENERIC_PLAYLIST_FRAGMENT)) {
            GenericPlaylistFragment fragment = GenericPlaylistFragment.newInstance(tag);
            activateFragment(R.id.generic_playlist_fragment_placeholder,
                    fragment,
                    GENERIC_PLAYLIST_FRAGMENT,
                    tag); //todo change tag info
        }
    }


    private boolean alreadyOnTop(String fragmentName) {
        if (_backstack.size() == 0) {
            return false;
        }

        MyBackstackEntry top = (MyBackstackEntry) _backstack.peek();
        if (top.getFragmentTag().equals(fragmentName)) {
            return true;
        }

        return false;
    }

    private void updateAppBarTitle() {

        if (_backstack.empty()) {
            _toolbar.setTitle(_toolbar.getContext().getResources().getString(R.string.default_fragment_title));
        }
        else {
            MyBackstackEntry topEntry = (MyBackstackEntry) _backstack.peek();
            _toolbar.setTitle(topEntry.getTitle());
        }
    }
}
