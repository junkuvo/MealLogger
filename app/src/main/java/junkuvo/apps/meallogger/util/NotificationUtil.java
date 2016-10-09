package junkuvo.apps.meallogger.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import junkuvo.apps.meallogger.ActivityLogListAll;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.receiver.NotificationEventReceiver;

public class NotificationUtil {
    private static final String TAG = NotificationUtil.class.getSimpleName();
    private final NotificationUtil self = this;

    public void showTimerDoneNotification(Context context, String notificationName) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.ic_stat);

        // Large icon appears on the left of the notification
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setTicker(context.getString(R.string.notification_ticker));

        if (SharedPreferencesUtil.getString(context, ActivityLogListAll.PREF_KEY_MEAL_NAME + notificationName) != null
                && SharedPreferencesUtil.getString(context, ActivityLogListAll.PREF_KEY_MEAL_PRICE + notificationName) != null) {
            builder.setContentText("前回の" + notificationName + "は "
                    + SharedPreferencesUtil.getString(context, ActivityLogListAll.PREF_KEY_MEAL_NAME + notificationName)
                    + " " + SharedPreferencesUtil.getString(context, ActivityLogListAll.PREF_KEY_MEAL_PRICE + notificationName) + "でした");
            builder.addAction(R.drawable.ic_action_meal_done, "前回の「" + notificationName + "」と同じ", getPendingIntentWithBroadcast(context, NotificationEventReceiver.ADD_NOTIFICATION));
//            builder.addAction(R.drawable.ic_stat, context.getString(R.string.notification_addNew), getPendingIntentWithBroadcast(context, NotificationEventReceiver.CLICK_NOTIFICATION));
            // Content text, which appears in smaller text below the title
            builder.setSubText("この通知を下へスワイプすると操作を短縮できます");
        } else {
            // The subtext, which appears under the text on newer devices.
            // This will show-up in the devices with Android 4.2 and above only
            builder.setSubText(context.getString(R.string.notification_subMessage));
            builder.setContentText(context.getString(R.string.notification_message));
        }
        builder.setContentIntent(getPendingIntentWithBroadcast(context, NotificationEventReceiver.CLICK_NOTIFICATION));
        builder.setDeleteIntent(getPendingIntentWithBroadcast(context, NotificationEventReceiver.DELETE_NOTIFICATION));

        long[] pattern = {500, 1000, 500, 1000}; // OFF/ON/OFF/ON...
        builder.setVibrate(pattern);
        // Will display the notification in the notification bar

        // ロックスクリーン上でどう見えるか
        builder.setVisibility(Notification.VISIBILITY_PUBLIC);

        builder.setPriority(Notification.PRIORITY_MAX);

        // 通知の種類 システムがうまいこと扱ってくれる https://developer.android.com/reference/android/app/Notification.html?hl=ja
        builder.setCategory(Notification.CATEGORY_ALARM);

        notificationManager.notify(R.string.app_name, builder.build());
    }

    public void cancelNotification(Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    private PendingIntent getPendingIntentWithBroadcast(Context context, String action) {
        return PendingIntent.getBroadcast(context, 0, new Intent(action), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public Notification createNotification(Context context) {

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.ic_stat_empty);

//        // This intent is fired when notification is clicked
//        Intent intent = new Intent(context, ActivityLogListAll.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//        // Set the intent that will fire when the user taps the notification.
//        builder.setContentIntent(pendingIntent);
        builder.setContentIntent(getPendingIntentWithBroadcast(context, NotificationEventReceiver.CLICK_SERVICE_NOTIFICATION));

        // Large icon appears on the left of the notification
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setTicker(context.getString(R.string.notification_ticker));

        // Content text, which appears in smaller text below the title
        builder.setContentText(context.getString(R.string.notification_message));

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText(context.getString(R.string.notification_subMessage));

        // ロックスクリーン上でどう見えるか
        builder.setVisibility(Notification.VISIBILITY_SECRET);

        // PRIORITY_MINだとどこにも表示されなくなる
        builder.setPriority(Notification.PRIORITY_MIN);

        return builder.build();
    }
}
