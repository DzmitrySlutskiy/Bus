/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*
 * DBReader - work with DB. Implemented as singleton. Use getInstance method for create object
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class DBReader extends DBStructure {

    /*   Log tags   */
    private static final String LOG_TAG = DBReader.class.getSimpleName();

    private volatile boolean mIsInUpdateState = false;

    private static DBReader sDbHelpInstance = null;

    /*  private constructor*/
    private DBReader(Context context) {
        super(context);
    }

    /*  singleton pattern   */

    /**
     * Create DBHelper class instance.
     *
     * @param context context to use to open or create the database
     * @return DBHelper class instance
     */
    public static DBReader getInstance(Context context) {
        if (sDbHelpInstance == null) {
            sDbHelpInstance = new DBReader(context);
        }
        return sDbHelpInstance;
    }

    public synchronized void setUpdateState(boolean state) {
        mIsInUpdateState = state;
        Log.d(LOG_TAG, "DBReader set isInUpdateState to:" + state);
    }

    synchronized boolean isInUpdateState() {
        return mIsInUpdateState;
    }

    /**
     * Get news from database
     *
     * @return List string with news
     */
    public Cursor getNewsCursor() {
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            return mDb.query(DB_TABLE_NEWS_LIST, null, null, null, null, null, null);
        }
        return null;
    }

    public Cursor getRoutesListCursor() {
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            return mDb.rawQuery(DB_SELECT_ROUTE_LIST, null);
        }
        return null;
    }

    /**
     * Get route detail string (string like "2 Девятовка 5 - Томина")
     *
     * @param routeId route ID
     * @return string in format "BUS_NUMBER   BEGIN_STOP - END STOP"
     */
    public Cursor getRouteDetailCursor(int routeId) {
        mDb = getDB();
        if (routeId >= 0 && mDb != null && mDb.isOpen()) {
            String[] args = new String[1];
            args[0] = "" + routeId;
            return mDb.rawQuery(DB_SELECT_ROUTE_BY_ROUTE_ID, args);
        }
        return null;
    }

    /**
     * Return stop list for some bus route if routeId >=0 otherwise return all stops list
     *
     * @param routeId - route ID
     * @return list of stops
     */
    public Cursor getRouteStopsListCursor(int routeId) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            if (routeId < 0) {
                return mDb.query(DB_TABLE_STOP_LIST, null, null, null, null, null, KEY_STOP_NAME);
            } else {
                String[] queryArgs = new String[1];
                queryArgs[0] = Integer.toString(routeId);

                return mDb.rawQuery(DB_SELECT_ROUTE_STOP_LIST_BY_ROUTE_ID, queryArgs);
            }
        }
        return null;
    }

    /**
     * Get type list for route list id
     *
     * @param routeListId route list ID
     * @return List of string
     */
    public Cursor getTypeListByRouteListIdCursor(int routeListId) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            String[] queryArgs = new String[1];
            queryArgs[0] = Integer.toString(routeListId);

            return mDb.rawQuery(DB_SELECT_DAY_TYPE_BY_ROUTE_LIST_ID, queryArgs);
        }
        return null;
    }

    /**
     * Get list with time for route with route list ID = routeListId
     *
     * @param routeListId route list ID
     * @return List of TimeList
     */
    public Cursor getTimeListByRouteListIdCursor(int routeListId) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            String[] queryArgs = new String[1];
            queryArgs[0] = Integer.toString(routeListId);

            return mDb.rawQuery(DB_SELECT_TIME_LIST_BY_ROUTE_LIST_ID, queryArgs);
        }
        return null;
    }

    /**
     * Get stop detail info with any route using this stop
     *
     * @param stopId stop ID
     * @param hour   hour
     * @return List of StopDetail
     */
    public Cursor getStopDetailCursor(int stopId, int hour) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            String[] queryArgs = new String[2];
            queryArgs[0] = Integer.toString(stopId);
            queryArgs[1] = Integer.toString(hour);

            return mDb.rawQuery(DB_SELECT_STOP_DETAIL, queryArgs);
        }
        return null;
    }

    /**
     * try to get database for write, if SQLiteException throws try to get readable database
     * if SQLiteException throws again - null will returned
     *
     * @return SQLiteDatabase instance or null if can't open connection to database
     */
    SQLiteDatabase getDB() {

        //не позволяем открыть базу если происходит ее обновление
        if (isInUpdateState()) {
            return null;
        }
        return super.getDB();
    }
}