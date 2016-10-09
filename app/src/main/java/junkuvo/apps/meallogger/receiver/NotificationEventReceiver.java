package junkuvo.apps.meallogger.receiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import junkuvo.apps.meallogger.ActivityLogListAll;
import junkuvo.apps.meallogger.ActivityLogRegister;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.entity.MealLogs;
import junkuvo.apps.meallogger.util.NotificationScheduler;
import junkuvo.apps.meallogger.util.NotificationUtil;
import junkuvo.apps.meallogger.util.PriceUtil;
import junkuvo.apps.meallogger.util.SharedPreferencesUtil;

public class NotificationEventReceiver extends BroadcastReceiver {
    private static final String TAG = NotificationEventReceiver.class.getSimpleName();
    private final NotificationEventReceiver self = this;

    private Realm realm;

    public static final String ACTION_ALARM = "ALARM";
    public static final String ADD_NOTIFICATION = "add";
    public static final String ADD_DIALOG = "add_dialog";
    public static final String CLICK_NOTIFICATION = "click_notification";
    public static final String DELETE_NOTIFICATION = "delete_notification";
    public static final String CLICK_SERVICE_NOTIFICATION = "click_service_notification";

    private Context mContext;

    private NotificationUtil mNotificationUtil;
    private String mNotificationName;


    public NotificationEventReceiver() {
        mNotificationUtil = new NotificationUtil();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        switch (intent.getAction()) {
            case ACTION_ALARM:
                Bundle bundle = intent.getExtras();
                mNotificationName = bundle.getString(ActivityLogListAll.INTENT_KEY_NOTIFICATION_NAME);

                if (!SharedPreferencesUtil.getBoolean(mContext, ActivityLogListAll.PREF_KEY_SHOW_DIALOG_FLAG)) {
                    Intent dialogIntent = new Intent(context, ActivityLogRegister.class);
                    dialogIntent.putExtra(ActivityLogListAll.INTENT_KEY_NOTIFICATION_NAME, mNotificationName);
                    context.startActivity(dialogIntent);
                }

                mNotificationUtil.showTimerDoneNotification(mContext, mNotificationName);
                // 今回のNotificationの名前を保存しIDとして次の時にメニューと金額を利用する
                SharedPreferencesUtil.saveString(mContext, ActivityLogListAll.PREF_KEY_NOTIFICATION_NAME, mNotificationName);
                break;

            case ADD_NOTIFICATION: // 前回と同じ
                mNotificationName = SharedPreferencesUtil.getString(mContext, ActivityLogListAll.PREF_KEY_NOTIFICATION_NAME);
                realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        MealLogs mealLogs = bgRealm.createObject(MealLogs.class);
                        // 下記がnullであればADD_NOTIFICATIONのActionは発生しないのでnull判定は不要
                        String mealName = SharedPreferencesUtil.getString(mContext, ActivityLogListAll.PREF_KEY_MEAL_NAME + mNotificationName);
                        String price = SharedPreferencesUtil.getString(mContext, ActivityLogListAll.PREF_KEY_MEAL_PRICE + mNotificationName);

                        mealLogs.setMealLog(R.mipmap.ic_launcher, mealName, new Date(System.currentTimeMillis()), PriceUtil.parsePriceToLong(price, "¥"), mNotificationName);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        createNextNotification();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        // トランザクションは失敗。自動的にキャンセルされます
                    }
                });
                break;
            case ADD_DIALOG:
                createNextNotification();
                break;
            case DELETE_NOTIFICATION:
            case CLICK_NOTIFICATION:
                // 現時点での通知IDを取得
                mNotificationName = SharedPreferencesUtil.getString(mContext, ActivityLogListAll.PREF_KEY_NOTIFICATION_NAME);
                NotificationScheduler notificationScheduler = new NotificationScheduler(mContext);
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                // // TODO: 9/13/16 :  createNextNotifyScheduleからSharedPreferencesUtilの部分をぶんりしたい
                Calendar calendar = notificationScheduler.createNextNotifySchedule();
                Intent broadCastIntent = new Intent(NotificationEventReceiver.ACTION_ALARM);
                // 次のNotificationの名前を保存
                broadCastIntent.putExtra(ActivityLogListAll.INTENT_KEY_NOTIFICATION_NAME, SharedPreferencesUtil.getString(mContext, ActivityLogListAll.PREF_KEY_NOTIFICATION_NAME));
                PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, broadCastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                // onCreateから呼びなおしているため、通知も元に戻る
                Intent clickIntent = new Intent(mContext, ActivityLogListAll.class);
                clickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // TODO : このフラグとかもEnumにするべき？
                clickIntent.putExtra(ActivityLogListAll.INTENT_KEY_FROM_NOTIFICATION, true);
                clickIntent.putExtra(ActivityLogListAll.INTENT_KEY_NOTIFICATION_NAME, mNotificationName);
                mContext.startActivity(clickIntent);
                break;
            case CLICK_SERVICE_NOTIFICATION:
                mContext.startActivity(new Intent(mContext, ActivityLogListAll.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
        }
    }

    public void createNextNotification() {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationUtil notificationUtil = new NotificationUtil();
        notificationUtil.cancelNotification(mContext, R.string.app_name);
        notificationManager.notify(R.string.app_name, notificationUtil.createNotification(mContext));

        NotificationScheduler notificationScheduler = new NotificationScheduler(mContext);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        // // TODO: 9/13/16 :  createNextNotifyScheduleからSharedPreferencesUtilの部分をぶんりしたい
        Calendar calendar = notificationScheduler.createNextNotifySchedule();
        Intent broadCastIntent = new Intent(NotificationEventReceiver.ACTION_ALARM);
        // 次のNotificationの名前を保存
        broadCastIntent.putExtra(ActivityLogListAll.INTENT_KEY_NOTIFICATION_NAME, SharedPreferencesUtil.getString(mContext, ActivityLogListAll.PREF_KEY_NOTIFICATION_NAME));
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, broadCastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }
}
