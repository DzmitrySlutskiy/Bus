package by.slutskiy.busschedule.providers.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * NewsContract
 * Version 1.0
 * 27.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class NewsContract extends BaseContract {

    public static final String PATH = "News";

    public static final Uri CONTENT_URI =
            Uri.withAppendedPath(AUTHORITY_URI, PATH);

    public static final String COLUMN_NEWS = "NewsText";

    private static final String DATABASE_CREATE = "create table "
            + PATH
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NEWS + " text);";

    public static final String[] availableColumns =
            new String[]{COLUMN_ID, COLUMN_NEWS};

    public static void onCreate(SQLiteDatabase database) {
        onCreate(database, DATABASE_CREATE, PATH);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        onUpgrade(database, oldVersion, newVersion, DATABASE_CREATE, PATH);
    }

    private NewsContract() {/*   code    */}
}
