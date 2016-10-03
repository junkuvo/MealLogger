package junkuvo.apps.meallogger;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class Application extends android.app.Application {

    private final long REALM_LATEST_SCHEME_VERSION = 2L;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this).build());
//        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build());
        Realm.setDefaultConfiguration(buildRealmConfiguration());
    }

    private RealmConfiguration buildRealmConfiguration() {
        return new RealmConfiguration.Builder(this).schemaVersion(REALM_LATEST_SCHEME_VERSION) // started 1L
                .migration(new MyMigration()) // Migration to run instead of throwing an exception
                .build();
    }

    private class MyMigration implements RealmMigration{

        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            RealmSchema schema = realm.getSchema();
            if (oldVersion == 0L) {
                oldVersion++;
            }

            // version 1 to version 2
            // added year and month in MealLogs TBL
            if(oldVersion == 1L){
                schema.get("MealLogs")
                        .addField("year", int.class, FieldAttribute.INDEXED)
                        .addField("month", int.class, FieldAttribute.INDEXED);
                oldVersion++;
            }
        }
    }
}
