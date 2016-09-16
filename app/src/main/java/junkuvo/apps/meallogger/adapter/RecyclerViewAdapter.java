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
import java.text.StringCharacterIterator;

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
    private int mMealLogsLastPosition;
    private View mMealLogsRowView;

    public RecyclerViewAdapter(Context context, RealmResults<MealLogs> data) {
        super(context ,data, true);
        this.mContext = context;

        mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        mMealLogsLastPosition = data.size() - 1;
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
                // TODO : いい感じのアイコン作成
                mAlertDialog.setIcon(android.R.drawable.ic_menu_manage);
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
//        if(position == mMealLogsLastPosition){
//            mMealLogsRowView.setPadding(0,0,0,mContext.getResources().getDimensionPixelSize(R.dimen.footer_padding_recyclerview));
//        }
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


    public int getmMealLogsLastPosition() {
        return mMealLogsLastPosition;
    }

    public void setmMealLogsLastPosition(int mMealLogsLastPosition) {
        this.mMealLogsLastPosition = mMealLogsLastPosition;
    }
}
