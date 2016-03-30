package dmangames.team4.reap.events;

import dmangames.team4.reap.fragments.ReapFragment;

/**
 * Created by brian on 3/30/16.
 */
public class SwitchFragmentEvent {
    public final ReapFragment fragment;
    public final boolean backstack;

    public SwitchFragmentEvent(ReapFragment fragment, boolean addToBackstack) {
        this.fragment = fragment;
        this.backstack = addToBackstack;
    }
}
