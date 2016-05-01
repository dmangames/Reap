package dmangames.team4.reap.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import dmangames.team4.reap.R;

/**
 * Created by Andrew on 5/1/2016.
 */
public class IntroActivity extends AppIntro2 {

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        int sandColor = ContextCompat.getColor(getApplicationContext(), R.color.sand);
        addSlide(AppIntroFragment.newInstance("Welcome to Reap", "Keep track of your life", R.drawable.reap_icon_splash, sandColor, Color.BLACK, Color.BLACK));
        addSlide(AppIntroFragment.newInstance("Custom Activities", "Create activities specific to your needs", R.drawable.icons, sandColor, Color.BLACK, Color.BLACK));
        addSlide(AppIntroFragment.newInstance("Timers", "Use either a simple timer or the Pomodoro timer", R.drawable.timers, sandColor, Color.BLACK, Color.BLACK));
        addSlide(AppIntroFragment.newInstance("Progress", "Track your progress for each activity", R.drawable.jars, sandColor, Color.BLACK, Color.BLACK));
        addSlide(AppIntroFragment.newInstance("You are all set. Enjoy Reap.", "Get Productive!", R.drawable.check, sandColor, Color.BLACK, Color.BLACK));
        setIndicatorColor(Color.WHITE, Color.GRAY);
    }

    @Override
    public void onDonePressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onSlideChanged() {

    }
}
