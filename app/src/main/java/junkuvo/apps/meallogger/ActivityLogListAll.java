package junkuvo.apps.meallogger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;

import java.util.Date;

import io.realm.Realm;
import junkuvo.apps.meallogger.adapter.LogListPagerAdapter;
import junkuvo.apps.meallogger.entity.MealLogs;
import junkuvo.apps.meallogger.service.NotificationService;

public class ActivityLogListAll extends AppCompatActivity
        implements RecurrencePickerDialogFragment.OnRecurrenceSetListener{

    private AlertDialog.Builder mAlertDialog;

    private static final String FRAG_TAG_RECUR_PICKER = "recurPicker";
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new LogListPagerAdapter(getSupportFragmentManager()));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(ActivityLogListAll.this, ActivityMealRegister.class);
//                startActivity(intent);
                realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        MealLogs mealLogs = bgRealm.createObject(MealLogs.class);
                        mealLogs.setId(System.currentTimeMillis());
                        mealLogs.setMenuName("test");
                        mealLogs.setCreatedAt(new Date(System.currentTimeMillis()));
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        // トランザクションは成功
                        Intent intent = new Intent(ActivityLogListAll.this, ActivityLogListAll.class);
                        startActivity(intent);
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        // トランザクションは失敗。自動的にキャンセルされます
                    }
                });

            }
        });

        FloatingActionButton fab_setting = (FloatingActionButton) findViewById(R.id.fab_setting);
        assert fab_setting != null;
        fab_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Bundle bundle = new Bundle();
                Time time = new Time();
                time.setToNow();
                bundle.putLong(RecurrencePickerDialogFragment.BUNDLE_START_TIME_MILLIS, time.toMillis(false));
                bundle.putString(RecurrencePickerDialogFragment.BUNDLE_TIME_ZONE, time.timezone);

                // may be more efficient to serialize and pass in EventRecurrence
                bundle.putString(RecurrencePickerDialogFragment.BUNDLE_RRULE, null);

                RecurrencePickerDialogFragment rpd = (RecurrencePickerDialogFragment) fm.findFragmentByTag(
                        FRAG_TAG_RECUR_PICKER);
                if (rpd != null) {
                    rpd.dismiss();
                }
                rpd = new RecurrencePickerDialogFragment();
                rpd.setArguments(bundle);
                rpd.setOnRecurrenceSetListener(ActivityLogListAll.this);
                rpd.show(fm, FRAG_TAG_RECUR_PICKER);
            }
        });

        Intent intent = new Intent(ActivityLogListAll.this, NotificationService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_log_list_all, menu);

        // メニューの要素を追加
        MenuItem actionItem = menu.add(Menu.NONE, MENU_SETTING_ID, MENU_SETTING_ID, this.getString(R.string.app_name));
        // SHOW_AS_ACTION_IF_ROOM:余裕があれば表示
        actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        // アイコンを設定
        actionItem.setIcon(R.drawable.ic_access_alarm_white_48dp);
        return true;
    }

    private static final int MENU_SETTING_ID = 0;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LayoutInflater inflater = LayoutInflater.from(ActivityLogListAll.this);
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == MENU_SETTING_ID) {

//            FragmentManager fm = getSupportFragmentManager();
//            Bundle bundle = new Bundle();
//            Time time = new Time();
//            time.setToNow();
//            bundle.putLong(RecurrencePickerDialogFragment.BUNDLE_START_TIME_MILLIS, time.toMillis(false));
//            bundle.putString(RecurrencePickerDialogFragment.BUNDLE_TIME_ZONE, time.timezone);
//
//            // may be more efficient to serialize and pass in EventRecurrence
//            bundle.putString(RecurrencePickerDialogFragment.BUNDLE_RRULE, null);
//
//            RecurrencePickerDialogFragment rpd = (RecurrencePickerDialogFragment) fm.findFragmentByTag(
//                    FRAG_TAG_RECUR_PICKER);
//            if (rpd != null) {
//                rpd.dismiss();
//            }
//            rpd = new RecurrencePickerDialogFragment();
//            rpd.setArguments(bundle);
//            rpd.setOnRecurrenceSetListener(ActivityLogListAll.this);
//            rpd.show(fm, FRAG_TAG_RECUR_PICKER);
//


            View layout = inflater.inflate(R.layout.setting_time, (ViewGroup) findViewById(R.id.layout_root));
            mAlertDialog = new AlertDialog.Builder(this);
//            mAlertDialog.setTitle(this.getString(R.string.app_name));
            mAlertDialog.setIcon(R.drawable.ic_access_alarm_white_48dp);
            mAlertDialog.setView(layout);
            mAlertDialog.setNegativeButton(this.getString(R.string.app_name), null);
            mAlertDialog.create().show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        killAlertService();
    }

    public void killAlertService() {
        unbindService(mServiceConnection); // バインド解除
        mNotificationService.stopSelf(); // サービスは必要ないので終了させる。
    }

    private NotificationService mNotificationService;

    // ServiceとActivityをBindするクラス
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mNotificationService = ((NotificationService.NotificationBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mNotificationService = null;
        }
    };

    @Override
    public void onRecurrenceSet(String rrule) {
        Toast.makeText(this, "recuurence called by "
                + Thread.currentThread().getName(), Toast.LENGTH_LONG).show();

    }
}
