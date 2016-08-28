package junkuvo.apps.meallogger.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NotificationTimes extends RealmObject {

    @PrimaryKey
    private long id;
    private String mTitle;
    private String mTime;
    private String mDays;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmDays() {
        return mDays;
    }

    public void setmDays(String mDays) {
        this.mDays = mDays;
    }

    public void setNotificationTime(String title, String time, String days){
        setId(System.currentTimeMillis());
        setmTitle(title);
        setmTime(time);
        setmDays(days);
    }
}
