package dmangames.team4.reap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.adapters.ActivityGridAdapter;
import dmangames.team4.reap.adapters.ActivityGridAdapter.ActivityGridListener;
import dmangames.team4.reap.annotations.HasInjections;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.dialogs.CreateNewActivityDialog;
import dmangames.team4.reap.dialogs.CreateNewActivityDialog.CreateNewActivityListener;
import dmangames.team4.reap.events.ActivityObjectChangedEvent;
import dmangames.team4.reap.events.ActivityObjectDeletedEvent;
import dmangames.team4.reap.events.ChooseActivityObjectEvent;
import dmangames.team4.reap.objects.ActivityObject;
import dmangames.team4.reap.objects.DataObject;
import timber.log.Timber;

/**
 * Fragment allowing user to pick current activity
 * Created by stevenzhang on 3/23/16.
 */
@Layout(R.layout.fragment_choose_activity)
public class ChooseActivityFragment extends ReapFragment
        implements CreateNewActivityListener, ActivityGridListener {
    @Bind(R.id.btn_new_activity) FloatingActionButton btn_new_activity;
    @Bind(R.id.rv_choose_grid) RecyclerView activityGrid;

    @Inject DataObject data;

    private ActivityGridAdapter adapter;

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        adapter = new ActivityGridAdapter(activity, this);

        activityGrid.setLayoutManager(new GridLayoutManager(activity, 3));
        activityGrid.setAdapter(adapter);
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
    public void createActivity(String name, String iconURL, int iconRes) {
        Timber.d("CreateActivity: name = %s", name);
        data.addNewActivity(name, iconRes);
        adapter.update();
    }

    @Override public void chooseActivity(ActivityObject object) {
        bus.postSticky(new ChooseActivityObjectEvent(object, false));
        goBack();
    }

    @Override public void activityChanged(ActivityObject object) {
        bus.postSticky(new ActivityObjectChangedEvent(object));
    }

    @Override public void activityDeleted(ActivityObject object) {
        bus.postSticky(new ActivityObjectDeletedEvent(object));
    }
}
