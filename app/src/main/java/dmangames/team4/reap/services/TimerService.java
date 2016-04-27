package dmangames.team4.reap.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import dmangames.team4.reap.enums.Time;
import dmangames.team4.reap.util.SecondTimer;
import dmangames.team4.reap.util.SecondTimer.SecondListener;
import timber.log.Timber;

import static dmangames.team4.reap.fragments.TimerFragment.KEY_TIMER_BREAK;
import static dmangames.team4.reap.objects.ActivityObject.KEY_ACTIVITYOBJ_NAME;
import static dmangames.team4.reap.objects.ActivityObject.KEY_ACTIVITYOBJ_SPENT;
import static dmangames.team4.reap.util.SecondTimer.Type.COUNT_DOWN;

/**
 * Created by stevenzhang on 4/15/16.
 */
public class TimerService extends Service implements SecondListener {

    private String activityName;
    private SecondTimer secondTimer;
    private boolean pomodoroBreak;
    private boolean invalidated;

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

        if (!intent.hasExtra(KEY_ACTIVITYOBJ_NAME)) {
            invalidated = true;
            stopSelf();
            return START_REDELIVER_INTENT;
        }

        invalidated = false;
        activityName = intent.getStringExtra(KEY_ACTIVITYOBJ_NAME);
        pomodoroBreak = intent.getBooleanExtra(KEY_TIMER_BREAK, false);
        secondTimer = new SecondTimer(this);
        secondTimer.unpack(intent);
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
        if (invalidated)
            return;
        Intent intent = new Intent(NOTIFICATION_SERVICE);
        intent.putExtra(KEY_ACTIVITYOBJ_NAME, activityName);
        intent.putExtra(KEY_ACTIVITYOBJ_SPENT, secondTimer.getSecondsElapsed());
        intent.putExtra(KEY_TIMER_BREAK, pomodoroBreak);
        secondTimer.pack(intent);
        Timber.d("TimeSpent: %d", secondTimer.getSecondsElapsed());
        sendBroadcast(intent);
    }

    @Override
    public void onTimerTick(long secs) {
    }

    @Override
    public void onTimerFinish() {
        //TODO: send a notification
        Timber.d(activityName);
        if (activityName.equals("Null"))
            stopSelf();
        else {
            if (secondTimer.getType() == COUNT_DOWN) {
                pomodoroBreak = !pomodoroBreak;
                if (pomodoroBreak)
                    secondTimer.setTotalSeconds(Time.POMODORO_BREAK_SECS);
                else
                    secondTimer.setTotalSeconds(Time.POMODORO_WORK_SECS);
            }

            secondTimer.reset();
            secondTimer.start();
        }
    }
}
