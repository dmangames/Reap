package dmangames.team4.reap.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dmangames.team4.reap.R;
import dmangames.team4.reap.activities.MainActivity;
import dmangames.team4.reap.activities.MainActivity.FragmentEventListener;
import dmangames.team4.reap.annotations.HasBusEvents;
import dmangames.team4.reap.annotations.Layout;
import dmangames.team4.reap.dagger.DaggerInjector;
import timber.log.Timber;

import static java.lang.String.format;

/**
 * Base {@link Fragment Fragment} class for Reap. Handles view binding via ButterKnife. All
 * classes which extend this class require a {@link Layout Layout} annotation. Each class
 * extending MUST ALSO be added to {@link dmangames.team4.reap.dagger.ReapModule ReapModule}'s
 * "injects" list (as each fragment has at least a bus available).
 *
 * @author Brian Wang
 * @version 3/21/16
 */
public class ReapFragment extends Fragment implements FragmentEventListener {
    @Inject EventBus bus;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        Class<? extends ReapFragment> cls = getClass();
        if (!cls.isAnnotationPresent(Layout.class)) {
            throw new NoSuchFieldError(
                    format("Fragment %s is missing @Layout specification", cls.getName()));
        }

        View view = inf.inflate(cls.getAnnotation(Layout.class).value(), parent, false);
        ButterKnife.bind(this, view);
        DaggerInjector.inject(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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
            Timber.e("Activity isn't an instance of MainActivity.");
    }

    public String tag() {
        return getClass().getSimpleName();
    }

    @Override public boolean onBackPressed() {
        return false;
    }

    @Override public void packToService(Class service, Intent packIntent) {

    }

    @Override public void unpackFromService(Class service, Intent restoreIntent) {

    }
}
