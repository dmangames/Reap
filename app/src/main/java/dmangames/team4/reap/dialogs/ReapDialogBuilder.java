package dmangames.team4.reap.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.ButterKnife;
import dmangames.team4.reap.annotations.Layout;

/**
 * Created by stevenzhang on 3/30/16.
 */
public class ReapDialogBuilder extends AlertDialog.Builder {
    private final Context context;

    public ReapDialogBuilder(Context context) {
        super(context);

        this.context = context;

        Class<? extends ReapDialogBuilder> cls = getClass();
        if (cls.isAnnotationPresent(Layout.class)) {
            int layout = cls.getAnnotation(Layout.class).value();
            View view = LayoutInflater.from(context).inflate(layout, null);
            ButterKnife.bind(this, view);
            setView(view);
        }
    }
}
