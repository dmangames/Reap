package dmangames.team4.reap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.events.ChooseActivityEvent;
import dmangames.team4.reap.events.ChooseFragmentEvent;

/**
 * Fragment allowing user to pick current activity
 * Created by stevenzhang on 3/23/16.
 */
@Layout(R.layout.fragment_choose_activity)
public class ChooseActivityFragment extends ReapFragment {

    EventBus bus;


    @Bind(R.id.guitar_activity) ImageButton guitar_activity_icon;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        View view = super.onCreateView(inf, parent, savedInstanceState);
        bus = ((MainActivity)getActivity()).bus();

        guitar_activity_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReapFragment fragment = TimerFragment.newInstance(TimerFragment.State.CHOOSE_TIMER, R.color.timer_blue, R.drawable.classic_acoustic_guitar, 0L);
                bus.post(new ChooseFragmentEvent(fragment));

            }

        });

        return view;
    }

    public static ChooseActivityFragment newInstance() {
        Bundle args = new Bundle(0);

        ChooseActivityFragment fragment = new ChooseActivityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //TODO: Placeholder subscribe, Eventbus throws errors if you register and don't have a subscribe
    @Subscribe
    public void onActivityChosen(ChooseActivityEvent event) {

    }


}
