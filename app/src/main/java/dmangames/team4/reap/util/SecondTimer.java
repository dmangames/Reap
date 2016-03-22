package dmangames.team4.reap.util;

import android.os.Handler;
import android.os.Looper;

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
public class SecondTimer extends Timer {
    public final Runnable finish = new Runnable() {
        @Override public void run() {
            listener.onFinish();
        }
    }, tick = new Runnable() {
        @Override public void run() {
            listener.onTick(current);
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

    private final Handler handler;
    private final Type type;
    private final long total;
    private final SecondListener listener;

    private long current;

    public SecondTimer(Type type, long seconds, SecondListener listener) {
        this.type = type;
        this.listener = listener;
        total = seconds;
        current = type == COUNT_DOWN ? total : 0;
        handler = new Handler(Looper.getMainLooper());
    }

    public void start() {
        scheduleAtFixedRate(type == COUNT_DOWN ? downTask : upTask, 0, 1000);
    }

    public void pause() {
        cancel();
    }

    public long getTotalSeconds() {
        return total;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        COUNT_UP(true), COUNT_DOWN(false);

        public final boolean id;

        Type(boolean id) {
            this.id = id;
        }
    }

    public interface SecondListener {
        void onTick(long secs);

        void onFinish();
    }
}
