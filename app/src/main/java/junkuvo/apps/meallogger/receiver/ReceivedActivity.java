package junkuvo.apps.meallogger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import junkuvo.apps.meallogger.util.NotificationUtil;

public class ReceivedActivity extends BroadcastReceiver {
    private static final String TAG = ReceivedActivity.class.getSimpleName();
    private final ReceivedActivity self = this;
    private NotificationUtil mNotificationUtil;


    public ReceivedActivity() {
        mNotificationUtil = new NotificationUtil();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationUtil.showTimerDoneNotification(context);
        Toast.makeText(context, "called ReceivedActivity", Toast.LENGTH_SHORT).show();
    }

}
