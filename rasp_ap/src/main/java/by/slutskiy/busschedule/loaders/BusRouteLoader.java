package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.database.Cursor;

import by.slutskiy.busschedule.data.DBReader;

/**
 * background data loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class BusRouteLoader extends BaseLoader {

    /**
     * @param context used to retrieve the application context.
     */
    public BusRouteLoader(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        DBReader dbReader = DBReader.getInstance(getContext());

        return dbReader.getRoutesListCursor();
    }
}
