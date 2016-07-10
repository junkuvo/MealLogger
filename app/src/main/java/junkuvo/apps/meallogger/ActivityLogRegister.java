package junkuvo.apps.meallogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.Date;

import io.realm.Realm;
import junkuvo.apps.meallogger.entity.MealLogs;

public class ActivityLogRegister extends AppCompatActivity {
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_register);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String menuName = ((EditText)findViewById(R.id.edtMealMenu)).getText().toString();

                realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        MealLogs mealLogs = bgRealm.createObject(MealLogs.class);
                        mealLogs.setId(System.currentTimeMillis());
                        mealLogs.setMenuName(menuName);
                        mealLogs.setCreatedAt(new Date(System.currentTimeMillis()));
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        // トランザクションは成功
                        Intent intent = new Intent(ActivityLogRegister.this, ActivityLogListAll.class);
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
    }
}
