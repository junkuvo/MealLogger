package junkuvo.apps.meallogger.util;

import android.content.Context;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import junkuvo.apps.meallogger.ActivityLogListAll;
import junkuvo.apps.meallogger.entity.NotificationTime;

public class NotificationScheduler {
    private static final String TAG = NotificationScheduler.class.getSimpleName();
    private final NotificationScheduler self = this;
    private static final int NUMBER_OF_ONE_WEEK = 7;

    private Context mContext;
    private RealmResults<NotificationTime> mItems;
    private Realm realm;
    private NotificationTime mNotificationTime;

    public NotificationScheduler(Context context) {
        this.mContext = context;
    }

    public Calendar createNextNotifySchedule(){
        // 通知時間取得
        boolean[] weekDays = new boolean[NUMBER_OF_ONE_WEEK];
        // In Calendar.java day of week order e.g Sun = 1 ... Sat = 7
        String[] dayOfWeekString = new DateFormatSymbols().getShortWeekdays();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        int hour        = calendar.get(Calendar.HOUR_OF_DAY);
        int minute      = calendar.get(Calendar.MINUTE);
        int day = today;
        RealmResults<NotificationTime> items;


        realm = Realm.getDefaultInstance();
        // 今日
        mItems = realm.where(NotificationTime.class).contains("mDays", dayOfWeekString[today]).findAll().sort("mTime", Sort.ASCENDING);
        int idx;
        if (mItems == null || mItems.size() == 0) {
            // 明日から土曜日
            for (idx = today + 1; idx <= weekDays.length; idx++) {
                items = realm.where(NotificationTime.class).contains("mDays", dayOfWeekString[idx]).findAll();
                if(items.size() > 0) {
                    mNotificationTime = items.sort("mTime", Sort.ASCENDING).first();
                    day = idx;
                    break;
                }
            }
        }else{
            // 今日の時間判定
            int hourNotification;
            int minuteNotification;
            for(idx = 0;idx < mItems.size();idx++){
                hourNotification = Integer.parseInt(mItems.get(idx).getmTime().split(":")[0]);
                minuteNotification = Integer.parseInt(mItems.get(idx).getmTime().split(":")[1]);
                if(hour < hourNotification || (minute < minuteNotification && hour == hourNotification)){
                    mNotificationTime = mItems.get(idx);
                    break;
                }
            }

            // 今日の最後のが終わっている場合
            if (mNotificationTime == null) {
                // 明日から土曜日
                for (idx = today + 1; idx <= weekDays.length; idx++) {
                    items = realm.where(NotificationTime.class).contains("mDays", dayOfWeekString[idx]).findAll();
                    if(items.size() > 0) {
                        mNotificationTime = items.sort("mTime", Sort.ASCENDING).first();
                        day = idx;
                        break;
                    }
                }
            }
        }
        // 日曜から来週の今日
        if (mNotificationTime == null) {
            for (idx = 1; idx <= today; idx++) {
                items = realm.where(NotificationTime.class).contains("mDays", dayOfWeekString[idx]).findAll();
                if(items.size() > 0) {
                    mNotificationTime = items.sort("mTime", Sort.ASCENDING).first();
                    // add 1 week
                    calendar.add(Calendar.DATE, NUMBER_OF_ONE_WEEK);
                    day = idx;
                    break;
                }
            }
        }

        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mNotificationTime.getmTime().split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(mNotificationTime.getmTime().split(":")[1]));
        //((Application)((Activity)mContext).getApplication()).mNotificationScheduleName = mNotificationTime.getmTitle();
        SharedPreferencesUtil.saveString(mContext, ActivityLogListAll.PREF_KEY_NOTIFICATION_NAME,mNotificationTime.getmTitle());

        return calendar;
    }
}
