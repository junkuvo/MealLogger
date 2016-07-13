package junkuvo.apps.meallogger.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import junkuvo.apps.meallogger.ActivityLogListAll;
import junkuvo.apps.meallogger.R;

public class NotificationService extends Service {
    private static final String TAG = NotificationService.class.getSimpleName();
    private final NotificationService self = this;

    Context mContext;
    Timer mTimer;
    TimerTask timerTask;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        handleOnStart(intent,startId);
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        onStart(intent, startId);
        handleOnStart(intent,startId);
        Toast.makeText(mContext, "onStartCommand called by "
                + Thread.currentThread().getName(), Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    // BindしたServiceをActivityに返す
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(mContext, "onBind called by "
                + Thread.currentThread().getName(), Toast.LENGTH_LONG).show();
        return new NotificationBinder();
    }

    @Override
    public void onRebind(Intent intent) {
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true; // 再度クライアントから接続された際に onRebind を呼び出させる場合は true を返す
    }

    public class NotificationBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

    private void handleOnStart(Intent intent, int startId) {
        startForeground(1, new Notification());
        showTimerDoneNotification();
    }

    private void showTimerDoneNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification(R.drawable.ic_add,"New Message", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, ActivityLogListAll.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
//
//        notification(MainActivity.this, "test","aaaaa", pendingIntent);
//        notificationManager.notify(9999, notification);

//        notification = new Notification.Builder(this)
//                .setSmallIcon(R.drawable.ic_add)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText(getString(R.string.app_name))
//                .setUsesChronometer(true)
//                .setWhen(System.currentTimeMillis())
//                .addAction(R.drawable.ic_add, getString(R.string.app_name),
//                        pendingIntent)
//                .build();
//        notificationManager.notify(1, notification);

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.ic_add);

        // This intent is fired when notification is clicked
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://stacktips.com/"));
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_add));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle("Notifications Title");

        // Content text, which appears in smaller text below the title
        builder.setContentText("Your notification content here.");

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText("Tap to view documentation about notifications.");

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(1, builder.build());
    }
}
