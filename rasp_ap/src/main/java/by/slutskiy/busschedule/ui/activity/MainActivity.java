/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import by.slutskiy.busschedule.BuildConfig;
import by.slutskiy.busschedule.services.UpdateService;
import by.slutskiy.busschedule.ui.fragments.NewsFragment;
import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.ui.fragments.RouteFragment;
import by.slutskiy.busschedule.ui.fragments.RouteStopFragment;
import by.slutskiy.busschedule.ui.fragments.StopDetailFragment;
import by.slutskiy.busschedule.ui.fragments.TimeListFragment;
import by.slutskiy.busschedule.data.DBReader;

/*
 * main application activity
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class MainActivity extends ActionBarActivity implements Handler.Callback,
        RouteFragment.OnRouteSelectedListener,
        View.OnClickListener, RouteStopFragment.OnRouteStopSelectedListener,
        StopDetailFragment.OnStopDetailListener {

    private volatile static int LOADER_ID = 0;

    public synchronized static int getNextLoaderId() {
        Log.d(MainActivity.class.getSimpleName(), "getNextLoaderId           LOADER_ID: " + LOADER_ID);
        return LOADER_ID++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_main, NewsFragment.newInstance());
            fragmentTransaction.commit();
        }

        ImageButton iBtnRoute = (ImageButton) findViewById(R.id.button_route);
        iBtnRoute.setOnClickListener(this);

        ImageButton iBtnNews = (ImageButton) findViewById(R.id.button_news);
        iBtnNews.setOnClickListener(this);

        ImageButton iBtnStops = (ImageButton) findViewById(R.id.button_stops);
        iBtnStops.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu_main, menu);
        return true;
    }

    /**
     * Handler.Callback - interface implementation
     * Get message from MyAsyncTask thread and show update progress information
     */
    public boolean handleMessage(Message msg) {
        String messageStr = "";
        switch (msg.what) {

            case UpdateService.MSG_UPDATE_FINISH:
                messageStr = getString(R.string.toast_update_finish);
                break;

            case UpdateService.MSG_NO_INTERNET:
                messageStr = getString(R.string.toast_no_internet);
                break;

            case UpdateService.MSG_UPDATE_FILE_STRUCTURE_ERROR:
                messageStr = getString(R.string.toast_file_struct_error);
                break;

            case UpdateService.MSG_UPDATE_DB_WORK_ERROR:
                messageStr = getString(R.string.toast_db_update_error);
                break;

            case UpdateService.MSG_IO_ERROR:
                messageStr = getString(R.string.toast_io_error) + " " + msg.obj;
                break;

            case UpdateService.MSG_UPDATE_BIFF_ERROR:
                messageStr = getString(R.string.toast_biff_error) + " " + msg.obj;
                break;

            case UpdateService.MSG_APP_ERROR:
                messageStr = getString(R.string.toast_app_error) + " " + msg.obj;
                break;

            case UpdateService.MSG_UPDATE_NOT_NEED:
                messageStr = getString(R.string.toast_update_not_available);
                break;

            case UpdateService.MSG_LAST_UPDATE:
                messageStr = getString(R.string.toast_update_available) + " " +
                        new SimpleDateFormat(UpdateService.USED_DATE_FORMAT).format((Date) msg.obj);
                break;

            default:
                break;
        }
        if (! messageStr.equals("")) {
            Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBReader dbReader = DBReader.getInstance(this);
        if (dbReader != null) {
            dbReader.closeDB();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:

                return true;

            case R.id.action_check_update:
                startService(getServiceIntent(true));
                return true;

            case R.id.action_update:
                startService(getServiceIntent(false));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Intent getServiceIntent(boolean isCheckUpdate) {
        Intent serviceIntent = new Intent(this, UpdateService.class);
        Messenger messenger = new Messenger(new Handler(this));
        serviceIntent.putExtra(UpdateService.MESSENGER, messenger);
        serviceIntent.putExtra(UpdateService.CHECK_UPDATE, isCheckUpdate);
        return serviceIntent;
    }

    public static Timestamp getLastUpdateDate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(BuildConfig.PACKAGE_NAME,
                Context.MODE_PRIVATE);
        return new Timestamp(preferences.getLong(UpdateService.PREF_LAST_UPDATE, 0));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_route:
                clearBackStack();
                replaceFragment(RouteFragment.newInstance());
                break;

            case R.id.button_news:
                clearBackStack();

                //in the top of back stack saved NewsFragment (method onCreate in this Activity)
                //replaceFragment(NewsFragment.newInstance());
                break;

            case R.id.button_stops:
                clearBackStack();
                replaceFragment(RouteStopFragment.newInstance(- 1));
                break;

            default:
                break;
        }
    }

    @Override
    public void OnRouteSelected(int _id) {
        replaceFragment(RouteStopFragment.newInstance(_id));
    }

    @Override
    public void OnRouteStopSelected(int _id, int routeId, String stopName, String stopDetail) {
        replaceFragment(TimeListFragment.newInstance(_id, routeId, stopName, stopDetail));
    }

    @Override
    public void OnStopSelected(int stopId, String stopName) {
        replaceFragment(StopDetailFragment.newInstance(stopId, stopName));
    }

    private void clearBackStack() {
        FragmentManager fManager = getSupportFragmentManager();

        //null - only the top state is popped
        fManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_main, fragment);
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (fragmentTransaction.isAddToBackStackAllowed()) {
            fragmentTransaction.addToBackStack(((Object) fragment).getClass().getSimpleName());
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onStopDetailSelected(int routeListId, int routeId, String stopName, String stopDetail) {
        replaceFragment(TimeListFragment.newInstance(routeListId, routeId, stopName, stopDetail));
    }
}
