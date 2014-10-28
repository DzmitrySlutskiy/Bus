package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import by.slutskiy.busschedule.providers.contracts.RouteContract;

/**
 * RouteDetailLoader
 * Version 1.0
 * 31.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class RouteDetailLoader extends BaseLoader {

    public static final String ATT_ROUT_ID = "routeId";

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public RouteDetailLoader(Context context, Bundle args) {
        super(context,
                Uri.withAppendedPath(RouteContract.CONTENT_URI, "" + args.getInt(ATT_ROUT_ID)),
                new String[]{
                        RouteContract.COLUMN_ID,
                        RouteContract.COLUMN_BUS_NUMBER,
                        RouteContract.COLUMN_BEGIN_STOP,
                        RouteContract.COLUMN_END_STOP
                }, null, null, null);
    }
}
