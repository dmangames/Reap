package dmangames.team4.reap.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 3/28/2016.
 */
public class DataObject {

    String name;
    List<ActivityObject> activities = new ArrayList<>();

    public String getName() {
        return name;
    }

    public List<ActivityObject> getActivities() {
        return activities;
    }

    public void setName(String name){
        this.name = name;
    }

    public void addActivity(ActivityObject activity){
        activities.add(activity);
    }

    public void removeActivity(String name){
        for(int i = 0; i < activities.size(); i++){
            if(activities.get(i).getActivityName().equals(name)) {
                activities.remove(i);
                return;
            }
        }
    }

}
