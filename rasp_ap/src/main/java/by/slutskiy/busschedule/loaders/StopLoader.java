package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import by.slutskiy.busschedule.db.DBHelper;
import by.slutskiy.busschedule.ui.fragments.RouteStopFragment;

/**
 * background download task
 * Version information
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopLoader extends AsyncTaskLoader<Object> {
    public static final String ATT_ROUT_ID = "routeId";

    private int routeId;

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public StopLoader(Context context, Bundle args) {
        super(context);
        if (args != null) {
            routeId = args.getInt(ATT_ROUT_ID);
        }
    }

    @Override
    public Object loadInBackground() {
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        return (getId() == RouteStopFragment.LOADER_ID_STOP_DETAIL)
                ? dbHelper.getRouteDetail(routeId)
                : dbHelper.getRouteStopsList(routeId);
    }
}
