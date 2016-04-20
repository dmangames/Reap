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
    /**
     * Denotes whether multiple instances of the fragment will be allowed to exist on the backstack.
     * If true, the fragment will be destroyed both upon the addition of a new fragment to the
     * backstack and on back.
     */
    public final boolean singleTop;
    @Nullable public final int[] anim;

    /**
     * @param singleTop See {@link SwitchFragmentEvent#singleTop}
     */
    public SwitchFragmentEvent(ReapFragment fragment, boolean singleTop, boolean animate) {
        this.fragment = fragment;
        this.singleTop = singleTop;
        if (animate) {
            if (singleTop) {
                anim = new int[] {
                        R.animator.fade_in,
                        R.animator.fade_out,
                        R.animator.fade_in,
                        R.animator.fade_out
                };
            } else {
                anim = new int[]{
                        R.animator.slide_in_bottom,
                        R.animator.fade_out,
                        R.animator.fade_in,
                        R.animator.slide_out_bottom
                };
            }
        } else
            anim = null;
    }

    /**
     * @param singleTop See {@link SwitchFragmentEvent#singleTop}
     */
    public SwitchFragmentEvent(ReapFragment fragment, boolean singleTop,
                               @AnimatorRes int enter, @AnimatorRes int exit,
                               @AnimatorRes int popEnter, @AnimatorRes int popExit) {
        this.fragment = fragment;
        this.singleTop = singleTop;
        anim = new int[]{ enter, exit, popEnter, popExit };
    }
}
