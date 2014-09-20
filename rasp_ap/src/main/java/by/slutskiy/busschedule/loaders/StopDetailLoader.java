package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.os.Bundle;

import java.util.List;

import by.slutskiy.busschedule.data.entities.StopDetail;
import by.slutskiy.busschedule.data.DBReader;

/**
 * background task loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopDetailLoader extends CacheLoader<List<StopDetail>> {

    public static final String ATT_STOP_ID = "stopId";
    public static final String ATT_HOUR = "currentHour";
    private int mStopIdLoader;
    private int mCurrentHourLoader;

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public StopDetailLoader(Context context, Bundle args) {
        super(context);

        if (args != null) {
            mStopIdLoader = args.getInt(ATT_STOP_ID);
            mCurrentHourLoader = args.getInt(ATT_HOUR);
        }
    }

    @Override
    public List<StopDetail> loadInBackground() {
        DBReader dbReader = DBReader.getInstance(getContext());

        return setCacheData(dbReader.getStopDetail(mStopIdLoader, mCurrentHourLoader));
    }
}
