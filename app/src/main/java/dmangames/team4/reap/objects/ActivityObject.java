package dmangames.team4.reap.objects;

import dmangames.team4.reap.R;

/**
 * Created by brian on 3/25/16.
 */
public class ActivityObject {

    String activityName;
    long timeSpent;
    int color;

    int iconRes;
    String iconURL;

    public ActivityObject(String name, int iconRes){
        activityName = name;
        if( iconRes != 0)
            this.iconRes = iconRes;
        else
            iconRes = R.drawable.no_activity_icon;
        timeSpent = 0;
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

    public void addTimeSpent(double additionalTimeSpent) {this.timeSpent += additionalTimeSpent; }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }
}