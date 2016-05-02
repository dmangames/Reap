package dmangames.team4.reap.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
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

    private static final int NOTIFICATION = 1;
    public static final String CLOSE_ACTION = "close";
    public static final String OPEN_ACTION = "open";
    private NotificationManager mNotificationManager;
    private final NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this);

    private long timeSpent;

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

        timeSpent = 0;
        invalidated = false;
        activityName = intent.getStringExtra(KEY_ACTIVITYOBJ_NAME);
        pomodoroBreak = intent.getBooleanExtra(KEY_TIMER_BREAK, false);
        secondTimer = new SecondTimer(this);
        secondTimer.unpack(intent);
        secondTimer.start();

        showPersistantNotification();

        return START_REDELIVER_INTENT;
    }

    @Override
    public boolean stopService(Intent name) {
        Timber.d("stopService");

        return super.stopService(name);
    }


    //TODO: when service is destroyed, write to DataObject
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (invalidated)
            return;

        secondTimer.stop();
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        mNotificationManager.cancel(NOTIFICATION);

        Intent intent = new Intent(NOTIFICATION_SERVICE);
        intent.putExtra(KEY_ACTIVITYOBJ_NAME, activityName);
        intent.putExtra(KEY_ACTIVITYOBJ_SPENT, secondTimer.getSecondsElapsed());
        intent.putExtra(KEY_TIMER_BREAK, pomodoroBreak);
        secondTimer.pack(intent);
        Timber.d("TimeSpent: %d", timeSpent);
        sendBroadcast(intent);

    }

    @Override
    public void onTimerTick(long secs) {
        timeSpent++;
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

    private void showPersistantNotification(){
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),0);

        PendingIntent pendingCloseIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .setAction(CLOSE_ACTION),
                0);

        mNotificationBuilder
                .setSmallIcon(R.drawable.ic_launcher)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(getText(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_view,
                        getString(R.string.action_open), pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                        getString(R.string.action_exit), pendingCloseIntent)
                .setOngoing(true);

        mNotificationBuilder
                .setTicker(getText(R.string.service_connected))
                .setContentText(getText(R.string.service_connected));

        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION, mNotificationBuilder.build());
        }
    }
}
