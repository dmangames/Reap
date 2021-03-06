package dmangames.team4.reap.util;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

import static dmangames.team4.reap.util.SecondTimer.Type.COUNT_DOWN;
import static dmangames.team4.reap.util.SecondTimer.Type.COUNT_UP;

/**
 * Timer class implemented from {@link Timer Timer}. Calls a method on the UI thread
 * every second, and can accommodate count up or down modes.
 *
 * @author Brian Wang
 * @version 3/21/16
 */
public class SecondTimer {
    public static final String KEY_TIMER_BUNDLE = "timer.bundle";
    private static final String KEY_TIMER_CURRENT = "timer.current";
    private static final String KEY_TIMER_TOTAL = "timer.total";
    private static final String KEY_TIMER_TYPE = "timer.type";

    public final Runnable finish = new Runnable() {
        @Override public void run() {
            for (SecondListener l : listeners)
                l.onTimerFinish();
        }
    }, tick = new Runnable() {
        @Override public void run() {
            for (SecondListener l : listeners)
                l.onTimerTick(current);
        }
    };

    private Timer timer;

    private final LinkedList<SecondListener> listeners;
    private final Handler handler;

    private Type type;
    private long current;
    private long total;

    public SecondTimer(Type type, long seconds, SecondListener listener) {
        this(listener);
        setup(type, seconds);
    }

    public SecondTimer(SecondListener listener) {
        this.listeners = new LinkedList<>();
        this.handler = new Handler(Looper.getMainLooper());
        timer = new Timer();
        listeners.add(listener);
    }

    public void setTotalSeconds(long seconds) {
        Timber.d("setTotalSeconds called");
        total = seconds;
        reset();
    }

    public void setup(Type type, long seconds) {
        this.type = type;
        setTotalSeconds(seconds);
    }

    public void addListener(SecondListener l) {
        listeners.add(l);
    }

    public void reset() {
        current = type == COUNT_DOWN ? total : 0;
    }

    public void start() {
        Timber.d("start");
        if (timer != null)
            stop();
        timer = new Timer();
        timer.scheduleAtFixedRate(type == COUNT_DOWN ? new DownTask() : new UpTask(), 0, 1000);
    }

    public void stop() {
        timer.cancel();
        timer.purge();
    }

    public Type getType() {
        return type;
    }

    public long getTotalSeconds() {
        return total;
    }

    public Intent pack(Intent inputIntent) {
        Bundle args = new Bundle(3);
        args.putLong(KEY_TIMER_CURRENT, current);
        args.putLong(KEY_TIMER_TOTAL, total);
        args.putBoolean(KEY_TIMER_TYPE, type.id);
        inputIntent.putExtra(KEY_TIMER_BUNDLE, args);
        return inputIntent;
    }

    public void unpack(Intent restoreIntent) {
        if (!restoreIntent.hasExtra(KEY_TIMER_BUNDLE)) {
            Timber.e(new InputMismatchException("Intent passed to unpack() does not " +
                            "have extra by key KEY_TIMER_BUNDLE!"), "Error in %s.unpack()!",
                    getClass().getSimpleName());
            return;
        }

        Bundle args = restoreIntent.getBundleExtra(KEY_TIMER_BUNDLE);
        setup(Type.fromId(args.getBoolean(KEY_TIMER_TYPE)), args.getLong(KEY_TIMER_TOTAL));
        current = args.getLong(KEY_TIMER_CURRENT);
    }

    public void setCurrentSeconds(long current) {
        this.current = current;
    }

    public long getCurrentSeconds() {
        return current;
    }

    public void addToCurrentSeconds(long add) {
        this.current += add;
    }

    public long getSecondsElapsed() {
        return type == COUNT_UP ? current : total - current;
    }

    public enum Type {
        COUNT_UP(true), COUNT_DOWN(false);

        public final boolean id;

        Type(boolean id) {
            this.id = id;
        }

        static Type fromId(boolean id) {
            return id == COUNT_UP.id ? COUNT_UP : COUNT_DOWN;
        }
    }

    public interface SecondListener {
        void onTimerTick(long secs);

        void onTimerFinish();
    }

    private class UpTask extends TimerTask {
        @Override public void run() {
            if (current >= total) {
                handler.post(finish);
                cancel();
                return;
            }
            handler.post(tick);
            ++current;
        }
    }

    private class DownTask extends TimerTask {
        @Override public void run() {
            if (current <= 0) {
                handler.post(finish);
                cancel();
                return;
            }
            handler.post(tick);
            --current;
        }
    }
}
