package by.slutskiy.busschedule.providers.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

/**
 * BaseContract
 * Version 1.0
 * 27.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class BaseContract {
    private static final String LOG_TAG = BaseContract.class.getSimpleName();

    public static final String COLUMN_ID = "_id";

    public static final String AUTHORITY = "by.slutskiy.busschedule.providers.busprovider";

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    BaseContract() {/*   code    */}

    /**
     * Check if the caller has requested a column which does not exists
     *
     * @param projection requested columns
     * @throws IllegalArgumentException if requested column does not exists in current table
     */
    static void checkColumns(String[] available, String[] projection) {
        if (projection != null && available != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));

            if (! availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection! available:" +
                        Arrays.toString(available) + " requested: " + Arrays.toString(projection) + ". See NewsContract!");
            }
        } else {
            throw new IllegalArgumentException("available and projection can't be null");
        }
    }

    protected static void onCreate(SQLiteDatabase database, String query, String tableName) {
        Log.w(LOG_TAG, "onCreate " + tableName);

        database.execSQL(query);
    }

    protected static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion, String createQuery, String tableName) {
        Log.w(LOG_TAG, "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");

        database.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(database, createQuery, tableName);
    }
}
