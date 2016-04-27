package dmangames.team4.reap.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dmangames.team4.reap.enums.Time;
import dmangames.team4.reap.objects.DataObject;
import dmangames.team4.reap.util.SecondTimer;
import dmangames.team4.reap.util.SecondTimer.SecondListener;
import timber.log.Timber;

/**
 * Created by stevenzhang on 4/15/16.
 */
public class TimerService extends Service implements SecondListener{

    private String activityName;
    private SecondTimer secondTimer;
    private long timeSpent;
    private SecondTimer.Type timerType;
    private boolean pomodoroBreak;
    private long currentSecs;

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
        Bundle bundle = intent.getExtras();
        activityName = bundle.getString("activityName");
        timerType = (SecondTimer.Type)bundle.get("timerType");
        pomodoroBreak = bundle.getBoolean("pomodoroBreak");
        currentSecs = bundle.getLong("currentSecs");
        secondTimer = new SecondTimer(this);
        timeSpent = 0;
        if(timerType == SecondTimer.Type.COUNT_UP)
            secondTimer.setup(SecondTimer.Type.COUNT_UP, Time.COUNT_UP_SECS);
        else if(timerType == SecondTimer.Type.COUNT_DOWN) {
            if(!pomodoroBreak)
                secondTimer.setup(SecondTimer.Type.COUNT_DOWN, Time.POMODORO_WORK_SECS);
            else
                secondTimer.setup(SecondTimer.Type.COUNT_DOWN, Time.POMODORO_BREAK_SECS);
        }
        secondTimer.setCurrentSeconds(currentSecs);
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
        intent.putExtra("currentSecs", currentSecs);
        intent.putExtra("timerType", timerType);
        intent.putExtra("pomodoroBreak", pomodoroBreak);
        Timber.d("TimeSpent: " + timeSpent);
        sendBroadcast(intent);
    }

    @Override
    public void onTimerTick(long secs) {
        currentSecs = secs;
        if(!pomodoroBreak)
            timeSpent++;
    }

    @Override
    public void onTimerFinish() {
        //TODO: send a notification
        Timber.d(activityName);
        if(activityName.equals("Null"))
            stopSelf();
        else {
            if (timerType == SecondTimer.Type.COUNT_DOWN) {
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
