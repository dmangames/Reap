package dmangames.team4.reap.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

import timber.log.Timber;

/**
 * Created by Andrew on 3/28/2016.
 */
public class DataObject {
    public static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

    String recentDate;
    String name;
    HashMap<String, ActivityBlob> history = new HashMap<>();
    ActivityBlob recentActivities;
    TreeMap<String, ActivityObject> activityList = new TreeMap<>();
    TreeMap<String, ActivityObject> breakList = new TreeMap<>();
    TreeMap<String, PixelPortrait> portraits = new TreeMap<>();

    public DataObject(String name, String date) {
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

    public void setName(String name) {
        this.name = name;
    }

    public ActivityBlob getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(ActivityBlob recentActivities) {
        this.recentActivities = recentActivities;
    }

//    public void deactivateActivity(String name) {
//        if (activityList.containsKey(name)) {
//
//            activityList.get(name).setActivityName(name);
//
//        }
//    }
//
//    public void activateActivity(String name){
//        if(activityList.containsKey(name))
//            activityList.put(name, true);
//    }

    public void addNewActivity(String activityName, int iconID) {
        if (!activityList.containsKey(activityName)) {
            ActivityObject activity = new ActivityObject(activityName, iconID);
            activityList.put(activityName, activity);
        }
    }

    public void addNewBreak(String activityName, int iconID) {
        Timber.d("Activity List: %s", activityList.toString());
        Timber.d("Break List: %s", breakList.toString());
        if (!breakList.containsKey(activityName)) {
            ActivityObject activity = new ActivityObject(activityName, iconID);
            breakList.put(activityName, activity);
        }
    }

    public void update(String oldName, String newName, int iconID) {
        if (!activityList.containsKey(oldName)) {
            Timber.e("Attempted to update nonexistent ActivityObject!");
            return;
        }

        ActivityObject obj = activityList.remove(oldName);
        obj.setActivityName(newName);
        obj.setIconURLFromRes(iconID);
        activityList.put(newName, obj);

        obj = recentActivities.removeActivity(oldName);
        obj.setActivityName(newName);
        obj.setIconURLFromRes(iconID);
        recentActivities.updateActivity(obj);
    }

    public boolean checkActivity(String name) {
        return recentActivities.checkActivity(name);
    }

    public void removeActivity(String name) {
        recentActivities.removeActivity(name);
        activityList.remove(name);
    }

    public void newDay(String newString) {
        if (!this.recentDate.equals(recentActivities.getString())) {
            archiveActivities(newString);
        }
        recentDate = newString;
    }

    public void archiveActivities(String newString) {
        recentActivities.removeNulls();
        history.put(recentActivities.getString(), recentActivities);
        recentActivities = new ActivityBlob(newString);
    }

    public ActivityBlob getActivityBlobByDate(String date) {
        return history.get(date);
    }

    public Set<String> getKeys() {
        return activityList.keySet();
    }

    public Set<String> getBreakKeys() {
        return breakList.keySet();
    }

    public ActivityObject getActivityByName(String name) {
        return activityList.get(name);
    }

    public ActivityObject getBreakByName(String name) {
        return breakList.get(name);
    }

    public int size() {
        return activityList.size();
    }

    public int breakListSize() {
        return breakList.size();
    }

    public ActivityBlob aggregateHistory() {
        ActivityBlob out = new ActivityBlob();
        for (String date : history.keySet()) {
            ActivityBlob blob = history.get(date);
            for (String activityName : blob.getKeys()) {
                if (!out.checkActivity(activityName)) {
                    ActivityObject temp = new ActivityObject(activityName, blob.getActivity(activityName).getIconURL());
                    out.addActivity(temp);
                    out.getActivity(activityName).addTimeSpent(blob.getActivity(activityName).timeSpent);
                } else {
                    out.getActivity(activityName).addTimeSpent(blob.getActivity(activityName).timeSpent);
                }
            }
        }

        for (String activityName : recentActivities.getKeys()) {
            if (!out.checkActivity(activityName)) {
                out.addActivity(recentActivities.getActivity(activityName));
            } else {
                out.getActivity(activityName).addTimeSpent(recentActivities.getActivity(activityName).timeSpent);
            }
        }

        return out;
    }

    public ActivityBlob aggregateHistoryRange(String start, String end) {

        Timber.d("Aggregating dates " + start + " to " + end);

        ActivityBlob out = new ActivityBlob();

        Date dateObject = new Date();
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = DATEFORMAT.parse(start);
            endDate = DATEFORMAT.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        for (String date : history.keySet()) {

            try {
                dateObject = DATEFORMAT.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!dateObject.before(startDate) && !dateObject.after(endDate)) {
                ActivityBlob blob = history.get(date);
                for (String activityName : blob.getKeys()) {
                    if (!out.checkActivity(activityName)) {
                        ActivityObject temp = new ActivityObject(activityName, blob.getActivity(activityName).getIconURL());
                        out.addActivity(temp);
                        out.getActivity(activityName).addTimeSpent(blob.getActivity(activityName).timeSpent);
                    } else {
                        out.getActivity(activityName).addTimeSpent(blob.getActivity(activityName).timeSpent);
                    }
                }
            }
        }

        try {
            dateObject = DATEFORMAT.parse(recentActivities.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!dateObject.before(startDate) && !dateObject.after(endDate)) {
            for (String activityName : recentActivities.getKeys()) {

                if (!out.checkActivity(activityName)) {
                    out.addActivity(recentActivities.getActivity(activityName));
                } else {
                    out.getActivity(activityName).addTimeSpent(recentActivities.getActivity(activityName).timeSpent);
                }
            }
        }

        return out;
    }
}
