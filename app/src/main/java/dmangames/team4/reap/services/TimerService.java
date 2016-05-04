package dmangames.team4.reap.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.enums.Time;
import dmangames.team4.reap.util.SecondTimer;
import dmangames.team4.reap.util.SecondTimer.SecondListener;
import timber.log.Timber;

import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.support.v7.app.NotificationCompat.VISIBILITY_PRIVATE;
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
    private static final int COMMON_NOTIFICATION = 2;

    public static final String CLOSE_ACTION = "close";
    public static final String OPEN_ACTION = "open";
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationCompat.Builder mCommonNotificationBuilder;

    private long timeSpent;
    private long breakMins;

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
        breakMins = TimeUnit.SECONDS.toMinutes(Time.POMODORO_BREAK_SECS);
        invalidated = false;
        activityName = intent.getStringExtra(KEY_ACTIVITYOBJ_NAME);
        pomodoroBreak = intent.getBooleanExtra(KEY_TIMER_BREAK, false);
        secondTimer = new SecondTimer(this);
        secondTimer.unpack(intent);
        secondTimer.start();

        mCommonNotificationBuilder = buildNotificationCommon(this);

        buildPersistentNotification();
        showPersistantNotification(NOTIFICATION);

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
        mNotificationManager.cancelAll();

        Intent intent = new Intent(NOTIFICATION_SERVICE);
        intent.putExtra(KEY_ACTIVITYOBJ_NAME, activityName);
        intent.putExtra(KEY_ACTIVITYOBJ_SPENT, timeSpent);
        intent.putExtra(KEY_TIMER_BREAK, pomodoroBreak);
        secondTimer.pack(intent);
        Timber.d("TimeSpent: %d", timeSpent);
        sendBroadcast(intent);

    }

    @Override
    public void onTimerTick(long secs) {
        if (!pomodoroBreak)
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
                if (pomodoroBreak) {
                    secondTimer.setTotalSeconds(Time.POMODORO_BREAK_SECS);
                    showCommonNotification(R.drawable.tomato, "Pomodoro Break Started!",
                            String.format(Locale.US, "Take a break for %d minutes!", breakMins));
                } else {
                    secondTimer.setTotalSeconds(Time.POMODORO_WORK_SECS);
                    showCommonNotification(R.drawable.tomato,
                            "Pomodoro Break Ended!", "Back to work!");
                }
            } else
                showCommonNotification(R.drawable.stopwatch, "Congrats", "You worked for 1 hour!");

            secondTimer.reset();
            secondTimer.start();
        }
    }

    private void buildPersistentNotification() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        mNotificationBuilder = new NotificationCompat.Builder(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

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
                .addAction(R.drawable.ic_open,
                        getString(R.string.action_open), pendingIntent)
                .addAction(R.drawable.ic_close,
                        getString(R.string.action_exit), pendingCloseIntent)
                .setOngoing(true)
                .setTicker("Current Activity: " + activityName)
                .setContentText("Current Activity: " + activityName);
    }

    private void showPersistantNotification(int id) {
        if (mNotificationManager != null) {
            mNotificationManager.notify(id, mNotificationBuilder.build());
        }
    }

    private void showCommonNotification(@DrawableRes int icon, String title, String text) {
        if (mNotificationManager != null) {
            mCommonNotificationBuilder.setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setWhen(System.currentTimeMillis());
            mNotificationManager.notify(COMMON_NOTIFICATION, mCommonNotificationBuilder.build());
        }
    }

    private static NotificationCompat.Builder buildNotificationCommon(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setWhen(System.currentTimeMillis())
                .setVisibility(VISIBILITY_PRIVATE)
                .setVibrate(new long[]{0, 1000})
                .setSound(RingtoneManager.getDefaultUri(TYPE_NOTIFICATION));
        return builder;
    }
}
