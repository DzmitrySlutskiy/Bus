package by.slutskiy.busschedule.providers.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * TypeContract
 * Version 1.0
 * 08.11.2014
 * Created by Dzmitry Slutskiy.
 */
public class TypeContract extends BaseContract {

    public static final String PATH = "TypeList";

    public static final Uri CONTENT_URI =
            Uri.withAppendedPath(AUTHORITY_URI, PATH);

    public static final String COLUMN_TYPE_NAME = "Type";

    private static final String DATABASE_CREATE = "create table "
            + PATH
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TYPE_NAME + " text);";

    public static final String[] availableColumns =
            new String[]{COLUMN_ID, COLUMN_TYPE_NAME};

    public static void onCreate(SQLiteDatabase database) {
        onCreate(database, DATABASE_CREATE, PATH);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        onUpgrade(database, oldVersion, newVersion, DATABASE_CREATE, PATH);
    }

    private TypeContract() {/*   code    */}
}
