package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.OrmDBHelper;
import by.slutskiy.busschedule.data.entities.RouteList;
import by.slutskiy.busschedule.data.entities.TimeList;
import by.slutskiy.busschedule.data.entities.StopDetail;

/**
 * background task loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopDetailLoader extends AsyncTaskLoader<List<StopDetail>> {

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
        List<StopDetail> stopDetailList = new ArrayList<StopDetail>();
        OrmDBHelper dbHelper = OrmDBHelper.getReaderInstance(getContext());

        if (dbHelper == null) {
            return null;
        }

        List<RouteList> routeList;
        try {
            QueryBuilder<RouteList, Integer> qbRouteList = dbHelper.getRouteListDao().queryBuilder();
            qbRouteList.where().eq(RouteList.STOP_ID, mStopIdLoader);

            routeList = qbRouteList.query();
        } catch (SQLException e) {
            return null;
        }

        for (RouteList item : routeList) {
            StopDetail stopDetail = new StopDetail();
            stopDetail.setRouteListId(item.getmId());
            stopDetail.setRouteId(item.getmRoutes().getmId());
            stopDetail.setRouteName(item.getmRoutes().toString());
            ForeignCollection<TimeList> timeLists = item.getmTimeList();
            for (TimeList timeListItem : timeLists) {
                if (timeListItem.getmHour() == mCurrentHourLoader) {
                    String minutes = timeListItem.getmMinutes();
                    if (minutes.isEmpty()) {
                        minutes = getContext().getString(R.string.schedule_noBus);
                    }
                    stopDetail.addMinute(timeListItem.getmDayType().getmType() +
                            " " + minutes);
                }
            }
            stopDetailList.add(stopDetail);
        }

        return stopDetailList;
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
