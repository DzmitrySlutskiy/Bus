package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.os.Bundle;

import by.slutskiy.busschedule.data.DBReader;

/**
 * RouteDetailLoader
 * Version 1.0
 * 31.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class RouteDetailLoader extends CacheLoader<String> {

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
        DBReader dbReader = DBReader.getInstance(getContext());

        return dbReader.getRouteDetail(routeId);
    }
}
