package us.johnchambers.podcast.misc;

/**
 * Created by johnchambers on 7/20/17.
 */

import android.content.Context;
import android.support.v4.app.FragmentManager;

import java.util.Stack;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.fragments.MyFragment;
import us.johnchambers.podcast.objects.FragmentBackstackType;
import us.johnchambers.podcast.screens.fragments.search.SearchFragment;
import us.johnchambers.podcast.screens.fragments.subscribe.SubscribeFragment;
import us.johnchambers.podcast.screens.fragments.subscribed.SubscribedFragment;
import us.johnchambers.podcast.objects.MyBackstackEntry;
import us.johnchambers.podcast.screens.fragments.search.SearchRow;
import us.johnchambers.podcast.screens.fragments.subscribed_detail.SubscribedDetailFragment;

public class MyFragmentManager {

    FragmentManager _fragmentManager;
    Context _context;

    private final String SEARCH_FRAGMENT = "SEARCH_FRAGMENT";
    private final String SUBSCRIBE_FRAGMENT = "SUBSCRIBE_FRAGMENT";
    private final String SUBSCRIBED_FRAGMENT = "SUBSCRIBED_FRAGMENT";
    private final String SUBSCRIBED_DETAIL_FRAGMENT = "SUBSCRIBED_DETAIL_FRAGMENT";

    private Stack _backstack = new Stack<MyBackstackEntry>();

    public MyFragmentManager(FragmentManager fragmentManager) {
        _fragmentManager = fragmentManager;
    }

    private void addToBackstack(FragmentBackstackType fragmentBackstackType, String fragmentName) {
        if (fragmentBackstackType == FragmentBackstackType.ROOT) {
            while (_backstack.size() > 0) {
                MyBackstackEntry currEntry = (MyBackstackEntry) _backstack.pop();
                _fragmentManager.beginTransaction().
                    remove(_fragmentManager.findFragmentByTag(currEntry.getFragmentTag()))
                    .commit();
            }
        }
        _backstack.push(new MyBackstackEntry(fragmentBackstackType, fragmentName));
    }

    public void popBackstackEntry() {
        if (_backstack.size() > 0) {
            MyBackstackEntry topEntry = (MyBackstackEntry) _backstack.peek();
            if (topEntry.getFragmentBackstackType() == FragmentBackstackType.BRANCH) {
                _backstack.pop();
                _fragmentManager.beginTransaction().
                        remove(_fragmentManager.findFragmentByTag(topEntry.getFragmentTag()))
                        .commit();
            }
        }
    }

    private void activateFragment(int containerViewId, MyFragment frag, String fragmentName) {
        _fragmentManager
                .beginTransaction()
                .add(containerViewId, frag, fragmentName)
                .commit();
        addToBackstack(frag.getBackstackType(), fragmentName);
    }

    public void activateSearchFragment() {
        activateFragment(R.id.search_placeholder,
                SearchFragment.newInstance(),
                SEARCH_FRAGMENT);
    }

    public void activateSubscribeFragment(SearchRow sr) {
        activateFragment(R.id.subscribe_placeholder,
                SubscribeFragment.newInstance(sr),
                SUBSCRIBE_FRAGMENT);
    }

    public void activateSubscribedFragment() {
        activateFragment(R.id.subscribed_placeholder,
                SubscribedFragment.newInstance(),
                SUBSCRIBED_FRAGMENT);
    }

    public void activateSubscribedDetailFragment(PodcastTable pt) {
        activateFragment(R.id.subscribed_detail_placeholder,
                SubscribedDetailFragment.newInstance(pt),
                SUBSCRIBED_DETAIL_FRAGMENT);
    }



}
