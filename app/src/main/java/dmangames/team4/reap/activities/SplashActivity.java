package dmangames.team4.reap.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Reap Splash Screen displays before app is loaded
 * Created by stevenzhang on 3/24/16.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
        if (isFirstStart) {
            SharedPreferences.Editor e = getPrefs.edit();
            e.putBoolean("firstStart", false);
            e.apply();
            Intent i = new Intent(this, IntroActivity.class);
            startActivity(i);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
