package dmangames.team4.reap.dialogs;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.Bind;
import dmangames.team4.reap.R;
import dmangames.team4.reap.adapters.IconGridAdapter;
import dmangames.team4.reap.annotations.Layout;

/**
 * Created by brian on 4/3/16.
 */
@Layout(R.layout.dialog_choose_icon)
public class ChooseActivityIconDialog extends ReapDialog implements IconGridAdapter.IconClickListener {

    @Bind(R.id.rv_chooseicon_grid) RecyclerView grid;

    private IconGridAdapter adapter;
    private final ChooseIconListener listener;

    public ChooseActivityIconDialog(Context context, ChooseIconListener listener) {
        super(context);

        adapter = new IconGridAdapter(context, this);
        grid.setLayoutManager(new GridLayoutManager(context, 3));
        grid.setAdapter(adapter);

        this.listener = listener;
    }

    @Override public void iconClicked(@DrawableRes int icon) {
        listener.iconChosen(icon);
    }

    public interface ChooseIconListener {
        void iconChosen(@DrawableRes int drawableRes);
    }
}
