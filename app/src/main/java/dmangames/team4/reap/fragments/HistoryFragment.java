package dmangames.team4.reap.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.adapters.TodayListAdapter;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.objects.DataObject;
import timber.log.Timber;

import static dmangames.team4.reap.objects.DataObject.DATEFORMAT;

/**
 * Created by Andrew on 4/19/2016.
 */
@Layout(R.layout.fragment_today)
public class HistoryFragment extends ReapFragment {
    @Bind(R.id.spinner_date_range) Spinner dateSpinner;
    @Bind(R.id.spinner_specific_date) Spinner specificDateSpinner;
    @Bind(R.id.rv_today_activity_list) RecyclerView activityList;
    @Bind(R.id.tv_today_title) TextView title;

    @Inject DataObject data;

    private TodayListAdapter JarListadapter;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayAdapter<String> specificDateSpinnerAdapter;

    private static final String[] dateRanges = {"Today", "Week", "Month", "Year", "Specific Date"};
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

        final String today = DATEFORMAT.format(new Date());

        activityList.setLayoutManager(new LinearLayoutManager(activity));
        title.setText(R.string.history);

        spinnerAdapter = new ArrayAdapter<>(activity, R.layout.simple_spinner_item, dateRanges);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(spinnerAdapter);
        dateSpinner.setVisibility(View.VISIBLE);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String start;
                Calendar cal = Calendar.getInstance();
                boolean specific = false;

                switch ((String) dateSpinner.getSelectedItem()) {
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
                    case "Specific Date":
                        String[] specificDates = data.getHistory().keySet().toArray(new String[data.getHistory().size()]);
                        Arrays.sort(specificDates, new Comparator<String>() {
                            @Override public int compare(String lhs, String rhs) {
                                try {
                                    return -DATEFORMAT.parse(lhs).compareTo(DATEFORMAT.parse(rhs));
                                } catch (ParseException e) {
                                    Timber.e(e, "Parse exception while sorting array!");
                                }
                                return 0;
                            }
                        });
                        specificDateSpinnerAdapter = new ArrayAdapter<>(activity, R.layout.simple_spinner_item, specificDates);
                        specificDateSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                        specificDateSpinner.setAdapter(specificDateSpinnerAdapter);
                        specificDateSpinner.setVisibility(View.VISIBLE);
                        activityList.setVisibility(View.GONE);
                        specific = true;
                        break;

                }

                if (!specific) {
                    start = DATEFORMAT.format(cal.getTime());
                    JarListadapter = new TodayListAdapter(activity, data.aggregateHistoryRange(start, today));
                    activityList.setAdapter(JarListadapter);
                    activityList.setVisibility(View.VISIBLE);
                    specificDateSpinner.setVisibility(View.GONE);
                    Timber.d("date range: " + start + " - " + today);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                dateSpinner.setSelection(0);
            }

        });

        specificDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String start = (String) specificDateSpinner.getSelectedItem();
                JarListadapter = new TodayListAdapter(activity, data.aggregateHistoryRange(start, start));
                activityList.setAdapter(JarListadapter);
                activityList.setVisibility(View.VISIBLE);
                Timber.d("date range: " + start + " - " + today);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}
