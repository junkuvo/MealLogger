package junkuvo.apps.meallogger.entity;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * 通知設定（NotificationTime）を一気に複数Insertするためのオブジェクト
 */

public class NotificationTimes extends RealmObject {
    public RealmList<NotificationTime> notificationTimes;
    // getters and setters
}

