package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import by.slutskiy.busschedule.providers.contracts.RouteListContract;
import by.slutskiy.busschedule.providers.contracts.StopContract;

/**
 * background download task
 * Version information
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopLoader extends BaseLoader {
    public static final String ATT_ROUT_ID = "routeId";

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public StopLoader(Context context, Bundle args) {
        super(context,

                (args != null)
                        ? Uri.withAppendedPath(RouteListContract.CONTENT_URI, "" + args.getInt(ATT_ROUT_ID))
                        : StopContract.CONTENT_URI,

                new String[]{
                        StopContract.COLUMN_ID,
                        StopContract.COLUMN_STOP_NAME},
                null, null,

                (args != null)
                        ? null
                        : StopContract.COLUMN_STOP_NAME
        );
    }
}
