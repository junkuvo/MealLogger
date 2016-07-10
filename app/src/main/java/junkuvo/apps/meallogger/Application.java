package junkuvo.apps.meallogger;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
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
                            final RealmObjectSchema tweetSchema = realm.getSchema().get("MealLogs");
                            tweetSchema.addField("favorited", boolean.class);
                            oldVersion++; }
                    } })
                .build();
    }
}
