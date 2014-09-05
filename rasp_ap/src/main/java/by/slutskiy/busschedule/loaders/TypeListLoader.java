package by.slutskiy.busschedule.loaders;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import by.slutskiy.busschedule.data.OrmDBHelper;
import by.slutskiy.busschedule.data.entities.TimeList;
import by.slutskiy.busschedule.data.entities.TypeList;

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
        OrmDBHelper dbHelper = OrmDBHelper.getReaderInstance(getContext());

        if (dbHelper == null) {
            return null;
        }

        try {
            QueryBuilder<TypeList, Integer> qbTypeList = dbHelper.getTypeListDao().queryBuilder();
            QueryBuilder<TimeList, Integer> qbTimeList = dbHelper.getTimeListDao().queryBuilder();

            qbTimeList.selectColumns(TimeList.DAY_TYPE_ID);
            qbTimeList.distinct().where().eq(TimeList.ROUTE_LIST_ID, mRouteListIdLoader);
            qbTypeList.where().in(TypeList.ID, qbTimeList);

            return qbTypeList.query();
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
