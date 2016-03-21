package dmangames.team4.reap.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import dmangames.team4.reap.annotations.Layout;

/**
 * Created by brian on 3/21/16.
 */
public class ReapFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Layout l = this.getClass().getAnnotation(Layout.class);
        if (l == null) {
            throw new NoSuchFieldError(
                    "Fragment " + getClass().getName() + " is missing @Layout specification");
        }

        View view = inflater.inflate(l.value(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}