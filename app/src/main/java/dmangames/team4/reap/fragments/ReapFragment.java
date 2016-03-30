package dmangames.team4.reap.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.annotations.HasBusEvents;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.annotations.Tag;

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

        View view = inf.inflate(cls.getAnnotation(Layout.class).value(), parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onStart() {
        super.onStart();
        if (getClass().isAnnotationPresent(HasBusEvents.class))
            ((MainActivity) getActivity()).bus().register(this);
    }

    @Override public void onStop() {
        super.onStop();
        if (getClass().isAnnotationPresent(HasBusEvents.class))
            ((MainActivity) getActivity()).bus().unregister(this);
    }

    public String tag() {
        Class<? extends ReapFragment> cls = getClass();
        if (cls.isAnnotationPresent(Tag.class))
            return getString(cls.getAnnotation(Tag.class).value());
        return cls.getName();
    }
}
