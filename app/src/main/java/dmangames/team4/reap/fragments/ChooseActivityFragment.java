package dmangames.team4.reap.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import butterknife.Bind;
import butterknife.OnClick;
import dmangames.team4.reap.R;
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
    @Bind(R.id.guitar_activity) ImageButton guitar_activity_icon;
    @Bind(R.id.btn_new_activity) Button btn_new_activity;

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        guitar_activity_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.postSticky(new ChooseActivityObjectEvent(new ActivityObject("Guitar", R.drawable.classic_acoustic_guitar)));
                goBack();
            }

        });
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
        ActivityObject ao = new ActivityObject(name, 0);
    }
}
