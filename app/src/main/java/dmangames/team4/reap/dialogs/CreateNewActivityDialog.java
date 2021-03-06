package dmangames.team4.reap.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import dmangames.team4.reap.R;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.dialogs.ChooseActivityIconDialog.ChooseIconListener;
import dmangames.team4.reap.objects.ActivityObject;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Used to create a new activity that will be tracked
 * Created by stevenzhang on 3/30/16.
 */
@Layout(R.layout.dialog_new_activity)
public class CreateNewActivityDialog extends ReapDialog implements ChooseIconListener {
    @Bind(R.id.et_activity_name) EditText activity_name;
    @Bind(R.id.iv_activity_icon) ImageView icon;

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
                createActivity();
            }
        });
        setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        chooseIconDialog = new ChooseActivityIconDialog(context, this);
    }

    private void createActivity() {
        String name = activity_name.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), R.string.empty_activity_name, LENGTH_SHORT).show();
            return;
        }
        CreateNewActivityDialog.this.listener.createActivity(
                name, "", iconRes);
    }

    public CreateNewActivityDialog(Context context, CreateNewActivityListener listener,
                                   ActivityObject activity) {
        this(context, listener);

        icon.setImageURI(Uri.parse(activity.getIconURL()));
        activity_name.setText(activity.getActivityName());
    }

    @OnClick(R.id.iv_activity_icon) void onIconClicked() {
        chooseIconDialog.show();
    }

    @OnEditorAction(R.id.et_activity_name) boolean onActivityNameKey(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            createActivity();
            dismiss();
            return true;
        }
        return false;
    }

    @Override public void iconChosen(@DrawableRes int drawableRes) {
        icon.setImageResource(drawableRes);
        this.iconRes = drawableRes;
        chooseIconDialog.dismiss();
    }

    public interface CreateNewActivityListener {
        void createActivity(String name, String iconURL, int iconRes);
    }
}
