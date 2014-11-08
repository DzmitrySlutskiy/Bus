package by.slutskiy.busschedule.providers.contracts;

import android.net.Uri;

import java.util.HashMap;

/**
 * StopDetailContract
 * Version 1.0
 * 27.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopDetailContract extends BaseContract {

    public static final String PATH = "STOP_DETAIL";

    public static final String JOIN_PATH = RouteListContract.PATH +
            " LEFT OUTER JOIN " + RouteContract.PATH + " ON " +
            RouteListContract.PATH + "." + RouteListContract.COLUMN_ROUTE_ID + " = " +
            RouteContract.PATH + "." + RouteContract.COLUMN_ID +

            " LEFT OUTER JOIN " + TimeListContract.PATH + " ON " +
            TimeListContract.PATH + "." + TimeListContract.COLUMN_ROUTE_LIST_ID + " = " +
            RouteListContract.PATH + "." + RouteListContract.COLUMN_ID;


    public static final Uri CONTENT_URI =
            Uri.withAppendedPath(AUTHORITY_URI, PATH);

    public static final String COLUMN_ROUTE_ID = RouteListContract.COLUMN_ROUTE_ID;
    public static final String COLUMN_BUS = RouteContract.COLUMN_BUS;
    public static final String COLUMN_ROUTE_NAME = RouteContract.COLUMN_ROUTE_NAME;
    public static final String COLUMN_FULL_ROUTE = "FullRoute";
    public static final String COLUMN_MINUTES = TimeListContract.COLUMN_MINUTES;
    public static final String COLUMN_TYPES = TimeListContract.COLUMN_TYPE_ID;


    private static final String[] availableColumns =
            new String[]{COLUMN_ID, COLUMN_ROUTE_ID, COLUMN_BUS, COLUMN_ROUTE_NAME,
                    COLUMN_FULL_ROUTE, COLUMN_MINUTES, COLUMN_TYPES};

    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();

    static {
        PROJECTION_MAP.put(COLUMN_ID, RouteListContract.PATH + "." + COLUMN_ID);
        PROJECTION_MAP.put(COLUMN_ROUTE_ID, COLUMN_ROUTE_ID);
        PROJECTION_MAP.put(COLUMN_BUS, COLUMN_BUS);
        PROJECTION_MAP.put(COLUMN_ROUTE_NAME, COLUMN_ROUTE_NAME);

        PROJECTION_MAP.put(COLUMN_FULL_ROUTE,
                COLUMN_BUS + " || '  ' || " + COLUMN_ROUTE_NAME + " AS " + COLUMN_FULL_ROUTE);

        PROJECTION_MAP.put(COLUMN_MINUTES, COLUMN_MINUTES);
        PROJECTION_MAP.put(COLUMN_TYPES, COLUMN_TYPES);
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

    private StopDetailContract() {/*   code    */}

}
