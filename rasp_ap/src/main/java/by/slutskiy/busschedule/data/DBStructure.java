package by.slutskiy.busschedule.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import by.slutskiy.busschedule.BuildConfig;
import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.utils.IOUtils;

/**
 * DBStructure extends SQLiteOpenHelper
 * Version 1.0
 * 27.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class DBStructure extends SQLiteOpenHelper {

    /**
     * Database Name
     */
    public static final String DEFAULT_DB_NAME = "ap.db";

    private static final String LOG_TAG = DBStructure.class.getSimpleName();

    /**
     * Версия базы данных
     */
    private static final int DB_VERSION = 3;

    /*  Table list  */
    public static final String DB_TABLE_NEWS_LIST = "News";
    public static final String DB_TABLE_STOP_LIST = "StopList";
    public static final String DB_TABLE_BUS_LIST = "BusList";
    public static final String DB_TABLE_ROUTES = "Routes";
    public static final String DB_TABLE_ROUTE_LIST = "RouteList";
    public static final String DB_TABLE_TIME_LIST = "TimeList";

    /*  Table fields    */
    public static final String KEY_NEWS_TEXT = "NewsText";
    public static final String KEY_STOP_NAME = "StopName";
    public static final String KEY_BUS_NUMBER = "BusNumber";
    public static final String KEY_BUS_ID = "BusId";
    public static final String KEY_BEGIN_STOP_ID = "BeginStopId";
    public static final String KEY_END_STOP_ID = "EndStopId";
    public static final String KEY_ROUTE_ID = "RouteId";
    public static final String KEY_STOP_ID = "StopId";
    public static final String KEY_STOP_INDEX = "StopIndex";
    public static final String KEY_ROUTE_LIST_ID = "RouteListId";
    public static final String KEY_HOUR = "Hour";
    public static final String KEY_MINUTES = "Minutes";
    public static final String KEY_ID = "_id";
    public static final String KEY_BEGIN_STOP = "BeginStop";
    public static final String KEY_END_STOP = "EndStop";

    public static final String FULL_ROUTE = "FullRoute";

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
    private static final String SQL_ORDER_BY = " ORDER BY ";
    private static final String SQL_ASC = " ASC ";

    /*  CREATE TABLE QUERY    */
    private static final String DB_CREATE_TIME_LIST = SQL_CREATE_TABLE +
            DB_TABLE_TIME_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_ROUTE_LIST_ID + " " + SQL_INTEGER + ", " +
            KEY_HOUR + " " + SQL_INTEGER + ", " +
            KEY_MINUTES + " " + SQL_TEXT + ");";

    private static final String DB_CREATE_ROUTE_LIST = SQL_CREATE_TABLE +
            DB_TABLE_ROUTE_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_ROUTE_ID + " " + SQL_INTEGER + ", " +
            KEY_STOP_ID + " " + SQL_INTEGER + "," +
            KEY_STOP_INDEX + " " + SQL_INTEGER + ");";

    private static final String DB_CREATE_ROUTES = SQL_CREATE_TABLE +
            DB_TABLE_ROUTES + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_BUS_ID + " " + SQL_INTEGER + ", " +
            KEY_BEGIN_STOP_ID + " " + SQL_INTEGER + "," +
            KEY_END_STOP_ID + " " + SQL_INTEGER + ");";

    private static final String DB_CREATE_BUS_LIST = SQL_CREATE_TABLE +
            DB_TABLE_BUS_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_BUS_NUMBER + " " + SQL_TEXT + ");";

    private static final String DB_CREATE_STOP_LIST = SQL_CREATE_TABLE +
            DB_TABLE_STOP_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_STOP_NAME + " " + SQL_TEXT + ");";

    private static final String DB_CREATE_NEWS_LIST = SQL_CREATE_TABLE +
            DB_TABLE_NEWS_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_NEWS_TEXT + " " + SQL_TEXT + ");";

    /*  CREATE INDEX QUERY  */
    private static final String DB_CREATE_INDEX_STOP_LIST = SQL_CREATE_INDEX + KEY_STOP_NAME + SQL_ON +
            DB_TABLE_STOP_LIST + " (\"" + KEY_STOP_NAME + "\");";

    private static final String DB_CREATE_INDEX_ROUTE_LIST = SQL_CREATE_INDEX + KEY_ROUTE_ID + SQL_ON +
            DB_TABLE_ROUTE_LIST + " (\"" + KEY_ROUTE_ID + "\");";

    private static final String DB_CREATE_INDEX_TIME_LIST = SQL_CREATE_INDEX + KEY_ROUTE_LIST_ID +
            SQL_ON + DB_TABLE_TIME_LIST + " (\"" + KEY_ROUTE_LIST_ID + "\");";

    /*   SELECT QUERY   */

    /*SELECT TimeList._id, TimeList.Hour, TimeList.Minutes
      FROM TimeList
      WHERE TimeList.RouteListId = ?
      ORDER BY TimeList._id*/
    static final String DB_SELECT_TIME_LIST_BY_ROUTE_LIST_ID = SQL_SELECT +
            DB_TABLE_TIME_LIST + "." + KEY_ID + ", " +
            DB_TABLE_TIME_LIST + "." + KEY_HOUR + ", " +
            DB_TABLE_TIME_LIST + "." + KEY_MINUTES +
            SQL_FROM + DB_TABLE_TIME_LIST + SQL_WHERE +
            DB_TABLE_TIME_LIST + "." + KEY_ROUTE_LIST_ID + " = ? " +
            SQL_ORDER_BY + DB_TABLE_TIME_LIST + "." + KEY_ID;

    /*SELECT
      RouteList._id, RouteList.RouteId,
      BusList.BusNumber || '   ' || StopList.StopName || ' - ' || StopList2.StopName AS Stop,
      TimeList.Minutes
      FROM RouteList
      INNER JOIN Routes ON RouteList.RouteId = Routes._id
      INNER JOIN BusList ON Routes.BusId = BusList._id
      INNER JOIN StopList ON Routes.BeginStopId = StopList._id
      INNER JOIN StopList AS StopList2 ON Routes.EndStopId = StopList2._id
      INNER JOIN TimeList ON TimeList.RouteListId = RouteList._id
      INNER JOIN TypeList ON TypeList._id = TimeList.DayTypeId
      WHERE RouteList.StopId = ? AND TimeList.Hour = ?*/
    static final String DB_SELECT_STOP_DETAIL = SQL_SELECT +
            DB_TABLE_ROUTE_LIST + "." + KEY_ID + ", " +
            DB_TABLE_ROUTE_LIST + "." + KEY_ROUTE_ID + ", " +
            DB_TABLE_BUS_LIST + "." + KEY_BUS_NUMBER + " || ' ' || " +
            DB_TABLE_STOP_LIST + "." + KEY_STOP_NAME + " || ' - ' || " +
            DB_TABLE_STOP_LIST + "2." + KEY_STOP_NAME + SQL_AS + FULL_ROUTE + ", " +
            DB_TABLE_TIME_LIST + "." + KEY_MINUTES +
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
            SQL_WHERE + DB_TABLE_ROUTE_LIST + "." + KEY_STOP_ID + " = ? AND " +
            DB_TABLE_TIME_LIST + "." + KEY_HOUR + " = ?";

    static final String DB_SELECT_ROUTE_BY_ROUTE_ID = SQL_SELECT +
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

    static final String DB_SELECT_ROUTE_LIST = SQL_SELECT +
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

    static final String DB_SELECT_ROUTE_STOP_LIST_BY_ROUTE_ID = SQL_SELECT +
            DB_TABLE_ROUTE_LIST + "." + KEY_ID + ", " +
            DB_TABLE_ROUTE_LIST + "." + KEY_ROUTE_ID + ", " +
            DB_TABLE_STOP_LIST + "." + KEY_STOP_NAME +
            SQL_FROM + DB_TABLE_ROUTE_LIST + "\n" +
            SQL_INNER_JOIN + DB_TABLE_STOP_LIST + SQL_ON +
            DB_TABLE_ROUTE_LIST + "." + KEY_STOP_ID + " = " + DB_TABLE_STOP_LIST + "." + KEY_ID +
            SQL_WHERE + KEY_ROUTE_ID + " = ?" +
            SQL_ORDER_BY + KEY_STOP_INDEX + SQL_ASC + ";";

    /*SELECT Minutes
      FROM TimeList
      WHERE TimeList.RouteListId = ?
      LIMIT 1*/
    static final String DB_SELECT_DAY_TYPE_BY_ROUTE_LIST_ID = SQL_SELECT +
            DB_TABLE_TIME_LIST + "." + KEY_MINUTES + " " +
            SQL_FROM + DB_TABLE_TIME_LIST +
            SQL_WHERE + DB_TABLE_TIME_LIST + "." + KEY_ROUTE_LIST_ID + " = ? " + " LIMIT 1";

    final Context mContext;
    SQLiteDatabase mDb;

    /*  public constructors */

    DBStructure(Context context) {
        this(context, DEFAULT_DB_NAME);
    }

    DBStructure(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);

        this.mContext = context;
        String mDbPath = context.getDatabasePath(dbName).getPath();

        /*cut mDbName from path - get full path */
        mDbPath = mDbPath.substring(0, mDbPath.length() - dbName.length() - 1);
        if (! new File(mDbPath + "/" + dbName).exists() &&
                (dbName.equals(DEFAULT_DB_NAME))) {

            /*  extract database from raw resource (zip file)*/
            IOUtils.mkDir(mDbPath);
            try {
                IOUtils.extractFile(context, R.raw.ap, mDbPath + "/" + dbName);
            } catch (IOException e) {
                Log.e(LOG_TAG, "unzip error: " + e.getMessage() + ". Run createDB()");
                createDB();
            }
        }
        openDB();
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(LOG_TAG, "Try to create DB");
        this.mDb = db;
        createDB();
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Database must be updated to new version " + newVersion);
        this.mDb = db;
        dropDB();
        onCreate(db);
    }

    /*  public methods  */

    /**
     * Open database connection
     */
    void openDB() {
        mDb = getDB();
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
            Log.i(LOG_TAG, "closeDB");
        }
    }

    /**
     * Drop all tables in database and create new clear database structure
     */
    public void clearDB() {
        dropDB();
        createDB();
    }

    /**
     * try to get database for write, if SQLiteException throws try to get readable database
     * if SQLiteException throws again - null will returned
     *
     * @return SQLiteDatabase instance or null if can't open connection to database
     */
    SQLiteDatabase getDB() {
        SQLiteDatabase sqLiteDatabase;
        try {
            sqLiteDatabase = getWritableDatabase();
        } catch (SQLiteException sqlExceptionWrite) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "getDB getWritableDatabase error:" + sqlExceptionWrite);
            }
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
        execSQL(DBStructure.SQL_DROP_TABLE + tableName);
    }

    /**
     * Execute SQL in string sqlStatement
     *
     * @param sqlStatement SQL query
     */
    private void execSQL(String sqlStatement) {
        Log.w(LOG_TAG, "Exec SQL: " + sqlStatement);
        if (mDb != null) {
            mDb.execSQL(sqlStatement);
        }
    }

    /**
     * Drop all tables in database
     */
    private void dropDB() {
        dropTable(DBStructure.DB_TABLE_STOP_LIST);
        dropTable(DBStructure.DB_TABLE_BUS_LIST);
        dropTable(DBStructure.DB_TABLE_NEWS_LIST);
        dropTable(DBStructure.DB_TABLE_ROUTES);
        dropTable(DBStructure.DB_TABLE_ROUTE_LIST);
        dropTable(DBStructure.DB_TABLE_TIME_LIST);
    }

    /**
     * Create tables and indexes in database
     */
    private void createDB() {
        execSQL(DBStructure.DB_CREATE_STOP_LIST);
        execSQL(DBStructure.DB_CREATE_BUS_LIST);
        execSQL(DBStructure.DB_CREATE_NEWS_LIST);
        execSQL(DBStructure.DB_CREATE_ROUTES);
        execSQL(DBStructure.DB_CREATE_ROUTE_LIST);
        execSQL(DBStructure.DB_CREATE_TIME_LIST);

        /*Create indexes*/
        execSQL(DBStructure.DB_CREATE_INDEX_TIME_LIST);
        execSQL(DBStructure.DB_CREATE_INDEX_ROUTE_LIST);
        execSQL(DBStructure.DB_CREATE_INDEX_STOP_LIST);
    }
}
