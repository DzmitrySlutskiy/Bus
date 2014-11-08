package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.support.v4.content.CursorLoader;

import by.slutskiy.busschedule.providers.contracts.RouteContract;

/**
 * background data loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class BusRouteLoader extends CursorLoader {

    /**
     * @param context used to retrieve the application context.
     */
    public BusRouteLoader(Context context) {
        super(context, RouteContract.CONTENT_URI,
                RouteContract.availableColumns, null, null, null);
    }
}
