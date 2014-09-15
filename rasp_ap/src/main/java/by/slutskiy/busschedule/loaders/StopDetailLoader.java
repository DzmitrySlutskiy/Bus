package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import by.slutskiy.busschedule.data.OrmDBHelper;
import by.slutskiy.busschedule.data.entities.RouteList;

/**
 * background task loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopDetailLoader extends AsyncTaskLoader<List<RouteList>> {

    public static final String ATT_STOP_ID = "stopId";
    private int mStopIdLoader;

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public StopDetailLoader(Context context, Bundle args) {
        super(context);

        if (args != null) {
            mStopIdLoader = args.getInt(ATT_STOP_ID);
        }
    }

    @Override
    public List<RouteList> loadInBackground() {
        OrmDBHelper dbHelper = OrmDBHelper.getReaderInstance(getContext());

        if (dbHelper == null) {
            return null;
        }

        try {
            QueryBuilder<RouteList, Integer> qbRouteList = dbHelper.getRouteListDao().queryBuilder();
            qbRouteList.where().eq(RouteList.STOP_ID, mStopIdLoader);

            return qbRouteList.query();
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        forceLoad();                //start a load.
    }
}
