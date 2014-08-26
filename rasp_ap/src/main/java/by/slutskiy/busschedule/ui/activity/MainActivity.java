/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
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

import by.slutskiy.busschedule.MyAsyncTask;
import by.slutskiy.busschedule.interfaces.OnRouteSelectedListener;
import by.slutskiy.busschedule.interfaces.OnRouteStopSelectedListener;
import by.slutskiy.busschedule.interfaces.OnStopDetailListener;
import by.slutskiy.busschedule.ui.fragments.NewsFragment;
import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.ui.fragments.RouteFragment;
import by.slutskiy.busschedule.ui.fragments.RouteStopFragment;
import by.slutskiy.busschedule.ui.fragments.StopDetailFragment;
import by.slutskiy.busschedule.ui.fragments.TimeListFragment;
import by.slutskiy.busschedule.db.DBHelper;

/*
 * main application activity
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class MainActivity extends ActionBarActivity implements OnRouteSelectedListener, Handler.Callback, ProgressDialog.OnCancelListener,
        View.OnClickListener, OnRouteStopSelectedListener, OnStopDetailListener {

    private ProgressDialog mProgressDialog;
    private MyAsyncTask mAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frmMain, NewsFragment.newInstance());
            fragmentTransaction.commit();
        }

        ImageButton iBtnRoute = (ImageButton) findViewById(R.id.iBtnRoute);
        iBtnRoute.setOnClickListener(this);

        ImageButton iBtnNews = (ImageButton) findViewById(R.id.iBtnNews);
        iBtnNews.setOnClickListener(this);

        ImageButton iBtnStops = (ImageButton) findViewById(R.id.iBtnStops);
        iBtnStops.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainactivity, menu);
        return true;
    }

    /**
     * ProgressDialog.OnCancelListener - interface implementation
     */
    public void onCancel(DialogInterface dialog) {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(false);
            mAsyncTask = null;
        }
    }

    /**
     * Handler.Callback - interface implementation
     * Get message from MyAsyncTask thread and show update progress information
     */
    public boolean handleMessage(Message msg) {
        String messageStr;
        switch (msg.what) {
            case MyAsyncTask.MSG_START_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle(getResources().getString(R.string.update_dialog_title));
                mProgressDialog.setMessage(getResources().getString(R.string.update_dialog_message));
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setMax(0);
                mProgressDialog.setProgress(0);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setOnCancelListener(this);
                mProgressDialog.show();
                mProgressDialog.setIndeterminate(false);
                break;

            case MyAsyncTask.MSG_UPDATE_PROGRESS:
                if (mProgressDialog != null) {
                    mProgressDialog.incrementProgressBy(msg.arg1);
                }
                break;

            case MyAsyncTask.MSG_END_PROGRESS:
                dismissDialog();
                break;

            case MyAsyncTask.MSG_UPDATE_CANCELED:
                dismissDialog();
                break;

            case MyAsyncTask.MSG_UPDATE_TEXT:
                messageStr = (String) msg.obj;
                if (mProgressDialog != null) {
                    mProgressDialog.setMessage(messageStr);
                }
                break;

            case MyAsyncTask.MSG_NO_INTERNET:
                dismissDialog();
                Toast.makeText(getApplicationContext(),
                        R.string.update_no_internet, Toast.LENGTH_LONG).show();
                break;

            case MyAsyncTask.MSG_UPDATE_FILE_SIZE:
                if (mProgressDialog != null) {
                    mProgressDialog.setMax(msg.arg1);
                    mProgressDialog.setProgress(0);
                }
                break;

            case MyAsyncTask.MSG_UPDATE_FILE_STRUCTURE_ERROR:
                dismissDialog();
                Toast.makeText(getApplicationContext(),
                        R.string.update_file_struct_error, Toast.LENGTH_LONG).show();
                break;

            case MyAsyncTask.MSG_UPDATE_DB_WORK_ERROR:
                dismissDialog();
                Toast.makeText(getApplicationContext(),
                        R.string.update_db_update_error, Toast.LENGTH_LONG).show();
                break;

            case MyAsyncTask.MSG_IO_ERROR:
                dismissDialog();
                messageStr = getResources().getString(R.string.update_io_error) + " " + msg.obj;
                Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_LONG).show();
                break;

            case MyAsyncTask.MSG_UPDATE_BIFF_ERROR:
                dismissDialog();
                messageStr = getResources().getString(R.string.update_biff_error) + " " + msg.obj;
                Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_LONG).show();
                break;

            case MyAsyncTask.MSG_APP_ERROR:
                dismissDialog();
                messageStr = getResources().getString(R.string.app_error) + " " + msg.obj;
                Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_LONG).show();
                break;

            default:
                dismissDialog();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBHelper dbHelper = DBHelper.getInstance(this);
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Object[] objArr = new Object[2];
            objArr[0] = new Handler(this);
            objArr[1] = getApplicationContext();
            mAsyncTask = new MyAsyncTask();
            mAsyncTask.execute(objArr);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iBtnRoute:
                clearBackStack();
                replaceFragment(RouteFragment.newInstance());
                break;

            case R.id.iBtnNews:
                clearBackStack();

                //in the top of backstack saved NewsFragment (method onCreate in this Activity)
                //replaceFragment(NewsFragment.newInstance());
                break;

            case R.id.iBtnStops:
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

    /**
     * dismiss dialog and set private variable mProgressDialog to null
     */
    private void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;

            //dismiss dialog when MyAsyncTask finish or crash
            mAsyncTask = null;
        }
    }

    private void clearBackStack() {
        FragmentManager fManager = getSupportFragmentManager();

        //null - only the top state is popped
        fManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frmMain, fragment);
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (fragmentTransaction.isAddToBackStackAllowed()) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onStopDetailSelected(int routeListId, int routeId, String stopName, String stopDetail) {
        replaceFragment(TimeListFragment.newInstance(routeListId, routeId, stopName, stopDetail));
    }
}
