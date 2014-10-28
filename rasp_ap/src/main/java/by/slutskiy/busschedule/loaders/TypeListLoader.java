package by.slutskiy.busschedule.loaders;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import by.slutskiy.busschedule.providers.contracts.TimeListContract;

/**
 * TypeListLoader
 * Version 1.0
 * 31.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class TypeListLoader extends BaseLoader {

    public static final String ATT_ROUT_LIST_ID = "routeListIdLoader";

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public TypeListLoader(Context context, Bundle args) {
        super(context,
                Uri.withAppendedPath(TimeListContract.CONTENT_TYPE_URI,
                        "" + args.getInt(ATT_ROUT_LIST_ID)),
                new String[]{TimeListContract.COLUMN_MINUTES},
                null, null, null);
    }
}
