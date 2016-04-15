package dmangames.team4.reap.events;

import dmangames.team4.reap.objects.ActivityObject;

/**
 * Created by brian on 4/15/16.
 */
public class ActivityObjectDeletedEvent {
    public final ActivityObject object;

    public ActivityObjectDeletedEvent(ActivityObject object) {
        this.object = object;
    }
}
