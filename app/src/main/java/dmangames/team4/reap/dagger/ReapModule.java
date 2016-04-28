package dmangames.team4.reap.dagger;

import android.content.Context;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.adapters.ActivityGridAdapter;
import dmangames.team4.reap.adapters.BreakGridAdapter;
import dmangames.team4.reap.adapters.ReapAdapter;
import dmangames.team4.reap.fragments.ChooseActivityFragment;
import dmangames.team4.reap.fragments.HistoryFragment;
import dmangames.team4.reap.fragments.PixelPortraitsFragment;
import dmangames.team4.reap.fragments.ReapFragment;
import dmangames.team4.reap.fragments.TimerFragment;
import dmangames.team4.reap.fragments.TodayFragment;
import dmangames.team4.reap.objects.ActivityBlob;
import dmangames.team4.reap.objects.DataObject;
import dmangames.team4.reap.objects.PixelPortrait;
import dmangames.team4.reap.util.GsonWrapper;
import timber.log.Timber;

/**
 * Created by brian on 4/20/16.
 */
@Module(
        injects = {
                // Activities
                MainActivity.class,

                // Fragments
                ReapFragment.class,
                ChooseActivityFragment.class,
                HistoryFragment.class,
                PixelPortraitsFragment.class,
                TimerFragment.class,
                TodayFragment.class,

                // Adapters
                ReapAdapter.class,
                ActivityGridAdapter.class,
                BreakGridAdapter.class,

                // Objects
                PixelPortrait.class,
        }
)
public class ReapModule {
    private final Context appContext;
    private final String today;
    private final boolean mockData = false;

    public ReapModule(Context context) {
        this.appContext = context;
        today = DataObject.DATEFORMAT.format(new Date());
    }

    @Provides Context provideApplicationContext() {
        return appContext;
    }

    @Provides @Singleton DataObject provideDataObject(Context context) {

        DataObject data;

        if(!mockData) {
            Timber.d("No mock");
            data = GsonWrapper.getDataObject(context);
            if (data == null)
                data = new DataObject("Steven", today);

            //Add breaks
            data.addNewBreak("sleep", R.drawable.bed);
            data.addNewBreak("restroom", R.drawable.restroom);
            data.addNewBreak("social", R.drawable.social);
            data.addNewBreak("eat", R.drawable.hamburger);
            data.addNewBreak("play", R.drawable.game);
        }
        else{
            Timber.d("Mock!");
            data = GsonWrapper.mockData(context);
        }

        return data;
    }

    @Provides @Singleton EventBus provideBus() {
        return new EventBus();
    }

    @Provides @Singleton ActivityBlob provideRecentBlob(DataObject data) {
        data.newDay(today);
        ActivityBlob blob = data.getRecentActivities();
        blob.removeNulls();
        return blob;
    }
}
