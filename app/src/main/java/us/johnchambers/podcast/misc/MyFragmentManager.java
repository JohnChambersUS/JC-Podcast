package us.johnchambers.podcast.misc;

/**
 * Created by johnchambers on 7/20/17.
 */

import android.content.Context;
import android.support.v4.app.FragmentManager;

import java.util.Stack;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.objects.FragmentBackstackType;
import us.johnchambers.podcast.screens.fragments.search.SearchFragment;
import us.johnchambers.podcast.screens.fragments.subscribe.SubscribeFragment;
import us.johnchambers.podcast.screens.fragments.subscribed.SubscribedFragment;
import us.johnchambers.podcast.objects.MyBackstackEntry;
import us.johnchambers.podcast.screens.fragments.search.SearchRow;

public class MyFragmentManager {

    FragmentManager _fragmentManager;
    Context _context;

    private final String SEARCH_FRAGMENT = "SEARCH_FRAGMENT";
    private final String SUBSCRIBE_FRAGMENT = "SUBSCRIBE_FRAGMENT";
    private final String SUBSCRIBED_FRAGMENT = "SUBSCRIBED_FRAGMENT";

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
        MyBackstackEntry topEntry = (MyBackstackEntry)_backstack.peek();
        if (topEntry.getFragmentBackstackType() == FragmentBackstackType.BRANCH) {
            _backstack.pop();
            _fragmentManager.beginTransaction().
                    remove(_fragmentManager.findFragmentByTag(topEntry.getFragmentTag()))
                    .commit();
        }
    }

    public void activateSearchFragment() {
        SearchFragment sr = SearchFragment.newInstance();
        _fragmentManager.beginTransaction()
            .add(R.id.your_placeholder, sr, SEARCH_FRAGMENT)
            .commit();
        addToBackstack(sr.getBackstackType(), SEARCH_FRAGMENT);
    }

    public void deactivateSearchFragment() {
        Boolean exists = (_fragmentManager.findFragmentByTag(SEARCH_FRAGMENT) != null);
        if (exists) {
            _fragmentManager.beginTransaction().hide(_fragmentManager.findFragmentByTag(SEARCH_FRAGMENT)).commit();
        }

    }

    public void activateSubscribeFragment(SearchRow sr) {
        SubscribeFragment sf = SubscribeFragment.newInstance(sr);
        _fragmentManager
            .beginTransaction()
            .add(R.id.subscribe_placeholder, sf, SUBSCRIBE_FRAGMENT)
            .commit();
        addToBackstack(sf.getBackstackType(), SUBSCRIBE_FRAGMENT);
    }

    public void activateSubscribedFragment() {
        SubscribedFragment sf = SubscribedFragment.newInstance();
        _fragmentManager
                .beginTransaction()
                .add(R.id.subscribed_placeholder, sf, SUBSCRIBED_FRAGMENT)
                .commit();
        addToBackstack(sf.getBackstackType(), SUBSCRIBED_FRAGMENT);
    }


}
