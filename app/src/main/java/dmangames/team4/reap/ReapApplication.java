package dmangames.team4.reap;

import android.app.Application;

import dmangames.team4.reap.dagger.DaggerInjector;
import timber.log.Timber;

/**
 * Created by brian on 3/25/16.
 */
public class ReapApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        DaggerInjector.newInstance(this);

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
    }
}
