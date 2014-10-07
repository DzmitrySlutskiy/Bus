/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.DBReader;
import by.slutskiy.busschedule.ui.fragments.BaseFragment;
import by.slutskiy.busschedule.ui.fragments.NewsFragment;
import by.slutskiy.busschedule.ui.fragments.RouteFragment;
import by.slutskiy.busschedule.ui.fragments.RouteStopFragment;
import by.slutskiy.busschedule.ui.fragments.RunUpdateDialogFragment;
import by.slutskiy.busschedule.ui.fragments.StopDetailFragment;
import by.slutskiy.busschedule.ui.fragments.TimeListFragment;
import by.slutskiy.busschedule.utils.BroadcastUtils;
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
        RouteFragment.OnRouteSelectedListener,
        RouteStopFragment.OnRouteStopSelectedListener,
        StopDetailFragment.OnStopDetailListener {

    public static final String UPDATE_AVAIL_RECEIVER =
            "by.slutskiy.busschedule.ui.activity.MainActivity.UpdateAvailReceiver";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String LAST_SHOW_FRAGMENT = "LAST_SHOW_FRAGMENT";

    private UpdateAvailReceiver mUpdateReceiver = null;
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

        mUpdateReceiver = new UpdateAvailReceiver();

        if (! PreferenceUtils.isManualUpdate(this) &&
                PreferenceUtils.isUpdateAllowed(this) &&
                ! PreferenceUtils.isUpdateFound(this)) {

            UpdateUtils.setRepeatingAlarm(getApplicationContext());  //set new alarm
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        invalidateOptionsMenu();
        BroadcastUtils.registerReceiver(this, mUpdateReceiver, UPDATE_AVAIL_RECEIVER);
    }

    @Override
    protected void onStop() {
        super.onStop();

        BroadcastUtils.unregisterReceiver(this, mUpdateReceiver);
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
            item.setVisible(
                    PreferenceUtils.isUpdateAllowed(this) &&
                    (PreferenceUtils.isManualUpdate(this) ||
                    PreferenceUtils.isUpdateFound(this)));
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
                    UpdateUtils.runUpdateService(this);
                    item.setVisible(false);
                } else if (PreferenceUtils.isManualUpdate(this)) {
                    UpdateUtils.runCheckUpdateServiceImmediately(this);
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
    private void showFragment(String fragmentTag, Bundle args) {
        FragmentManager fManager = getSupportFragmentManager();

        BaseFragment currentFragment =
                (BaseFragment) fManager.findFragmentByTag(getCurrentFragmentTag());

        BaseFragment showingFragment = (BaseFragment) fManager.findFragmentByTag(fragmentTag);
        if (showingFragment == null) {

            showingFragment = buildFragment(fragmentTag);

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

    private BaseFragment buildFragment(String fragmentTag) {
        if (RouteFragment.TAG.equals(fragmentTag)) {
            return new RouteFragment();
        } else if (RouteStopFragment.TAG.equals(fragmentTag)) {
            return new RouteStopFragment();
        } else if (StopDetailFragment.TAG.equals(fragmentTag)) {
            return new StopDetailFragment();
        } else if (TimeListFragment.TAG.equals(fragmentTag)) {
            return new TimeListFragment();
        } else if (NewsFragment.TAG.equals(fragmentTag)) {
            return new NewsFragment();
        } else {         //else throw exception (in order to don't forget add new fragment here)
            throw new IllegalArgumentException("unsupported fragment: " + fragmentTag);
        }
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
                if ((className != null) && className.equals(item.getTag())){    //in tag saved ClassName
//                        (((Object) item).getClass().getSimpleName().equals(className))) {
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

    private void showTimeListFragment(int id, String stopName, String stopDetail) {
        Bundle args = new Bundle();
        args.putInt(TimeListFragment.ROUTE_LIST_ID, id);
        args.putString(TimeListFragment.STOP_NAME, stopName);
        args.putString(TimeListFragment.STOP_DETAIL, stopDetail);

        showFragment(TimeListFragment.TAG, args);
    }

    private void showRouteStopFragment(int routeId) {
        Bundle args = new Bundle();
        args.putInt(RouteStopFragment.ROUTE_ID, routeId);

        showFragment(RouteStopFragment.TAG, args);
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

        showFragment(StopDetailFragment.TAG, args);
    }

    public void onClickNews(View view) {
        clearBackStack();
    }

    public void onClickRoute(View view) {
        clearBackStack();
        showFragment(RouteFragment.TAG, null);
    }

    public void onClickStops(View view) {
        clearBackStack();
        showRouteStopFragment(- 1);
    }

    class UpdateAvailReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String messageStr = "";
            switch (intent.getIntExtra(UpdateUtils.TYPE, 0)) {

                case UpdateUtils.MSG_UPDATE_FINISH:
                    messageStr = getString(R.string.toast_update_finish);
                    invalidateOptionsMenu();
                    break;

                case UpdateUtils.MSG_NO_INTERNET:
                    messageStr = getString(R.string.toast_no_internet);
                    break;

                case UpdateUtils.MSG_UPDATE_FILE_STRUCTURE_ERROR:
                    messageStr = getString(R.string.toast_file_struct_error);
                    break;

                case UpdateUtils.MSG_UPDATE_DB_WORK_ERROR:
                    messageStr = getString(R.string.toast_db_update_error);
                    break;

                case UpdateUtils.MSG_IO_ERROR:
                    messageStr = getString(R.string.toast_io_error) + " " +
                            intent.getStringExtra(UpdateUtils.MESSAGE);
                    break;

                case UpdateUtils.MSG_UPDATE_BIFF_ERROR:
                    messageStr = getString(R.string.toast_biff_error) + " " +
                            intent.getStringExtra(UpdateUtils.MESSAGE);
                    break;

                case UpdateUtils.MSG_APP_ERROR:
//                    messageStr = getString(R.string.toast_app_error) + " " +
//                            intent.getStringExtra(UpdateService.MESSAGE);
                    break;

                case UpdateUtils.MSG_UPDATE_NOT_NEED:
                    messageStr = getString(R.string.toast_update_not_available);
                    break;

                case UpdateUtils.MSG_LAST_UPDATE:
                    invalidateOptionsMenu();
                    if (PreferenceUtils.isManualUpdate(getApplicationContext())) {
                        new RunUpdateDialogFragment().show(
                                getSupportFragmentManager(),
                                RunUpdateDialogFragment.class.getSimpleName());
                    }
                    break;

                default:
                    break;
            }
            if (! messageStr.equals("")) {
                Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_LONG).show();
            }
        }
    }
}