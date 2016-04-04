package dmangames.team4.reap;

import android.app.Application;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by brian on 3/25/16.
 */
public class ReapApplication extends Application {
    private EventBus bus;

    @Override public void onCreate() {
        super.onCreate();

        bus = new EventBus();
    }

    public EventBus bus() {
        return bus;
    }
}
