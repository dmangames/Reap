package dmangames.team4.reap.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.ButterKnife;
import dmangames.team4.reap.annotations.HasInjections;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.dagger.DaggerInjector;

/**
 * Created by stevenzhang on 3/30/16.
 */
public class ReapDialog extends AlertDialog {
    private final Context context;

    protected ReapDialog(Context context) {
        super(context);

        this.context = context;

        Class<? extends ReapDialog> cls = getClass();
        if (cls.isAnnotationPresent(Layout.class)) {
            int layout = cls.getAnnotation(Layout.class).value();
            View view = LayoutInflater.from(context).inflate(layout, null);
            ButterKnife.bind(this, view);
            setView(view);
        }

        if (cls.isAnnotationPresent(HasInjections.class))
            DaggerInjector.inject(this);
    }

    public void setPositiveButton(@StringRes int text, OnClickListener listener) {
        setButton(BUTTON_POSITIVE, context.getString(text), listener);
    }

    public void setNegativeButton(@StringRes int text, OnClickListener listener) {
        setButton(BUTTON_NEGATIVE, context.getString(text), listener);
    }
}
