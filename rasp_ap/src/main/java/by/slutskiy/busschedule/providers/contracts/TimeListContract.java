package by.slutskiy.busschedule.providers.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.HashMap;

/**
 * TimeListContract
 * Version 1.0
 * 27.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class TimeListContract extends BaseContract {

    public static final String PATH = "TimeList";
    public static final String JOIN_PATH = PATH +
            " LEFT OUTER JOIN " + TypeContract.PATH + " ON " +
            TimeListContract.COLUMN_TYPE_ID + " = " +
            TypeContract.PATH + "." + TypeContract.COLUMN_ID;

    public static final Uri CONTENT_URI =
            Uri.withAppendedPath(AUTHORITY_URI, PATH);

    public static final Uri CONTENT_TYPE_URI =
            Uri.withAppendedPath(AUTHORITY_URI, PATH + "/type");

    public static final String COLUMN_ROUTE_LIST_ID = "RouteListId";
    public static final String COLUMN_HOUR = "Hour";
    public static final String COLUMN_MINUTES = "Minutes";
    public static final String COLUMN_TYPE_ID = "TypeId";
    public static final String COLUMN_TYPES = TypeContract.COLUMN_TYPE_NAME;

    private static final String DATABASE_CREATE = "CREATE TABLE "
            + PATH
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_ROUTE_LIST_ID + " INTEGER, "
            + COLUMN_HOUR + " INTEGER, "
            + COLUMN_MINUTES + " TEXT, "
            + COLUMN_TYPE_ID + " TEXT );";

    public static final String[] availableColumns =
            new String[]{COLUMN_ID, COLUMN_ROUTE_LIST_ID, COLUMN_HOUR, COLUMN_MINUTES, COLUMN_TYPE_ID};

    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();

    static {
        PROJECTION_MAP.put(COLUMN_ID, PATH + "." + COLUMN_ID);
        PROJECTION_MAP.put(COLUMN_ROUTE_LIST_ID, PATH + "." + COLUMN_ROUTE_LIST_ID);
        PROJECTION_MAP.put(COLUMN_HOUR, PATH + "." + COLUMN_HOUR);
        PROJECTION_MAP.put(COLUMN_MINUTES, PATH + "." + COLUMN_MINUTES);
        PROJECTION_MAP.put(COLUMN_TYPE_ID, PATH + "." + COLUMN_TYPE_ID);

        PROJECTION_MAP.put(COLUMN_TYPES, TypeContract.PATH + "." + TypeContract.COLUMN_TYPE_NAME);
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

    private TimeListContract() {/*   code    */}

}
