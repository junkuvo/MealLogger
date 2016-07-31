package junkuvo.apps.meallogger.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import junkuvo.apps.meallogger.ActivityLogRegister;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.receiver.ReceivedActivity;

public class NotificationUtil {
    private static final String TAG = NotificationUtil.class.getSimpleName();
    private final NotificationUtil self = this;

    public void showTimerDoneNotification(Context context) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.ic_stat);

        // This intent is fired when notification is clicked
        Intent intent = new Intent(context, ActivityLogRegister.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_stat));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle("Notifications Title");
        builder.setTicker("Ticker！");

        // Content text, which appears in smaller text below the title
        builder.setContentText("Your notification content here.");

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText("Tap to view documentation about notifications.");

        builder.addAction(R.drawable.ic_add, context.getString(R.string.app_name),getPendingIntentWithBroadcast(context, ReceivedActivity.ADD_NOTIFICATION));
        builder.setContentIntent(getPendingIntentWithBroadcast(context, ReceivedActivity.CLICK_NOTIFICATION));
        builder.setDeleteIntent(getPendingIntentWithBroadcast(context, ReceivedActivity.DELETE_NOTIFICATION));

        long[] pattern = {100, 100, 100, 100}; // OFF/ON/OFF/ON...
        builder.setVibrate(pattern);
        // Will display the notification in the notification bar
        notificationManager.notify(R.string.app_name, builder.build());
    }

    private PendingIntent getPendingIntentWithBroadcast(Context context, String action) {
        return PendingIntent.getBroadcast(context, 0 , new Intent(action), 0);
    }
}
