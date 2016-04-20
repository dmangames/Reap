package dmangames.team4.reap.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.adapters.TodayListAdapter;
import dmangames.team4.reap.annotations.HasInjections;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.objects.DataObject;
import timber.log.Timber;

/**
 * Created by Andrew on 4/19/2016.
 */
@HasInjections
@Layout(R.layout.fragment_today)
public class HistoryFragment extends ReapFragment {
    @Bind(R.id.spinner_date_range) Spinner dateSpinner;
    @Bind(R.id.rv_today_activity_list) RecyclerView activityList;

    @Inject DataObject data;

    private TodayListAdapter JarListadapter;
    private ArrayAdapter<String> spinnerAdapter;

    private static final String[] dateRanges = {"Today", "Week", "Month", "Year"};
//
//    private static final Map<String, Integer> rangeValues = new HashMap<String, Integer>() {{
//        put("Today", 0);
//        put("Week", 6);
//        put("Month", 30);
//        put("Year", 365);
//    }};

    public static HistoryFragment newInstance() {
        Bundle args = new Bundle(0);

        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final MainActivity activity = (MainActivity) getActivity();

        final String today = DataObject.DATEFORMAT.format(new Date());

//        JarListadapter = new TodayListAdapter(activity, data.aggregateHistoryRange(today, today));
        activityList.setLayoutManager(new LinearLayoutManager(activity));
//        activityList.setAdapter(JarListadapter);

        spinnerAdapter = new ArrayAdapter<>(activity, R.layout.simple_spinner_item, dateRanges);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(spinnerAdapter);
        dateSpinner.setVisibility(View.VISIBLE);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String start;
                Calendar cal = Calendar.getInstance();

                switch((String)dateSpinner.getSelectedItem()){
                    case "Today":
                        break;
                    case "Week":
                        cal.add(Calendar.DATE, -6);
                        break;
                    case "Month":
                        cal.add(Calendar.MONTH, -1);
                        break;
                    case "Year":
                        cal.add(Calendar.YEAR, -1);
                        break;
                }

                start = DataObject.DATEFORMAT.format(cal.getTime());
                JarListadapter = new TodayListAdapter(activity, data.aggregateHistoryRange(start,today));
                activityList.setAdapter(JarListadapter);

                Timber.d("date range: "+start + " - " + today);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                dateSpinner.setSelection(0);
            }

        });

    }

}
