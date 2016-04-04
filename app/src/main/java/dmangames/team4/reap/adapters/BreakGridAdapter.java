package dmangames.team4.reap.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import dmangames.team4.reap.R;

/**
 * Created by brian on 4/3/16.
 */
public class BreakGridAdapter extends RecyclerView.Adapter<BreakGridAdapter.BreakHolder> {
    public class BreakHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_iconitem_icon) ImageView icon;

        public BreakHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setBreak(int iconRes) {
            this.icon.setImageResource(iconRes);
        }
    }

    private final Context context;
    private final int[] breaks = {
            R.drawable.bed,
            R.drawable.restroom,
            R.drawable.social,
            R.drawable.hamburger,
            R.drawable.game
    };

    public BreakGridAdapter(Context context) {
        this.context = context;
    }

    @Override
    public BreakHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BreakHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_grid_icon, parent, false));
    }

    @Override public void onBindViewHolder(BreakHolder holder, int position) {
        holder.setBreak(breaks[position]);
    }

    @Override public int getItemCount() {
        return breaks.length;
    }
}
