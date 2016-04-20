package dmangames.team4.reap.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import dmangames.team4.reap.R;
import dmangames.team4.reap.dialogs.CreateNewActivityDialog;
import dmangames.team4.reap.objects.ActivityBlob;
import dmangames.team4.reap.objects.ActivityObject;
import dmangames.team4.reap.objects.DataObject;
import dmangames.team4.reap.views.IconView;

/**
 * Created by Andrew on 4/20/2016.
 */
public class TodayListAdapter extends RecyclerView.Adapter<TodayListAdapter.TodayViewHolder> {

    public class TodayViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.today_activity_jars) IconView iconView;
        @Bind(R.id.today_activity_text) TextView name;


        private ActivityObject activity;

        public TodayViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setActivityObject(ActivityObject object) {
            this.activity = object;


            iconView.changeIcon(object.getIconRes());
            float numJars = (float)object.getTimeSpent() / 60;
            iconView.setNumIcons(numJars);
            iconView.invalidate();
            Log.d("something", numJars+"");
            name.setText(object.getActivityName());

            if((int)object.getTimeSpent()==0){
                iconView.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
            }
        }

    }

    private final ActivityBlob todayBlob;
    private final Context context;

    private ArrayList<String> names;

    public TodayListAdapter(Context context, ActivityBlob todayBlob) {
        this.context = context;
        this.todayBlob = todayBlob;

        names = new ArrayList<>(todayBlob.getKeys());

    }

    @Override
    public TodayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TodayViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_today_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(TodayViewHolder holder, int position) {
        holder.setActivityObject(todayBlob.getActivity(names.get(position)));
    }

    public void update() {
        names = new ArrayList<>(todayBlob.getKeys());
        notifyDataSetChanged();
    }

    @Override public int getItemCount() {
        return todayBlob.size();
    }

}
