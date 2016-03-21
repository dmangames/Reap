package dmangames.team4.reap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import butterknife.Bind;
import dmangames.team4.reap.R;
import dmangames.team4.reap.annotations.Layout;

/**
 * Created by brian on 3/21/16.
 */
@Layout(R.layout.fragment_countup)
public class CountUpFragment extends ReapFragment {

    @Bind(R.id.cm_countup_timer)
    Chronometer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        timer.start();
        return view;
    }
}
