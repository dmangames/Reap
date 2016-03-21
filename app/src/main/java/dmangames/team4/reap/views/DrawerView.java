package dmangames.team4.reap.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import dmangames.team4.reap.R;

/**
 * Created by brian on 3/21/16.
 */
public class DrawerView extends FrameLayout {



    public DrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_drawer, this);
    }
}
