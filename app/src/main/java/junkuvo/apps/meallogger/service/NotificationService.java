package junkuvo.apps.meallogger.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import junkuvo.apps.meallogger.receiver.ReceivedActivity;

public class NotificationService extends Service {
    private static final String TAG = NotificationService.class.getSimpleName();
    private final NotificationService self = this;

    private Context mContext;
    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmIntent;
    private ReceivedActivity mReceivedActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
//        handleOnStart(intent,startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        handleOnStart(intent,startId);
//        Toast.makeText(mContext, "onStartCommand called by "
//                + Thread.currentThread().getName(), Toast.LENGTH_LONG).show();



//            mHandler.postDelayed(new Runnable() {
//                public void run() {
//                    showTimerDoneNotification();
//                }
//            }, 10000);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mReceivedActivity != null) {
            unregisterReceiver(mReceivedActivity);
        }
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

    private void handleOnStart(Intent intent, int startId) {
//        startForeground(1, new Notification());

    }

    public class NotificationBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

}


