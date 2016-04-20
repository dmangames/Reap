package dmangames.team4.reap.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import dmangames.team4.reap.annotations.HasInjections;
import dmangames.team4.reap.dagger.DaggerInjector;

/**
 * Created by brian on 4/20/16.
 */
public abstract class ReapAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    protected final Context context;

    public ReapAdapter(Context context) {
        this.context = context;
        if (getClass().isAnnotationPresent(HasInjections.class))
            DaggerInjector.inject(this);
    }
}
