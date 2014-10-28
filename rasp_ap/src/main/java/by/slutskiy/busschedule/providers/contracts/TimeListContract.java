package by.slutskiy.busschedule.providers.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;

import by.slutskiy.busschedule.data.DBStructure;

/**
 * TimeListContract
 * Version 1.0
 * 27.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class TimeListContract extends BaseContract {

    public static final String PATH = DBStructure.DB_TABLE_TIME_LIST;

    public static final Uri CONTENT_URI =
            Uri.withAppendedPath(AUTHORITY_URI, PATH);

    public static final Uri CONTENT_TYPE_URI =
            Uri.withAppendedPath(AUTHORITY_URI, PATH + "/type");

    public static final String COLUMN_ROUTE_LIST_ID = DBStructure.KEY_ROUTE_LIST_ID;
    public static final String COLUMN_HOUR = DBStructure.KEY_HOUR;
    public static final String COLUMN_MINUTES = DBStructure.KEY_MINUTES;

    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();

    private static final String DATABASE_CREATE = "CREATE TABLE "
            + PATH
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_ROUTE_LIST_ID + " INTEGER, "
            + COLUMN_HOUR + " INTEGER, "
            + COLUMN_MINUTES + " TEXT);";

    private static final String[] availableColumns =
            new String[]{COLUMN_ID, COLUMN_ROUTE_LIST_ID, COLUMN_HOUR, COLUMN_MINUTES};

    static {
        PROJECTION_MAP.put(COLUMN_ID, PATH + "." + COLUMN_ID);
        PROJECTION_MAP.put(COLUMN_ROUTE_LIST_ID, PATH + "." + COLUMN_ROUTE_LIST_ID);
        PROJECTION_MAP.put(COLUMN_HOUR,
                PATH + "." + COLUMN_HOUR);
        PROJECTION_MAP.put(COLUMN_MINUTES,
                PATH + "." + COLUMN_MINUTES);
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(TimeListContract.class.getSimpleName(), "Upgrading database from version "
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

    private TimeListContract() {/*   code    */}

}
