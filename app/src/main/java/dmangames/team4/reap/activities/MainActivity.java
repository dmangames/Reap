package dmangames.team4.reap.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dmangames.team4.reap.R;
import dmangames.team4.reap.ReapApplication;
import dmangames.team4.reap.fragments.TimerFragment;
import dmangames.team4.reap.util.SecondTimer;
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

        getFragmentManager()
                .beginTransaction()
                .add(R.id.fl_main_container, fragment)
                .commit();

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

    public EventBus bus() {
        return ((ReapApplication) getApplication()).bus();
    }

    public void postToBus(Object obj) {
        bus().post(obj);
    }
}
