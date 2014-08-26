package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import by.slutskiy.busschedule.StopDetail;
import by.slutskiy.busschedule.db.DBHelper;

/**
 * background task loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopDetailLoader extends AsyncTaskLoader<List<StopDetail>> {

    public static final String ATT_STOP_ID = "stopId";
    public static final String ATT_HOUR = "currentHour";
    private final int mStopIdLoader;
    private final int mCurrentHourLoader;

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public StopDetailLoader(Context context, Bundle args) {
        super(context);

        mStopIdLoader = args.getInt(ATT_STOP_ID);
        mCurrentHourLoader = args.getInt(ATT_HOUR);
    }

    @Override
    public List<StopDetail> loadInBackground() {
        DBHelper dbHelper1 = DBHelper.getInstance(getContext());

        return dbHelper1.getStopDetail(mStopIdLoader, mCurrentHourLoader);
    }
}
