package dmangames.team4.reap.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import static dmangames.team4.reap.util.SecondTimer.Type.COUNT_DOWN;

/**
 * Timer class implemented from {@link Timer Timer}. Calls a method on the UI thread
 * every second, and can accommodate count up or down modes.
 *
 * @author Brian Wang
 * @version 3/21/16
 */
public class SecondTimer {
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
    private final TimerTask upTask = new TimerTask() {
        @Override public void run() {
            if (current >= total) {
                handler.post(finish);
                cancel();
                return;
            }
            handler.post(tick);
            ++current;
            Log.d("Timer Task", "Counting");
        }
    }, downTask = new TimerTask() {
        @Override public void run() {
            if (current <= 0) {
                handler.post(finish);
                cancel();
                return;
            }
            handler.post(tick);
            --current;
        }
    };

    private Timer timer;

    private final LinkedList<SecondListener> listeners;
    private final Handler handler;

    private Type type;
    private long current;
    private long total;

    public SecondTimer(Type type, long seconds, SecondListener listener) {
        this.listeners = new LinkedList<>();
        handler = new Handler(Looper.getMainLooper());
        timer = new Timer();
        listeners.add(listener);
        setup(type, seconds);
    }

    public void setTotalSeconds(long seconds) {
        Log.d("Second Timer", "setTotalSeconds called");
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
        timer = new Timer();
        timer.scheduleAtFixedRate(type == COUNT_DOWN ? downTask : upTask, 0, 1000);
    }

    public void stop() {
        timer.cancel();
    }

    public Type getType() {
        return type;
    }

    public long getTotalSeconds() {
        return total;
    }

    public void setCurrentSeconds(long current) { this.current = current; }

    public enum Type {
        COUNT_UP(true), COUNT_DOWN(false);

        public final boolean id;

        Type(boolean id) {
            this.id = id;
        }
    }

    public interface SecondListener {
        void onTimerTick(long secs);

        void onTimerFinish();
    }
}
