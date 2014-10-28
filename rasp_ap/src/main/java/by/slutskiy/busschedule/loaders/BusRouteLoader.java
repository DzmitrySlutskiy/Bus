package by.slutskiy.busschedule.loaders;

import android.content.Context;

import by.slutskiy.busschedule.providers.contracts.RouteContract;

/**
 * background data loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class BusRouteLoader extends BaseLoader {

    /**
     * @param context used to retrieve the application context.
     */
    public BusRouteLoader(Context context) {
        super(context, RouteContract.CONTENT_URI,
                new String[]{
                        RouteContract.COLUMN_ID,
                        RouteContract.COLUMN_BUS_NUMBER,
                        RouteContract.COLUMN_BEGIN_STOP,
                        RouteContract.COLUMN_END_STOP}
                , null, null, null);
    }
}
