package dmangames.team4.reap.objects;

/**
 * Created by brian on 3/25/16.
 */
public class ActivityObject {

    String activityName;
    double timeSpent;
    int color;

    int iconRes;
    String iconURL;

    public ActivityObject(String name){
        activityName = name;
        timeSpent = 0.0;
    }

    public void setTimeSpent(Double timeSpent) {
        this.timeSpent = timeSpent;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Double getTimeSpent() {
        return timeSpent;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setTimeSpent(double timeSpent) {
        this.timeSpent = timeSpent;
    }

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
