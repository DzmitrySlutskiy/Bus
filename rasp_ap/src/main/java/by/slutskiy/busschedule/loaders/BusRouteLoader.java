package by.slutskiy.busschedule.loaders;

import android.content.Context;

import java.util.List;

import by.slutskiy.busschedule.data.entities.BusRoute;
import by.slutskiy.busschedule.data.DBReader;

/**
 * background data loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class BusRouteLoader extends BaseLoader<List<BusRoute>> {

    private List<BusRoute> mBusRoute = null;

    /**
     * @param context used to retrieve the application context.
     */
    public BusRouteLoader(Context context) {
        super(context);
    }

    @Override
    public List<BusRoute> loadInBackground() {
        DBReader dbReader = DBReader.getInstance(getContext());

        return mBusRoute = dbReader.getRoutesList();
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mBusRoute == null) {
            forceLoad();
        } else {
            deliverResult(mBusRoute);
        }
    }
}
