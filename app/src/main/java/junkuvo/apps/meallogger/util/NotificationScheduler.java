package junkuvo.apps.meallogger.util;

import android.content.Context;
import android.os.Build;

import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class NotificationScheduler {
    private static final String TAG = NotificationScheduler.class.getSimpleName();
    private final NotificationScheduler self = this;
    private static final int NUMBER_OF_ONE_WEEK = 7;

    private Context mContext;

    public NotificationScheduler(Context mContext) {
        this.mContext = mContext;
    }

    public Calendar createNextNotifySchedule(){
        // 通知時間取得
        int hour;
        int minute;
        boolean[] weekDays = new boolean[NUMBER_OF_ONE_WEEK];
        // In Calendar.java day of week order e.g Sun = 1 ... Sat = 7
        String[] dayOfWeekString = new DateFormatSymbols().getShortWeekdays();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int idx;
        for (idx = 1; idx <= weekDays.length; idx++){
            weekDays[idx - 1] = (com.codetroopers.betterpickers.SharedPreferencesUtil.getBoolean(mContext,dayOfWeekString[idx]));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = com.codetroopers.betterpickers.SharedPreferencesUtil.getInt(mContext, RecurrencePickerDialogFragment.SHARED_PREF_KEY_HOUR);
            minute = com.codetroopers.betterpickers.SharedPreferencesUtil.getInt(mContext, RecurrencePickerDialogFragment.SHARED_PREF_KEY_MINUTE);
        }else{
            hour = com.codetroopers.betterpickers.SharedPreferencesUtil.getInt(mContext, RecurrencePickerDialogFragment.SHARED_PREF_KEY_HOUR);
            minute = com.codetroopers.betterpickers.SharedPreferencesUtil.getInt(mContext, RecurrencePickerDialogFragment.SHARED_PREF_KEY_MINUTE);
        }

        boolean decidedFlag = false;
        // 今日で今の時刻以降 or 明日以降
        if(weekDays[today] && (calendar.get(Calendar.HOUR_OF_DAY) < hour // 時刻が未来
                || (calendar.get(Calendar.HOUR_OF_DAY) == hour && calendar.get(Calendar.MINUTE) < minute))) {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            decidedFlag = true;
        }

        // 翌日以降の今週中
        if(!decidedFlag) {
            for (idx = today + 1; idx < weekDays.length; idx++) {
                if (weekDays[idx]) {
                    calendar.set(Calendar.DAY_OF_WEEK, idx + 1);
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    decidedFlag = true;
                }
            }
        }

        // 翌日以降の今週中
        if(!decidedFlag) {
            for (idx = 0; idx <= today; idx++) {
                if (weekDays[idx]) {
                    calendar.add(Calendar.DATE, NUMBER_OF_ONE_WEEK);
                    calendar.set(Calendar.DAY_OF_WEEK, idx + 1);
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    decidedFlag = true;
                }
            }
        }

        return calendar;
    }
}
