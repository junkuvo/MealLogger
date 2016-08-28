package junkuvo.apps.meallogger.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Date;

import io.realm.Realm;
import junkuvo.apps.meallogger.ActivityLogListAll;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.entity.MealLogs;
import junkuvo.apps.meallogger.util.NotificationScheduler;
import junkuvo.apps.meallogger.util.NotificationUtil;
import junkuvo.apps.meallogger.util.SharedPreferencesUtil;

public class NotificationEventReceiver extends BroadcastReceiver {
    private static final String TAG = NotificationEventReceiver.class.getSimpleName();
    private final NotificationEventReceiver self = this;

    private Realm realm;

    public static final String ACTION_ALARM = "ALARM";
    public static final String ADD_NOTIFICATION = "add";
    public static final String CLICK_NOTIFICATION = "click_notification";
    public static final String DELETE_NOTIFICATION = "delete_notification";

    private Context mContext;

    private NotificationUtil mNotificationUtil;


    public NotificationEventReceiver() {
        mNotificationUtil = new NotificationUtil();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        switch (intent.getAction()) {
            case ACTION_ALARM:
                mNotificationUtil.showTimerDoneNotification(mContext);
                break;

            case ADD_NOTIFICATION:
                realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        MealLogs mealLogs = bgRealm.createObject(MealLogs.class);
                        // 下記がnullであればADD_NOTIFICATIONはBroadcastされないのでnull判定は不要
                        String mealName = SharedPreferencesUtil.getString(mContext, ActivityLogListAll.PREF_KEY_MEAL_NAME);
                        String price = SharedPreferencesUtil.getString(mContext, ActivityLogListAll.PREF_KEY_MEAL_PRICE);

                        mealLogs.setMealLog(R.mipmap.ic_launcher, mealName, new Date(System.currentTimeMillis()), price);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        // トランザクションは成功
                        Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        // トランザクションは失敗。自動的にキャンセルされます
                    }
                });
                mNotificationUtil.cancelNotification(mContext, R.string.app_name);
                break;
            case DELETE_NOTIFICATION:
            case CLICK_NOTIFICATION:
                // 次回のアラームを設定　Fixme:これは通知を出したタイミングがいいのでは？
                NotificationScheduler notificationScheduler = new NotificationScheduler(mContext);
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Intent broadCastIntent = new Intent(NotificationEventReceiver.ACTION_ALARM);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, broadCastIntent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, notificationScheduler.createNextNotifySchedule().getTimeInMillis(), alarmIntent);

                mContext.startActivity(new Intent(mContext, ActivityLogListAll.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                mNotificationUtil.cancelNotification(mContext, R.string.app_name);
                break;
        }
    }

}
