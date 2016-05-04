package dmangames.team4.reap.dialogs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnItemSelected;
import dmangames.team4.reap.R;
import dmangames.team4.reap.annotations.HasInjections;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.objects.ActivityBlob;
import dmangames.team4.reap.objects.ActivityObject;
import dmangames.team4.reap.objects.DataObject;
import timber.log.Timber;

import static java.lang.Long.parseLong;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Created by brian on 5/4/16.
 */
@HasInjections
@Layout(R.layout.dialog_edit_time)
public class EditTimeDialog extends ReapDialog {
    @Bind(R.id.sp_edittime_spinner) Spinner mode;
    @Bind(R.id.et_edittime_hours) EditText hours;
    @Bind(R.id.et_edittime_mins) EditText minutes;

    @Inject DataObject data;

    private boolean addTime;
    private final Context context;
    private final ActivityObject activity;

    private static String[] spinnerStrings;
    private static final int[] spinnerIds = {
            R.string.add,
            R.string.subtract
    };

    public EditTimeDialog(Context context, ActivityObject activity) {
        super(context);
        if (spinnerStrings == null) {
            spinnerStrings = new String[spinnerIds.length];
            for (int k = 0; k < spinnerIds.length; k++)
                spinnerStrings[k] = context.getString(spinnerIds[k]);
        }

        mode.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, spinnerStrings));
        mode.setSelection(0);
        modeSelected(0);

        this.context = context;
        this.activity = activity;

        setTitle(R.string.edit_time);
        setPositiveButton(R.string.ok, new OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                showDatePicker();
            }
        });
        setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @OnItemSelected(R.id.sp_edittime_spinner) void modeSelected(int position) {
        switch (spinnerIds[position]) {
            case R.string.add:
                addTime = true;
                break;
            case R.string.subtract:
                addTime = false;
                break;
            default:
                Timber.e("Error: unknown selection state!");
        }
    }

    public void showDatePicker() {
        final Calendar cal = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String name = activity.getActivityName();

                long secs = TimeUnit.HOURS.toSeconds(getSafeNum(hours)) +
                        TimeUnit.MINUTES.toSeconds(getSafeNum(minutes));

                cal.set(YEAR, year);
                cal.set(MONTH, monthOfYear);
                cal.set(DAY_OF_MONTH, dayOfMonth);
                String formattedDate = DataObject.DATEFORMAT.format(cal.getTime());
                if (!data.blobExists(formattedDate))
                    data.createBlob(formattedDate);
                ActivityBlob blob = data.getActivityBlobByDate(formattedDate);
                ActivityObject obj = blob.getActivity(name);
                if (obj == null) {
                    obj = new ActivityObject(name, activity.getIconURL());
                    blob.addActivity(obj);
                }
                long spent = obj.getTimeSpent();
                if (!addTime && spent < secs)
                    obj.setTimeSpent(0);
                else
                    obj.addTimeSpent(addTime ? secs : -secs);

                obj = data.getActivityByName(name);
                if (!addTime && spent < secs)
                    obj.addTimeSpent(-spent);
                else
                    obj.addTimeSpent(addTime ? secs : -secs);
            }
        }, cal.get(YEAR), cal.get(MONTH), cal.get(DAY_OF_MONTH));
        dpd.getDatePicker().setMaxDate(cal.getTimeInMillis());
        dpd.show();
    }

    private long getSafeNum(EditText text) {
        String s = text.getText().toString();
        if (s.isEmpty())
            return 0;

        try {
            return parseLong(s);
        } catch (NumberFormatException e) {
            Timber.e(e, "Nonfatal exception while parsing long. May want to evaluate formatting!");
        }

        return 0;
    }
}
