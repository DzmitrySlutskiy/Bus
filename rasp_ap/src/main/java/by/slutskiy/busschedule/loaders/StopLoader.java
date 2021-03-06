package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

import by.slutskiy.busschedule.providers.contracts.RouteListContract;
import by.slutskiy.busschedule.providers.contracts.StopContract;

/**
 * background download task
 * Version information
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopLoader extends CursorLoader {
    public static final String ATT_ROUT_ID = "routeId";

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public StopLoader(Context context, Bundle args) {
        super(context,
                getUri(args),
                StopContract.availableColumns,
                getSelection(args),
                getSelectionArgs(args),
                getSortOrder(args)
        );
    }

    private static String getSortOrder(Bundle args) {
        return (args != null)
                ? null
                : StopContract.COLUMN_STOP_NAME;
    }

    private static String[] getSelectionArgs(Bundle args) {
        return (args != null)
                ? new String[]{Integer.toString(args.getInt(ATT_ROUT_ID))}
                : null;
    }

    private static String getSelection(Bundle args) {
        return (args != null)
                ? RouteListContract.COLUMN_ROUTE_ID + " = ?"
                : null;
    }

    private static Uri getUri(Bundle args) {
        return (args != null)
                ? RouteListContract.CONTENT_URI
                : StopContract.CONTENT_URI;
    }
}
