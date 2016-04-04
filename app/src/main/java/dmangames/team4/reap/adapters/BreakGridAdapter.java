package dmangames.team4.reap.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmangames.team4.reap.R;
import dmangames.team4.reap.objects.ActivityObject;
import dmangames.team4.reap.objects.DataObject;

/**
 * Created by brian on 4/3/16.
 */
public class BreakGridAdapter extends RecyclerView.Adapter<BreakGridAdapter.BreakHolder> {
    public class BreakHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_iconitem_icon) ImageView icon;

        private ActivityObject activityObject;

        public BreakHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setBreakActivityObject(ActivityObject activityObject) {
            this.activityObject = activityObject;
            this.icon.setImageResource(activityObject.getIconRes());
        }

        @OnClick(R.id.iv_iconitem_icon) void onClick(){
            listener.chooseBreak(activityObject);
        }
    }

    private final Context context;
    private final BreakGridListener listener;
    private final DataObject activities;
    private final String[] breaks = {
            "sleep",
            "restroom",
            "social",
            "eat",
            "play"
    };

    public BreakGridAdapter(Context context, DataObject activities, BreakGridListener listener) {
        this.context = context;
        this.activities = activities;
        this.listener = listener;

    }

    @Override
    public BreakHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BreakHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_grid_icon, parent, false));
    }

    @Override public void onBindViewHolder(BreakHolder holder, int position) {
        Log.d("Break", position + "");
        holder.setBreakActivityObject(activities.getBreakByName((breaks[position])));
    }

    @Override public int getItemCount() {
        return breaks.length;
    }

    public interface BreakGridListener{
        void chooseBreak(ActivityObject activityObject);
    }
}
