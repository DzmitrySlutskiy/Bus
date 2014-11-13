package by.slutskiy.busschedule.providers.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * RouteContract
 * Version 1.0
 * 27.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class RouteContract extends BaseContract {

    public static final String PATH = "Routes";

    public static final Uri CONTENT_URI =
            Uri.withAppendedPath(AUTHORITY_URI, PATH);

    public static final String COLUMN_BUS = "BusNumber";
    public static final String COLUMN_ROUTE_NAME = "RouteName";

    private static final String DATABASE_CREATE = "CREATE TABLE "
            + PATH
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_BUS + " TEXT, "
            + COLUMN_ROUTE_NAME + " TEXT);";

    public static final String[] availableColumns =
            new String[]{COLUMN_ID, COLUMN_BUS, COLUMN_ROUTE_NAME};

    public static void onCreate(SQLiteDatabase database) {
        onCreate(database, DATABASE_CREATE, PATH);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        onUpgrade(database, oldVersion, newVersion, DATABASE_CREATE, PATH);
    }

    private RouteContract() {/*   code    */}
}
