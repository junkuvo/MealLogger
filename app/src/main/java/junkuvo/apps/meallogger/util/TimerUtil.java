package junkuvo.apps.meallogger.util;

import android.os.Handler;

import java.util.TimerTask;

public class TimerUtil extends TimerTask {
    private static final String TAG = TimerUtil.class.getSimpleName();
    private final TimerUtil self = this;
    private Handler handler = new Handler();

    @Override
    public void run() {
        // handlerを使って処理をキューイングする
        handler.post(new Runnable() {
            public void run() {
            }
        });
    }
}
