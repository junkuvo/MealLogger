package junkuvo.apps.meallogger.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import com.codetroopers.betterpickers.SharedPreferencesUtil;
import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;

import java.util.Calendar;

import junkuvo.apps.meallogger.receiver.ReceivedActivity;

public class NotificationService extends Service {
    private static final String ACTION_ALARM = "ALARM";
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

        mReceivedActivity = new ReceivedActivity();
        IntentFilter filter = new IntentFilter(ACTION_ALARM);
        registerReceiver(mReceivedActivity, filter);

        // 通知時間取得
        int hour;
        int minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = SharedPreferencesUtil.getInt(mContext, RecurrencePickerDialogFragment.SHARED_PREF_KEY_HOUR);
            minute = SharedPreferencesUtil.getInt(mContext, RecurrencePickerDialogFragment.SHARED_PREF_KEY_MINUTE);
        }else{
            hour = SharedPreferencesUtil.getInt(mContext, RecurrencePickerDialogFragment.SHARED_PREF_KEY_HOUR);
            minute = SharedPreferencesUtil.getInt(mContext, RecurrencePickerDialogFragment.SHARED_PREF_KEY_MINUTE);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent broadCastIntent = new Intent(mContext, ReceivedActivity.class);
        mAlarmIntent = PendingIntent.getBroadcast(mContext, 0, broadCastIntent, 0);

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mAlarmIntent);
//        mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, mAlarmIntent);
    }

    public class NotificationBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

}


