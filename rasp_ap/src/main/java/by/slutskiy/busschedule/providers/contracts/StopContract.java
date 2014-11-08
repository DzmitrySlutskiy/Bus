package by.slutskiy.busschedule.providers.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * StopContract
 * Version 1.0
 * 27.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopContract extends BaseContract {

    public static final String PATH = "StopList";

    public static final Uri CONTENT_URI =
            Uri.withAppendedPath(AUTHORITY_URI, PATH);

    public static final String COLUMN_STOP_NAME = "StopName";

    private static final String DATABASE_CREATE = "create table "
            + PATH
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_STOP_NAME + " text);";

    public static final String[] defaultColumns =
            new String[]{COLUMN_ID, COLUMN_STOP_NAME};

    private static final String[] availableColumns =
            new String[]{COLUMN_ID, COLUMN_STOP_NAME};

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

    private StopContract() {/*   code    */}


}
