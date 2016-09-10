package junkuvo.apps.meallogger;

import com.codetroopers.betterpickers.SharedPreferencesUtil;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import junkuvo.apps.meallogger.entity.NotificationTime;
import junkuvo.apps.meallogger.entity.NotificationTimes;

public class Application extends android.app.Application {

    private static final String PREF_KEY_INITIALIZED_FLAG = "initialized_flag";
    public String mNotificationScheduleName;

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
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    NotificationTimes notificationTimes = bgRealm.createObject(NotificationTimes.class);
                    NotificationTime notificationTimesMorning = bgRealm.createObject(NotificationTime.class);
                    notificationTimesMorning.setNotificationTime("朝", "8:00", "月火水木金");
                    notificationTimes.notificationTimes.add(notificationTimesMorning);
                    NotificationTime notificationTimesLunch = bgRealm.createObject(NotificationTime.class);
                    notificationTimesLunch.setNotificationTime("昼", "12:00", "月火水木金");
                    notificationTimes.notificationTimes.add(notificationTimesLunch);
                    NotificationTime notificationTimesDinner = bgRealm.createObject(NotificationTime.class);
                    notificationTimesDinner.setNotificationTime("夜", "20:00", "月火水木金");
                    notificationTimes.notificationTimes.add(notificationTimesDinner);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    SharedPreferencesUtil.saveBoolean(getApplicationContext(), PREF_KEY_INITIALIZED_FLAG, true);
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();
                }
            });
        }
    }
}
