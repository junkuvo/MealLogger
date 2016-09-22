package junkuvo.apps.meallogger.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.codetroopers.betterpickers.SharedPreferencesUtil;

import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import junkuvo.apps.meallogger.ActivityLogListAll;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.entity.MealLogs;
import junkuvo.apps.meallogger.util.NumberTextFormatter;
import junkuvo.apps.meallogger.util.PriceUtil;
import junkuvo.apps.meallogger.view.ListRowViewHolder;

public class RecyclerViewAdapter extends RealmRecyclerViewAdapter<MealLogs, ListRowViewHolder> {
    private static final String TAG = RecyclerViewAdapter.class.getSimpleName();
    private final RecyclerViewAdapter self = this;

    private Context mContext;
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm";
    private SimpleDateFormat mDateFormat;

    private Realm realm;
    private AlertDialog.Builder mAlertDialog;
    private View mMealLogsRowView;

    private int mLastPosition;

    public RecyclerViewAdapter(Context context, RealmResults<MealLogs> data) {
        super(context ,data, true);
        this.mContext = context;
        mLastPosition = data.size();
        mDateFormat = new SimpleDateFormat(DATE_FORMAT);
    }

    @Override
    public ListRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        mMealLogsRowView = inflater.inflate(R.layout.list_row, null);

        // Return a new holder instance
        ListRowViewHolder viewHolder = new ListRowViewHolder(mContext, mMealLogsRowView);

        mMealLogsRowView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final long id = Long.parseLong(((TextView)v.findViewById(R.id.txtId)).getText().toString());
                String mealMenu = ((TextView) v.findViewById(R.id.txtMealMenu)).getText().toString();
                String mealPrice = ((TextView) v.findViewById(R.id.txtPrice)).getText().toString();

                LayoutInflater inflater = LayoutInflater.from(mContext);
                final View layout;
                layout = inflater.inflate(R.layout.dialog_log_register, null);
                EditText edtMealMenu = (EditText) layout.findViewById(R.id.edtMealMenu);
                edtMealMenu.setText(mealMenu);
                EditText priceText = (EditText) layout.findViewById(R.id.edtMealPrice);
                priceText.setText(mealPrice);
                priceText.addTextChangedListener(new NumberTextFormatter(priceText, "#,###"));
                mAlertDialog = new AlertDialog.Builder(mContext);
                mAlertDialog.setTitle(mContext.getString(R.string.dialog_register_update_title));
                mAlertDialog.setIcon(R.drawable.ic_meal_done);
                mAlertDialog.setView(layout);
                mAlertDialog.setPositiveButton(mContext.getString(R.string.dialog_log_create), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        realm = Realm.getDefaultInstance();
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                final RealmResults<MealLogs> result =
                                        bgRealm.where(MealLogs.class)
                                                .equalTo("id", id)
                                                .findAll();

                                // TODO : これはUtilクラスに移殖
                                String mealMenu = ((EditText)layout.findViewById(R.id.edtMealMenu)).getText().toString();
                                String mealPrice = ((EditText)layout.findViewById(R.id.edtMealPrice)).getText().toString();

                                result.get(0).setMenuName(mealMenu);
                                result.get(0).setPrice(PriceUtil.parsePriceToLong(mealPrice,"¥"));
                                SharedPreferencesUtil.saveString(mContext, ActivityLogListAll.PREF_KEY_MEAL_NAME,mealMenu);
                                SharedPreferencesUtil.saveString(mContext,ActivityLogListAll.PREF_KEY_MEAL_PRICE,mealPrice);
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
                });
                mAlertDialog.setNegativeButton(mContext.getString(R.string.dialog_log_cancel), null);
                mAlertDialog.setNeutralButton("削除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(mContext.getString(R.string.dialog_title));
                        builder.setIcon(R.drawable.ic_delete_forever_black_48dp);
                        builder.setMessage(R.string.dialog_message);
                        builder.setPositiveButton(mContext.getString(R.string.dialog_log_delete_yes), new DialogInterface.OnClickListener() {
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
                        builder.setNegativeButton(mContext.getString(R.string.dialog_log_delete_no), null);
                        builder.create().show();
                    }
                });
                mAlertDialog.create().show();
            }
        });

        mMealLogsRowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final long id = Long.parseLong(((TextView) v.findViewById(R.id.txtId)).getText().toString());
                // Handle long click
                mAlertDialog = new AlertDialog.Builder(mContext);
                mAlertDialog.setTitle(mContext.getString(R.string.dialog_title));
                mAlertDialog.setIcon(R.drawable.ic_delete_forever_black_48dp);
                mAlertDialog.setMessage(R.string.dialog_message);
                mAlertDialog.setPositiveButton(mContext.getString(R.string.dialog_log_delete_yes), new DialogInterface.OnClickListener() {
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
                mAlertDialog.setNegativeButton(mContext.getString(R.string.dialog_log_delete_no), null);
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
        holder.getTxtPrice().setText(PriceUtil.parseLongToPrice(mealLogs.getPrice(),"¥"));
        holder.getTxtId().setText(String.valueOf(mealLogs.getId()));
        if(position == mLastPosition - 1) {
            holder.getTxtFooterSpace().setVisibility(View.VISIBLE);
        }else{
            holder.getTxtFooterSpace().setVisibility(View.GONE);
        }
    }

//    /*
//    表示の上限ぽい
//     */
//    @Override
//    public int getItemCount() {
//        return mItemList.size();
//    }


    public int getmLastPosition() {
        return mLastPosition;
    }

    public void setmLastPosition(int mLastPosition) {
        this.mLastPosition = mLastPosition;
    }
}
