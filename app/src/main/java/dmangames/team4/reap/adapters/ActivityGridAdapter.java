package dmangames.team4.reap.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmangames.team4.reap.R;
import dmangames.team4.reap.objects.ActivityObject;
import dmangames.team4.reap.objects.DataObject;

/**
 * Created by brian on 4/1/16.
 */
public class ActivityGridAdapter extends Adapter<ActivityGridAdapter.ActivityViewHolder> {
    public class ActivityViewHolder extends ViewHolder {
        @Bind(R.id.iv_griditem_icon) ImageView icon;
        @Bind(R.id.tv_griditem_text) TextView name;

        private ActivityObject activity;

        public ActivityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setActivityObject(ActivityObject object) {
            this.activity = object;

            icon.setImageResource(object.getIconRes());
            name.setText(object.getActivityName());
        }

        @OnClick(R.id.ll_griditem_container) void onClick() {
            listener.chooseActivity(activity);
        }
    }

    private final DataObject activities;
    private final Context context;
    private final ActivityGridListener listener;

    private ArrayList<String> names;

    public ActivityGridAdapter(Context context, DataObject activities,
                               ActivityGridListener listener) {
        this.context = context;
        this.activities = activities;
        this.listener = listener;

        names = new ArrayList<>(activities.getKeys());
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ActivityViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_grid_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        holder.setActivityObject(activities.getActivityByName(names.get(position)));
    }

    public void update() {
        names = new ArrayList<>(activities.getKeys());
        notifyDataSetChanged();
    }

    @Override public int getItemCount() {
        return activities.size();
    }

    public interface ActivityGridListener {
        void chooseActivity(ActivityObject object);
    }
}
