package junkuvo.apps.meallogger.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;

import java.util.Calendar;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import junkuvo.apps.meallogger.ActivityLogListAll;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.entity.NotificationTime;
import junkuvo.apps.meallogger.receiver.NotificationEventReceiver;
import junkuvo.apps.meallogger.util.NotificationScheduler;
import junkuvo.apps.meallogger.util.SharedPreferencesUtil;
import junkuvo.apps.meallogger.view.TimerListRowViewHolder;

public class NotificationRecyclerViewAdapter extends RealmRecyclerViewAdapter<NotificationTime, TimerListRowViewHolder>
        implements RecurrencePickerDialogFragment.OnRecurrenceSetListener{

    private static final String TAG = NotificationRecyclerViewAdapter.class.getSimpleName();
    private final NotificationRecyclerViewAdapter self = this;

    private Context mContext;

    private Realm realm;
    private AlertDialog.Builder mAlertDialog;
    private FragmentManager mFragmentManager;
    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmIntent;

    public NotificationRecyclerViewAdapter(Context context, OrderedRealmCollection<NotificationTime> data) {
        super(context ,data, true);
        this.mContext = context;
    }

    public NotificationRecyclerViewAdapter(Context context, OrderedRealmCollection<NotificationTime> data, FragmentManager fragmentManager) {
        super(context ,data, true);
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
    }

    @Override
    public TimerListRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View rowView = inflater.inflate(R.layout.time_list_row, null);
        realm = Realm.getDefaultInstance();

        // Return a new holder instance
        TimerListRowViewHolder viewHolder = new TimerListRowViewHolder(mContext, rowView);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final long id = Long.parseLong(((TextView)v.findViewById(R.id.txtId)).getText().toString());
                String title = ((TextView)v.findViewById(R.id.txtTitle)).getText().toString();
                int hour = Integer.parseInt(((TextView)v.findViewById(R.id.txtTime)).getText().toString().split(":")[0]);
                int minute = Integer.parseInt(((TextView)v.findViewById(R.id.txtTime)).getText().toString().split(":")[1]);
                // item clicked
                Bundle bundle = new Bundle();
                Time time = new Time();
                time.setToNow();
                bundle.putLong(RecurrencePickerDialogFragment.BUNDLE_START_TIME_MILLIS, time.toMillis(false));
                bundle.putString(RecurrencePickerDialogFragment.BUNDLE_TIME_ZONE, time.timezone);
                bundle.putString(RecurrencePickerDialogFragment.BUNDLE_TITLE,title);
                bundle.putInt(RecurrencePickerDialogFragment.BUNDLE_HOUR,hour);
                bundle.putInt(RecurrencePickerDialogFragment.BUNDLE_MINUTE,minute);

                String dayStr = ((TextView)v.findViewById(R.id.txtDays)).getText().toString();
                String[] days = dayStr.split("");
                bundle.putStringArray(RecurrencePickerDialogFragment.BUNDLE_DAYS, days);

                // may be more efficient to serialize and pass in EventRecurrence
                bundle.putString(RecurrencePickerDialogFragment.BUNDLE_RRULE, null);

                RecurrencePickerDialogFragment rpd = (RecurrencePickerDialogFragment) mFragmentManager.findFragmentByTag(ActivityLogListAll.FRAG_TAG_RECUR_PICKER);
                if (rpd != null) {
                    rpd.dismiss();
                }
                rpd = new RecurrencePickerDialogFragment();
                rpd.setArguments(bundle);
                rpd.setOnRecurrenceSetListener(NotificationRecyclerViewAdapter.this);
                rpd.setOnOkBtnClickListener(new RecurrencePickerDialogFragment.OnOkBtnClickListener() {
                    @Override
                    public void onOkClicked(final View view) {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                final RealmResults<NotificationTime> result = bgRealm.where(NotificationTime.class).equalTo("id", id).findAll();

                                // TODO : これはUtilクラスに移殖
                                String notificationTitle = ((EditText)view.findViewById(com.codetroopers.betterpickers.R.id.txtNotificationTitle)).getText().toString();
                                Integer hour;
                                int minute;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    hour = ((TimePicker)view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getHour();
                                    minute = ((TimePicker)view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getMinute();
                                } else {
                                    hour = ((TimePicker)view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getCurrentHour();
                                    minute = ((TimePicker)view.findViewById(com.codetroopers.betterpickers.R.id.timePicker)).getCurrentMinute();
                                }
                                //　ここも共通化できる
                                String hourStr = String.valueOf(hour).length() == 1 ? "0" + String.valueOf(hour) : String.valueOf(hour);
                                String minuteStr = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
                                String notificationTime = hourStr + ":" + minuteStr;
                                StringBuilder sb = new StringBuilder();
                                ToggleButton toggleButton;
                                for(int i = 0; i < 7;i++) {
                                    toggleButton = (ToggleButton) ((LinearLayout) view.findViewById(com.codetroopers.betterpickers.R.id.weekGroup)).getChildAt(i);
                                    sb.append(toggleButton.isChecked() ? toggleButton.getTextOn() : "");
                                }

                                result.get(0).setmDays(sb.toString());
                                result.get(0).setmTitle(notificationTitle);
                                result.get(0).setmTime(notificationTime);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                if(mAlarmIntent != null) {
                                    mAlarmManager.cancel(mAlarmIntent);
                                }
                                NotificationScheduler notificationScheduler = new NotificationScheduler(mContext);
                                Calendar calendar = notificationScheduler.createNextNotifySchedule();
                                mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                                Intent broadCastIntent = new Intent(NotificationEventReceiver.ACTION_ALARM);
                                broadCastIntent.putExtra(ActivityLogListAll.INTENT_KEY_NOTIFICATION_NAME, SharedPreferencesUtil.getString(mContext, ActivityLogListAll.PREF_KEY_NOTIFICATION_NAME));
                                mAlarmIntent = PendingIntent.getBroadcast(mContext, 0, broadCastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mAlarmIntent);
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                // トランザクションは失敗。自動的にキャンセルされます
                            }
                        });
                    }
                });
                rpd.show(mFragmentManager, ActivityLogListAll.FRAG_TAG_RECUR_PICKER);
            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final long id = Long.parseLong(((TextView) v.findViewById(R.id.txtId)).getText().toString());
                // Handle long click
                mAlertDialog = new AlertDialog.Builder(mContext);
                mAlertDialog.setTitle(mContext.getString(R.string.dialog_time_delete));
                mAlertDialog.setMessage("「" + ((TextView)v.findViewById(R.id.txtTitle)).getText() + "」を削除してよろしいですか？"); // TODO : 引数わたしみたいに
                mAlertDialog.setIcon(R.drawable.ic_delete_forever_black_48dp);
                mAlertDialog.setPositiveButton(mContext.getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realm = Realm.getDefaultInstance();
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                // realm はUIスレッドから変更できない
                                final RealmResults<NotificationTime> result =
                                        bgRealm.where(NotificationTime.class)
                                                .equalTo("id", id)
                                                .findAll();
                                result.deleteFromRealm(0);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                // トランザクションは失敗。自動的にキャンセルされます
                            }
                        });
                    }
                });
                mAlertDialog.setNegativeButton(mContext.getString(R.string.dialog_no), null);
                mAlertDialog.create().show();
                return true;
            }
        });

        // なぜかここで指定しないと中途半端な幅になる
        rowView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TimerListRowViewHolder holder, int position) {
        NotificationTime notificationTime = getData().get(position);
        holder.getTxtTitle().setText(notificationTime.getmTitle());
        String time = notificationTime.getmTime();
        String hour = time.split(":")[0];
        String minute = time.split(":")[1];

        holder.getTxtTime().setText(hour + ":" + minute);
        holder.getTxtDays().setText(notificationTime.getmDays());
        holder.getTxtId().setText(String.valueOf(notificationTime.getId()));

        if(Integer.parseInt(hour) >= 5 && Integer.parseInt(hour) < 10){
            holder.getImgNotificationIcon().setImageResource(R.drawable.ic_morning);
        }else if(Integer.parseInt(hour) >= 10 && Integer.parseInt(hour) < 15) {
            holder.getImgNotificationIcon().setImageResource(R.drawable.ic_noon);
        }else if(Integer.parseInt(hour) >= 15 && Integer.parseInt(hour) < 18){
            holder.getImgNotificationIcon().setImageResource(R.drawable.ic_evening);
        }else{
            holder.getImgNotificationIcon().setImageResource(R.drawable.ic_night);
        }
    }

    /*
//    表示の上限ぽい
//     */
//    @Override
//    public int getItemCount() {
//        return 5;
//    }

    @Override
    public void onRecurrenceSet(String rrule) {

    }
}
