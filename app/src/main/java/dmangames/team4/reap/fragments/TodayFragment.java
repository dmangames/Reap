package dmangames.team4.reap.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import javax.inject.Inject;

import butterknife.Bind;
import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.adapters.TodayListAdapter;
import dmangames.team4.reap.annotations.HasInjections;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.objects.ActivityBlob;

/**
 * Created by Andrew on 4/19/2016.
 */
@Layout(R.layout.fragment_today)
public class TodayFragment extends ReapFragment {
    @Bind(R.id.rv_today_activity_list) RecyclerView activityList;

    @Inject ActivityBlob blob;

    private TodayListAdapter adapter;

    public static TodayFragment newInstance() {
        Bundle args = new Bundle(0);

        TodayFragment fragment = new TodayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        adapter = new TodayListAdapter(activity, blob);

        activityList.setLayoutManager(new LinearLayoutManager(activity));
        activityList.setAdapter(adapter);
    }

}
