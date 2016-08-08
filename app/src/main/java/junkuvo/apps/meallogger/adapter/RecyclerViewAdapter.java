package junkuvo.apps.meallogger.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.entity.MealLogs;
import junkuvo.apps.meallogger.view.ListRowViewHolder;

public class RecyclerViewAdapter extends RealmRecyclerViewAdapter<MealLogs, ListRowViewHolder> {
    private static final String TAG = RecyclerViewAdapter.class.getSimpleName();
    private final RecyclerViewAdapter self = this;

    private Context mContext;
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private SimpleDateFormat mDateFormat;

    private Realm realm;
    private AlertDialog.Builder mAlertDialog;

    public RecyclerViewAdapter(Context context, OrderedRealmCollection<MealLogs> data) {
        super(context ,data, true);
        this.mContext = context;

        mDateFormat = new SimpleDateFormat(DATE_FORMAT);
    }

    @Override
    public ListRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View rowView = inflater.inflate(R.layout.list_row, null);

        // Return a new holder instance
        ListRowViewHolder viewHolder = new ListRowViewHolder(mContext, rowView);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // item clicked
                        Toast.makeText(mContext,"test", Toast.LENGTH_SHORT).show();
            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final long id = Long.parseLong(((TextView) v.findViewById(R.id.txtId)).getText().toString());
                // Handle long click
                mAlertDialog = new AlertDialog.Builder(mContext);
                mAlertDialog.setTitle(mContext.getString(R.string.dialog_title));
                mAlertDialog.setIcon(R.drawable.ic_delete_forever_black_48dp);
                mAlertDialog.setMessage(R.string.dialog_message);
                mAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realm = Realm.getDefaultInstance();
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                // realm はUIスレッドから変更できない
                                final RealmResults<MealLogs> result =
                                        bgRealm.where(MealLogs.class)
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
                mAlertDialog.setNegativeButton("CANCEL", null);
                mAlertDialog.create().show();
                return true;
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListRowViewHolder holder, int position) {
        MealLogs mealLogs = getData().get(position);
        holder.getTxtMealMenu().setText(mealLogs.getMenuName());
        if(mealLogs.getCreatedAt() != null) {
            holder.getTxtMealDate().setText(mDateFormat.format(mealLogs.getCreatedAt()));
        }
        holder.getImvThumbnail().setImageResource(mealLogs.getThumbnailResourceID());
        holder.getTxtPrice().setText(String.valueOf(mealLogs.getPrice()));
        holder.getTxtId().setText(String.valueOf(mealLogs.getId()));

    }

//    /*
//    表示の上限ぽい
//     */
//    @Override
//    public int getItemCount() {
//        return mItemList.size();
//    }

//    @Override
//    public boolean onLongClick(View view) {
//        // RecyclerViewのせいか？呼ばれない
//        final RealmResults<MealLogs> result =
//                realm.where(MealLogs.class)
//                        .equalTo("id", ((TextView)view.findViewById(R.id.txtId)).getText().toString())
//                        .findAll();
//
//        // Handle long click
//        mAlertDialog = new AlertDialog.Builder(mContext);
//        mAlertDialog.setTitle(mContext.getString(R.string.action_settings));
//        mAlertDialog.setIcon(android.R.drawable.ic_menu_manage);
//        mAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                realm = Realm.getDefaultInstance();
//                realm.executeTransactionAsync(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm bgRealm) {
//                        result.deleteFromRealm(0);
//                    }
//                }, new Realm.Transaction.OnSuccess() {
//                    @Override
//                    public void onSuccess() {
//                    }
//                }, new Realm.Transaction.OnError() {
//                    @Override
//                    public void onError(Throwable error) {
//                        // トランザクションは失敗。自動的にキャンセルされます
//                    }
//                });
//            }
//        });
//        mAlertDialog.setNegativeButton("CANCEL", null);
//        mAlertDialog.create().show();

//        return true; // true : avoid to fire onClick(v)
//    }


}
