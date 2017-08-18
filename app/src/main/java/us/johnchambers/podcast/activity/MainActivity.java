package us.johnchambers.podcast.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.misc.MyFileManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyFileManager.getInstance(this).makeStorageDirectories();

        new CountDownTimer(3000, 1000) {
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

