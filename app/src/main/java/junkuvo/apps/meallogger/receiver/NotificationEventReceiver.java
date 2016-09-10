package junkuvo.apps.meallogger.receiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import junkuvo.apps.meallogger.ActivityLogListAll;
import junkuvo.apps.meallogger.Application;
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
    public static final String CLICK_NOTIFICATION = "click_notification";
    public static final String DELETE_NOTIFICATION = "delete_notification";
    public static final String CLICK_SERVICE_NOTIFICATION = "click_service_notification";

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
                Bundle bundle = intent.getExtras();
                String notificationName = bundle.getString(ActivityLogListAll.INTENT_KEY_NOTIFICATION_NAME);
                mNotificationUtil.showTimerDoneNotification(mContext, notificationName);
                break;

            case ADD_NOTIFICATION: // 前回と同じ
                realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        MealLogs mealLogs = bgRealm.createObject(MealLogs.class);
                        // 下記がnullであればADD_NOTIFICATIONはBroadcastされないのでnull判定は不要
                        String mealName = SharedPreferencesUtil.getString(mContext, ActivityLogListAll.PREF_KEY_MEAL_NAME);
                        String price = SharedPreferencesUtil.getString(mContext, ActivityLogListAll.PREF_KEY_MEAL_PRICE);

                        mealLogs.setMealLog(R.mipmap.ic_launcher, mealName, new Date(System.currentTimeMillis()), PriceUtil.parsePriceToLong(price,"¥"));
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
                break;
            case DELETE_NOTIFICATION:
            case CLICK_NOTIFICATION:
                // 次回のアラームを設定　Fixme:これは通知を出したタイミングがいいのでは？
                // TODO : ここから開いた場合は新規登録ダイアログを開いておきたい
                NotificationScheduler notificationScheduler = new NotificationScheduler(mContext);
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Calendar calendar = notificationScheduler.createNextNotifySchedule();
                Intent broadCastIntent = new Intent(NotificationEventReceiver.ACTION_ALARM);
                broadCastIntent.putExtra(ActivityLogListAll.INTENT_KEY_NOTIFICATION_NAME, ((Application)((Activity)mContext).getApplication()).mNotificationScheduleName);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, broadCastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                // onCreateから呼びなおしているため、通知も元に戻る
                mContext.startActivity(new Intent(mContext, ActivityLogListAll.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            case CLICK_SERVICE_NOTIFICATION:
                mContext.startActivity(new Intent(mContext, ActivityLogListAll.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
        }
    }

}
