package by.slutskiy.busschedule.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.providers.contracts.NewsContract;
import by.slutskiy.busschedule.providers.contracts.RouteContract;
import by.slutskiy.busschedule.providers.contracts.RouteListContract;
import by.slutskiy.busschedule.providers.contracts.StopContract;
import by.slutskiy.busschedule.providers.contracts.TimeListContract;
import by.slutskiy.busschedule.providers.contracts.TypeContract;
import by.slutskiy.busschedule.utils.IOUtils;

/**
 * DBHelper
 * Version 1.0
 * 27.10.2014
 * Created by Dzmitry Slutskiy.
 */
class DBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DBHelper.class.getSimpleName();
    /*  private fields  */
    private static final String DEFAULT_DB_NAME = "ap.db";
    private static final int DB_VERSION = 4;
    /*  public constructors */

    public DBHelper(Context context) {
        super(context, DEFAULT_DB_NAME, null, DB_VERSION);

        File dbFile = context.getDatabasePath(DEFAULT_DB_NAME);
        if (! dbFile.exists()) {

            /*  extract database from raw resource (zip file)*/
            IOUtils.mkDir(dbFile.getParent());
            try {
                IOUtils.extractFile(context, R.raw.ap, dbFile.getAbsolutePath());
            } catch (IOException e) {
                Log.e(LOG_TAG, "unzip error: " + e.getMessage() + ". Run onCreate()");
            }
        }
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
        NewsContract.onCreate(db);
        StopContract.onCreate(db);
        RouteContract.onCreate(db);
        RouteListContract.onCreate(db);
        TimeListContract.onCreate(db);
        TypeContract.onCreate(db);
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

        NewsContract.onUpgrade(db, oldVersion, newVersion);
        StopContract.onUpgrade(db, oldVersion, newVersion);
        RouteContract.onUpgrade(db, oldVersion, newVersion);
        RouteListContract.onUpgrade(db, oldVersion, newVersion);
        TimeListContract.onUpgrade(db, oldVersion, newVersion);
        TypeContract.onUpgrade(db, oldVersion, newVersion);
    }

    public static void beginTransaction(SQLiteDatabase dbHelper) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            dbHelper.beginTransactionNonExclusive();
        } else {
            dbHelper.beginTransaction();
        }
    }

    public static void setTransactionSuccessful(SQLiteDatabase dbHelper) {
        dbHelper.setTransactionSuccessful();
    }

    public static void endTransaction(SQLiteDatabase dbHelper) {
        dbHelper.endTransaction();
    }
}
