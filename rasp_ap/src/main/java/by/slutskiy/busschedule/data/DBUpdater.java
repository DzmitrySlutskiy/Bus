package by.slutskiy.busschedule.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * DBUpdater save data to temp db
 * Version 1.0
 * 27.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class DBUpdater extends DBStructure {

    /**
     * Database Name
     */
    public static final String DB_NAME = "ap_update.db";

    /*   Log tags   */
    private static final String LOG_TAG = DBUpdater.class.getSimpleName();

    private SQLiteDatabase mDb;

    private static DBUpdater sDbHelpInstance = null;

    /*  private constructor*/
    private DBUpdater(Context context) {
        super(context, DB_NAME);
    }

    /*  singleton pattern   */

    /**
     * Create DBHelper class instance.
     *
     * @param context context to use to open or create the database
     * @return DBHelper class instance
     */
    public static DBUpdater getInstance(Context context) {
        if (sDbHelpInstance == null) {
            sDbHelpInstance = new DBUpdater(context);
        }
        return sDbHelpInstance;
    }

    /**
     * Add news string to database
     *
     * @param news string for adding to database
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public int addNews(String news) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
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
        if (mDb != null && mDb.isOpen()) {
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
        if (mDb != null && mDb.isOpen()) {
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
        if (mDb != null && mDb.isOpen()) {
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
        if (mDb != null && mDb.isOpen()) {
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
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public int addTime(int routeListId, int hour, String minutes/*, int dayTypeId*/) {
        mDb = getDB();
        if (mDb != null && mDb.isOpen()) {
            ContentValues content = new ContentValues();
            content.put(KEY_ROUTE_LIST_ID, routeListId);
            content.put(KEY_HOUR, hour);
            content.put(KEY_MINUTES, minutes);

            return (int) mDb.insert(DB_TABLE_TIME_LIST, null, content);
        } else return - 1;
    }

    /**
     * Begin transaction
     */
    public void beginTran() {
        SQLiteDatabase sqLiteDatabase = getDB();
        if ((sqLiteDatabase != null) && (! sqLiteDatabase.inTransaction())) {
            sqLiteDatabase.beginTransaction();
            Log.d(LOG_TAG, "Begin update transaction");
        }
    }

    /**
     * End transaction
     */
    public void endTran() {
        SQLiteDatabase sqLiteDatabase = getDB();
        if ((sqLiteDatabase != null) && (sqLiteDatabase.inTransaction())) {
            sqLiteDatabase.endTransaction();
            Log.d(LOG_TAG, "End update transaction");
        }
    }

    /**
     * Set transaction successful
     */
    public void setTranSuccessful() {
        SQLiteDatabase sqLiteDatabase = getDB();
        if ((sqLiteDatabase != null) && (sqLiteDatabase.inTransaction())) {
            sqLiteDatabase.setTransactionSuccessful();
            Log.d(LOG_TAG, "Set update transaction successful");
        }
    }

    /**
     * try to get database for write, if SQLiteException throws try to get readable database
     * if SQLiteException throws again - null will returned
     *
     * @return SQLiteDatabase instance or null if can't open connection to database
     */
    SQLiteDatabase getDB() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return (! sqLiteDatabase.isReadOnly()) ? sqLiteDatabase : null;
    }
}