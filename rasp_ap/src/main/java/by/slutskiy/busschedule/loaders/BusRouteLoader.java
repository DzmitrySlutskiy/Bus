package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.sql.SQLException;
import java.util.List;

import by.slutskiy.busschedule.data.OrmDBHelper;
import by.slutskiy.busschedule.data.entities.Routes;

/**
 * background data loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class BusRouteLoader extends AsyncTaskLoader<List<Routes>> {

    /**
     * @param context used to retrieve the application context.
     */
    public BusRouteLoader(Context context) {
        super(context);
    }

    @Override
    public List<Routes> loadInBackground() {
        OrmDBHelper dbHelper = OrmDBHelper.getReaderInstance(getContext());

        if (dbHelper == null) { //null if DB update
            return null;
        }

        try {
            return dbHelper.getRoutesDao().queryForAll();
        } catch (SQLException e) {
            return null;
        }

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
