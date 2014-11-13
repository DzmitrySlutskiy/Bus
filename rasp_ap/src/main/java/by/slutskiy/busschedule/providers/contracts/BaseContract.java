package by.slutskiy.busschedule.providers.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

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
