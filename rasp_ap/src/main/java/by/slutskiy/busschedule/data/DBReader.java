/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import by.slutskiy.busschedule.data.entities.BusRoute;
import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.entities.Stop;
import by.slutskiy.busschedule.data.entities.StopDetail;
import by.slutskiy.busschedule.data.entities.TimeList;

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
    public List<String> getNews() {
        List<String> newsList = new ArrayList<String>();
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            Cursor cursor = mDb.query(DB_TABLE_NEWS_LIST, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    int fieldIndex = cursor.getColumnIndex(KEY_NEWS_TEXT);
                    if (fieldIndex >= 0) {
                        newsList.add(cursor.getString(fieldIndex));
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return newsList;
    }

    /**
     * Get route list
     *
     * @return List of BusRoute objects
     */
    public List<BusRoute> getRoutesList() {
        List<BusRoute> routesList = new ArrayList<BusRoute>();
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            Cursor cursor = mDb.rawQuery(DB_SELECT_ROUTE_LIST, null);
            if (cursor.moveToFirst()) {
                do {
                    BusRoute busRoute = new BusRoute();

                    int fieldIndex = cursor.getColumnIndex(KEY_ID);//0
                    if (fieldIndex >= 0) {
                        busRoute.setRouteId(cursor.getInt(fieldIndex));
                    }

                    fieldIndex = cursor.getColumnIndex(KEY_BUS_NUMBER);//1
                    if (fieldIndex >= 0) {
                        busRoute.setBusNumber(cursor.getString(fieldIndex));
                    }

                    fieldIndex = cursor.getColumnIndex(KEY_BEGIN_STOP);//2
                    if (fieldIndex >= 0) {
                        busRoute.setBeginStop(cursor.getString(fieldIndex));
                    }

                    fieldIndex = cursor.getColumnIndex(KEY_END_STOP);//3
                    if (fieldIndex >= 0) {
                        busRoute.setEndStop(cursor.getString(fieldIndex));
                    }
                    routesList.add(busRoute);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return routesList;
    }

    /**
     * Get route detail string (string like "2 Девятовка 5 - Томина")
     *
     * @param routeId route ID
     * @return string in format "BUS_NUMBER   BEGIN_STOP - END STOP"
     */
    public String getRouteDetail(int routeId) {
        String result = "";
        mDb = getDB();
        if (routeId >= 0 && mDb != null && mDb.isOpen()) {
            String[] args = new String[1];
            args[0] = "" + routeId;
            Cursor cursor = mDb.rawQuery(DB_SELECT_ROUTE_BY_ROUTE_ID, args);
            if (cursor.moveToFirst()) {
                String busNumber = "";
                String beginStop = "";
                String endStop = "";

                int fieldIndex = cursor.getColumnIndex(KEY_BUS_NUMBER);//1
                if (fieldIndex >= 0) {
                    busNumber = cursor.getString(fieldIndex);
                }

                fieldIndex = cursor.getColumnIndex(KEY_BEGIN_STOP);//2
                if (fieldIndex >= 0) {
                    beginStop = cursor.getString(fieldIndex);
                }

                fieldIndex = cursor.getColumnIndex(KEY_END_STOP);//3
                if (fieldIndex >= 0) {
                    endStop = cursor.getString(fieldIndex);
                }

                result = busNumber + "   " + beginStop + " - " + endStop;
            }
            cursor.close();
        }
        return result;
    }

    /**
     * Return stop list for some bus route if routeId >=0 otherwise return all stops list
     *
     * @param routeId - route ID
     * @return list of stops
     */
    public List<Stop> getRouteStopsList(int routeId) {
        List<Stop> stopList = new ArrayList<Stop>();
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            Cursor cursor;
            if (routeId < 0) {
                cursor = mDb.query(DB_TABLE_STOP_LIST, null, null, null, null, null, null);
            } else {
                String[] queryArgs = new String[1];
                queryArgs[0] = Integer.toString(routeId);

                cursor = mDb.rawQuery(DB_SELECT_ROUTE_STOP_LIST_BY_ROUTE_ID, queryArgs);
            }
            if (cursor.moveToFirst()) {
                do {
                    Stop stop = new Stop();

                    int fieldIndex = cursor.getColumnIndex(KEY_ID);
                    if (fieldIndex >= 0) {
                        stop.setKey(cursor.getInt(fieldIndex));
                    }

                    fieldIndex = cursor.getColumnIndex(KEY_STOP_NAME);
                    if (fieldIndex >= 0) {
                        stop.setStopName(cursor.getString(fieldIndex));
                    }

                    stopList.add(stop);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return stopList;
    }

    /**
     * Get type list for route list id
     *
     * @param routeListId route list ID
     * @return List of string
     */
    public List<String> getTypeListByRouteListId(int routeListId) {
        List<String> typeList = new ArrayList<String>();
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            String[] queryArgs = new String[1];
            queryArgs[0] = Integer.toString(routeListId);

            Cursor cursor = mDb.rawQuery(DB_SELECT_DAY_TYPE_BY_ROUTE_LIST_ID, queryArgs);
            if (cursor.moveToFirst()) {
                do {
                    int fieldIndex = cursor.getColumnIndex(KEY_TYPE);
                    if (fieldIndex >= 0) {
                        String type = cursor.getString(fieldIndex);
                        typeList.add(type);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return typeList;
    }

    /**
     * Get list with time for route with route list ID = routeListId
     *
     * @param routeListId route list ID
     * @return List of TimeList
     */
    public List<TimeList> getTimeListByRouteListId(int routeListId) {
        List<TimeList> timeList = new ArrayList<TimeList>();
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            String[] queryArgs = new String[1];
            queryArgs[0] = Integer.toString(routeListId);

            Cursor cursor = mDb.rawQuery(DB_SELECT_TIME_LIST_BY_ROUTE_LIST_ID, queryArgs);
            TimeList timeListItem = null;
            int lastHour = - 1;
            if (cursor.moveToFirst()) {
                do {
                    int currentHour = - 1;

                    int fieldIndex = cursor.getColumnIndex(KEY_HOUR);
                    if (fieldIndex >= 0) {
                        currentHour = cursor.getInt(fieldIndex);
                    }

                    if (lastHour == - 1 || currentHour != lastHour) {
                        if (timeListItem != null) {
                            timeList.add(timeListItem);
                        }

                        timeListItem = new TimeList();
                        lastHour = currentHour;
                        timeListItem.setHour(currentHour);
                    }

                    fieldIndex = cursor.getColumnIndex(KEY_MINUTES);
                    if (fieldIndex >= 0) {
                        timeListItem.addMin(cursor.getString(fieldIndex));
                    }

                } while (cursor.moveToNext());
                if (! timeList.contains(timeListItem)) {
                    timeList.add(timeListItem);
                }
            }
            cursor.close();
        }
        return timeList;
    }

    /**
     * Get stop detail info with any route using this stop
     *
     * @param stopId stop ID
     * @param hour   hour
     * @return List of StopDetail
     */
    public List<StopDetail> getStopDetail(int stopId, int hour) {
        List<StopDetail> stopDetailList = new ArrayList<StopDetail>();
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            String[] queryArgs = new String[2];
            queryArgs[0] = Integer.toString(stopId);
            queryArgs[1] = Integer.toString(hour);

            Cursor cursor = mDb.rawQuery(DB_SELECT_STOP_DETAIL, queryArgs);
            if (cursor.moveToFirst()) {
                StopDetail stopDetail = new StopDetail();
                int lastRouteId = - 1;
                do {
                    int routeId = 0;

                    int fieldIndex = cursor.getColumnIndex(KEY_ROUTE_ID);
                    if (fieldIndex >= 0) {
                        routeId = cursor.getInt(fieldIndex);
                    }

                    if (lastRouteId != routeId) {
                        if (lastRouteId != - 1) stopDetail = new StopDetail();
                        lastRouteId = routeId;
                        String routeName = "";

                        fieldIndex = cursor.getColumnIndex(KEY_BUS_NUMBER);
                        if (fieldIndex >= 0) {
                            routeName += cursor.getString(fieldIndex) + "   ";
                        }

                        fieldIndex = cursor.getColumnIndex(KEY_BEGIN_STOP);
                        if (fieldIndex >= 0) {
                            routeName += cursor.getString(fieldIndex) + " - ";
                        }

                        fieldIndex = cursor.getColumnIndex(KEY_END_STOP);
                        if (fieldIndex >= 0) {
                            routeName += cursor.getString(fieldIndex);
                        }

                        stopDetailList.add(stopDetail);//сохраняем ссылку в списке

                        fieldIndex = cursor.getColumnIndex(KEY_ID);
                        if (fieldIndex >= 0) {
                            stopDetail.setRouteListId(cursor.getInt(fieldIndex));
                        }
                        stopDetail.setRouteId(routeId);
                        stopDetail.setRouteName(routeName);
                    }
                    String type = "";
                    String minutes = "";

                    fieldIndex = cursor.getColumnIndex(KEY_TYPE);
                    if (fieldIndex >= 0) {
                        type = cursor.getString(fieldIndex);
                    }

                    fieldIndex = cursor.getColumnIndex(KEY_MINUTES);
                    if (fieldIndex >= 0) {
                        minutes = cursor.getString(fieldIndex).trim();
                    }

                    if (minutes.isEmpty()) {
                        minutes = mContext.getString(R.string.text_view_no_bus);
                    }
                    stopDetail.addMinute(type + " " + minutes);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return stopDetailList;
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