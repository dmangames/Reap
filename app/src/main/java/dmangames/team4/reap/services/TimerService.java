package dmangames.team4.reap.services;

import android.app.IntentService;
import android.content.Intent;

import dmangames.team4.reap.util.SecondTimer;
import dmangames.team4.reap.util.SecondTimer.SecondListener;
/**
 * Created by stevenzhang on 4/15/16.
 */
public class TimerService extends IntentService implements SecondListener{

    private String activityName;
    private SecondTimer secondTimer;
    private int timeSpent;

    public TimerService(){
        super("TimerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        activityName = intent.getExtras().getString("activityName");
        secondTimer = new SecondTimer(this);
        timeSpent = 0;
        secondTimer.start();

    }

    @Override
    public void onTimerTick(long secs) {
        timeSpent++;
    }

    @Override
    public void onTimerFinish() {

    }
}
