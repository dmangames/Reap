package dmangames.team4.reap.objects;

import java.util.Calendar;
import java.util.Set;
import java.util.TreeMap;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Created by Andrew on 3/29/2016.
 */
public class ActivityBlob {

    String date;
    TreeMap<String, ActivityObject> ActivityMap = new TreeMap<>();

    public ActivityBlob() {
        Calendar c = Calendar.getInstance();
        date = String.format("%s-%s-%s", c.get(MONTH), c.get(DAY_OF_MONTH), c.get(YEAR));
    }

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

    public Set<String> getKeys() {
        return ActivityMap.keySet();
    }

    public int size() {
        return ActivityMap.size();
    }
}
