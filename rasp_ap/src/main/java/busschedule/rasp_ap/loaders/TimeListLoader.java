package busschedule.rasp_ap.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import busschedule.rasp_ap.db.DBHelper;
import busschedule.rasp_ap.ui.fragments.TimeListFragment;

/**
 * TimeListLoader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class TimeListLoader extends AsyncTaskLoader<Object> {

    /**
     * background loader
     * if Id set as LOADER_TYPE_ID task load TypeList
     * if Id set as LOADER_TIME_ID task load TimeList
     * data will return as reference to Object class
     * in onLoadFinished data will cast to self type
     * this method help create universal loader for 2 different task
     */

    public static final String ATT_ROUT_LIST_ID = "routeListIdLoader";
    private int mRouteListIdLoader;

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public TimeListLoader(Context context, Bundle args) {
        super(context);

        if (args != null) {
            mRouteListIdLoader = args.getInt(ATT_ROUT_LIST_ID);
        }
    }

    @Override
    public Object loadInBackground() {
        DBHelper dbHelper = DBHelper.getInstance(getContext());

        return (getId() == TimeListFragment.LOADER_TYPE_ID)
                ? dbHelper.getTypeListByRouteListId(mRouteListIdLoader)
                : dbHelper.getTimeListByRouteListId(mRouteListIdLoader);
    }
}