package junkuvo.apps.meallogger.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class LocationUtil implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = LocationUtil.class.getSimpleName();
    private final LocationUtil self = this;

    private Context mContext;
    private Activity mActiviry;

    public LocationUtil(Context context) {
        mContext = context;
    }

    static final int REQUEST_CODE_LOCATION = 1;

    public void getLocation(Activity activity) {

        mActiviry = activity;
        // Android 6, API 23以上でパーミッシンの確認
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkLocationPermission()) {
                updateLocation(activity);
            } else {
                requestLocationPermission(activity);
            }
        } else {
            updateLocation(activity);
        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // パーミッションが必要な処理
                    updateLocation(mActiviry);
                } else {
                    // パーミッションが得られなかった時
                    // 処理を中断する・エラーメッセージを出す・アプリケーションを終了する等
                }
            }
        }
    }

    // 位置情報許可の確認
    public boolean checkLocationPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        // 拒否していた場合
        else {
            return false;
        }
    }

    // 許可を求める
    private void requestLocationPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        } else {
            Toast toast = Toast.makeText(mContext, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);

        }
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        mContext.startActivity(intent);
    }

    private void updateLocation(Activity activity) {
        // 許可済みの場合、もしくはAndroid 6.0以前
        // パーミッションが必要な処理
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            // GPSを設定するように促す
            openSettings();
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE); // 低精度
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低消費電力
        String provider = locationManager.getBestProvider(criteria, true);
        if (locationManager != null) {
            // バックグラウンドから戻ってしまうと例外が発生する場合がある
            try {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                // TODO : providerによる分岐が必要
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(mContext, "例外が発生、位置情報のPermissionを許可していますか？", Toast.LENGTH_SHORT);
                toast.show();

            }
        }
    }

}
