package junkuvo.apps.meallogger.util;

import android.os.Handler;

import java.util.TimerTask;

public class TimerUtil extends TimerTask {
    private static final String TAG = TimerUtil.class.getSimpleName();
    private final TimerUtil self = this;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    @Override
    public void run() {
        new Runnable() {
            public void run() {
            }
        };
    }
//
//    public long getFireDurationMilliSec(Date){
//
//    }


}
