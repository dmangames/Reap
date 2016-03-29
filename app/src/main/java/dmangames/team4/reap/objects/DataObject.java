package dmangames.team4.reap.objects;

import java.util.HashMap;

/**
 * Created by Andrew on 3/28/2016.
 */
public class DataObject {

    String recentDate;
    String name;
    HashMap<String, ActivityBlob> history = new HashMap<>();
    ActivityBlob recentActivities;
    HashMap<String, Boolean> activityList = new HashMap<>();

    public DataObject(String name, String date){
        this.recentDate = date;
        this.name = name;
        recentActivities = new ActivityBlob(date);
    }

    public String getName() {
        return name;
    }

    public HashMap<String, ActivityBlob> getHistory() {
        return history;
    }

    public void setName(String name){
        this.name = name;
    }

    public ActivityBlob getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(ActivityBlob recentActivities) {
        this.recentActivities = recentActivities;
    }

    public void deactivateActivity(String name){
        if(activityList.containsKey(name))
            activityList.put(name, false);
    }

    public void activateActivity(String name){
        if(activityList.containsKey(name))
            activityList.put(name, true);
    }

    public void addActivity(ActivityObject activity){
        recentActivities.addActivity(activity);
        if(!activityList.containsKey(activity.getActivityName()))
            activityList.put(activity.getActivityName(), true);
    }

    public boolean checkActivity(String name){
        return recentActivities.checkActivity(name);
    }

    public void removeActivity(String name){
        recentActivities.removeActivity(name);
    }

    public void newDay(String newString){
        if(this.recentDate.equals(recentActivities.getString())){
            archiveActivities(newString);
        }
        recentDate = newString;
    }

    public void archiveActivities(String newString){
        history.put(recentActivities.getString(), recentActivities);
        recentActivities = new ActivityBlob(newString);
    }

    public ActivityBlob getActivityBlobByDate(String date){
        return history.get(date);
    }
}
