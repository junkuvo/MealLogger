package junkuvo.apps.meallogger.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.util.NotificationUtil;

public class NotificationService extends Service {
    private static final String TAG = NotificationService.class.getSimpleName();
    private final NotificationService self = this;

    private Context mContext;

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
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // BindしたServiceをActivityに返す
    @Override
    public IBinder onBind(Intent intent) {
        return null;//new NotificationBinder();
    }

    @Override
    public void onRebind(Intent intent) {
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true; // 再度クライアントから接続された際に onRebind を呼び出させる場合は true を返す
    }

    private void handleOnStart(Intent intent, int startId) {
        NotificationUtil notificationUtil = new NotificationUtil();
        startForeground(R.string.app_name, notificationUtil.createNotification(mContext));
    }

    public class NotificationBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

}


