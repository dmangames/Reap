package dmangames.team4.reap.activities;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import dmangames.team4.reap.R;
import dmangames.team4.reap.fragments.CountUpFragment;
import dmangames.team4.reap.views.DrawerView;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.fl_main_container)
    FrameLayout container;
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
    }
}
