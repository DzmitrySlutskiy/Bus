/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.List;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.DBReader;
import by.slutskiy.busschedule.services.UpdateService;
import by.slutskiy.busschedule.ui.fragments.BaseFragment;
import by.slutskiy.busschedule.ui.fragments.NewsFragment;
import by.slutskiy.busschedule.ui.fragments.RouteFragment;
import by.slutskiy.busschedule.ui.fragments.RouteStopFragment;
import by.slutskiy.busschedule.ui.fragments.RunUpdateDialogFragment;
import by.slutskiy.busschedule.ui.fragments.StopDetailFragment;
import by.slutskiy.busschedule.ui.fragments.TimeListFragment;
import by.slutskiy.busschedule.utils.PreferenceUtils;
import by.slutskiy.busschedule.utils.UpdateUtils;

/*
 * main application activity
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class MainActivity extends ActionBarActivity implements
        View.OnClickListener, RouteFragment.OnRouteSelectedListener,
        RouteStopFragment.OnRouteStopSelectedListener,
        StopDetailFragment.OnStopDetailListener {

    public static final String UPDATE_AVAIL_RECEIVER =
            "by.slutskiy.busschedule.ui.activity.MainActivity.UpdateAvailReceiver";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String LAST_SHOW_FRAGMENT = "LAST_SHOW_FRAGMENT";

    private UpdateAvailReceiver mUpdateReceiver = null;
    private LocalBroadcastManager mManager = null;
    private volatile static int LOADER_ID = 0;

    public synchronized static int getNextLoaderId() {
        return LOADER_ID++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceUtils.initDefaultPreference(this);            //initialize default preferences

        if (savedInstanceState == null) {
            addFragmentToManager(new NewsFragment(), NewsFragment.TAG, false);
        } else {
            //recovery last shown fragment (has been saved in onSaveInstanceState)
            String className = savedInstanceState.getString(LAST_SHOW_FRAGMENT, NewsFragment.TAG);

            recoverFragmentState(className);
        }

        ImageButton iBtnRoute = (ImageButton) findViewById(R.id.button_route);
        iBtnRoute.setOnClickListener(this);

        ImageButton iBtnNews = (ImageButton) findViewById(R.id.button_news);
        iBtnNews.setOnClickListener(this);

        ImageButton iBtnStops = (ImageButton) findViewById(R.id.button_stops);
        iBtnStops.setOnClickListener(this);

        mUpdateReceiver = new UpdateAvailReceiver();
        mManager = LocalBroadcastManager.getInstance(this);

        if (! PreferenceUtils.isManualUpdate(this)) {
            UpdateService.runCheckUpdateService(this);

            /*if update allowed and not found and update not set to "manual"*/

            if (PreferenceUtils.isUpdateAllowed(this) &&
                    ! PreferenceUtils.isUpdateFound(this)) {
                UpdateUtils.cancelAlarm(this);                           //delete old alarm
                UpdateUtils.setRepeatingAlarm(getApplicationContext());  //set new alarm
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mManager.registerReceiver(mUpdateReceiver, new IntentFilter(UPDATE_AVAIL_RECEIVER));
    }

    @Override
    protected void onStop() {
        super.onStop();

        mManager.unregisterReceiver(mUpdateReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.action_update);
        if (item != null) {
            //schedule time in millisecond = 0 when user set manual update
            //PREF_UPDATE_BUTTON_STATE set in true when update found earlier
            item.setVisible(PreferenceUtils.isManualUpdate(this) ||
                    PreferenceUtils.isUpdateFound(this));
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(LAST_SHOW_FRAGMENT, getCurrentFragmentTag());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, PrefActivity.class));
                return true;

            case R.id.action_update:
                if (PreferenceUtils.isUpdateFound(this)) {
                    UpdateService.runUpdateService(this);
                } else if (PreferenceUtils.isManualUpdate(this)) {
                    UpdateService.runCheckUpdateServiceImmediately(this);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*  methods work with fragments */

    /**
     * add fragment to manager
     *
     * @param fragment fragment for adding
     */
    private void addFragmentToManager(Fragment fragment, String tag, boolean needHide) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_main, fragment, tag);
        if (needHide) {
            fragmentTransaction.hide(fragment);
        }
        fragmentTransaction.commit();
    }

    private String getCurrentFragmentTag() {
        FragmentManager fManager = getSupportFragmentManager();
        int count = fManager.getBackStackEntryCount();
        FragmentManager.BackStackEntry entry = null;
        if (count > 0) {
            entry = fManager.getBackStackEntryAt(count - 1);
        }
        if (entry == null) {
            return NewsFragment.TAG;
        } else {
            return entry.getName();
        }
    }

    /**
     * Show specified fragment
     *
     * @param fragmentTag fragment for showing
     */
    private void showFragment(Class<? extends BaseFragment> cls, String fragmentTag, Bundle args) {
        FragmentManager fManager = getSupportFragmentManager();

        BaseFragment currentFragment =
                (BaseFragment) fManager.findFragmentByTag(getCurrentFragmentTag());

        BaseFragment showingFragment = (BaseFragment) fManager.findFragmentByTag(fragmentTag);
        if (showingFragment == null) {

            try {
                showingFragment = cls.newInstance();
            } catch (ReflectiveOperationException e) {
                Log.e(LOG_TAG, e.getMessage());
                showingFragment = new NewsFragment();
            }

            addFragmentToManager(showingFragment, fragmentTag, true);
        }
        showingFragment.changeArguments(args);          //set new arguments

        FragmentTransaction fragmentTransaction = fManager.beginTransaction();

        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        //show current fragment
        fragmentTransaction.show(showingFragment);

        //add this operation to back stack (if this operation allowed
        if (fragmentTransaction.isAddToBackStackAllowed()) {
            fragmentTransaction.addToBackStack(fragmentTag);
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

    //View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_route:
                clearBackStack();
                showFragment(RouteFragment.class, RouteFragment.TAG, null);
                break;

            case R.id.button_news:
                clearBackStack();

                //in the top of back stack saved NewsFragment (method onCreate in this Activity)
                break;

            case R.id.button_stops:
                clearBackStack();
                showRouteStopFragment(- 1);
                break;

            default:
                break;
        }
    }


    private void showTimeListFragment(int id, String stopName, String stopDetail) {
        Bundle args = new Bundle();
        args.putInt(TimeListFragment.ROUTE_LIST_ID, id);
        args.putString(TimeListFragment.STOP_NAME, stopName);
        args.putString(TimeListFragment.STOP_DETAIL, stopDetail);

        showFragment(TimeListFragment.class, TimeListFragment.TAG, args);
    }

    private void showRouteStopFragment(int routeId) {
        Bundle args = new Bundle();
        args.putInt(RouteStopFragment.ROUTE_ID, routeId);

        showFragment(RouteStopFragment.class, RouteStopFragment.TAG, args);
    }

    //fragment interaction interface implementations
    @Override
    public void OnRouteSelected(int id) {
        showRouteStopFragment(id);
    }

    @Override
    public void OnRouteStopSelected(int id, String stopName, String stopDetail) {
        showTimeListFragment(id, stopName, stopDetail);
    }

    @Override
    public void onStopDetailSelected(int routeListId, String stopName, String stopDetail) {
        showTimeListFragment(routeListId, stopName, stopDetail);
    }

    @Override
    public void OnStopSelected(int stopId, String stopName) {
        Bundle args = new Bundle();
        args.putInt(StopDetailFragment.STOP_ID, stopId);
        args.putString(StopDetailFragment.STOP_NAME, stopName);

        showFragment(StopDetailFragment.class, StopDetailFragment.TAG, args);
    }

    class UpdateAvailReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String messageStr = "";
            switch (intent.getIntExtra(UpdateService.TYPE, 0)) {

                case UpdateService.MSG_UPDATE_FINISH:
//                    messageStr = getString(R.string.toast_update_finish);
                    invalidateOptionsMenu();
                    break;

                case UpdateService.MSG_NO_INTERNET:
//                    messageStr = getString(R.string.toast_no_internet);
                    break;

                case UpdateService.MSG_UPDATE_FILE_STRUCTURE_ERROR:
//                    messageStr = getString(R.string.toast_file_struct_error);
                    break;

                case UpdateService.MSG_UPDATE_DB_WORK_ERROR:
//                    messageStr = getString(R.string.toast_db_update_error);
                    break;

                case UpdateService.MSG_IO_ERROR:
//                    messageStr = getString(R.string.toast_io_error) + " " +
//                            intent.getStringExtra(UpdateService.MESSAGE);
                    break;

                case UpdateService.MSG_UPDATE_BIFF_ERROR:
//                    messageStr = getString(R.string.toast_biff_error) + " " +
//                            intent.getStringExtra(UpdateService.MESSAGE);
                    break;

                case UpdateService.MSG_APP_ERROR:
//                    messageStr = getString(R.string.toast_app_error) + " " +
//                            intent.getStringExtra(UpdateService.MESSAGE);
                    break;

                case UpdateService.MSG_UPDATE_NOT_NEED:
//                    messageStr = getString(R.string.toast_update_not_available);
                    break;

                case UpdateService.MSG_LAST_UPDATE:
//                    messageStr = getString(R.string.toast_update_available) + " " +
//                            intent.getStringExtra(UpdateService.MESSAGE);
                    invalidateOptionsMenu();
                    if (PreferenceUtils.isManualUpdate(getApplicationContext())) {
                        new RunUpdateDialogFragment().show(getSupportFragmentManager(),
                                RunUpdateDialogFragment.class.getSimpleName());

                        //UpdateService.runCheckUpdateServiceImmediately(this);
                    }
                    break;

                default:
                    break;
            }
            /*if (! messageStr.equals("")) {
                Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_LONG).show();
            }*/
        }
    }
}