package busschedule.rasp_ap.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import busschedule.rasp_ap.BusRoute;
import busschedule.rasp_ap.db.DBHelper;

/**
 * background data loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class BusRouteLoader extends AsyncTaskLoader<List<BusRoute>> {

    /**
     * @param context used to retrieve the application context.
     */
    public BusRouteLoader(Context context) {
        super(context);
    }

    @Override
    public List<BusRoute> loadInBackground() {
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        return dbHelper.getRoutesList();
    }

}
