/*
 * Bus schedule for Grodno
 */

package busschedule.rasp_ap.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import busschedule.rasp_ap.BuildConfig;
import busschedule.rasp_ap.BusRoute;
import busschedule.rasp_ap.Constants;
import busschedule.rasp_ap.R;
import busschedule.rasp_ap.Stop;
import busschedule.rasp_ap.StopDetail;
import busschedule.rasp_ap.TimeList;

/*
 * DBHelper - work with DB. Implemented as singleton. Use getInstance method for create object
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_TRY_CREATE = "try to create DB";
    private static final String DB_MUST_UPDATE = "database must be updated to new version ";
    private static final String DB_EXEC_SQL = "Exec SQL: ";

    /*  default buffer size  */
    private static final int BUFFER_SIZE = 8192;

    /**
     * Database Name
     */
    private static final String DB_NAME = "ap.db";

    /**
     * Версия базы данных
     */
    private static final int DB_VERSION = 2;

    /*  Table list  */
    private static final String DB_TABLE_NEWS_LIST = "News";
    private static final String DB_TABLE_STOP_LIST = "StopList";
    private static final String DB_TABLE_BUS_LIST = "BusList";
    private static final String DB_TABLE_ROUTES = "Routes";
    private static final String DB_TABLE_ROUTE_LIST = "RouteList";
    private static final String DB_TABLE_TIME_LIST = "TimeList";
    private static final String DB_TABLE_TYPE_LIST = "TypeList";
    /*  Table fields    */
    private static final String KEY_NEWS_TEXT = "NewsText";
    private static final String KEY_STOP_NAME = "StopName";
    private static final String KEY_BUS_NUMBER = "BusNumber";

    private static final String KEY_BUS_ID = "BusId";
    private static final String KEY_BEGIN_STOP_ID = "BeginStopId";
    private static final String KEY_END_STOP_ID = "EndStopId";

    private static final String KEY_ROUTE_ID = "RouteId";
    private static final String KEY_STOP_ID = "StopId";
    private static final String KEY_STOP_INDEX = "StopIndex";
    private static final String KEY_ROUTE_LIST_ID = "RouteListId";
    private static final String KEY_HOUR = "Hour";
    private static final String KEY_MINUTES = "Minutes";
    private static final String KEY_DAY_TYPE_ID = "DayTypeId";

    private static final String KEY_TYPE = "Type";

    private static final String KEY_ID = "_id";

    private static final String KEY_BEGIN_STOP = "BeginStop";
    private static final String KEY_END_STOP = "EndStop";
    /*  SQLite data types */
    private static final String SQL_INTEGER = "INTEGER";
    private static final String SQL_TEXT = "TEXT";
    private static final String SQL_P_KEY = "PRIMARY KEY AUTOINCREMENT";
    /*  SQL query substring   */
    private static final String SQL_ON = " ON ";
    private static final String SQL_INNER_JOIN = "INNER JOIN ";
    private static final String SQL_SELECT = "SELECT ";
    private static final String SQL_CREATE_TABLE = "CREATE TABLE ";
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String SQL_FROM = " FROM ";
    private static final String SQL_WHERE = " WHERE ";
    private static final String SQL_CREATE_INDEX = "CREATE INDEX idx";
    private static final String SQL_AS = " AS ";
    private static final String SQL_DISTINCT = " DISTINCT ";
    private static final String SQL_ORDER_BY = " ORDER BY ";
    private static final String SQL_ASC = " ASC ";
    private static final String SQL_IN = " IN ";

    /*  CREATE TABLE QUERY    */
    private static final String DB_CREATE_NEWS_LIST = SQL_CREATE_TABLE +
            DB_TABLE_NEWS_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_NEWS_TEXT + " " + SQL_TEXT + ");";

    private static final String DB_CREATE_STOP_LIST = SQL_CREATE_TABLE +
            DB_TABLE_STOP_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_STOP_NAME + " " + SQL_TEXT + ");";

    private static final String DB_CREATE_BUS_LIST = SQL_CREATE_TABLE +
            DB_TABLE_BUS_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_BUS_NUMBER + " " + SQL_TEXT + ");";

    private static final String DB_CREATE_ROUTES = SQL_CREATE_TABLE +
            DB_TABLE_ROUTES + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_BUS_ID + " " + SQL_INTEGER + ", " +
            KEY_BEGIN_STOP_ID + " " + SQL_INTEGER + "," +
            KEY_END_STOP_ID + " " + SQL_INTEGER + ");";

    private static final String DB_CREATE_ROUTE_LIST = SQL_CREATE_TABLE +
            DB_TABLE_ROUTE_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_ROUTE_ID + " " + SQL_INTEGER + ", " +
            KEY_STOP_ID + " " + SQL_INTEGER + "," +
            KEY_STOP_INDEX + " " + SQL_INTEGER + ");";

    private static final String DB_CREATE_TIME_LIST = SQL_CREATE_TABLE +
            DB_TABLE_TIME_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_ROUTE_LIST_ID + " " + SQL_INTEGER + ", " +
            KEY_HOUR + " " + SQL_INTEGER + ", " +
            KEY_MINUTES + " " + SQL_TEXT + ", " +
            KEY_DAY_TYPE_ID + " " + SQL_INTEGER + ");";

    private static final String DB_CREATE_TYPE_LIST = SQL_CREATE_TABLE +
            DB_TABLE_TYPE_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_TYPE + " " + SQL_TEXT + ");";

    /*  CREATE INDEX QUERY  */
    private static final String DB_CREATE_INDEX_TIME_LIST = SQL_CREATE_INDEX + KEY_ROUTE_LIST_ID +
            SQL_ON + DB_TABLE_TIME_LIST + " (\"" + KEY_ROUTE_LIST_ID + "\");";

    private static final String DB_CREATE_INDEX_ROUTE_LIST = SQL_CREATE_INDEX + KEY_ROUTE_ID + SQL_ON +
            DB_TABLE_ROUTE_LIST + " (\"" + KEY_ROUTE_ID + "\");";

    private static final String DB_CREATE_INDEX_STOP_LIST = SQL_CREATE_INDEX + KEY_STOP_NAME + SQL_ON +
            DB_TABLE_STOP_LIST + " (\"" + KEY_STOP_NAME + "\");";

    /*   SELECT QUERY   */
    private static final String DB_SELECT_ROUTE_LIST = SQL_SELECT +
            DB_TABLE_ROUTES + "." + KEY_ID + ", " +
            DB_TABLE_BUS_LIST + "." + KEY_BUS_NUMBER + ", " +
            DB_TABLE_STOP_LIST + "." + KEY_STOP_NAME + SQL_AS + KEY_BEGIN_STOP + ", " +
            DB_TABLE_STOP_LIST + "2." + KEY_STOP_NAME + SQL_AS + KEY_END_STOP + "\n" +
            SQL_FROM + DB_TABLE_ROUTES + "\n " +
            SQL_INNER_JOIN + DB_TABLE_BUS_LIST + SQL_ON + DB_TABLE_ROUTES + "." + KEY_BUS_ID + " = " +
            DB_TABLE_BUS_LIST + "." + KEY_ID + "\n " +
            SQL_INNER_JOIN + DB_TABLE_STOP_LIST + SQL_ON + DB_TABLE_ROUTES + "." +
            KEY_BEGIN_STOP_ID + " = " + DB_TABLE_STOP_LIST + "." + KEY_ID + "\n" +
            SQL_INNER_JOIN + DB_TABLE_STOP_LIST + SQL_AS + DB_TABLE_STOP_LIST + "2 " +
            SQL_ON + DB_TABLE_ROUTES + "." + KEY_END_STOP_ID + " = " +
            DB_TABLE_STOP_LIST + "2." + KEY_ID + ";";

    private static final String DB_SELECT_ROUTE_STOP_LIST_BY_ROUTE_ID = SQL_SELECT +
            DB_TABLE_ROUTE_LIST + "." + KEY_ID + ", " +
            DB_TABLE_ROUTE_LIST + "." + KEY_ROUTE_ID + ", " +
            DB_TABLE_STOP_LIST + "." + KEY_STOP_NAME +
            SQL_FROM + DB_TABLE_ROUTE_LIST + "\n" +
            SQL_INNER_JOIN + DB_TABLE_STOP_LIST + SQL_ON +
            DB_TABLE_ROUTE_LIST + "." + KEY_STOP_ID + " = " + DB_TABLE_STOP_LIST + "." + KEY_ID +
            SQL_WHERE + KEY_ROUTE_ID + " = ?" +
            SQL_ORDER_BY + KEY_STOP_INDEX + SQL_ASC + ";";

    private static final String DB_SELECT_DAY_TYPE_BY_ROUTE_LIST_ID = SQL_SELECT + "*" +
            SQL_FROM + DB_TABLE_TYPE_LIST + SQL_WHERE + DB_TABLE_TYPE_LIST + "." + KEY_ID +
            SQL_IN + "(" + SQL_SELECT + SQL_DISTINCT + KEY_DAY_TYPE_ID + SQL_FROM +
            DB_TABLE_TIME_LIST + SQL_WHERE + KEY_ROUTE_LIST_ID + " = ?);";

    private static final String DB_SELECT_TIME_LIST_BY_ROUTE_LIST_ID = SQL_SELECT +
            DB_TABLE_TIME_LIST + "." + KEY_HOUR + ", " + DB_TABLE_TIME_LIST + "." + KEY_MINUTES + ", " +
            DB_TABLE_TIME_LIST + ". " + KEY_DAY_TYPE_ID +
            SQL_FROM + DB_TABLE_TIME_LIST + SQL_WHERE + KEY_ROUTE_LIST_ID + " = ?;";

    private static final String DB_SELECT_ROUTE_BY_ROUTE_ID = SQL_SELECT +
            DB_TABLE_ROUTES + "." + KEY_ID + ", " +
            DB_TABLE_BUS_LIST + "." + KEY_BUS_NUMBER + ", " +
            DB_TABLE_STOP_LIST + "." + KEY_STOP_NAME + SQL_AS + KEY_BEGIN_STOP + ", " +
            DB_TABLE_STOP_LIST + "2." + KEY_STOP_NAME + SQL_AS + KEY_END_STOP + "\n" +
            SQL_FROM + DB_TABLE_ROUTES + "\n " +
            SQL_INNER_JOIN + DB_TABLE_BUS_LIST + SQL_ON + DB_TABLE_ROUTES + "." +
            KEY_BUS_ID + " = " + DB_TABLE_BUS_LIST + "." + KEY_ID + "\n " +
            SQL_INNER_JOIN + DB_TABLE_STOP_LIST + SQL_ON + DB_TABLE_ROUTES + "." +
            KEY_BEGIN_STOP_ID + " = " + DB_TABLE_STOP_LIST + "." + KEY_ID + "\n" +
            SQL_INNER_JOIN + DB_TABLE_STOP_LIST + SQL_AS + DB_TABLE_STOP_LIST + "2 " +
            SQL_ON + DB_TABLE_ROUTES + "." + KEY_END_STOP_ID + " = " + DB_TABLE_STOP_LIST + "2." +
            KEY_ID + SQL_WHERE + DB_TABLE_ROUTES + "." + KEY_ID + "= ? ;";

    private static final String DB_SELECT_STOP_DETAIL = SQL_SELECT +
            DB_TABLE_ROUTE_LIST + "." + KEY_ID + ", " +
            DB_TABLE_ROUTE_LIST + "." + KEY_STOP_ID + ", " +
            DB_TABLE_ROUTE_LIST + "." + KEY_STOP_INDEX + ", " +
            DB_TABLE_ROUTE_LIST + "." + KEY_ROUTE_ID + ", " +
            DB_TABLE_ROUTES + "." + KEY_BUS_ID + ", " +
            DB_TABLE_BUS_LIST + "." + KEY_BUS_NUMBER + ", " +
            DB_TABLE_STOP_LIST + "." + KEY_STOP_NAME + SQL_AS + KEY_BEGIN_STOP + ", " +
            DB_TABLE_STOP_LIST + "2." + KEY_STOP_NAME + SQL_AS + KEY_END_STOP + ", " +
            DB_TABLE_TIME_LIST + "." + KEY_HOUR + ", " +
            DB_TABLE_TIME_LIST + "." + KEY_MINUTES + ", " +
            DB_TABLE_TYPE_LIST + "." + KEY_TYPE + " " +
            SQL_FROM + DB_TABLE_ROUTE_LIST + " " +
            SQL_INNER_JOIN + DB_TABLE_ROUTES + SQL_ON + DB_TABLE_ROUTE_LIST + "." +
            KEY_ROUTE_ID + " = " + DB_TABLE_ROUTES + "." + KEY_ID + " " +
            SQL_INNER_JOIN + DB_TABLE_BUS_LIST + SQL_ON + DB_TABLE_ROUTES + "." +
            KEY_BUS_ID + " = " + DB_TABLE_BUS_LIST + "." + KEY_ID + " " +
            SQL_INNER_JOIN + DB_TABLE_STOP_LIST + SQL_ON + DB_TABLE_ROUTES + "." +
            KEY_BEGIN_STOP_ID + " = " + DB_TABLE_STOP_LIST + "." + KEY_ID + " " +
            SQL_INNER_JOIN + DB_TABLE_STOP_LIST + SQL_AS + DB_TABLE_STOP_LIST + "2" +
            SQL_ON + DB_TABLE_ROUTES + "." + KEY_END_STOP_ID + " = " +
            DB_TABLE_STOP_LIST + "2." + KEY_ID + " " +
            SQL_INNER_JOIN + DB_TABLE_TIME_LIST + SQL_ON + DB_TABLE_TIME_LIST + "." +
            KEY_ROUTE_LIST_ID + " = " + DB_TABLE_ROUTE_LIST + "." + KEY_ID + " " +
            SQL_INNER_JOIN + DB_TABLE_TYPE_LIST + SQL_ON + DB_TABLE_TYPE_LIST + "." +
            KEY_ID + " = " + DB_TABLE_TIME_LIST + "." + KEY_DAY_TYPE_ID + " " +
            SQL_WHERE + DB_TABLE_ROUTE_LIST + "." + KEY_STOP_ID + " = ? AND " +
            DB_TABLE_TIME_LIST + "." + KEY_HOUR + " = ?";

    private SQLiteDatabase mDb;
    private final Context mContext;
    private String mDbPath;

    private static DBHelper sDbHelpInstance = null;

    /*  private constructor*/
    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        mDbPath = context.getDatabasePath(DB_NAME).getPath();

        /*cut DB_NAME from path - get full path */
        mDbPath = mDbPath.substring(0, mDbPath.length() - DB_NAME.length() - 1);
        if (! new File(mDbPath + "/" + DB_NAME).exists()) {

            /*  extract database from raw resource (zip file)*/
            extractDB();
        }
        openDB();
    }

    /*  singleton pattern   */

    /**
     * Create DBHelper class instance.
     *
     * @param context context to use to open or create the database
     * @return DBHelper class instance
     */
    public static DBHelper getInstance(Context context) {
        if (sDbHelpInstance == null) {
            sDbHelpInstance = new DBHelper(context);
        }
        return sDbHelpInstance;
    }

    /**
     * Open database connection
     */
    void openDB() {
        mDb = getDB();
        if (BuildConfig.DEBUG) {
            Log.i(Constants.LOG_TAG, "openDB " + (mDb != null ? "successful" : "unsuccessful"));
        }
    }

    /**
     * Close database connection
     */
    public void closeDB() {
        if ((mDb != null) && (mDb.isOpen())) {
            mDb.close();
        }
        mDb = null;
        if (BuildConfig.DEBUG) {
            Log.i(Constants.LOG_TAG, "DBHelper.closeDB");
        }
    }

    /**
     * Override method from SQLiteOpenHelper class
     * calls when database file will create
     * Will create database structure
     *
     * @param db SQLiteDatabase instance
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(Constants.LOG_TAG, DB_TRY_CREATE);
        this.mDb = db;
        createDB();
    }

    /**
     * Override method from SQLiteOpenHelper class
     * calls when database need to update from oldVersion to newVersion
     *
     * @param db         SQLiteDatabase instance
     * @param oldVersion old version
     * @param newVersion new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Constants.LOG_TAG, DB_MUST_UPDATE + newVersion);
        this.mDb = db;
        dropDB();
        onCreate(db);
    }

    /**
     * Drop all tables in database and create new clear database structure
     */
    public void clearDB() {
        dropDB();
        createDB();
    }

    /**
     * Add news string to database
     *
     * @param news string for adding to database
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public int addNews(String news) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen() && ! mDb.isReadOnly()) {
            ContentValues content = new ContentValues();
            content.put(KEY_NEWS_TEXT, news);
            return (int) mDb.insert(DB_TABLE_NEWS_LIST, null, content);
        } else return - 1;
    }

    /**
     * Add stop to database
     *
     * @param stopName stop name string
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public int addStop(String stopName) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen() && ! mDb.isReadOnly()) {
            ContentValues content = new ContentValues();
            content.put(KEY_STOP_NAME, stopName);
            return (int) mDb.insert(DB_TABLE_STOP_LIST, null, content);
        } else return - 1;
    }

    /**
     * Add bus to database
     *
     * @param bus bus string
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public int addBus(String bus) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen() && ! mDb.isReadOnly()) {
            ContentValues content = new ContentValues();
            content.put(KEY_BUS_NUMBER, bus);
            return (int) mDb.insert(DB_TABLE_BUS_LIST, null, content);
        } else return - 1;
    }

    /**
     * Add route info to database
     *
     * @param busId       Bus ID (row ID current bus in bus table)
     * @param beginStopId begin stop ID (row ID in database for begin stop)
     * @param endStopId   end stop ID (row ID in database for end stop)
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public int addRoutes(int busId, int beginStopId, int endStopId) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen() && ! mDb.isReadOnly()) {
            ContentValues content = new ContentValues();
            content.put(KEY_BUS_ID, busId);
            content.put(KEY_BEGIN_STOP_ID, beginStopId);
            content.put(KEY_END_STOP_ID, endStopId);
            return (int) mDb.insert(DB_TABLE_ROUTES, null, content);
        } else return - 1;
    }

    /**
     * Add route list info (for route with routeID, write stop ID info and stop index in that route)
     *
     * @param routeId   route ID (row ID in database for current row)
     * @param stopId    stop ID (row ID for current stop)
     * @param stopIndex stop index (index stop in route, first stop has index0, second stop = 1 etc)
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public int addRouteList(int routeId, int stopId, int stopIndex) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen() && ! mDb.isReadOnly()) {
            ContentValues content = new ContentValues();
            content.put(KEY_ROUTE_ID, routeId);
            content.put(KEY_STOP_ID, stopId);
            content.put(KEY_STOP_INDEX, stopIndex);
            return (int) mDb.insert(DB_TABLE_ROUTE_LIST, null, content);
        } else return - 1;
    }

    /**
     * Add time for route
     *
     * @param routeListId route list ID
     * @param hour        hour
     * @param minutes     minutes string (store full string: minutes "10 15 50" for this route)
     * @param dayTypeId   day type ID
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public int addTime(int routeListId, int hour, String minutes, int dayTypeId) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen() && ! mDb.isReadOnly()) {
            ContentValues content = new ContentValues();
            content.put(KEY_ROUTE_LIST_ID, routeListId);
            content.put(KEY_HOUR, hour);
            content.put(KEY_MINUTES, minutes);
            content.put(KEY_DAY_TYPE_ID, dayTypeId);
            return (int) mDb.insert(DB_TABLE_TIME_LIST, null, content);
        } else return - 1;
    }

    /**
     * Add type to database
     *
     * @param type type string (used types: "Вых", "раб", "пт", "ежедневно" etc, often change and
     *             add new)
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public int addType(String type) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen() && ! mDb.isReadOnly()) {
            ContentValues content = new ContentValues();
            content.put(KEY_TYPE, type);
            return (int) mDb.insert(DB_TABLE_TYPE_LIST, null, content);
        } else return - 1;
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

            if (cursor.moveToFirst()) {
                do {
                    TimeList timeListItem = new TimeList();

                    int fieldIndex = cursor.getColumnIndex(KEY_HOUR);
                    if (fieldIndex >= 0) {
                        timeListItem.setHour(cursor.getInt(fieldIndex));
                    }

                    fieldIndex = cursor.getColumnIndex(KEY_MINUTES);
                    if (fieldIndex >= 0) {
                        timeListItem.setMinutes(cursor.getString(fieldIndex));
                    }

                    timeList.add(timeListItem);
                } while (cursor.moveToNext());
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
                        minutes = mContext.getString(R.string.schedule_noBus);
                    }
                    stopDetail.addMinute(type + " " + minutes);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return stopDetailList;
    }

    /**
     * Begin transaction
     */
    public void beginTran() {
        SQLiteDatabase sqLiteDatabase = getDB();
        if ((sqLiteDatabase != null) && (! sqLiteDatabase.inTransaction())) {
            sqLiteDatabase.beginTransaction();
        }
    }

    /**
     * End transaction
     */
    public void endTran() {
        SQLiteDatabase sqLiteDatabase = getDB();
        if ((sqLiteDatabase != null) && (sqLiteDatabase.inTransaction())) {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Set transaction successful
     */
    public void setTranSuccessful() {
        SQLiteDatabase sqLiteDatabase = getDB();
        if ((sqLiteDatabase != null) && (sqLiteDatabase.inTransaction())) {
            sqLiteDatabase.setTransactionSuccessful();
        }
    }

    /**
     * try to get database for write, if SQLiteException throws try to get readable database
     * if SQLiteException throws again - null will returned
     *
     * @return SQLiteDatabase instance or null if can't open connection to database
     */
    private SQLiteDatabase getDB() {
        SQLiteDatabase sqLiteDatabase;
        try {
            sqLiteDatabase = getWritableDatabase();
        } catch (SQLiteException sqlExceptionWrite) {
            try {
                sqLiteDatabase = getReadableDatabase();
            } catch (SQLiteException sqlExceptionRead) {
                sqLiteDatabase = null;
            }
        }
        return sqLiteDatabase;
    }

    /**
     * Drop table in database with name tableName
     *
     * @param tableName table name for deleting
     */
    private void dropTable(String tableName) {
        execSQL(SQL_DROP_TABLE + tableName);
    }

    /**
     * Execute SQL in string sqlStatement
     *
     * @param sqlStatement SQL query
     */
    private void execSQL(String sqlStatement) {
        Log.w(Constants.LOG_TAG, DB_EXEC_SQL + sqlStatement);
        if (mDb != null) {
            mDb.execSQL(sqlStatement);
        }
    }

    /**
     * Drop all tables in database
     */
    private void dropDB() {
        dropTable(DB_TABLE_STOP_LIST);
        dropTable(DB_TABLE_BUS_LIST);
        dropTable(DB_TABLE_NEWS_LIST);
        dropTable(DB_TABLE_ROUTES);
        dropTable(DB_TABLE_ROUTE_LIST);
        dropTable(DB_TABLE_TIME_LIST);
        dropTable(DB_TABLE_TYPE_LIST);
    }

    /**
     * Create tables and indexes in database
     */
    private void createDB() {
        execSQL(DB_CREATE_STOP_LIST);
        execSQL(DB_CREATE_BUS_LIST);
        execSQL(DB_CREATE_NEWS_LIST);
        execSQL(DB_CREATE_ROUTES);
        execSQL(DB_CREATE_ROUTE_LIST);
        execSQL(DB_CREATE_TIME_LIST);
        execSQL(DB_CREATE_TYPE_LIST);


        /*Create indexes*/
        /*   These indexes will reduce query time in two or more times (for queries using these tables)   */
        execSQL(DB_CREATE_INDEX_TIME_LIST);
        execSQL(DB_CREATE_INDEX_ROUTE_LIST);
        execSQL(DB_CREATE_INDEX_STOP_LIST);
    }

    /**
     * Extract database from zip file stored as raw resource
     */
    private void extractDB() {
        Log.i(Constants.LOG_TAG, "try extract db from zip file");

        File dirCreator = new File(mDbPath);
        if (! dirCreator.mkdirs()) {
            Log.i(Constants.LOG_TAG, "make dir return false! path:" + dirCreator.getPath());
        }
        File fileDst = new File(mDbPath + "/" + DB_NAME);

        /*   streams for unzip file and save to fileDst   */
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.ap);

        ZipInputStream zipInputStream = null;
        OutputStream outputStream = null;
        try {
            zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
            outputStream = new BufferedOutputStream(new FileOutputStream(fileDst));

            byte[] buffer = new byte[BUFFER_SIZE];
            int count;
            while (zipInputStream.getNextEntry() != null) {
                while ((count = zipInputStream.read(buffer)) != - 1) {
                    outputStream.write(buffer, 0, count);
                }
            }

            Log.i(Constants.LOG_TAG, "extract db from zip file complete");
        } catch (IOException ioe) {
            Log.e(Constants.LOG_TAG, "unzip error: " + ioe.getMessage() + ". Run createDB()");
            Toast.makeText(mContext.getApplicationContext(),
                    R.string.extract_error, Toast.LENGTH_LONG).show();
            createDB();                                     //create clear DB
        } finally {

            /*  close all streams   */
            if (inputStream != null) {
                closeStream(inputStream);
            }

            if (outputStream != null) {
                closeStream(outputStream);
            }

            if (zipInputStream != null) {
                closeStream(zipInputStream);
            }
        }
    }

    /**
     * Close stream
     *
     * @param needClose stream implemented interface Closeable
     */
    private void closeStream(Closeable needClose) {
        try {
            needClose.close();
        } catch (IOException ioError) {
            Log.e(Constants.LOG_TAG, "closeStream: " + ioError.getMessage());
            Toast.makeText(mContext.getApplicationContext(),
                    R.string.extract_error, Toast.LENGTH_LONG).show();
        }

    }
}