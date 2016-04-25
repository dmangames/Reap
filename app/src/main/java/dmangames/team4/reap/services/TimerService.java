package dmangames.team4.reap.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dmangames.team4.reap.objects.DataObject;
import dmangames.team4.reap.util.SecondTimer;
import dmangames.team4.reap.util.SecondTimer.SecondListener;
import timber.log.Timber;
import dmangames.team4.reap.enums.Time;

/**
 * Created by stevenzhang on 4/15/16.
 */
public class TimerService extends Service implements SecondListener{

    private String activityName;
    private SecondTimer secondTimer;
    private int timeSpent;

    @Inject DataObject data;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("onStartCommand");
        super.onStartCommand(intent, flags, startId);
        activityName = intent.getExtras().getString("activityName");
        secondTimer = new SecondTimer(this);
        timeSpent = 0;
        secondTimer.setup(SecondTimer.Type.COUNT_UP, Time.COUNT_UP_SECS);
        secondTimer.start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public boolean stopService(Intent name) {
        Timber.d("stopService");
        secondTimer.stop();
        return super.stopService(name);
    }


    //TODO: when service is destroyed, write to DataObject
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(NOTIFICATION_SERVICE);
        intent.putExtra("activityName", activityName);
        intent.putExtra("timeSpent", timeSpent);
        Timber.d("TimeSpent: " + timeSpent);
        sendBroadcast(intent);
    }

    @Override
    public void onTimerTick(long secs) {
        Timber.d("Ticking...");
        timeSpent++;
    }

    @Override
    public void onTimerFinish() {
//        ActivityObject activityObject = data.getActivityByName(activityName);
//        activityObject.addTimeSpent(timeSpent);
        Intent intent = new Intent(NOTIFICATION_SERVICE);
        intent.putExtra("activityName", activityName);
        intent.putExtra("timeSpent", timeSpent);
        sendBroadcast(intent);
    }
}
