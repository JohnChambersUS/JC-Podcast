package us.johnchambers.podcast;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

