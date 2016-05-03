package dmangames.team4.reap.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dmangames.team4.reap.R;
import dmangames.team4.reap.annotations.HasInjections;
import dmangames.team4.reap.objects.ActivityObject;
import dmangames.team4.reap.objects.DataObject;
import dmangames.team4.reap.objects.PixelPortrait;

/**
 * Created by brian on 5/2/16.
 */
@HasInjections
public class PixelPortraitsAdapter extends ReapAdapter<PixelPortraitsAdapter.PortraitHolder> {
    public class PortraitHolder extends ViewHolder {
        @Bind(R.id.iv_griditem_icon) ImageView portrait;
        @Bind(R.id.tv_griditem_text) TextView name;

        private ActivityObject activity;

        public PortraitHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setActivity(ActivityObject obj) {
            activity = obj;

            final String n = obj.getActivityName();
            this.name.setText(n);
            if (!portraits.containsKey(n))
                portraits.put(n, new PixelPortrait(context, n));
            portrait.post(new Runnable() {
                @Override public void run() {
                    portraits.get(n).loadInto(context, portrait);
                }
            });
        }
    }

    @Inject DataObject activities;

    private ArrayList<String> names;
    private HashMap<String, PixelPortrait> portraits;

    public PixelPortraitsAdapter(Context context) {
        super(context);
        names = new ArrayList<>(activities.getKeys());
        portraits = new HashMap<>();
    }

    @Override
    public PortraitHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_grid_activity, parent, false);
        return new PortraitHolder(view);
    }

    @Override
    public void onBindViewHolder(PortraitHolder holder, int position) {
        holder.setActivity(activities.getActivityByName(names.get(position)));
    }

    @Override public int getItemCount() {
        return names.size();
    }

    public void update() {
        names = new ArrayList<>(activities.getKeys());
        notifyDataSetChanged();
    }
}
