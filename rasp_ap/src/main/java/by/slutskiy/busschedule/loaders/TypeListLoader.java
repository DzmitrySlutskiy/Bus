package by.slutskiy.busschedule.loaders;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

import by.slutskiy.busschedule.providers.contracts.TimeListContract;

/**
 * TypeListLoader
 * Version 1.0
 * 31.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class TypeListLoader extends CursorLoader {

    public static final String ATT_ROUT_LIST_ID = "routeListIdLoader";

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public TypeListLoader(Context context, Bundle args) {
        super(context,
                TimeListContract.CONTENT_TYPE_URI,
                new String[]{TimeListContract.COLUMN_TYPES},
                TimeListContract.COLUMN_ROUTE_LIST_ID + " = ?",
                new String[]{Integer.toString(args.getInt(ATT_ROUT_LIST_ID))},
                null);
    }
}
