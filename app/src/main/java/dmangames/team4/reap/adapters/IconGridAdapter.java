package dmangames.team4.reap.adapters;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmangames.team4.reap.R;

/**
 * Created by brian on 4/3/16.
 */
public class IconGridAdapter extends RecyclerView.Adapter<IconGridAdapter.IconHolder> {
    public class IconHolder extends RecyclerView.ViewHolder implements OnClickListener {
        @Bind(R.id.iv_iconitem_icon) ImageView iconView;

        private int icon;

        public IconHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setIcon(@DrawableRes int icon) {
            this.icon = icon;
            iconView.setImageResource(icon);
        }

        @OnClick(R.id.iv_iconitem_icon) public void onClick(View v) {
            listener.iconClicked(icon);
        }
    }

    private final Context context;
    private final IconClickListener listener;
    private final int[] icons = {
            R.drawable.classic_acoustic_guitar
    };

    public IconGridAdapter(Context context, IconClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override public IconHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IconHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_grid_icon, parent, false));
    }

    @Override public void onBindViewHolder(IconHolder holder, int position) {
        holder.setIcon(icons[position]);
    }

    @Override public int getItemCount() {
        return icons.length;
    }

    public interface IconClickListener {
        void iconClicked(@DrawableRes int icon);
    }
}
