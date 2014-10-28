package by.slutskiy.busschedule.providers.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import by.slutskiy.busschedule.data.DBStructure;

/**
 * BusContract
 * Version 1.0
 * 27.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class BusContract extends BaseContract {

    public static final String PATH = DBStructure.DB_TABLE_BUS_LIST;

    public static final String COLUMN_BUS_NUMBER = DBStructure.KEY_BUS_NUMBER;

    private static final String DATABASE_CREATE = "create table "
            + PATH
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_BUS_NUMBER + " text);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(BusContract.class.getSimpleName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + PATH);
        onCreate(database);
    }

    private BusContract() {/*   code    */}

}
