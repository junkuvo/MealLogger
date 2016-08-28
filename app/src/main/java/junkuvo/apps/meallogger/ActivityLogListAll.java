package junkuvo.apps.meallogger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.codetroopers.betterpickers.SharedPreferencesUtil;
import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;

import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import junkuvo.apps.meallogger.adapter.LogListPagerAdapter;
import junkuvo.apps.meallogger.adapter.NotificationRecyclerViewAdapter;
import junkuvo.apps.meallogger.entity.MealLogs;
import junkuvo.apps.meallogger.entity.NotificationTimes;
import junkuvo.apps.meallogger.receiver.NotificationEventReceiver;
import junkuvo.apps.meallogger.service.NotificationService;
import junkuvo.apps.meallogger.util.NotificationScheduler;
import junkuvo.apps.meallogger.util.NumberTextFormatter;

public class ActivityLogListAll extends AppCompatActivity
        implements RecurrencePickerDialogFragment.OnRecurrenceSetListener{

    public static final String PREF_KEY_MEAL_NAME = "mealName";
    public static final String PREF_KEY_MEAL_PRICE = "mealPrice";


    private AlertDialog.Builder mAlertDialog;

    public static final String FRAG_TAG_RECUR_PICKER = "recurPicker";
    private Realm realm;

    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new LogListPagerAdapter(getSupportFragmentManager()));

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(ActivityLogListAll.this);
                final View layout;
                layout = inflater.inflate(R.layout.activity_log_register, (ViewGroup) findViewById(R.id.layout_root));
                EditText priceText = (EditText) layout.findViewById(R.id.edtMealPrice);
                priceText.addTextChangedListener(new NumberTextFormatter(priceText, "#,###"));
                mAlertDialog = new AlertDialog.Builder(ActivityLogListAll.this);
                mAlertDialog.setTitle(getString(R.string.dialog_register_title));
                // TODO : いい感じのアイコン作成
                mAlertDialog.setIcon(android.R.drawable.ic_menu_manage);
                mAlertDialog.setView(layout);
                mAlertDialog.setPositiveButton(getString(R.string.dialog_log_create), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realm = Realm.getDefaultInstance();
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                // TODO : ここでユーザ入力を登録
                                String menuName = ((EditText)layout.findViewById(R.id.edtMealMenu)).getText().toString();
                                String price = ((EditText)layout.findViewById(R.id.edtMealPrice)).getText().toString();
                                MealLogs mealLogs = bgRealm.createObject(MealLogs.class);
                                mealLogs.setMealLog(R.mipmap.ic_launcher, menuName, new Date(System.currentTimeMillis()), price);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                // トランザクションは成功
                                String menuName = ((EditText)layout.findViewById(R.id.edtMealMenu)).getText().toString();
                                String price = ((EditText)layout.findViewById(R.id.edtMealPrice)).getText().toString();
                                SharedPreferencesUtil.saveString(getApplicationContext(),PREF_KEY_MEAL_NAME,menuName);
                                SharedPreferencesUtil.saveString(getApplicationContext(),PREF_KEY_MEAL_PRICE,price);
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                // トランザクションは失敗。自動的にキャンセルされます
                            }
                        });
                    }
                });
                mAlertDialog.setNegativeButton(getString(R.string.dialog_log_cancel), null);

                mAlertDialog.create().show();
            }
        });

        FloatingActionButton fab_setting = (FloatingActionButton) findViewById(R.id.fab_setting);
        assert fab_setting != null;
        fab_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotificationScheduleDialog();
            }
        });

        Intent intent = new Intent(ActivityLogListAll.this, NotificationService.class);
        // アプリのプロセス自体が消えるとアラームが実行されないのでサービスにしておく
        // 一度設定したアラームはプロセスを消すと消える
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }
    private FragmentManager mFragmentManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_log_list_all, menu);

//        // メニューの要素を追加
//        MenuItem actionItem = menu.add(Menu.NONE, MENU_SETTING_ID, MENU_SETTING_ID, this.getString(R.string.app_name));
//        // SHOW_AS_ACTION_IF_ROOM:余裕があれば表示
//        actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        // アイコンを設定
//        actionItem.setIcon(R.drawable.ic_access_alarm_white_48dp);
        return true;
    }

    private static final int MENU_SETTING_ID = 0;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LayoutInflater inflater = LayoutInflater.from(ActivityLogListAll.this);
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == MENU_SETTING_ID) {
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
        Toast.makeText(this, "recurrence called by "
                + Thread.currentThread().getName(), Toast.LENGTH_LONG).show();

        // Todo : カレンダーの初期値
        // // TODO: 8/3/16 通知出すかどうかのON/OFF設定

        NotificationScheduler notificationScheduler = new NotificationScheduler(this);
        mAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent broadCastIntent = new Intent(NotificationEventReceiver.ACTION_ALARM);
        mAlarmIntent = PendingIntent.getBroadcast(this, 0, broadCastIntent, 0);

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, notificationScheduler.createNextNotifySchedule().getTimeInMillis(), mAlarmIntent);

    }

    public void showScheduleDialog(){
        FragmentManager fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        Time time = new Time();
        time.setToNow();
        bundle.putLong(RecurrencePickerDialogFragment.BUNDLE_START_TIME_MILLIS, time.toMillis(false));
        bundle.putString(RecurrencePickerDialogFragment.BUNDLE_TIME_ZONE, time.timezone);

        // may be more efficient to serialize and pass in EventRecurrence
        bundle.putString(RecurrencePickerDialogFragment.BUNDLE_RRULE, null);

        RecurrencePickerDialogFragment rpd = (RecurrencePickerDialogFragment) fm.findFragmentByTag(FRAG_TAG_RECUR_PICKER);
        if (rpd != null) {
            rpd.dismiss();
        }
        rpd = new RecurrencePickerDialogFragment();
        rpd.setArguments(bundle);
        rpd.setOnOkBtnClickListener(new RecurrencePickerDialogFragment.OnOkBtnClickListener() {
            @Override
            public void onOkClicked(final View view) {
                realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        // TODO : ここでユーザ入力を登録
                        String notifiationTitle = ((EditText)view.findViewById(com.codetroopers.betterpickers.R.id.txtTitle)).getText().toString();
                        int hour;
                        int minute;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = ((TimePicker)view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getHour();
                            minute = ((TimePicker)view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getMinute();
                        } else {
                            hour = ((TimePicker)view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getCurrentHour();
                            minute = ((TimePicker)view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getCurrentMinute();
                        }
                        String notificationTime = String.valueOf(hour) + ":" + String.valueOf(minute);
                        StringBuilder sb = new StringBuilder();
                        ToggleButton toggleButton;
                        for(int i = 0; i < 7;i++) {
                            toggleButton = (ToggleButton) ((LinearLayout) view.findViewById(com.codetroopers.betterpickers.R.id.weekGroup)).getChildAt(i);
                            sb.append(toggleButton.isChecked() ? toggleButton.getTextOn() : "");
                        }

//                        SharedPreferencesUtil.saveBoolean(getContext(),SHARED_PREF_KEY_REPEAT,mRepeatSwitch.isChecked());

                        NotificationTimes notificationTimes = bgRealm.createObject(NotificationTimes.class);
                        notificationTimes.setNotificationTime(notifiationTitle, notificationTime, sb.toString());
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        showNotificationScheduleDialog();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });
            }
        });
        rpd.setOnCancelBtnClickListener(new RecurrencePickerDialogFragment.OnCancelBtnClickListener() {
            @Override
            public void onCancelClicked() {
                showNotificationScheduleDialog();
            }
        });
        rpd.show(fm, FRAG_TAG_RECUR_PICKER);
    }

    public void showNotificationScheduleDialog(){
        // TODO: メモリリークしない？ まとめろ。
        LayoutInflater inflater = LayoutInflater.from(ActivityLogListAll.this);
        final View layout;
        layout = inflater.inflate(R.layout.dialog_time_list, null);

        FragmentManager fm = getSupportFragmentManager();
        mAlertDialog = new AlertDialog.Builder(ActivityLogListAll.this);
        mAlertDialog.setTitle(getString(R.string.dialog_scheduler_title));
        // TODO : いい感じのアイコン作成
        mAlertDialog.setIcon(R.drawable.ic_add_alarm_white_48dp);
        mAlertDialog.setMessage(getString(R.string.dialog_scheduler_message));
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityLogListAll.this));
        realm = Realm.getDefaultInstance();
        OrderedRealmCollection<NotificationTimes> times = realm.where(NotificationTimes.class).findAllAsync();
        NotificationRecyclerViewAdapter adapter = new NotificationRecyclerViewAdapter(ActivityLogListAll.this,times,fm);
        recyclerView.setAdapter(adapter);
        mAlertDialog.setView(layout);
        mAlertDialog.setPositiveButton(getString(R.string.dialog_time_create), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showScheduleDialog();
            }
        });
        mAlertDialog.setNegativeButton(getString(R.string.dialog_time_cancel), null);
        mAlertDialog.create().show();

    }
}
