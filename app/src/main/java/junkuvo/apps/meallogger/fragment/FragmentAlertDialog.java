package junkuvo.apps.meallogger.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import junkuvo.apps.meallogger.ActivityLogListAll;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.entity.MealLogs;
import junkuvo.apps.meallogger.entity.MonthlyMealLog;
import junkuvo.apps.meallogger.util.NumberTextFormatter;
import junkuvo.apps.meallogger.util.PriceUtil;
import junkuvo.apps.meallogger.util.SharedPreferencesUtil;

import static junkuvo.apps.meallogger.ActivityLogListAll.PREF_KEY_MEAL_NAME;
import static junkuvo.apps.meallogger.ActivityLogListAll.PREF_KEY_MEAL_PRICE;

public class FragmentAlertDialog extends DialogFragment {

    Realm realm;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        final String notificationName = bundle.getString(ActivityLogListAll.INTENT_KEY_NOTIFICATION_NAME);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_log_register, null);
        final EditText mealText = (EditText) view.findViewById(R.id.edtMealMenu);
        // TODO : カスタムビューにしたいな
        final EditText priceText = (EditText) view.findViewById(R.id.edtMealPrice);
        priceText.addTextChangedListener(new NumberTextFormatter(priceText, "#,###"));

        builder.setView(view);
        if(SharedPreferencesUtil.getString(getActivity(), PREF_KEY_MEAL_NAME + notificationName) != null){
            builder.setNeutralButton(getString(R.string.dialog_add_same, notificationName), null);
        }
        builder.setPositiveButton(getString(R.string.dialog_log_create), null);
        builder.setNegativeButton(getString(R.string.dialog_log_cancel), null);
        realm = Realm.getDefaultInstance();
        final AlertDialog dialog = builder.setTitle(getString(R.string.dialog_register_title)).setIcon(R.drawable.ic_meal_done).create();

        // dialog を showしないとgetButtonがnul pointerになるので、コールバックを利用
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {                    //
                Button buttonSameAsLastTime = ((AlertDialog)dialog).getButton( DialogInterface.BUTTON_NEUTRAL );
                buttonSameAsLastTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String mealName = SharedPreferencesUtil.getString(getActivity(), PREF_KEY_MEAL_NAME + notificationName);
                        String price = SharedPreferencesUtil.getString(getActivity(), PREF_KEY_MEAL_PRICE + notificationName);
                        mealText.setText(mealName);
                        priceText.setText(price);
                    }
                });

                Button buttonOK = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                buttonOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String mealName = mealText.getText().toString();
                        final String price = priceText.getText().toString();
                        // 入力が空の場合
                        if (mealName.equals("") || price.equals("")) {
                            Toast.makeText(getActivity(), getString(R.string.validation_message), Toast.LENGTH_LONG).show();
                        } else {
                            final MealLogs mealLogToInsert = new MealLogs();
                            mealLogToInsert.setMealLog(R.mipmap.ic_launcher, mealName, new Date(System.currentTimeMillis()), PriceUtil.parsePriceToLong(price, "¥"), notificationName);
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
                                    SharedPreferencesUtil.saveString(getActivity().getBaseContext(), PREF_KEY_MEAL_NAME + notificationName, mealName);
                                    SharedPreferencesUtil.saveString(getActivity().getBaseContext(), PREF_KEY_MEAL_PRICE + notificationName, price);
                                    dialog.dismiss();

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
            }
        });

//        dialog.setCanceledOnTouchOutside(true);

        dialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutDialogAnimation;
        return dialog;
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
        getActivity().finish();
    }
}
