package dmangames.team4.reap.events;

import dmangames.team4.reap.objects.ActivityObject;

/**
 * Created by brian on 3/25/16.
 */
public class ChooseActivityEvent {
    public final ActivityObject activity;

    public ChooseActivityEvent(ActivityObject activity) {
        this.activity = activity;
    }
}
