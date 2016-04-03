package dmangames.team4.reap;

import android.app.Application;

import org.greenrobot.eventbus.EventBus;

import dmangames.team4.reap.objects.ActivityBlob;

/**
 * Created by brian on 3/25/16.
 */
public class ReapApplication extends Application {
    private EventBus bus;
    private ActivityBlob blob;

    @Override public void onCreate() {
        super.onCreate();

        bus = new EventBus();
        blob = new ActivityBlob();
    }

    public EventBus bus() {
        return bus;
    }

    public ActivityBlob blob() {
        return blob;
    }
}
