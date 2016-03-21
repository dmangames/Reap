package dmangames.team4.reap.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import dmangames.team4.reap.R;
import dmangames.team4.reap.fragments.CountUpFragment;
import dmangames.team4.reap.views.DrawerView;
import dmangames.team4.reap.views.DrawerView.DrawerListener;
import dmangames.team4.reap.views.DrawerView.Option;

public class MainActivity extends AppCompatActivity implements DrawerListener {
    public static final String TAG = "MainActivity";

    @Bind(R.id.dv_main_drawer)
    DrawerView drawer;
    @Bind(R.id.dl_main_drawerlayout)
    DrawerLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.fl_main_container, new CountUpFragment())
                .commit();

        drawer.setListener(this);
    }

    @Override
    public void switchTo(Option option) {
        switch (option) {
            case TODAY:
                break;
            case HISTORY:
                break;
            case MILESTONES:
                break;
            case FRIENDS:
                break;
            case EXTRAS:
                break;
            default:
                Log.e(TAG, "Unknown option in switchTo");
        }

        layout.closeDrawer(drawer);
    }
}
