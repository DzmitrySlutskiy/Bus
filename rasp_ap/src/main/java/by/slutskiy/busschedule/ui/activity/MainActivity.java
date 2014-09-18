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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        View.OnClickListener, RouteFragment.OnRouteSelectedListener,
        RouteStopFragment.OnRouteStopSelectedListener,
        StopDetailFragment.OnStopDetailListener {

    private static final String LAST_SHOW_FRAGMENT = "LAST_SHOW_FRAGMENT";
    private volatile static int LOADER_ID = 0;

    public synchronized static int getNextLoaderId() {
        return LOADER_ID++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Fragment fragment = NewsFragment.getInstance();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_main, fragment);
            fragmentTransaction.commit();
        } else {
            //recovery last shown fragment (has been saved in onSaveInstanceState)
            String className = savedInstanceState.getString(LAST_SHOW_FRAGMENT,
                    NewsFragment.class.getSimpleName());

            recoverFragmentState(className);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        DBReader dbReader = DBReader.getInstance(this);
        if (dbReader != null) {
            dbReader.closeDB();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //save class name visible fragment
        //when application's activity has restarted (screen rotate etc)
        //in Bundle savedInstanceState was stored last visible fragment class name
        //and we can hide all fragments except saved class name
        FragmentManager fManager = getSupportFragmentManager();
        List<Fragment> fragmentList = fManager.getFragments();

        for (Fragment item : fragmentList) {
            if (item != null && item.isVisible()) {
                outState.putString(LAST_SHOW_FRAGMENT, ((Object) item).getClass().getSimpleName());
            }
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

    /**
     * create and init intent for UpdateService. if isCheckUpdate set true - service only check
     * for update availability
     *
     * @param isCheckUpdate flag for check update
     * @return intent instance
     */
    private Intent getServiceIntent(boolean isCheckUpdate) {
        Intent serviceIntent = new Intent(this, UpdateService.class);
        Messenger messenger = new Messenger(new Handler(this));
        serviceIntent.putExtra(UpdateService.MESSENGER, messenger);
        serviceIntent.putExtra(UpdateService.CHECK_UPDATE, isCheckUpdate);

        return serviceIntent;
    }

    /**
     * get last update field from shared preference
     *
     * @param context context for getting shared preference
     * @return Date saved on shared preference, or 0
     */
    public static Date getLastUpdateDate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(BuildConfig.PACKAGE_NAME,
                Context.MODE_PRIVATE);
        return new Date(preferences.getLong(UpdateService.PREF_LAST_UPDATE, 0));
    }

    /*  methods work with fragments */

    /**
     * add fragment to manager
     *
     * @param fragment fragment for adding
     */
    private void addFragmentToManager(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_main, fragment);
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();
    }

    /**
     * Show specified fragment
     *
     * @param fragment fragment for showing
     */
    private void showFragment(Fragment fragment) {
        FragmentManager fManager = getSupportFragmentManager();

        List<Fragment> fragmentList = fManager.getFragments();
        if (fragmentList != null && ! fragmentList.contains(fragment)) {
            addFragmentToManager(fragment);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        //hide all fragments in fragment manager

        if (fragmentList != null) {
            for (Fragment item : fragmentList) {
                if (item != null && item.isVisible()) {
                    fragmentTransaction.hide(item);
                }
            }
        }

        //show current fragment
        fragmentTransaction.show(fragment);

        //add this operation to back stack (if this operation allowed
        if (fragmentTransaction.isAddToBackStackAllowed()) {
            fragmentTransaction.addToBackStack(((Object) fragment).getClass().getSimpleName());
        }

        fragmentTransaction.commit();
    }

    /**
     * show fragment with className class and hide another
     *
     * @param className name class for showing (used when activity restarted at config changed)
     */
    private void recoverFragmentState(String className) {

        FragmentManager fManager = getSupportFragmentManager();

        //begin transaction: we hide all fragments except fragment with className class
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        //hide all fragments in fragment manager
        List<Fragment> fragmentList = fManager.getFragments();

        if (fragmentList != null) {
            for (Fragment item : fragmentList) {
                if ((className != null) &&
                        (((Object) item).getClass().getSimpleName().equals(className))) {
                    fragmentTransaction.show(item);
                } else {
                    fragmentTransaction.hide(item);
                }
            }
        }

        fragmentTransaction.commit();
    }

    /**
     * clear back stack, save only top element
     */
    private void clearBackStack() {
        FragmentManager fManager = getSupportFragmentManager();

        //null - only the top state is popped
        fManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /*      interface implementations   */

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

    //View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_route:
                clearBackStack();
                showFragment(RouteFragment.getInstance());
                break;

            case R.id.button_news:
                clearBackStack();

                //in the top of back stack saved NewsFragment (method onCreate in this Activity)
                break;

            case R.id.button_stops:
                clearBackStack();
                showFragment(RouteStopFragment.getInstance(- 1));
                break;

            default:
                break;
        }
    }

    //fragment interaction interface implementations
    @Override
    public void OnRouteSelected(int _id) {
        showFragment(RouteStopFragment.getInstance(_id));
    }

    @Override
    public void OnRouteStopSelected(int _id, String stopName, String stopDetail) {
        showFragment(TimeListFragment.getInstance(_id, stopName, stopDetail));
    }

    @Override
    public void onStopDetailSelected(int routeListId, String stopName, String stopDetail) {
        showFragment(TimeListFragment.getInstance(routeListId, stopName, stopDetail));
    }

    @Override
    public void OnStopSelected(int stopId, String stopName) {
        showFragment(StopDetailFragment.getInstance(stopId, stopName));
    }
}
