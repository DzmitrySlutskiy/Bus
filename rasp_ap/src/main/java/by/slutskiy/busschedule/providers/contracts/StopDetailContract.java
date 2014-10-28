package by.slutskiy.busschedule.providers.contracts;

import android.net.Uri;

import java.util.HashMap;

import by.slutskiy.busschedule.data.DBStructure;

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

            " LEFT OUTER JOIN " + BusContract.PATH + " ON " +
            RouteContract.PATH + "." + RouteContract.COLUMN_BUS_ID + " = " +
            BusContract.PATH + "." + RouteContract.COLUMN_ID +

            " LEFT OUTER JOIN " + StopContract.PATH + " ON " +
            RouteContract.PATH + "." + RouteContract.COLUMN_BEGIN_STOP_ID + " = " +
            StopContract.PATH + "." + StopContract.COLUMN_ID +

            " LEFT OUTER JOIN " + StopContract.PATH + " AS S2 ON " +
            RouteContract.PATH + "." + RouteContract.COLUMN_END_STOP_ID + " = " +
            "S2." + StopContract.COLUMN_ID +

            " LEFT OUTER JOIN " + TimeListContract.PATH + " ON " +
            TimeListContract.PATH + "." + TimeListContract.COLUMN_ROUTE_LIST_ID + " = " +
            RouteListContract.PATH + "." + RouteListContract.COLUMN_ID;


    public static final Uri CONTENT_URI =
            Uri.withAppendedPath(AUTHORITY_URI, PATH);

    public static final String COLUMN_ROUTE_ID = DBStructure.KEY_ROUTE_ID;
    public static final String COLUMN_BUS_ID = DBStructure.KEY_BUS_ID;
    public static final String COLUMN_BUS_NUMBER = DBStructure.KEY_BUS_NUMBER;
    public static final String COLUMN_BEGIN_STOP_ID = DBStructure.KEY_BEGIN_STOP_ID;
    public static final String COLUMN_BEGIN_STOP = DBStructure.KEY_BEGIN_STOP;
    public static final String COLUMN_END_STOP_ID = DBStructure.KEY_END_STOP_ID;
    public static final String COLUMN_END_STOP = DBStructure.KEY_END_STOP;
    public static final String COLUMN_FULL_ROUTE = DBStructure.FULL_ROUTE;
    public static final String COLUMN_MINUTES = DBStructure.KEY_MINUTES;

    private static final String[] availableColumns =
            new String[]{COLUMN_ID, COLUMN_ROUTE_ID, COLUMN_BUS_ID, COLUMN_BUS_NUMBER,
                    COLUMN_BEGIN_STOP_ID, COLUMN_BEGIN_STOP,
                    COLUMN_END_STOP_ID, COLUMN_END_STOP, COLUMN_FULL_ROUTE, COLUMN_MINUTES};

    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();

    static {
        PROJECTION_MAP.put(COLUMN_ID, RouteListContract.PATH + "." + COLUMN_ID);
        PROJECTION_MAP.put(COLUMN_ROUTE_ID, COLUMN_ROUTE_ID);
        PROJECTION_MAP.put(COLUMN_BUS_ID, COLUMN_BUS_ID);
        PROJECTION_MAP.put(COLUMN_BUS_NUMBER, COLUMN_BUS_NUMBER);
        PROJECTION_MAP.put(COLUMN_BEGIN_STOP_ID, COLUMN_BEGIN_STOP_ID);
        PROJECTION_MAP.put(COLUMN_BEGIN_STOP, COLUMN_BEGIN_STOP);
        PROJECTION_MAP.put(COLUMN_END_STOP_ID, COLUMN_END_STOP_ID);
        PROJECTION_MAP.put(COLUMN_END_STOP, COLUMN_END_STOP);
        PROJECTION_MAP.put(COLUMN_MINUTES, COLUMN_MINUTES);
        PROJECTION_MAP.put(COLUMN_FULL_ROUTE,
                BusContract.PATH + "." + BusContract.COLUMN_BUS_NUMBER + " || '  ' || " +
                        StopContract.PATH + "." + StopContract.COLUMN_STOP_NAME + " || ' - ' || " +
                        "S2." + StopContract.COLUMN_STOP_NAME + " AS " + COLUMN_FULL_ROUTE);
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
