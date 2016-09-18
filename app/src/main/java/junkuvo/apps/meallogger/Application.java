package junkuvo.apps.meallogger;

import com.codetroopers.betterpickers.SharedPreferencesUtil;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import junkuvo.apps.meallogger.entity.NotificationTime;
import junkuvo.apps.meallogger.entity.NotificationTimes;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this).build());
//        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build());
        Realm.setDefaultConfiguration(buildRealmConfiguration());
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
}
