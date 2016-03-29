package dmangames.team4.reap.events;

import dmangames.team4.reap.fragments.ReapFragment;

/**
 * Created by stevenzhang on 3/28/16.
 */
public class ChooseFragmentEvent {

    public final ReapFragment fragment;

    public ChooseFragmentEvent(ReapFragment fragment) {
        this.fragment = fragment;
    }

}
