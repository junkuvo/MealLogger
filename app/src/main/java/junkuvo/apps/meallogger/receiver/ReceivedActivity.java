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

public class ReceivedActivity extends BroadcastReceiver {
    private static final String TAG = ReceivedActivity.class.getSimpleName();
    private final ReceivedActivity self = this;

    private Realm realm;

    public static final String ACTION_ALARM = "ALARM";
    public static final String ADD_NOTIFICATION = "add";
    public static final String CLICK_NOTIFICATION = "click_notification";
    public static final String DELETE_NOTIFICATION = "delete_notification";

    private Context mContext;

    private NotificationUtil mNotificationUtil;


    public ReceivedActivity() {
        mNotificationUtil = new NotificationUtil();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        switch (intent.getAction()) {
            case ACTION_ALARM:
                mNotificationUtil.showTimerDoneNotification(mContext);
                Toast.makeText(context, "called ReceivedActivityaaa", Toast.LENGTH_SHORT).show();
                break;

            case ADD_NOTIFICATION:
                realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        MealLogs mealLogs = bgRealm.createObject(MealLogs.class);
                        // TODO : ここの値どうしよう。。。
                        mealLogs.setMealLog(R.mipmap.ic_launcher, "from Notification", new Date(System.currentTimeMillis()), "1000");
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
                NotificationScheduler notificationScheduler = new NotificationScheduler(mContext);
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Intent broadCastIntent = new Intent(ReceivedActivity.ACTION_ALARM);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, broadCastIntent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, notificationScheduler.createNotifySchedule().getTimeInMillis(), alarmIntent);

                mContext.startActivity(new Intent(mContext, ActivityLogListAll.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                mNotificationUtil.cancelNotification(mContext, R.string.app_name);
                break;
        }
    }

}
