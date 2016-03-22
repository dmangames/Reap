package dmangames.team4.reap.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import dmangames.team4.reap.R;

/**
 * Created by brian on 3/21/16.
 */
public class DrawerView extends FrameLayout {
    @Bind(R.id.lv_drawer_list) ListView optionsView;

    private DrawerListener listener;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> options;

    public DrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_drawer, this);
        ButterKnife.bind(this, view);

        options = new ArrayList<>();
        for (Option o : Option.values())
            options.add(context.getString(o.id));
        adapter = new ArrayAdapter<>(context, R.layout.item_drawer, options);
        optionsView.setAdapter(adapter);
    }

    public void setListener(DrawerListener listener) {
        this.listener = listener;
    }

    @OnItemClick(R.id.lv_drawer_list) void optionsItemSelected(int position) {
        if (listener == null)
            throw new NullPointerException("DrawerListener was never set");
        listener.switchTo(Option.values()[position]);
    }

    public enum Option {
        TODAY(R.string.today),
        HISTORY(R.string.history),
        MILESTONES(R.string.milestones),
        FRIENDS(R.string.friends),
        EXTRAS(R.string.extras);

        public final int id;

        Option(int id) {
            this.id = id;
        }
    }

    public interface DrawerListener {
        void switchTo(Option option);
    }
}
