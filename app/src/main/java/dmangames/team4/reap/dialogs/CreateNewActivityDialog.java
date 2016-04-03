package dmangames.team4.reap.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.Bind;
import dmangames.team4.reap.R;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.dialogs.ChooseActivityIconDialog.ChooseIconListener;

/**
 * Used to create a new activity that will be tracked
 * Created by stevenzhang on 3/30/16.
 */
@Layout(R.layout.dialog_new_activity)
public class CreateNewActivityDialog extends ReapDialogBuilder implements ChooseIconListener {
    @Bind(R.id.et_activity_name) EditText activity_name;
    @Bind(R.id.iv_activity_icon) ImageView icon;

    public interface CreateNewActivityListener {

        void createActivity(String name, String iconURL, int iconRes);
    }
    private final CreateNewActivityListener listener;

    private String iconURL = null;
    private AlertDialog chooseIconDialog;
    private int iconRes = R.drawable.no_activity_icon;

    public CreateNewActivityDialog(Context context, CreateNewActivityListener listener) {
        super(context);
        this.listener = listener;

        setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                CreateNewActivityDialog.this.listener.createActivity(
                        activity_name.getText().toString(), "", iconRes);
            }
        });
        setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        chooseIconDialog = new ChooseActivityIconDialog(context, this).create();
        icon.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                chooseIconDialog.show();
            }
        });
    }

    @Override public void iconChosen(@DrawableRes int drawableRes) {
        icon.setImageResource(drawableRes);
        this.iconRes = drawableRes;
        chooseIconDialog.dismiss();
    }
}