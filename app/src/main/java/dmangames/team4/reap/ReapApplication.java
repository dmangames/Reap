package dmangames.team4.reap;

import android.app.Application;

import com.squareup.picasso.Picasso;

import dmangames.team4.reap.dagger.DaggerInjector;
import dmangames.team4.reap.util.IconURIs;
import timber.log.Timber;

/**
 * Created by brian on 3/25/16.
 */
public class ReapApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        DaggerInjector.newInstance(this);
        IconURIs.newInstance(this);

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        // Initialize default Picasso instance.
        Picasso.with(this);
    }
}
