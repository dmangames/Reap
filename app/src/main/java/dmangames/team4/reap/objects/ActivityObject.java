package dmangames.team4.reap.objects;

import android.support.annotation.DrawableRes;

import dmangames.team4.reap.R;
import dmangames.team4.reap.util.IconURIs;

/**
 * Created by brian on 3/25/16.
 */
public class ActivityObject {
    public static final String KEY_ACTIVITYOBJ_NAME = "activityObject.name";
    public static final String KEY_ACTIVITYOBJ_SPENT = "activityObject.timeSpent";

    String activityName;
    long timeSpent;
    int color;

    String iconURL;

    public ActivityObject(String name, @DrawableRes int iconRes) {
        this(name, iconRes == 0 ? IconURIs.get(R.drawable.no_activity_icon) : IconURIs.get(iconRes));
    }

    public ActivityObject(String name, String iconURL) {
        activityName = name;
        timeSpent = 0;
        this.iconURL = iconURL;
    }

    @Override public boolean equals(Object o) {
        return o instanceof ActivityObject &&
                ((ActivityObject) o).activityName.equals(activityName);
    }

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public String getActivityName() {
        return activityName;
    }

    public void addTimeSpent(double additionalTimeSpent) {
        this.timeSpent += additionalTimeSpent;
    }

    public int getColor() {
        return color;
    }

    public void setIconURLFromRes(@DrawableRes int iconRes) {
        iconURL = iconRes == 0 ? IconURIs.get(R.drawable.no_activity_icon) : IconURIs.get(iconRes);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }
}
