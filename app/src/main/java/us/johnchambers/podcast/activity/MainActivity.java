package us.johnchambers.podcast.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import us.johnchambers.podcast.R;
import us.johnchambers.podcast.misc.MyFileManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        MyFileManager.getInstance(this).makeStorageDirectories();

        new CountDownTimer(2000, 1000) {
            public void onFinish() {
                Intent startActivity = new Intent(MainActivity.this, MainNavigationActivity.class);
                startActivity(startActivity);
                finish();
            }
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }



}

