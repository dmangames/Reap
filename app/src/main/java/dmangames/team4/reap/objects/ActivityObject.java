package dmangames.team4.reap.objects;

/**
 * Created by brian on 3/25/16.
 */
public class ActivityObject {

    String activityName;
    Double timeSpent;

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


}
