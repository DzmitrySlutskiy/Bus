package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import by.slutskiy.busschedule.data.OrmDBHelper;
import by.slutskiy.busschedule.data.entities.RouteList;
import by.slutskiy.busschedule.data.entities.StopList;

/**
 * background download task
 * Version information
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopLoader extends AsyncTaskLoader<List<StopList>> {
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
    public List<StopList> loadInBackground() {
        OrmDBHelper dbHelper = OrmDBHelper.getReaderInstance(getContext());

        if (dbHelper == null) {
            return null;
        }

        try {
            if (routeId < 0) {
                return dbHelper.getStopDao().queryForAll();
            } else {
                QueryBuilder<RouteList, Integer> qbRouteList = dbHelper.getRouteListDao().queryBuilder();
                QueryBuilder<StopList, Integer> qbStopList = dbHelper.getStopDao().queryBuilder();

                qbStopList.join(qbRouteList);

                qbRouteList.where().eq(RouteList.ROUTE_ID, routeId);
                qbRouteList.orderBy(RouteList.STOP_INDEX, true);

                return qbStopList.query();
            }

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
