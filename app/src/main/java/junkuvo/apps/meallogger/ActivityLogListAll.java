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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.codetroopers.betterpickers.SharedPreferencesUtil;
import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;

import java.util.Calendar;
import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import junkuvo.apps.meallogger.adapter.LogListPagerAdapter;
import junkuvo.apps.meallogger.adapter.NotificationRecyclerViewAdapter;
import junkuvo.apps.meallogger.entity.MealLogs;
import junkuvo.apps.meallogger.entity.NotificationTime;
import junkuvo.apps.meallogger.receiver.NotificationEventReceiver;
import junkuvo.apps.meallogger.service.NotificationService;
import junkuvo.apps.meallogger.util.NotificationScheduler;
import junkuvo.apps.meallogger.util.NumberTextFormatter;
import junkuvo.apps.meallogger.util.PriceUtil;
import junkuvo.apps.meallogger.view.EllipseTextView;

public class ActivityLogListAll extends AppCompatActivity
        implements RecurrencePickerDialogFragment.OnRecurrenceSetListener{

    public static final String PREF_KEY_MEAL_NAME = "mealName";
    public static final String PREF_KEY_MEAL_PRICE = "mealPrice";
    public static final String PREF_KEY_NOTIFICATION_NAME = "prefnotificationScheduleName";
    public static final String PREF_KEY_NOTIFICATION_ID = "prefnotificationScheduleId";
    // TODO:不要
    public static final String INTENT_KEY_NOTIFICATION_NAME = "notificationScheduleName";


    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;

    public static final String FRAG_TAG_RECUR_PICKER = "recurPicker";
    private Realm realm;

    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmIntent;
    private EllipseTextView mEllipseTextView;
    private boolean mIsDialogShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEllipseTextView = (EllipseTextView)findViewById(R.id.txtSumPrice);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new LogListPagerAdapter(getSupportFragmentManager(),this));

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(ActivityLogListAll.this);
                final View layout;
                layout = inflater.inflate(R.layout.dialog_log_register, (ViewGroup) findViewById(R.id.layout_root));

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityLogListAll.this, android.R.layout.simple_dropdown_item_1line, MENUS);
                AutoCompleteTextView textView = (AutoCompleteTextView)layout.findViewById(R.id.edtMealMenu);
                textView.setAdapter(adapter);

                EditText priceText = (EditText) layout.findViewById(R.id.edtMealPrice);
                priceText.addTextChangedListener(new NumberTextFormatter(priceText, "#,###"));
                mAlertDialogBuilder = new AlertDialog.Builder(ActivityLogListAll.this);
                mAlertDialogBuilder.setTitle(getString(R.string.dialog_register_title));
                // TODO : いい感じのアイコン作成(箸と茶碗の画像)
                mAlertDialogBuilder.setIcon(R.drawable.ic_meal_done);
                mAlertDialogBuilder.setView(layout);
                mAlertDialogBuilder.setPositiveButton(getString(R.string.dialog_log_create), null);
                mAlertDialogBuilder.setNegativeButton(getString(R.string.dialog_log_cancel), null);
                final AlertDialog alertDialog = mAlertDialogBuilder.show();
                mIsDialogShown = true;
                Button buttonOK = alertDialog.getButton( DialogInterface.BUTTON_POSITIVE );
                buttonOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String menuName = ((EditText)layout.findViewById(R.id.edtMealMenu)).getText().toString();
                        String price = ((EditText)layout.findViewById(R.id.edtMealPrice)).getText().toString();
                        if(menuName.equals("") || price.equals("")){
                            Toast.makeText(ActivityLogListAll.this, "食べたもの・値段を入力してください", Toast.LENGTH_LONG).show();
                        }else {
                            realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    String menuName = ((EditText) layout.findViewById(R.id.edtMealMenu)).getText().toString();
                                    String price = ((EditText) layout.findViewById(R.id.edtMealPrice)).getText().toString();
                                    MealLogs mealLogs = bgRealm.createObject(MealLogs.class);
                                    mealLogs.setMealLog(R.mipmap.ic_launcher, menuName, new Date(System.currentTimeMillis()), PriceUtil.parsePriceToLong(price, "¥"));
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    // トランザクションは成功
                                    String menuName = ((EditText) layout.findViewById(R.id.edtMealMenu)).getText().toString();
                                    String price = ((EditText) layout.findViewById(R.id.edtMealPrice)).getText().toString();
                                    // TODO : 修正の場合もこれ ＋ 通知IDで各通知と紐付ける
                                    String notificationName = junkuvo.apps.meallogger.util.SharedPreferencesUtil.getString(getApplicationContext(), ActivityLogListAll.PREF_KEY_NOTIFICATION_NAME);
                                    SharedPreferencesUtil.saveString(getApplicationContext(), PREF_KEY_MEAL_NAME + notificationName, menuName);
                                    SharedPreferencesUtil.saveString(getApplicationContext(), PREF_KEY_MEAL_PRICE + notificationName, price);
                                    alertDialog.dismiss();
                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    // トランザクションは失敗。自動的にキャンセルされます
                                    error.printStackTrace();
                                }
                            });
                        }
                    }
                });
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

        EllipseTextView ellipseTextView = (EllipseTextView) findViewById(R.id.txtSumPrice);
        ellipseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), ((EllipseTextView)v).getText(),Toast.LENGTH_SHORT).show();
            }
        });

        setNotification();

        Intent intent = new Intent(ActivityLogListAll.this, NotificationService.class);
        intent.putExtra(INTENT_KEY_NOTIFICATION_NAME, SharedPreferencesUtil.getString(this,ActivityLogListAll.PREF_KEY_NOTIFICATION_NAME));
        // アプリのプロセス自体が消えるとアラームが実行されないのでサービスにしておく
        // 一度設定したアラームはプロセスを消すと消える
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume(){
        super.onResume();

    }


    // TODO : これをrealmのヒストリーから取れるようにしたいなぁ
    private static final String[] MENUS = new String[] {
            "ランチ", "夕食", "昼食", "大戸屋", "ご飯"
    };

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
        // 新規追加
        rpd.setOnOkBtnClickListener(new RecurrencePickerDialogFragment.OnOkBtnClickListener() {
            @Override
            public void onOkClicked(final View view) {
                realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        // TODO : ここでユーザ入力を登録
                        String notificationTitle = ((EditText)view.findViewById(com.codetroopers.betterpickers.R.id.txtTitle)).getText().toString();
                        int hour;
                        int minute;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = ((TimePicker)view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getHour();
                            minute = ((TimePicker)view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getMinute();
                        } else {
                            hour = ((TimePicker)view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getCurrentHour();
                            minute = ((TimePicker)view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getCurrentMinute();
                        }
                        String hourStr = String.valueOf(hour).length() == 1 ? "0" + String.valueOf(hour) : String.valueOf(hour);
                        String minuteStr = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
                        String notificationHour = hourStr + ":" + minuteStr;
                        StringBuilder sb = new StringBuilder();
                        ToggleButton toggleButton;
                        for(int i = 0; i < 7;i++) {
                            toggleButton = (ToggleButton) ((LinearLayout) view.findViewById(com.codetroopers.betterpickers.R.id.weekGroup)).getChildAt(i);
                            sb.append(toggleButton.isChecked() ? toggleButton.getTextOn() : "");
                        }
                        NotificationTime notificationTime = bgRealm.createObject(NotificationTime.class);
                        notificationTime.setNotificationTime(notificationTitle, notificationHour, sb.toString());
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        showNotificationScheduleDialog();
                        setNotification();
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
        mAlertDialogBuilder = new AlertDialog.Builder(ActivityLogListAll.this);
        mAlertDialogBuilder.setTitle(getString(R.string.dialog_scheduler_title));
        // TODO : いい感じのアイコン作成
        mAlertDialogBuilder.setIcon(R.drawable.ic_alarm_black_48dp);
        mAlertDialogBuilder.setMessage(getString(R.string.dialog_scheduler_message));
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityLogListAll.this));
        realm = Realm.getDefaultInstance();
        OrderedRealmCollection<NotificationTime> times = realm.where(NotificationTime.class).findAllAsync();
        NotificationRecyclerViewAdapter adapter = new NotificationRecyclerViewAdapter(ActivityLogListAll.this,times,fm);
        recyclerView.setAdapter(adapter);
        mAlertDialogBuilder.setView(layout);
        mAlertDialogBuilder.setPositiveButton(getString(R.string.dialog_time_cancel), null);
        mAlertDialogBuilder.setNegativeButton(getString(R.string.dialog_time_create), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showScheduleDialog();
            }
        });
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    public void setNotification(){
        if(mAlarmIntent != null) {
            mAlarmManager.cancel(mAlarmIntent);
        }
        NotificationScheduler notificationScheduler = new NotificationScheduler(this);
        Calendar calendar = notificationScheduler.createNextNotifySchedule();
        mAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent broadCastIntent = new Intent(NotificationEventReceiver.ACTION_ALARM);
        broadCastIntent.putExtra(INTENT_KEY_NOTIFICATION_NAME, SharedPreferencesUtil.getString(this,ActivityLogListAll.PREF_KEY_NOTIFICATION_NAME));
        mAlarmIntent = PendingIntent.getBroadcast(this, 0, broadCastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mAlarmIntent);
    }

    @Override
    public void onBackPressed(){
//        super.onBackPressed();
        if(mAlertDialog == null || !mAlertDialog.isShowing()) {
            mAlertDialogBuilder = new AlertDialog.Builder(ActivityLogListAll.this);
            mAlertDialogBuilder.setMessage("アプリを終了しますか？");
            mAlertDialogBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            mAlertDialogBuilder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            });
            mAlertDialog = mAlertDialogBuilder.create();
            mAlertDialog.show();
        }
    }
}
