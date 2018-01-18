package us.johnchambers.podcast.activity;

//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.database.PodcastTable;
//todo delete import us.johnchambers.podcast.misc.PodcastDownloader;
import us.johnchambers.podcast.misc.MyPlayer;
import us.johnchambers.podcast.misc.PodcastUpdater;
import us.johnchambers.podcast.screens.fragments.about.AboutFragment;
import us.johnchambers.podcast.screens.fragments.search.SearchFragment;
import us.johnchambers.podcast.screens.fragments.subscribe.SubscribeFragment;
import us.johnchambers.podcast.screens.fragments.subscribed.SubscribedFragment;
import us.johnchambers.podcast.misc.MyFragmentManager;
import us.johnchambers.podcast.misc.VolleyQueue;
import us.johnchambers.podcast.screens.fragments.search.SearchRow;
import us.johnchambers.podcast.screens.fragments.subscribed_detail.SubscribedDetailFragment;
import us.johnchambers.podcast.screens.fragments.player.PlayerFragment;

public class MainNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchFragment.OnFragmentInteractionListener,
        SubscribeFragment.OnFragmentInteractionListener,
        SubscribedFragment.OnFragmentInteractionListener,
        SubscribedDetailFragment.OnFragmentInteractionListener,
        PlayerFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener {

    MyFragmentManager _myFragmentManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        VolleyQueue.getInstance(this); //inits volley queue
        _myFragmentManager = new MyFragmentManager(getSupportFragmentManager());
        PodcastDatabaseHelper.getInstance(getApplicationContext()); //init database helper
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed(); commented out to prevent app closure
        }
        _myFragmentManager.popBackstackEntry();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_navigation, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            activateSearchFragment();
        } else if (id == R.id.nav_show_subscribed) {
            _myFragmentManager.activateSubscribedFragment();
        } else if (id == R.id.nav_player) {
            _myFragmentManager.activatePlayerFragment(null);
        } else if (id == R.id.nav_update_podcasts) {
            Thread updaterThread = new Thread(new Runnable(){
                @Override
                public void run(){
                    PodcastUpdater pdu = new PodcastUpdater(getApplicationContext());
                }
            });
            updaterThread.start();
        } else if (id == R.id.nav_about) {
            _myFragmentManager.activateAboutFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void activateSearchFragment() {
        _myFragmentManager.activateSearchFragment();
    }

    public void activateSubscribeFragment(SearchRow sr) {
        _myFragmentManager.activateSubscribeFragment(sr);
    }

    //************************************************
    //* Interface implementations
    //***********************************************

    public void onSearchRowItemClicked(SearchRow sr) {
        activateSubscribeFragment(sr);
    }

    public void onCloseSubscribeFragment() {
        _myFragmentManager.popBackstackEntry();
    }

    public void onSubscribedFragmentRowItemClicked(PodcastTable pt) {
        _myFragmentManager.activateSubscribedDetailFragment(pt);
    }

    //play the stream
    public void onSubscribedDetailFragmentDoesSomething(String url) {
        _myFragmentManager.activatePlayerFragment(url);
    }

    public void onSubscribedDetailFragmentUnsubscribe() {
        _myFragmentManager.activateSubscribedFragment();
    }

    public void onPlayerFragmentDoesSomething() {}

    public void onAboutFragmentInteraction() {}
}
