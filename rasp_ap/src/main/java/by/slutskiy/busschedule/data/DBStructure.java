package by.slutskiy.busschedule.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
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
import java.util.zip.ZipInputStream;

import by.slutskiy.busschedule.BuildConfig;
import by.slutskiy.busschedule.R;

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

    /*  default buffer size  */
    private static final int BUFFER_SIZE = 8192;

    /**
     * Версия базы данных
     */
    private static final int DB_VERSION = 2;

    /*  Table list  */
    static final String DB_TABLE_NEWS_LIST = "News";
    static final String DB_TABLE_STOP_LIST = "StopList";
    static final String DB_TABLE_BUS_LIST = "BusList";
    static final String DB_TABLE_ROUTES = "Routes";
    static final String DB_TABLE_ROUTE_LIST = "RouteList";
    static final String DB_TABLE_TIME_LIST = "TimeList";
    static final String DB_TABLE_TYPE_LIST = "TypeList";

    /*  Table fields    */
    static final String KEY_NEWS_TEXT = "NewsText";
    static final String KEY_STOP_NAME = "StopName";
    static final String KEY_BUS_NUMBER = "BusNumber";
    static final String KEY_BUS_ID = "BusId";
    static final String KEY_BEGIN_STOP_ID = "BeginStopId";
    static final String KEY_END_STOP_ID = "EndStopId";
    static final String KEY_ROUTE_ID = "RouteId";
    static final String KEY_STOP_ID = "StopId";
    static final String KEY_STOP_INDEX = "StopIndex";
    static final String KEY_ROUTE_LIST_ID = "RouteListId";
    static final String KEY_HOUR = "Hour";
    static final String KEY_MINUTES = "Minutes";
    static final String KEY_DAY_TYPE_ID = "DayTypeId";
    static final String KEY_TYPE = "Type";
    static final String KEY_ID = "_id";
    static final String KEY_BEGIN_STOP = "BeginStop";
    static final String KEY_END_STOP = "EndStop";

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
    private static final String DB_CREATE_TYPE_LIST = SQL_CREATE_TABLE +
            DB_TABLE_TYPE_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_TYPE + " " + SQL_TEXT + ");";

    private static final String DB_CREATE_TIME_LIST = SQL_CREATE_TABLE +
            DB_TABLE_TIME_LIST + "(" +
            KEY_ID + " " + SQL_INTEGER + " " + SQL_P_KEY + ", " +
            KEY_ROUTE_LIST_ID + " " + SQL_INTEGER + ", " +
            KEY_HOUR + " " + SQL_INTEGER + ", " +
            KEY_MINUTES + " " + SQL_TEXT + ", " +
            KEY_DAY_TYPE_ID + " " + SQL_INTEGER + ");";

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
    static final String DB_SELECT_TIME_LIST_BY_ROUTE_LIST_ID = SQL_SELECT +
            DB_TABLE_TIME_LIST + "." + KEY_HOUR + ", " + DB_TABLE_TIME_LIST + "." + KEY_MINUTES + ", " +
            DB_TABLE_TIME_LIST + ". " + KEY_DAY_TYPE_ID +
            SQL_FROM + DB_TABLE_TIME_LIST + SQL_WHERE + KEY_ROUTE_LIST_ID + " = ?;";

    static final String DB_SELECT_STOP_DETAIL = SQL_SELECT +
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

    static final String DB_SELECT_DAY_TYPE_BY_ROUTE_LIST_ID = SQL_SELECT + "*" +
            SQL_FROM + DB_TABLE_TYPE_LIST + SQL_WHERE + DB_TABLE_TYPE_LIST + "." + KEY_ID +
            SQL_IN + "(" + SQL_SELECT + SQL_DISTINCT + KEY_DAY_TYPE_ID + SQL_FROM +
            DB_TABLE_TIME_LIST + SQL_WHERE + KEY_ROUTE_LIST_ID + " = ?);";

    private String mDbPath;
    private final String mDbName;
    final Context mContext;
    SQLiteDatabase mDb;

    /*  public constructors */

    DBStructure(Context context) {
        this(context, DEFAULT_DB_NAME);
    }

    DBStructure(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);

        mDbName = dbName;
        this.mContext = context;
        mDbPath = context.getDatabasePath(mDbName).getPath();

        /*cut mDbName from path - get full path */
        mDbPath = mDbPath.substring(0, mDbPath.length() - mDbName.length() - 1);
        if (! new File(mDbPath + "/" + mDbName).exists() &&
                (mDbName.equals(DEFAULT_DB_NAME))) {

            /*  extract database from raw resource (zip file)*/
            extractDB();
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
        dropTable(DBStructure.DB_TABLE_TYPE_LIST);
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
        execSQL(DBStructure.DB_CREATE_TYPE_LIST);

        /*Create indexes*/
        /*   These indexes will reduce query time in two or more times (for queries using these tables)   */
        execSQL(DBStructure.DB_CREATE_INDEX_TIME_LIST);
        execSQL(DBStructure.DB_CREATE_INDEX_ROUTE_LIST);
        execSQL(DBStructure.DB_CREATE_INDEX_STOP_LIST);
    }

    /**
     * Extract database from zip file stored as raw resource
     */
    private void extractDB() {
        Log.i(LOG_TAG, "try extract db from zip file");

        File dirCreator = new File(mDbPath);
        if (! dirCreator.mkdirs()) {
            Log.i(LOG_TAG, "make dir return false! path:" + dirCreator.getPath());
        }
        File fileDst = new File(mDbPath + "/" + mDbName);

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

            Log.i(LOG_TAG, "extract db from zip file complete");
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "unzip error: " + ioe.getMessage() + ". Run createDB()");
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
            Log.e(LOG_TAG, "closeStream: " + ioError.getMessage());
            Toast.makeText(mContext.getApplicationContext(),
                    R.string.extract_error, Toast.LENGTH_LONG).show();
        }
    }
}
