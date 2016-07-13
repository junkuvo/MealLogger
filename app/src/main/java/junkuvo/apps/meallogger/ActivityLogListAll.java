package junkuvo.apps.meallogger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import junkuvo.apps.meallogger.adapter.LogListPagerAdapter;
import junkuvo.apps.meallogger.service.NotificationService;

public class ActivityLogListAll extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new LogListPagerAdapter(getSupportFragmentManager()));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(ActivityLogListAll.this, ActivityLogRegister.class);
                startActivity(intent);
            }
        });

        Intent intent = new Intent(ActivityLogListAll.this, NotificationService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_log_list_all, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        killAlertService();
    }

    public void killAlertService() {
        unbindService(mServiceConnection); // バインド解除
        mNotificationService.stopSelf(); // サービスは必要ないので終了させる。
    }

    private NotificationService mNotificationService;

    // ServiceとActivityをBindするクラス
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mNotificationService = ((NotificationService.NotificationBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mNotificationService = null;
        }
    };
}
