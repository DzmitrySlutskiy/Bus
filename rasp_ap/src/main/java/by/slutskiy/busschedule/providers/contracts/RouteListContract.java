package by.slutskiy.busschedule.providers.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.HashMap;

/**
 * RouteListContract
 * Version 1.0
 * 27.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class RouteListContract extends BaseContract {

    public static final String PATH = "RouteList";
    public static final String JOIN_PATH = PATH +
            " LEFT OUTER JOIN " + StopContract.PATH + " ON " +
            RouteListContract.COLUMN_STOP_ID + " = " +
            StopContract.PATH + "." + StopContract.COLUMN_ID;

    public static final Uri CONTENT_URI =
            Uri.withAppendedPath(AUTHORITY_URI, PATH);

    public static final String COLUMN_ROUTE_ID = "RouteId";
    public static final String COLUMN_STOP_ID = "StopId";
    public static final String COLUMN_STOP_INDEX = "StopIndex";

    public static final String COLUMN_STOP_NAME = StopContract.COLUMN_STOP_NAME;

    private static final String DATABASE_CREATE = "create table "
            + PATH
            + "("
            + COLUMN_ID + " INTEGER primary key autoincrement, "
            + COLUMN_ROUTE_ID + " INTEGER,"
            + COLUMN_STOP_ID + " INTEGER,"
            + COLUMN_STOP_INDEX + " INTEGER );";

    private static final String[] availableColumns =
            new String[]{COLUMN_ID, COLUMN_ROUTE_ID, COLUMN_STOP_ID, COLUMN_STOP_INDEX,
                    COLUMN_STOP_NAME};

    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();

    static {
        PROJECTION_MAP.put(COLUMN_ID, PATH + "." + COLUMN_ID);
        PROJECTION_MAP.put(COLUMN_ROUTE_ID, PATH + "." + COLUMN_ROUTE_ID);
        PROJECTION_MAP.put(COLUMN_STOP_ID, PATH + "." + COLUMN_STOP_ID);
        PROJECTION_MAP.put(COLUMN_STOP_INDEX, PATH + "." + COLUMN_STOP_INDEX);

        PROJECTION_MAP.put(COLUMN_STOP_NAME, StopContract.PATH + "." + StopContract.COLUMN_STOP_NAME);
    }

    public static void onCreate(SQLiteDatabase database) {
        onCreate(database, DATABASE_CREATE, PATH);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        onUpgrade(database, oldVersion, newVersion, DATABASE_CREATE, PATH);
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

    private RouteListContract() {/*   code    */}

}
