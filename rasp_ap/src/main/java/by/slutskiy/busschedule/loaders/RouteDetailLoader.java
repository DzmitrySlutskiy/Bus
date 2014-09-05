package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import by.slutskiy.busschedule.data.OrmDBHelper;
import by.slutskiy.busschedule.data.entities.RouteList;
import by.slutskiy.busschedule.data.entities.Routes;

/**
 * RouteDetailLoader
 * Version 1.0
 * 31.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class RouteDetailLoader extends AsyncTaskLoader<String> {

    public static final String ATT_ROUT_ID = "routeId";

    private int routeId;

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public RouteDetailLoader(Context context, Bundle args) {
        super(context);

        if (args != null) {
            routeId = args.getInt(ATT_ROUT_ID);
        }
    }

    @Override
    public String loadInBackground() {
        OrmDBHelper dbHelper = OrmDBHelper.getReaderInstance(getContext());

        if (dbHelper == null) {
            return null;
        }
        try {
            QueryBuilder<RouteList, Integer> qbRouteList = dbHelper.getRouteListDao().queryBuilder();
            QueryBuilder<Routes, Integer> qbRoutes = dbHelper.getRoutesDao().queryBuilder();

            qbRouteList.where().eq(RouteList.ROUTE_ID, routeId);
            qbRoutes.join(qbRouteList);
            List<Routes> routesList = qbRoutes.query();
            if (routesList.size() > 0) {
                Routes routes = routesList.get(0);
                return routes.toString();
            }
            return "";
        } catch (SQLException e) {
            return "";
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
