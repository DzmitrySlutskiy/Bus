/*
 * Bus schedule for Grodno
 */

package busschedule.rasp_ap;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

/*
 * main application activity
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class MainActivity extends ActionBarActivity implements RouteFragment.OnRouteSelectedListener, Handler.Callback, ProgressDialog.OnCancelListener,
        View.OnClickListener, RouteStopFragment.OnRouteStopSelectedListener, TimeListFragment.OnTimeListSelectedListener, StopDetailFragment.OnStopDetailListener {

    private ProgressDialog mProgressDialog;
    private MyAsyncTask mAsyncTask;

    private android.support.v4.app.FragmentTransaction mFragTrans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        if (savedInstanceState == null) {
            mFragTrans = getSupportFragmentManager().beginTransaction();
            mFragTrans.replace(R.id.frmMain, NewsFragment.newInstance());
            mFragTrans.commit();
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
        mAsyncTask.cancel(false);
    }

    /**
     * Handler.Callback - interface implementation
     * Get message from MyAsyncTask thread and show update progress information
     */
    public boolean handleMessage(Message msg) {
        String messageStr;
        switch (msg.what) {
            case Constants.MSG_START_PROGRESS:
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

            case Constants.MSG_UPDATE_PROGRESS:
                mProgressDialog.incrementProgressBy(msg.arg1);
                break;

            case Constants.MSG_END_PROGRESS:
                mProgressDialog.dismiss();
                break;

            case Constants.MSG_UPDATE_CANCELED:
                mProgressDialog.dismiss();
                break;

            case Constants.MSG_UPDATE_TEXT:
                messageStr = (String) msg.obj;
                mProgressDialog.setMessage(messageStr);
                break;

            case Constants.MSG_NO_INTERNET:
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        R.string.update_no_internet, Toast.LENGTH_LONG).show();
                break;

            case Constants.MSG_UPDATE_FILE_SIZE:
                mProgressDialog.setMax(msg.arg1);
                mProgressDialog.setProgress(0);
                break;

            case Constants.MSG_UPDATE_FILE_STRUCTURE_ERROR:
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        R.string.update_file_struct_error, Toast.LENGTH_LONG).show();
                break;

            case Constants.MSG_UPDATE_DB_WORK_ERROR:
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        R.string.update_db_update_error, Toast.LENGTH_LONG).show();
                break;

            case Constants.MSG_IO_ERROR:
                mProgressDialog.dismiss();
                messageStr = getResources().getString(R.string.update_io_error) + " " + msg.obj;
                Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_LONG).show();
                break;

            case Constants.MSG_UPDATE_BIFF_ERROR:
                mProgressDialog.dismiss();
                messageStr = getResources().getString(R.string.update_biff_error) + " " + msg.obj;
                Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_LONG).show();
                break;

            case Constants.MSG_APP_ERROR:
                mProgressDialog.dismiss();
                messageStr = getResources().getString(R.string.app_error) + " " + msg.obj;
                Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_LONG).show();
                break;

            default:
                mProgressDialog.dismiss();
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
                replaceFragment(RouteFragment.newInstance());
                break;

            case R.id.iBtnNews:
                replaceFragment(NewsFragment.newInstance());
                break;

            case R.id.iBtnStops:
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

    private void replaceFragment(Fragment fragment) {
        mFragTrans = getSupportFragmentManager().beginTransaction();
        mFragTrans.replace(R.id.frmMain, fragment);
        //mFragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        mFragTrans.addToBackStack(null);
        mFragTrans.commit();
    }

    @Override
    public void onStopDetailSelected(int routeListId, int routeId, String stopName, String stopDetail) {
        replaceFragment(TimeListFragment.newInstance(routeListId, routeId, stopName, stopDetail));
    }
}
