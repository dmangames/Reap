package dmangames.team4.reap.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;
import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.dialogs.CreateNewActivityDialog;
import dmangames.team4.reap.events.ChooseActivityObjectEvent;
import dmangames.team4.reap.objects.ActivityObject;

/**
 * Fragment allowing user to pick current activity
 * Created by stevenzhang on 3/23/16.
 */
@Layout(R.layout.fragment_choose_activity)
public class ChooseActivityFragment extends ReapFragment implements CreateNewActivityDialog.CreateNewActivityListener {
    EventBus bus;

    @Bind(R.id.guitar_activity) ImageButton guitar_activity_icon;
    @Bind(R.id.btn_new_activity) Button btn_new_activity;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        View view = super.onCreateView(inf, parent, savedInstanceState);
        bus = ((MainActivity) getActivity()).bus();

        guitar_activity_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new ChooseActivityObjectEvent(new ActivityObject("Guitar")));
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


    @OnClick(R.id.btn_new_activity) void newActivity() {
        CreateNewActivityDialog dialog = new CreateNewActivityDialog(getActivity(), this);
        dialog.show();
    }

    @Override
    public void createActivity(String name) {
        //TODO: put the new activity in the activity blob
        Log.e(tag(), "name= " + name);
        ActivityObject ao = new ActivityObject(name);
    }
}
