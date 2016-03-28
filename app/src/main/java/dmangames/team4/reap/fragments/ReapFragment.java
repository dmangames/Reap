package dmangames.team4.reap.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.annotations.Layout;

import static java.lang.String.format;

/**
 * Base {@link Fragment Fragment} class for Reap. Handles view binding via ButterKnife. All
 * classes which extend this class require a {@link Layout Layout} annotation.
 *
 * @author Brian Wang
 * @version 3/21/16
 */
public class ReapFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        Class<? extends ReapFragment> cls = getClass();
        if (!cls.isAnnotationPresent(Layout.class)) {
            throw new NoSuchFieldError(
                    format("Fragment %s is missing @Layout specification", cls.getName()));
        }

        ((MainActivity) getActivity()).bus().register(this);
        View view = inf.inflate(cls.getAnnotation(Layout.class).value(), parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onStop() {
        super.onStop();

        ((MainActivity) getActivity()).bus().unregister(this);
    }
}
