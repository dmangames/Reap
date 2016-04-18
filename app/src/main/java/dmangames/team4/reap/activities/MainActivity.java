package dmangames.team4.reap.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.AnimatorRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import dmangames.team4.reap.R;
import dmangames.team4.reap.ReapApplication;
import dmangames.team4.reap.events.SwitchFragmentEvent;
import dmangames.team4.reap.fragments.ReapFragment;
import dmangames.team4.reap.fragments.TimerFragment;
import dmangames.team4.reap.objects.ActivityBlob;
import dmangames.team4.reap.objects.DataObject;
import dmangames.team4.reap.util.GsonWrapper;
import dmangames.team4.reap.views.DrawerView;
import dmangames.team4.reap.views.DrawerView.DrawerListener;
import dmangames.team4.reap.views.DrawerView.Option;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity
        implements DrawerListener {

    public static final String TAG = "MainActivity";

    @Bind(R.id.dv_main_drawer) DrawerView drawer;
    @Bind(R.id.dl_main_drawerlayout) DrawerLayout layout;
    @Bind(R.id.tb_main_toolbar) Toolbar toolbar;

    private ActionBarDrawerToggle drawerToggle;

    public DataObject data;
    public ActivityBlob blob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Create data object
        data = GsonWrapper.getDataObject(getApplicationContext());

        TimerFragment fragment = TimerFragment.newInstance();
        switchFragment(fragment, true);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy"); //Whatever date format we decide on
        String formatted = format1.format(cal.getTime());

        if (data == null) {
            data = new DataObject("Steven", formatted);
        }

        data.newDay(formatted);
        blob = data.getRecentActivities();
        Log.d("blob", blob.size() + "");

        //Add breaks
        data.addNewBreak("sleep", R.drawable.bed);
        data.addNewBreak("restroom", R.drawable.restroom);
        data.addNewBreak("social", R.drawable.social);
        data.addNewBreak("eat", R.drawable.hamburger);
        data.addNewBreak("play", R.drawable.game);

        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, layout, toolbar, 0, 0);
        layout.addDrawerListener(drawerToggle);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
        drawerToggle.syncState();

        drawer.setListener(this);
    }

    @Override
    public void switchTo(Option option) {
        switch (option) {
            case TODAY:
                Log.d(TAG, "Switch to: Today");
                break;
            case HISTORY:
                Log.d(TAG, "Switch to: History");
                break;
            case MILESTONES:
                Log.d(TAG, "Switch to: Milestones");
                break;
            case FRIENDS:
                Log.d(TAG, "Switch to: Friends");
                break;
            case EXTRAS:
                Log.d(TAG, "Switch to: Extras");
                break;
            default:
                Log.e(TAG, "Unknown option in switchTo");
        }

        Toast.makeText(this, "To be implemented", LENGTH_SHORT).show();
        layout.closeDrawer(drawer);
    }


    protected void switchFragment(ReapFragment fragment, boolean addToBackStack,
                                  @AnimatorRes int... anim) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        switch (anim.length) {
            case 2:
                transaction.setCustomAnimations(anim[0], anim[1], anim[0], anim[1]);
                break;
            case 4:
                transaction.setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);
                break;
            default:
        }
        transaction.replace(R.id.fl_main_container, fragment);
        if (addToBackStack)
            transaction.addToBackStack(fragment.tag());
        transaction.commit();
    }

    public EventBus bus() {
        return ((ReapApplication) getApplication()).bus();
    }

    public void postToBus(Object obj) {
        bus().post(obj);
    }

    public ActivityBlob blob() {
        return blob;
    }

    @Subscribe public void receiveFragmentEvent(SwitchFragmentEvent event) {
        if (event.anim == null)
            switchFragment(event.fragment, event.backstack);
        else switchFragment(event.fragment, event.backstack, event.anim);
    }

    /**
     * This method should be called instead of {@link MainActivity#onBackPressed()}. Emulates a
     * back button pressed, but governs the listener calls.
     *
     * @param programmatic If set false, this method will call the {@link BackButtonListener}
     *                     of the current fragment, if the fragment has attached one.
     */
    public void goBack(boolean programmatic) {
        FragmentManager manager = getFragmentManager();
        if (!programmatic) {
            Fragment f = manager.findFragmentById(R.id.fl_main_container);
            if (f instanceof BackButtonListener && ((BackButtonListener) f).onBackPressed())
                return;
        }

        if (manager.getBackStackEntryCount() <= 1) {
            super.onBackPressed();
            return;
        }
        manager.popBackStack();
    }

    /**
     * This method should only be called by the system. Use {@link MainActivity#goBack(boolean)}
     * instead.
     */
    @Override public void onBackPressed() {
        goBack(false);
    }

    @Override protected void onStart() {
        super.onStart();
        bus().register(this);
    }

    @Override protected void onStop() {
        super.onStop();
        bus().unregister(this);
        data.setRecentActivities(blob());
        GsonWrapper.commitData(data, getApplicationContext());
    }

    public interface BackButtonListener {
        /**
         * Called if {@link MainActivity#goBack(boolean)} is called with false.
         *
         * @return If the back event has been consumed.
         */
        boolean onBackPressed();
    }
}
