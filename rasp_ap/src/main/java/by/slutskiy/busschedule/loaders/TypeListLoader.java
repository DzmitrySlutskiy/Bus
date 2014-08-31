package by.slutskiy.busschedule.loaders;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import by.slutskiy.busschedule.data.DBReader;

/**
 * TypeListLoader
 * Version 1.0
 * 31.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class TypeListLoader extends AsyncTaskLoader<List<?>> {

    /**
     * background loader
     * if Id set as LOADER_TYPE_ID task load TypeList
     * if Id set as LOADER_TIME_ID task load TimeList
     * data will return as reference to Object class
     * in onLoadFinished data will cast to self type
     * this method help create universal loader for 2 different task
     */

    public static final String ATT_ROUT_LIST_ID = "routeListIdLoader";

    private int mRouteListIdLoader;

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public TypeListLoader(Context context, Bundle args) {
        super(context);

        if (args != null) {
            mRouteListIdLoader = args.getInt(ATT_ROUT_LIST_ID);
        }
    }

    @Override
    public List<?> loadInBackground() {
        DBReader dbReader = DBReader.getInstance(getContext());

        return dbReader.getTypeListByRouteListId(mRouteListIdLoader);
    }
}
