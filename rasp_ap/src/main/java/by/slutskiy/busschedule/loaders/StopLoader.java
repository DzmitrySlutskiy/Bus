package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import by.slutskiy.busschedule.data.DBReader;
import by.slutskiy.busschedule.data.entities.Stop;

/**
 * background download task
 * Version information
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopLoader extends AsyncTaskLoader<List<Stop>> {
    public static final String ATT_ROUT_ID = "routeId";

    private int routeId;

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public StopLoader(Context context, Bundle args) {
        super(context);

        if (args != null) {
            routeId = args.getInt(ATT_ROUT_ID);
        }
    }

    @Override
    public List<Stop> loadInBackground() {
        DBReader dbReader = DBReader.getInstance(getContext());

        return dbReader.getRouteStopsList(routeId);
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        forceLoad();                //start a load.
    }
}
