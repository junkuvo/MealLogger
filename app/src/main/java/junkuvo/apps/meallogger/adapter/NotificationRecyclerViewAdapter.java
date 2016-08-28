package junkuvo.apps.meallogger.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import junkuvo.apps.meallogger.ActivityLogListAll;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.entity.NotificationTimes;
import junkuvo.apps.meallogger.view.TimerListRowViewHolder;

public class NotificationRecyclerViewAdapter extends RealmRecyclerViewAdapter<NotificationTimes, TimerListRowViewHolder>
        implements RecurrencePickerDialogFragment.OnRecurrenceSetListener{

    private static final String TAG = NotificationRecyclerViewAdapter.class.getSimpleName();
    private final NotificationRecyclerViewAdapter self = this;

    private Context mContext;

    private Realm realm;
    private AlertDialog.Builder mAlertDialog;
    private FragmentManager mFragmentManager;

    public NotificationRecyclerViewAdapter(Context context, OrderedRealmCollection<NotificationTimes> data) {
        super(context ,data, true);
        this.mContext = context;
    }

    public NotificationRecyclerViewAdapter(Context context, OrderedRealmCollection<NotificationTimes> data, FragmentManager fragmentManager) {
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

        // Return a new holder instance
        TimerListRowViewHolder viewHolder = new TimerListRowViewHolder(mContext, rowView);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String title = ((TextView)v.findViewById(R.id.txtTitle)).getText().toString();
                // item clicked
                Bundle bundle = new Bundle();
                Time time = new Time();
                time.setToNow();
                bundle.putLong(RecurrencePickerDialogFragment.BUNDLE_START_TIME_MILLIS, time.toMillis(false));
                bundle.putString(RecurrencePickerDialogFragment.BUNDLE_TIME_ZONE, time.timezone);
                bundle.putString(RecurrencePickerDialogFragment.BUNDLE_TITLE,title);

                // may be more efficient to serialize and pass in EventRecurrence
                bundle.putString(RecurrencePickerDialogFragment.BUNDLE_RRULE, null);

                RecurrencePickerDialogFragment rpd = (RecurrencePickerDialogFragment) mFragmentManager.findFragmentByTag(ActivityLogListAll.FRAG_TAG_RECUR_PICKER);
                if (rpd != null) {
                    rpd.dismiss();
                }
                rpd = new RecurrencePickerDialogFragment();
                rpd.setArguments(bundle);
                rpd.setOnRecurrenceSetListener(NotificationRecyclerViewAdapter.this);
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
                mAlertDialog.setMessage("「" + ((TextView)v.findViewById(R.id.txtTitle)).getText() + "」を削除します。"); // TODO : 引数わたしみたいに
                mAlertDialog.setIcon(R.drawable.ic_delete_forever_black_48dp);
                mAlertDialog.setPositiveButton(mContext.getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realm = Realm.getDefaultInstance();
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                // realm はUIスレッドから変更できない
                                final RealmResults<NotificationTimes> result =
                                        bgRealm.where(NotificationTimes.class)
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

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TimerListRowViewHolder holder, int position) {
        NotificationTimes notificationTimes = getData().get(position);
        holder.getTxtTitle().setText(notificationTimes.getmTitle());
        holder.getTxtTime().setText(notificationTimes.getmTime());
        holder.getTxtDays().setText(notificationTimes.getmDays());
        holder.getTxtId().setText(String.valueOf(notificationTimes.getId()));
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
