package dmangames.team4.reap.events;

import android.support.annotation.AnimatorRes;
import android.support.annotation.Nullable;

import dmangames.team4.reap.R;
import dmangames.team4.reap.fragments.ReapFragment;

/**
 * Created by brian on 3/30/16.
 */
public class SwitchFragmentEvent {
    public final ReapFragment fragment;
    public final boolean backstack;
    @AnimatorRes @Nullable public final int[] anim;

    public SwitchFragmentEvent(ReapFragment fragment, boolean addToBackstack, boolean animate) {
        this.fragment = fragment;
        this.backstack = addToBackstack;
        anim = !animate ? null : new int[]{
                R.animator.slide_in_bottom,
                R.animator.fade_out,
                R.animator.fade_in,
                R.animator.slide_out_bottom
        };
    }

    public SwitchFragmentEvent(ReapFragment fragment, boolean addToBackstack,
                               @AnimatorRes int enter, @AnimatorRes int exit,
                               @AnimatorRes int popEnter, @AnimatorRes int popExit) {
        this.fragment = fragment;
        this.backstack = addToBackstack;
        anim = new int[]{ enter, exit, popEnter, popExit };
    }
}
