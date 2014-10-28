package by.slutskiy.busschedule.providers.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;

import by.slutskiy.busschedule.data.DBStructure;

/**
 * RouteContract
 * Version 1.0
 * 27.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class RouteContract extends BaseContract {

    public static final String PATH = DBStructure.DB_TABLE_ROUTES;
    public static final String JOIN_PATH = PATH +
            " LEFT OUTER JOIN " + BusContract.PATH + " ON " +
            RouteContract.COLUMN_BUS_ID + " = " +
            BusContract.PATH + "." + BusContract.COLUMN_ID +
            " LEFT OUTER JOIN " + StopContract.PATH + " ON " +
            RouteContract.COLUMN_BEGIN_STOP_ID + " = " +
            StopContract.PATH + "." + StopContract.COLUMN_ID +
            " LEFT OUTER JOIN " + StopContract.PATH + " AS S2 ON " +
            RouteContract.COLUMN_END_STOP_ID + " = S2." + StopContract.COLUMN_ID;

    public static final Uri CONTENT_URI =
            Uri.withAppendedPath(AUTHORITY_URI, PATH);

    public static final String COLUMN_BEGIN_STOP_ID = DBStructure.KEY_BEGIN_STOP_ID;
    public static final String COLUMN_END_STOP_ID = DBStructure.KEY_END_STOP_ID;
    public static final String COLUMN_BUS_ID = DBStructure.KEY_BUS_ID;

    public static final String COLUMN_BEGIN_STOP = DBStructure.KEY_BEGIN_STOP;
    public static final String COLUMN_END_STOP = DBStructure.KEY_END_STOP;
    public static final String COLUMN_BUS_NUMBER = DBStructure.KEY_BUS_NUMBER;

    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();

    private static final String DATABASE_CREATE = "CREATE TABLE "
            + PATH
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_BUS_ID + "INTEGER, "
            + COLUMN_BEGIN_STOP_ID + "INTEGER, "
            + COLUMN_END_STOP_ID + "INTEGER);";

    private static final String[] availableColumns =
            new String[]{COLUMN_ID, COLUMN_BUS_ID, COLUMN_BEGIN_STOP_ID, COLUMN_END_STOP_ID,
                    COLUMN_BUS_NUMBER, COLUMN_END_STOP, COLUMN_BEGIN_STOP};

    static {
        PROJECTION_MAP.put(COLUMN_ID, PATH + "." + COLUMN_ID);
        PROJECTION_MAP.put(COLUMN_BUS_ID, PATH + "." + COLUMN_BUS_ID);
        PROJECTION_MAP.put(COLUMN_BEGIN_STOP_ID,
                PATH + "." + COLUMN_BEGIN_STOP_ID);
        PROJECTION_MAP.put(COLUMN_END_STOP_ID,
                PATH + "." + COLUMN_END_STOP_ID);

        PROJECTION_MAP.put(BusContract.COLUMN_BUS_NUMBER,
                BusContract.PATH + "." + BusContract.COLUMN_BUS_NUMBER);

        PROJECTION_MAP.put(COLUMN_BEGIN_STOP,
                StopContract.PATH + "." + StopContract.COLUMN_STOP_NAME + " AS " + COLUMN_BEGIN_STOP);

        PROJECTION_MAP.put(COLUMN_END_STOP,
                "S2." + StopContract.COLUMN_STOP_NAME + " AS " + COLUMN_END_STOP);
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(RouteContract.class.getSimpleName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + PATH);
        onCreate(database);
    }

    /**
     * Check if the caller has requested a column which does not exists
     *
     * @param projection requested columns
     * @throws IllegalArgumentException if requested column does not exists in current table
     */
    public static void checkColumns(String[] projection) {
        checkColumns(availableColumns, projection);
    }

    private RouteContract() {/*   code    */}
}
