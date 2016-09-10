package junkuvo.apps.meallogger.entity;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MealLogs extends RealmObject {
    @PrimaryKey
    private long id;
//    @Required
    private Date createdAt;
    private String screenName;
    private String menuName;
    private int thumbnailResourceID;
    private long price;

    public MealLogs() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getThumbnailResourceID() {
        return thumbnailResourceID;
    }

    public void setThumbnailResourceID(int thumbnailResourceID) {
        this.thumbnailResourceID = thumbnailResourceID;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setMealLog(int thumbnailResourceID, String menuName, Date createdAt, long price){
        setId(System.currentTimeMillis());
        setMenuName(menuName);
        setPrice(price);
        setThumbnailResourceID(thumbnailResourceID);
        setCreatedAt(createdAt);
    }
}