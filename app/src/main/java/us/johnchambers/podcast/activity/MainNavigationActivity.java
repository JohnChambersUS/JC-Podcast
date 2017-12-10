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
        PlayerFragment.OnFragmentInteractionListener {

    //FragmentManager fragmentManager = null;
    MyFragmentManager _myFragmentManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();
        //    }
        //});

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        VolleyQueue.getInstance(this); //inits volley queue
        _myFragmentManager = new MyFragmentManager(getSupportFragmentManager());
        PodcastDatabaseHelper.getInstance(getApplicationContext()); //init database helper
        activteDownloadBroadcastReciever();
        //todo delete PodcastDownloader.getInstance(getApplicationContext()).wake(); //init and clean queue
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
        _myFragmentManager.popBackstackEntry();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            Toast.makeText(getApplicationContext(), "Pressed Search", Toast.LENGTH_LONG).show();
            activateSearchFragment();
        } else if (id == R.id.nav_show_subscribed) {
            _myFragmentManager.activateSubscribedFragment();
        } else if (id == R.id.nav_player) {
            _myFragmentManager.activatePlayerFragment(null);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
/*
    public android.support.v4.app.FragmentManager getCurrentFragmentManager() {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        return fragmentManager;
    }
*/
    public void activateSearchFragment() {
        //FragmentManager fm = getCurrentFragmentManager();
        //add search fragment and activate
        //SearchFragment sr = SearchFragment.newInstance();
        //android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        //transaction.add(R.id.your_placeholder, sr, "SEARCH_FRAGMENT");
        //transaction.commit();
        //transaction.show(sr).commit();
        _myFragmentManager.activateSearchFragment();
    }

    public void deactivateSearchFragment() {
        //getCurrentFragmentManager();
        //android.support.v4.app.Fragment exists = fragmentManager.findFragmentByTag("SEARCH_FRAGMENT");
        //fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("SEARCH_FRAGMENT")).commit();
        //_myFragmentManager.deactivateSearchFragment();
    }

    public void activateSubscribeFragment(SearchRow sr) {
        //deactivateSearchFragment();
        //getCurrentFragmentManager()
        //        .beginTransaction()
         //       .add(R.id.subscribe_placeholder, SubscribeFragment.newInstance(sr), "SUBSCRIBE_FRAGMENT")
         //       .commit();
        _myFragmentManager.activateSubscribeFragment(sr);
    }

    //**********************************************
    //* Broadcast receiver
    //**********************************************

    //todo delete broadcast receiver
    public void activteDownloadBroadcastReciever() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //do something with reference
                //PodcastDownloader.getInstance(getApplicationContext()).wake();
            }
        };
        registerReceiver(receiver, filter);
    }


    //************************************************
    //* Interface implementations
    //***********************************************

    public void onSearchRowItemClicked(SearchRow sr) {
        Toast.makeText(getApplicationContext(), "on search row clicked in parent", Toast.LENGTH_SHORT).show();
        //todo make call for subscribe panel
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

    public void onPlayerFragmentDoesSomething() {}
}
