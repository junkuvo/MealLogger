package junkuvo.apps.meallogger;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

import junkuvo.apps.meallogger.fragment.FragmentAlertDialog;

public class ActivityLogRegister extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        FragmentAlertDialog fragment = new FragmentAlertDialog();
        String notificationName = getIntent().getStringExtra(ActivityLogListAll.INTENT_KEY_NOTIFICATION_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(ActivityLogListAll.INTENT_KEY_NOTIFICATION_NAME, notificationName);
        fragment.setArguments(bundle);
        fragment.show(getFragmentManager(),"ActivityLogRegister");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}
