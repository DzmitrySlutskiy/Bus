package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

import by.slutskiy.busschedule.data.DBReader;

/**
 * TimeListLoader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class TimeListLoader extends CursorLoader {

    public static final String ATT_ROUT_LIST_ID = "routeListIdLoader";
    private int mRouteListIdLoader;

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public TimeListLoader(Context context, Bundle args) {
        super(context);

        if (args != null) {
            mRouteListIdLoader = args.getInt(ATT_ROUT_LIST_ID);
        }
    }

    @Override
    public Cursor loadInBackground() {
        DBReader dbReader = DBReader.getInstance(getContext());

        return dbReader.getTimeListByRouteListIdCursor(mRouteListIdLoader);
    }
}