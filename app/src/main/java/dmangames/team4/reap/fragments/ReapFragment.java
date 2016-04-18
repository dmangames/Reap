package dmangames.team4.reap.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.activities.MainActivity.BackButtonListener;
import dmangames.team4.reap.annotations.HasBusEvents;
import dmangames.team4.reap.annotations.Layout;

import static java.lang.String.format;

/**
 * Base {@link Fragment Fragment} class for Reap. Handles view binding via ButterKnife. All
 * classes which extend this class require a {@link Layout Layout} annotation.
 *
 * @author Brian Wang
 * @version 3/21/16
 */
public class ReapFragment extends Fragment implements BackButtonListener {
    protected EventBus bus;

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

    @Override
    public void onStart() {
        super.onStart();
        bus = ((MainActivity) getActivity()).bus();
        if (getClass().isAnnotationPresent(HasBusEvents.class))
            bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getClass().isAnnotationPresent(HasBusEvents.class))
            bus.unregister(this);
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (nextAnim == R.animator.slide_in_bottom || nextAnim == R.animator.slide_out_bottom)
            return getSlideAnimator(enter);
        return super.onCreateAnimator(transit, enter, nextAnim);
    }

    private Animator getSlideAnimator(boolean enter) {
        Animator anim;
        int duration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
        float height = point.y;

        if (enter) {
            anim = ObjectAnimator.ofFloat(this, "translationY", height, 0);
            anim.setInterpolator(new DecelerateInterpolator());
        } else {
            anim = ObjectAnimator.ofFloat(this, "translationY", 0, height);
            anim.setInterpolator(new AccelerateInterpolator());
        }
        anim.setDuration(duration);
        return anim;
    }

    /**
     * Goes back by calling back to the {@link MainActivity}.
     */
    public void goBack() {
        Activity activity = getActivity();
        if (activity instanceof MainActivity)
            ((MainActivity) activity).goBack(false);
        else
            Log.e(tag(), "Activity isn't an instance of MainActivity.");
    }

    public String tag() {
       return getClass().getSimpleName();
    }

    @Override public boolean onBackPressed() {
        return false;
    }
}
