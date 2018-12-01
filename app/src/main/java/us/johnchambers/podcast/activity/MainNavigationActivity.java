package us.johnchambers.podcast.activity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;

import us.johnchambers.podcast.Events.fragment.CloseSubscribeFragmentEvent;
import us.johnchambers.podcast.Events.fragment.CloseSubscribedDetailFragmentEvent;
import us.johnchambers.podcast.Events.fragment.OpenGenericPlaylistFragment;
import us.johnchambers.podcast.Events.fragment.OpenPodcastOptionsFragment;
import us.johnchambers.podcast.Events.fragment.OpenPodcatTagListFragmentEvent;
import us.johnchambers.podcast.Events.fragment.OpenSubscribeFragment;
import us.johnchambers.podcast.Events.fragment.OpenSubscribedDetailEvent;
import us.johnchambers.podcast.Events.fragment.OpenSubscribedFragmentEvent;
import us.johnchambers.podcast.Events.fragment.OpenTagAddToPodcastFragment;
import us.johnchambers.podcast.Events.fragment.RefreshManualPlaylistFragment;
import us.johnchambers.podcast.Events.fragment.RefreshSubscribedFragment;
import us.johnchambers.podcast.Events.fragment.SubscribedFragmentRowItemClickedEvent;
import us.johnchambers.podcast.Events.keys.AnyKeyEvent;
import us.johnchambers.podcast.Events.player.ClosePlayerEvent;
import us.johnchambers.podcast.Events.player.ResumePlaylistEvent;
import us.johnchambers.podcast.Events.service.UpdatePodcastsEvent;
import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.misc.Constants;
import us.johnchambers.podcast.objects.DocketEmpty;
import us.johnchambers.podcast.services.player.PlayerServiceController;
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
        implements NavigationView.OnNavigationItemSelectedListener {

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
        _myFragmentManager = new MyFragmentManager(getSupportFragmentManager(), toolbar);
        PodcastDatabaseHelper.getInstance(getApplicationContext()); //init database helper

        PlayerServiceController.getInstance(getApplicationContext()); //init player controller

        setUpdateAlarm();

        EventBus.getDefault().register(this);

        initDatabaseIfNeeded();

        //leave as last item
        drawer.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlayerServiceController.getInstance().stopService();
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancelAll();
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
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        EventBus.getDefault().post(new AnyKeyEvent());
        super.onKeyUp(keyCode, event);
        return true;
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
            _myFragmentManager.activateSubscribedFragment("root");
        } else if (id == R.id.nav_player) {
            _myFragmentManager.activatePlayerFragment(new DocketEmpty());
        } else if (id == R.id.nav_update_podcasts) {
            Intent intent = new Intent() ;
            intent.setClassName("us.johnchambers.podcast" ,
                    "us.johnchambers.podcast.services.updater.PodcastUpdateService") ;
            this.startService(intent);
        } else if (id == R.id.nav_about) {
            _myFragmentManager.activateAboutFragment();
        } else if (id == R.id.nav_latest_playlist) {
            _myFragmentManager.activateLatestPlaylistFragment();
        } else if (id == R.id.nav_manual_playlist) {
            _myFragmentManager.activateManualPlaylistFragment();
        }  else if (id == R.id.nav_global_options) {
            _myFragmentManager.activateGlobalOptionsFragment();
         } else if (id == R.id.nav_tags) {
            _myFragmentManager.activateTagFragment();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void activateSearchFragment() {
        _myFragmentManager.activateSearchFragment();
    }

    public void activateSubscribeFragment(SearchRow sr) {
        _myFragmentManager.activateSubscribeFragment(sr);
    }

    public void setUpdateAlarm() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Constants.UPDATE_HOUR);
        cal.set(Calendar.MINUTE, Constants.UPDATE_MINUTE);

        Intent intent = new Intent(this, us.johnchambers.podcast.services.updater.PodcastUpdateBroadcastReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(),
                Constants.UPDATE_INTERVAL,
                pendingIntent);
    }

    private void initDatabaseIfNeeded() {
        Integer count = PodcastDatabaseHelper.getInstance().getOptionsTableGlobalCount();
        if (count == 0) { //options needs to be initialized
            //add first global options
        }
    }

    //****************************
    //* Events
    //****************************
    @Subscribe
    public void onEvent(ClosePlayerEvent event) {
        _myFragmentManager.popBackstackEntry();
    }

    @Subscribe
    public void onEvent(ResumePlaylistEvent event) {
        _myFragmentManager.activatePlayerFragment(event.getDocketPackage());
    }

    @Subscribe
    public void onEvent(OpenSubscribedDetailEvent event) {
        _myFragmentManager.activateSubscribedDetailFragment(event.getPodcast());
    }

    @Subscribe
    public void onEvent(OpenPodcastOptionsFragment event) {
        _myFragmentManager.activatePoldcastOptionsFragment(event.getPodcastId());
    }

    @Subscribe
    public void onEvent(OpenSubscribeFragment event) {
        activateSubscribeFragment(event.getPodcastInfo());
    }

    @Subscribe
    public void onEvent(CloseSubscribeFragmentEvent event) {
        _myFragmentManager.popBackstackEntry();
    }

    @Subscribe
    public void onEvent(CloseSubscribedDetailFragmentEvent event) {
        _myFragmentManager.popBackstackEntry();
        _myFragmentManager.refreshSubscribedFragment();
    }

    @Subscribe
    public void onEvent(RefreshManualPlaylistFragment event) {
        _myFragmentManager.refreshManualPlaylistFragment();
    }

    @Subscribe
    public void onEvent(SubscribedFragmentRowItemClickedEvent event) {
        _myFragmentManager.activateSubscribedDetailFragment(event.getPodcastTable());
    }

    @Subscribe
    public void onEvent(OpenSubscribedFragmentEvent event) {
        _myFragmentManager.activateSubscribedFragment();
    }

    @Subscribe
    public void onEvent(OpenPodcatTagListFragmentEvent event) {
        _myFragmentManager.activatePodcatsTagListFragment(event.get_tag());
    }


    @Subscribe
    public void onEvent(OpenTagAddToPodcastFragment event) {
        _myFragmentManager.activateTagAddToPodcastFragment(event.getPid());
    }

    @Subscribe
    public void onEvent(OpenGenericPlaylistFragment event) {
        _myFragmentManager.activateGenericPlaylistFragment(event.get_tag());
    }


    @Subscribe
    public void onEvent(UpdatePodcastsEvent event) {
        Intent intent = new Intent() ;
        intent.setClassName("us.johnchambers.podcast" ,
                "us.johnchambers.podcast.services.updater.PodcastUpdateService") ;
        this.startService(intent);
    }

}

