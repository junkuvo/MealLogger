package junkuvo.apps.meallogger;

import com.codetroopers.betterpickers.SharedPreferencesUtil;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

public class Application extends android.app.Application {

    private static final String PREF_KEY_INITIALIZED_FLAG = "initialized_flag";

    @Override
    public void onCreate() {
        super.onCreate();
//        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this).build());
//        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build());
        Realm.setDefaultConfiguration(buildRealmConfiguration());
        initializeNotificationTimesRealm();
    }

    private RealmConfiguration buildRealmConfiguration() {
        return new RealmConfiguration.Builder(this).schemaVersion(1L)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        if (oldVersion == 0L) {
                            oldVersion++; }
                    } })
                .build();
    }

    private void initializeNotificationTimesRealm(){

        if(!SharedPreferencesUtil.getBoolean(getApplicationContext(),PREF_KEY_INITIALIZED_FLAG)) {
            Realm realm = Realm.getDefaultInstance();

//            realm.executeTransactionAsync(new Realm.Transaction() {
//                @Override
//                public void execute(Realm bgRealm) {
//                    NotificationTimes notificationTimes = bgRealm.createObject(NotificationTimes.class);
//                    notificationTimes.setNotificationTime("昼食","12:00");
//                }
//            }, new Realm.Transaction.OnSuccess() {
//                @Override
//                public void onSuccess() {
//                    // トランザクションは成功
//                    SharedPreferencesUtil.saveBoolean(getApplicationContext(), PREF_KEY_INITIALIZED_FLAG, true);
//                }
//            }, new Realm.Transaction.OnError() {
//                @Override
//                public void onError(Throwable error) {
//                    // トランザクションは失敗。自動的にキャンセルされます
//                }
//            });
        }
    }
}
