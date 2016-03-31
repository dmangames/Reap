package dmangames.team4.reap.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import butterknife.Bind;
import dmangames.team4.reap.R;
import dmangames.team4.reap.annotations.Layout;

/**
 * Used to create a new activity that will be tracked
 * Created by stevenzhang on 3/30/16.
 */
@Layout(R.layout.dialog_new_activity)
public class CreateNewActivityDialog extends ReapDialogBuilder {
    @Bind(R.id.et_activity_name) EditText activity_name;
    public interface CreateNewActivityListener {
        void createActivity(String name);
    }

    private final CreateNewActivityListener listener;

    public CreateNewActivityDialog(Context context, CreateNewActivityListener alistener) {
        super(context);
        this.listener = alistener;
        setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                    listener.createActivity(activity_name.getText().toString());
            }
        });
        setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
    }
}
