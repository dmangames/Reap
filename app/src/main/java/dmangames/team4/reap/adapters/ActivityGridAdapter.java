package dmangames.team4.reap.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import dmangames.team4.reap.R;
import dmangames.team4.reap.annotations.HasInjections;
import dmangames.team4.reap.dialogs.CreateNewActivityDialog;
import dmangames.team4.reap.dialogs.CreateNewActivityDialog.CreateNewActivityListener;
import dmangames.team4.reap.objects.ActivityObject;
import dmangames.team4.reap.objects.DataObject;
import dmangames.team4.reap.util.DaggerInjector;

/**
 * Created by brian on 4/1/16.
 */
@HasInjections
public class ActivityGridAdapter extends ReapAdapter<ActivityGridAdapter.ActivityViewHolder> {
    public class ActivityViewHolder extends ViewHolder
            implements Dialog.OnClickListener, CreateNewActivityListener {
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

        @OnLongClick(R.id.ll_griditem_container) boolean onLongClick() {
            AlertDialog.Builder builder = new AlertDialog
                    .Builder(context).setItems(longPressStrings, this);
            builder.setTitle(activity.getActivityName());
            builder.show();
            return true;
        }

        @Override public void onClick(DialogInterface dialog, int which) {
            switch (longPressOptions[which]) {
                case R.string.edit :
                    new CreateNewActivityDialog(context, this, activity).show();
                    return;
                case R.string.delete :
                    new AlertDialog.Builder(context)
                            .setMessage(R.string.are_you_sure)
                            .setTitle(activity.getActivityName())
                            .setCancelable(true)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    activities.removeActivity(activity.getActivityName());
                                    listener.activityDeleted(activity);
                                    update();
                                }
                            }).setNegativeButton(R.string.no, null).show();
            }
        }

        @Override public void createActivity(String name, String iconURL, int iconRes) {
            activities.update(activity.getActivityName(), name, iconRes);
            setActivityObject(activities.getActivityByName(name));

            listener.activityChanged(activity);
            update();
        }
    }

    @Inject DataObject activities;
    private final ActivityGridListener listener;

    private ArrayList<String> names;

    private static final int[] longPressOptions = {R.string.edit, R.string.delete};

    private static String[] longPressStrings;

    public ActivityGridAdapter(Context context, ActivityGridListener listener) {
        super(context);
        this.listener = listener;
        DaggerInjector.inject(this);

        names = new ArrayList<>(activities.getKeys());

        if (longPressStrings == null) {
            longPressStrings = new String[longPressOptions.length];
            for (int k = 0; k < longPressStrings.length; k++)
                longPressStrings[k] = context.getString(longPressOptions[k]);
        }
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
        void activityChanged(ActivityObject object);
        void activityDeleted(ActivityObject object);
    }
}
