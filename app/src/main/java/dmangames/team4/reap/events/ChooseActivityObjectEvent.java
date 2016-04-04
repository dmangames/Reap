package dmangames.team4.reap.events;

import dmangames.team4.reap.objects.ActivityObject;

/**
 * Created by stevenzhang on 3/30/16.
 */
public class ChooseActivityObjectEvent {
    public final ActivityObject object;
    public final boolean isBreak;

    public ChooseActivityObjectEvent(ActivityObject object, boolean isBreak) {
        this.object = object;
        this.isBreak = isBreak;
    }
}
