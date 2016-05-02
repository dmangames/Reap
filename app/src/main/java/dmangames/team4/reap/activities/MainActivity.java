package dmangames.team4.reap.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.AnimatorRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dmangames.team4.reap.R;
import dmangames.team4.reap.dagger.DaggerInjector;
import dmangames.team4.reap.events.SwitchFragmentEvent;
import dmangames.team4.reap.fragments.HistoryFragment;
import dmangames.team4.reap.fragments.ReapFragment;
import dmangames.team4.reap.fragments.TimerFragment;
import dmangames.team4.reap.fragments.TodayFragment;
import dmangames.team4.reap.objects.ActivityBlob;
import dmangames.team4.reap.objects.ActivityObject;
import dmangames.team4.reap.objects.DataObject;
import dmangames.team4.reap.services.TimerService;
import dmangames.team4.reap.services.TimerService.TimerBinder;
import dmangames.team4.reap.util.GsonWrapper;
import dmangames.team4.reap.views.DrawerView;
import dmangames.team4.reap.views.DrawerView.DrawerListener;
import dmangames.team4.reap.views.DrawerView.Option;
import timber.log.Timber;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity
        implements DrawerListener, ServiceConnection {
    @Bind(R.id.dv_main_drawer) DrawerView drawer;
    @Bind(R.id.dl_main_drawerlayout) DrawerLayout layout;
    @Bind(R.id.tb_main_toolbar) Toolbar toolbar;

    @Inject EventBus bus;
    @Inject DataObject data;
    @Inject ActivityBlob blob;

    private ActionBarDrawerToggle drawerToggle;
    private boolean singleTop = false;
    private boolean serviceBound = false;
    private TimerBinder timerBinder;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("TimerService broadcast received");
            if (intent.getExtras() != null) {
                Fragment fragment = getFragmentManager().findFragmentById(R.id.fl_main_container);
                if (fragment instanceof FragmentEventListener)
                    ((FragmentEventListener) fragment).unpackFromService(TimerService.class, intent);
                //Unregister receiver if exists
                try {
                    unregisterReceiver(receiver);
                } catch (IllegalArgumentException e) {
                    Timber.e(e, "Exception in BroadcastReceiver");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        DaggerInjector.inject(this);

        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, layout, toolbar, 0, 0);
        layout.addDrawerListener(drawerToggle);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
        drawerToggle.syncState();

        drawer.setListener(this);

        switchFragment(TimerFragment.newInstance(), false);

    }

    @Override
    public void switchTo(Option option) {
        switch (option) {
            case TODAY:
                bus.post(new SwitchFragmentEvent(TodayFragment.newInstance(), true, true));
                break;
            case HISTORY:
                bus.post(new SwitchFragmentEvent(HistoryFragment.newInstance(), true, true));
                break;
            case PIXEL_PORTRAITS:
                Toast.makeText(this, "To be implemented", LENGTH_SHORT).show();
                break;
            default:
                Timber.e("Unknown option in switchTo");
        }

        Timber.d("Switch to: %s", option.name());
        layout.closeDrawer(drawer);
    }

    protected void switchFragment(ReapFragment fragment, boolean singleTop,
                                  @AnimatorRes int... anim) {
        FragmentManager manager = getFragmentManager();
        if (this.singleTop) {
            if (manager.findFragmentById(R.id.fl_main_container).getTag().equals(fragment.tag()))
                return;
            manager.popBackStack();
        }
        this.singleTop = singleTop;
        FragmentTransaction transaction = manager.beginTransaction();
        switch (anim.length) {
            case 2:
                transaction.setCustomAnimations(anim[0], anim[1], anim[0], anim[1]);
                break;
            case 4:
                transaction.setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);
                break;
            default:
        }
        transaction.replace(R.id.fl_main_container, fragment, fragment.tag());
        transaction.addToBackStack(fragment.tag());
        transaction.commit();
    }

    public ActivityBlob blob() {
        return blob;
    }

    @Subscribe public void receiveFragmentEvent(SwitchFragmentEvent event) {
        if (event.anim == null)
            switchFragment(event.fragment, event.singleTop);
        else switchFragment(event.fragment, event.singleTop, event.anim);
    }

    /**
     * This method should be called instead of {@link MainActivity#onBackPressed()}. Emulates a
     * back button pressed, but governs the listener calls.
     *
     * @param programmatic If set false, this method will call the {@link FragmentEventListener}
     *                     of the current fragment, if the fragment has attached one.
     */
    public void goBack(boolean programmatic) {
        FragmentManager manager = getFragmentManager();
        if (!programmatic) {
            Fragment f = manager.findFragmentById(R.id.fl_main_container);
            if (f instanceof FragmentEventListener && ((FragmentEventListener) f).onBackPressed())
                return;
        }

        singleTop = false;
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
        Timber.d("onStart");
        super.onStart();
        bus.register(this);
        Intent intent = new Intent(this, TimerService.class);
        stopService(intent);
        Timber.d("onStartEnd");
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
        data.setRecentActivities(blob());
        GsonWrapper.commitData(data, getApplicationContext());

        //Register Receiver
        //TODO: I'll need to change the flag used here
        registerReceiver(receiver, new IntentFilter(NOTIFICATION_SERVICE));
        // Start timer service to keep track of time in background
        Intent intent = new Intent(this, TimerService.class);
        Fragment f = getFragmentManager().findFragmentById(R.id.fl_main_container);
        if (f instanceof FragmentEventListener)
            ((FragmentEventListener) f).packToService(TimerService.class, intent);
        startService(intent);
        bindService(intent, this, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        Timber.d("onDestroy");
        //Unregister receiver if exists
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            Timber.e(e, "Nonfatal error in onDestroy!");
        }

        super.onDestroy();
    }

    public interface FragmentEventListener {
        /**
         * Called if {@link MainActivity#goBack(boolean)} is called with false.
         *
         * @return If the back event has been consumed.
         */
        boolean onBackPressed();

        void packToService(Class service, Intent packIntent);

        void unpackFromService(Class service, Intent restoreIntent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case TimerService.CLOSE_ACTION:
                exit();
                break;
            case TimerService.OPEN_ACTION:
                break;
        }
    }

    @Override public void onServiceConnected(ComponentName name, IBinder service) {
        timerBinder = (TimerBinder) service;
        serviceBound = true;
    }

    @Override public void onServiceDisconnected(ComponentName name) {
        serviceBound = false;
        timerBinder = null;
    }

    private void exit() {
        ActivityObject object = data.getActivityByName(timerBinder.getActivityName());
        if (object == null)
            object = data.getBreakByName(timerBinder.getActivityName());
        object.addTimeSpent(timerBinder.getTimeSpent());

        unbindService(this);
        onServiceDisconnected(null);
        stopService(new Intent(this, TimerService.class));
        finish();
    }
}
