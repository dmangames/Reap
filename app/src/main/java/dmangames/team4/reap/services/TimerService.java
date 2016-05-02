package dmangames.team4.reap.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
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
    public class TimerBinder extends Binder {
        public long getTimeSpent() {
            return timeSpent;
        }

        public String getActivityName() {
            return activityName;
        }
    }

    private String activityName;
    private SecondTimer secondTimer;
    private boolean pomodoroBreak;
    private boolean invalidated;

    private static final int NOTIFICATION = 1;
    private static final int COMMON_NOTIFICATION = 2;

    public static final String CLOSE_ACTION = "close";
    public static final String OPEN_ACTION = "open";
    private NotificationManager mNotificationManager;
    private final NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this);
    private final NotificationCompat.Builder mCommonNotificationBuilder = buildNotificationCommon(this);

    private long timeSpent;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new TimerBinder();
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

        buildPersistantNotification();
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
                if (pomodoroBreak) {
                    secondTimer.setTotalSeconds(Time.POMODORO_BREAK_SECS);
                    mCommonNotificationBuilder.setSmallIcon(R.drawable.tomato);
                    mCommonNotificationBuilder.setContentTitle("Pomodoro Break Started!");
                    mCommonNotificationBuilder.setContentText("Take a break for " + Time.POMODORO_BREAK_SECS / 60 + " minutes!");
                    mCommonNotificationBuilder.setWhen(System.currentTimeMillis());
                    showCommonNotification(COMMON_NOTIFICATION);
                }
                else {
                    secondTimer.setTotalSeconds(Time.POMODORO_WORK_SECS);
                    mCommonNotificationBuilder.setSmallIcon(R.drawable.tomato);
                    mCommonNotificationBuilder.setContentTitle("Pomodoro Break Ended!");
                    mCommonNotificationBuilder.setContentText("Back to work!");
                    mCommonNotificationBuilder.setWhen(System.currentTimeMillis());
                    showCommonNotification(COMMON_NOTIFICATION);
                }
            }
            else{
                mCommonNotificationBuilder.setSmallIcon(R.drawable.stopwatch);
                mCommonNotificationBuilder.setContentTitle("Congrats");
                mCommonNotificationBuilder.setContentText("You worked for 1 hour!");
                mCommonNotificationBuilder.setWhen(System.currentTimeMillis());
                showCommonNotification(COMMON_NOTIFICATION);
            }

            secondTimer.reset();
            secondTimer.start();
        }
    }

    private void buildPersistantNotification(){
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
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
                .setTicker(getText(R.string.service_connected))
                .setContentText(getText(R.string.service_connected));
    }
    private void showPersistantNotification(int id) {
        if (mNotificationManager != null) {
            mNotificationManager.notify(id, mNotificationBuilder.build());
        }
    }

    private void showCommonNotification(int id) {
        if (mNotificationManager != null) {
            mNotificationManager.notify(id, mCommonNotificationBuilder.build());
        }
    }

    private static NotificationCompat.Builder buildNotificationCommon(Context _context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(_context);

        builder.setWhen(System.currentTimeMillis())
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

        //Vibration
        builder.setVibrate(new long[]{0, 1000, 1000, 1000, 1000 });

        //Ton
        //builder.setSound(Uri.parse("uri://sadfasdfasdf.mp3"));

        return builder;
    }
}
