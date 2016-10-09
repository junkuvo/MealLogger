package junkuvo.apps.meallogger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;
import com.github.florent37.tutoshowcase.TutoShowcase;

import java.util.Calendar;
import java.util.Date;

import icepick.Icepick;
import icepick.State;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import junkuvo.apps.meallogger.adapter.LogListPagerAdapter;
import junkuvo.apps.meallogger.adapter.NotificationRecyclerViewAdapter;
import junkuvo.apps.meallogger.entity.MealLogs;
import junkuvo.apps.meallogger.entity.MonthlyMealLog;
import junkuvo.apps.meallogger.entity.NotificationTime;
import junkuvo.apps.meallogger.entity.NotificationTimes;
import junkuvo.apps.meallogger.params.ParamDays;
import junkuvo.apps.meallogger.receiver.NotificationEventReceiver;
import junkuvo.apps.meallogger.service.NotificationService;
import junkuvo.apps.meallogger.util.NotificationScheduler;
import junkuvo.apps.meallogger.util.NumberTextFormatter;
import junkuvo.apps.meallogger.util.PriceUtil;
import junkuvo.apps.meallogger.util.SharedPreferencesUtil;
import junkuvo.apps.meallogger.view.EllipseTextView;

public class ActivityLogListAll extends AppCompatActivity {

    public static final String PREF_KEY_MEAL_NAME = "mealName";
    public static final String PREF_KEY_MEAL_PRICE = "mealPrice";
    public static final String PREF_KEY_NOTIFICATION_NAME = "prefnotificationScheduleName";
    private static final String PREF_KEY_INITIALIZED_FLAG = "initialized_flag";
    public static final String PREF_KEY_SHOW_DIALOG_FLAG = "show_dialog_flag";
    // TODO:不要
    public static final String INTENT_KEY_NOTIFICATION_NAME = "notificationScheduleName";
    public static final String INTENT_KEY_FROM_NOTIFICATION = "fromnotification";


    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;

    public static final String FRAG_TAG_RECUR_PICKER = "recurPicker";
    private Realm realm;

    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmIntent;
    private EllipseTextView mEllipseTextView;
    private boolean mIsDialogShown = false;
    private String mNotificationName;
    private boolean mIsFromNotification;
    private LogListPagerAdapter mLogListPagerAdapter;

    @State
    String mMenuName = "";
    @State
    String mPrice = "";
    @State
    public boolean mIsChecked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Icepick.restoreInstanceState(this, savedInstanceState);
        realm = Realm.getDefaultInstance();

        mEllipseTextView = (EllipseTextView) findViewById(R.id.txtSumPrice);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        mLogListPagerAdapter = new LogListPagerAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(mLogListPagerAdapter);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        mIsFromNotification = getIntent().getBooleanExtra(INTENT_KEY_FROM_NOTIFICATION, false);
        if (mIsFromNotification) {
            mNotificationName = getIntent().getStringExtra(INTENT_KEY_NOTIFICATION_NAME);
            showMealLogCreateDialog();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pager.getCurrentItem() == 0) {
                    showMealLogCreateDialog();
                } else {
                    Toast.makeText(getBaseContext(), "検討中。アイディア募集中です。", Toast.LENGTH_LONG).show();
                }
            }
        });

        final FloatingActionButton fab_setting = (FloatingActionButton) findViewById(R.id.fab_setting);
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
                Toast.makeText(getApplicationContext(), ((EllipseTextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        if (SharedPreferencesUtil.getBoolean(getApplicationContext(), PREF_KEY_INITIALIZED_FLAG)) {
            setNotification();
        } else {
            initializeSampleMealLog();
        }

        getWindow().setBackgroundDrawableResource(R.color.colorBackground);

        final TutoShowcase tutoShowcase = TutoShowcase.from(this);
        tutoShowcase.setContentView(R.layout.tutorial_app_welcome)
                .onClickContentView(R.id.txtNext, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tutoShowcase.dismiss();
                        final TutoShowcase tutoShowcaseNotification = TutoShowcase.from(ActivityLogListAll.this);
                        tutoShowcaseNotification.setContentView(R.layout.tutorial_notification)
                                .on(fab_setting)
                                .addCircle()
                                .onClick(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        tutoShowcaseNotification.dismiss();
                                        final TutoShowcase tutoShowcaseAddLog = TutoShowcase.from(ActivityLogListAll.this);
                                        tutoShowcaseAddLog.setContentView(R.layout.tutorial_add_log)
                                                .on(fab)
                                                .addCircle()
                                                .onClick(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        tutoShowcaseAddLog.dismiss();
                                                        showNotificationScheduleDialog();
                                                    }
                                                })
                                                .showOnce(String.valueOf(R.layout.tutorial_add_log));
                                    }
                                })
                                .showOnce(String.valueOf(R.layout.tutorial_notification));

                    }
                })
                .showOnce(String.valueOf(R.layout.tutorial_app_welcome));

        Intent intent = new Intent(ActivityLogListAll.this, NotificationService.class);
        intent.putExtra(INTENT_KEY_NOTIFICATION_NAME, SharedPreferencesUtil.getString(this, ActivityLogListAll.PREF_KEY_NOTIFICATION_NAME));
        // アプリのプロセス自体が消えるとアラームが実行されないのでサービスにしておく
        // 一度設定したアラームはプロセスを消すと消える
        startService(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    // TODO : これをrealmのヒストリーから取れるようにしたいなぁ
    private static final String[] MENUS = new String[]{
            "ランチ", "夕食", "昼食", "大戸屋", "ご飯"
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;
    }

    public void showScheduleDialog() {
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
        // 通知タイミングの新規作成
        rpd.setOnOkBtnClickListener(new RecurrencePickerDialogFragment.OnOkBtnClickListener() {
            @Override
            public void onOkClicked(final View view) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        String notificationTitle = ((EditText) view.findViewById(com.codetroopers.betterpickers.R.id.txtNotificationTitle)).getText().toString();
                        int hour;
                        int minute;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = ((TimePicker) view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getHour();
                            minute = ((TimePicker) view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getMinute();
                        } else {
                            hour = ((TimePicker) view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getCurrentHour();
                            minute = ((TimePicker) view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getCurrentMinute();
                        }
                        String hourStr = String.valueOf(hour).length() == 1 ? "0" + String.valueOf(hour) : String.valueOf(hour);
                        String minuteStr = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
                        String notificationHour = hourStr + ":" + minuteStr;
                        StringBuilder sb = new StringBuilder();
                        ToggleButton toggleButton;
                        for (int i = 0; i < 7; i++) {
                            toggleButton = (ToggleButton) ((LinearLayout) view.findViewById(com.codetroopers.betterpickers.R.id.weekGroup)).getChildAt(i);
                            sb.append(toggleButton.isChecked() ? toggleButton.getTextOn() : "");
                        }
                        NotificationTime notificationTime = new NotificationTime();
                        notificationTime.setNotificationTime(notificationTitle, notificationHour, sb.toString());
                        bgRealm.insert(notificationTime);
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


    public void showNotificationScheduleDialog() {
        // TODO: メモリリークしない？ まとめろ。
        LayoutInflater inflater = LayoutInflater.from(ActivityLogListAll.this);
        final View layout;
        layout = inflater.inflate(R.layout.dialog_time_list, null);

        mIsChecked = SharedPreferencesUtil.getBoolean(this, PREF_KEY_SHOW_DIALOG_FLAG);
        ((CheckBox) layout.findViewById(R.id.chkDialogNotify)).setChecked(mIsChecked);
        ((CheckBox) layout.findViewById(R.id.chkDialogNotify)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mIsChecked = b;
                SharedPreferencesUtil.saveBoolean(ActivityLogListAll.this, PREF_KEY_SHOW_DIALOG_FLAG, mIsChecked);
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        mAlertDialogBuilder = new AlertDialog.Builder(ActivityLogListAll.this);
        mAlertDialogBuilder.setTitle(getString(R.string.dialog_scheduler_title));
        mAlertDialogBuilder.setIcon(R.drawable.ic_alarm_black_48dp);
//        mAlertDialogBuilder.setMessage(getString(R.string.dialog_scheduler_message));
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityLogListAll.this));
        OrderedRealmCollection<NotificationTime> times = realm.where(NotificationTime.class).findAllAsync().sort("mTime");
        NotificationRecyclerViewAdapter adapter = new NotificationRecyclerViewAdapter(ActivityLogListAll.this, times, fm);
        recyclerView.setAdapter(adapter);
        mAlertDialogBuilder.setView(layout);
        mAlertDialogBuilder.setPositiveButton(getString(R.string.dialog_time_done), null);
        mAlertDialogBuilder.setNeutralButton(getString(R.string.dialog_time_create), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showScheduleDialog();
            }
        });
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    public void setNotification() {
        if (mAlarmIntent != null) {
            mAlarmManager.cancel(mAlarmIntent);
        }
        NotificationScheduler notificationScheduler = new NotificationScheduler(this);
        Calendar calendar = notificationScheduler.createNextNotifySchedule();
        mAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent broadCastIntent = new Intent(NotificationEventReceiver.ACTION_ALARM);
        broadCastIntent.putExtra(INTENT_KEY_NOTIFICATION_NAME, SharedPreferencesUtil.getString(this, ActivityLogListAll.PREF_KEY_NOTIFICATION_NAME));
        SharedPreferencesUtil.saveBoolean(this, "intent", mIsChecked);

        mAlarmIntent = PendingIntent.getBroadcast(this, 0, broadCastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mAlarmIntent);
    }

    public void showMealLogCreateDialog() {
        LayoutInflater inflater = LayoutInflater.from(ActivityLogListAll.this);
        final View layout;
        layout = inflater.inflate(R.layout.dialog_log_register, (ViewGroup) findViewById(R.id.layout_root));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityLogListAll.this, android.R.layout.simple_dropdown_item_1line, MENUS);
        final AutoCompleteTextView txtMealName = (AutoCompleteTextView) layout.findViewById(R.id.edtMealMenu);
        txtMealName.setAdapter(adapter);
        // savedInstanceを利用するために最新の入力値を保持
        txtMealName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mMenuName = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        final EditText priceText = (EditText) layout.findViewById(R.id.edtMealPrice);
        priceText.addTextChangedListener(new NumberTextFormatter(priceText, "#,###"));
        // savedInstanceを利用するために最新の入力値を保持
        priceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPrice = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        if (!mMenuName.equals("")) {
            txtMealName.setText(mMenuName);
        }
        if (!mPrice.equals("")) {
            priceText.setText(mPrice);
        }

        mAlertDialogBuilder = new AlertDialog.Builder(ActivityLogListAll.this);
        mAlertDialogBuilder.setTitle(getString(R.string.dialog_register_title));
        mAlertDialogBuilder.setIcon(R.drawable.ic_meal_done);
        mAlertDialogBuilder.setView(layout);
        mAlertDialogBuilder.setPositiveButton(getString(R.string.dialog_log_create), null);
        mAlertDialogBuilder.setNegativeButton(getString(R.string.dialog_log_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mMenuName = "";
                mPrice = "";
            }
        });
        mAlertDialogBuilder.setCancelable(false);

        if (mIsFromNotification) {
            mAlertDialogBuilder.setNeutralButton(getString(R.string.dialog_add_same, mNotificationName), null);
        }

        mAlertDialog = mAlertDialogBuilder.show();
        mIsDialogShown = true;
        // タップしても閉じないようにsetPositiveButtonでなくgetButton使う
        Button buttonOK = mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuName = ((EditText) layout.findViewById(R.id.edtMealMenu)).getText().toString();
                mPrice = ((EditText) layout.findViewById(R.id.edtMealPrice)).getText().toString();
                // 入力が空の場合
                if (mMenuName.equals("") || mPrice.equals("")) {
                    Toast.makeText(ActivityLogListAll.this, getString(R.string.validation_message), Toast.LENGTH_LONG).show();
                } else {
                    final MealLogs mealLogToInsert = new MealLogs();
                    mealLogToInsert.setMealLog(R.mipmap.ic_launcher, mMenuName, new Date(System.currentTimeMillis()), PriceUtil.parsePriceToLong(mPrice, "¥"), mNotificationName);
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            // createObjectより早い
                            bgRealm.insert(mealLogToInsert);
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            // トランザクションは成功
                            SharedPreferencesUtil.saveString(getApplicationContext(), PREF_KEY_MEAL_NAME + mNotificationName, mMenuName);
                            SharedPreferencesUtil.saveString(getApplicationContext(), PREF_KEY_MEAL_PRICE + mNotificationName, mPrice);
                            mMenuName = "";
                            mPrice = "";
                            mIsFromNotification = false;
                            mAlertDialog.dismiss();

                            final int year = mealLogToInsert.getYear();
                            final int month = mealLogToInsert.getMonth();
                            long sum;
                            RealmResults mealLogsForSum = realm.where(MealLogs.class).equalTo("month", month).equalTo("year", year).findAll();
                            sum = mealLogsForSum.sum("price").longValue();
                            final MonthlyMealLog monthlyMealLogToUpdate = new MonthlyMealLog();
                            monthlyMealLogToUpdate.setMonthlyMealLog(year, month, sum);

                            // AsyncによってUIスレッドとは別スレッドで処理
                            // execute中身はなるべく軽く
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.where(MonthlyMealLog.class).equalTo("month", month).equalTo("year", year).findAll().deleteAllFromRealm();
                                    realm.insert(monthlyMealLogToUpdate);
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    error.printStackTrace();
                                }
                            });
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

        Button buttonSameAsLastTime = mAlertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        buttonSameAsLastTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuName = SharedPreferencesUtil.getString(getApplicationContext(), PREF_KEY_MEAL_NAME + mNotificationName);
                mPrice = SharedPreferencesUtil.getString(getApplicationContext(), PREF_KEY_MEAL_PRICE + mNotificationName);
                txtMealName.setText(mMenuName);
                priceText.setText(mPrice);
            }
        });
    }

    private void initializeSampleMealLog() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                MealLogs mealLogs = new MealLogs();
                mealLogs.setMealLog(R.mipmap.ic_launcher, getString(R.string.sample_data_title), new Date(System.currentTimeMillis()), PriceUtil.parsePriceToLong("1000", "¥"), "test");
                bgRealm.insert(mealLogs);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                initializeNotificationTimesRealm();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // トランザクションは失敗。自動的にキャンセルされます
                error.printStackTrace();
            }
        });
    }

    private void initializeNotificationTimesRealm() {

        StringBuilder sb = new StringBuilder();
        sb.append(ParamDays.SUN.getLabel());
        sb.append(ParamDays.MON.getLabel());
        sb.append(ParamDays.TUE.getLabel());
        sb.append(ParamDays.WED.getLabel());
        sb.append(ParamDays.THU.getLabel());
        sb.append(ParamDays.FRI.getLabel());
        sb.append(ParamDays.SAT.getLabel());

        final String initialDays = sb.toString();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                NotificationTimes notificationTimes = new NotificationTimes();
                notificationTimes.notificationTimes = new RealmList<NotificationTime>();
                NotificationTime notificationTimesMorning = new NotificationTime();
                notificationTimesMorning.setNotificationTime(R.string.initial_notification_morning, getString(R.string.initial_notification_morning), "08:00", initialDays);
                notificationTimes.notificationTimes.add(notificationTimesMorning);
                NotificationTime notificationTimesLunch = new NotificationTime();
                notificationTimesLunch.setNotificationTime(R.string.initial_notification_evening, getString(R.string.initial_notification_evening), "12:00", initialDays);
                notificationTimes.notificationTimes.add(notificationTimesLunch);
                NotificationTime notificationTimesDinner = new NotificationTime();
                notificationTimesDinner.setNotificationTime(R.string.initial_notification_night, getString(R.string.initial_notification_night), "20:00", initialDays);
                notificationTimes.notificationTimes.add(notificationTimesDinner);
                bgRealm.insert(notificationTimes.notificationTimes);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                SharedPreferencesUtil.saveBoolean(getApplicationContext(), PREF_KEY_INITIALIZED_FLAG, true);
                setNotification();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (mAlertDialog == null || !mAlertDialog.isShowing()) {
            mAlertDialogBuilder = new AlertDialog.Builder(ActivityLogListAll.this);
            mAlertDialogBuilder.setMessage(getString(R.string.dialog_back_title));
            mAlertDialogBuilder.setPositiveButton(getString(R.string.dialog_back_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    stopService(new Intent(ActivityLogListAll.this, NotificationService.class));
                    finish();
                }
            });
            mAlertDialogBuilder.setNegativeButton(getString(R.string.dialog_back_no), new DialogInterface.OnClickListener() {
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
