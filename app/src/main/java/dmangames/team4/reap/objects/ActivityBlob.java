package dmangames.team4.reap.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andrew on 3/29/2016.
 */
public class ActivityBlob {

    String date;
    HashMap<String, ActivityObject> ActivityMap = new HashMap<>();

    public ActivityBlob(String date){
        this.date = date;
    }

    public String getString() {
        return date;
    }

    public void setString(String date) {
        this.date = date;
    }

    public void addActivity(ActivityObject activity){
        if(!checkActivity(activity.getActivityName()))
            ActivityMap.put(activity.getActivityName(),activity);
    }

    public boolean checkActivity(String name){
        return ActivityMap.containsKey(name);
    }

    public void removeActivity(String name){
        ActivityMap.remove(name);
    }

    public ActivityObject getActivity(String name){
        return ActivityMap.get(name);
    }
}
