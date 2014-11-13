package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

import by.slutskiy.busschedule.providers.contracts.RouteListContract;
import by.slutskiy.busschedule.providers.contracts.StopDetailContract;
import by.slutskiy.busschedule.providers.contracts.TimeListContract;

/**
 * background task loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopDetailLoader extends CursorLoader {

    public static final String ATT_STOP_ID = "stopId";
    public static final String ATT_HOUR = "currentHour";

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public StopDetailLoader(Context context, Bundle args) {
        super(context,
                StopDetailContract.CONTENT_URI,
                new String[]{StopDetailContract.COLUMN_ID,
                        StopDetailContract.COLUMN_FULL_ROUTE,
                        StopDetailContract.COLUMN_MINUTES,
                        StopDetailContract.COLUMN_TYPES},
                RouteListContract.PATH + "." + RouteListContract.COLUMN_STOP_ID + " = ? AND " +
                        TimeListContract.PATH + "." + TimeListContract.COLUMN_HOUR + " = ? ",
                new String[]{Integer.toString(args.getInt(ATT_STOP_ID)),
                        Integer.toString(args.getInt(ATT_HOUR))}, null);

    }
}
