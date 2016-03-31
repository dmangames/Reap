package dmangames.team4.reap.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.AnimatorRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import dmangames.team4.reap.R;
import dmangames.team4.reap.ReapApplication;
import dmangames.team4.reap.events.SwitchFragmentEvent;
import dmangames.team4.reap.fragments.ReapFragment;
import dmangames.team4.reap.fragments.TimerFragment;
import dmangames.team4.reap.views.DrawerView;
import dmangames.team4.reap.views.DrawerView.DrawerListener;
import dmangames.team4.reap.views.DrawerView.Option;

public class MainActivity extends AppCompatActivity implements DrawerListener {
    public static final String TAG = "MainActivity";

    @Bind(R.id.dv_main_drawer) DrawerView drawer;
    @Bind(R.id.dl_main_drawerlayout) DrawerLayout layout;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        TimerFragment fragment = TimerFragment.newInstance();
        switchFragment(fragment, true);

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

    @Subscribe public void receiveFragmentEvent(SwitchFragmentEvent event) {
        if (event.anim == null)
            switchFragment(event.fragment, event.backstack);
        else switchFragment(event.fragment, event.backstack, event.anim);
    }

    @Override public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() <= 1) {
            super.onBackPressed();
            return;
        }
        getFragmentManager().popBackStack();
    }

    @Override protected void onStart() {
        super.onStart();
        bus().register(this);
    }

    @Override protected void onStop() {
        super.onStop();
        bus().unregister(this);
    }
}
