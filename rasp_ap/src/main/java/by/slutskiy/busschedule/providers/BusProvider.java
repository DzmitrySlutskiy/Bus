package by.slutskiy.busschedule.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import by.slutskiy.busschedule.providers.contracts.BaseContract;
import by.slutskiy.busschedule.providers.contracts.NewsContract;
import by.slutskiy.busschedule.providers.contracts.RouteContract;
import by.slutskiy.busschedule.providers.contracts.RouteListContract;
import by.slutskiy.busschedule.providers.contracts.StopContract;
import by.slutskiy.busschedule.providers.contracts.StopDetailContract;
import by.slutskiy.busschedule.providers.contracts.TimeListContract;

/**
 * BusProvider
 * Version 1.0
 * 22.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class BusProvider extends ContentProvider {
    private static final String LOG_TAG = BusProvider.class.getSimpleName();

    private static final int CODE_NEWS = 1;

    private static final int CODE_ROUTES = 10;
    private static final int CODE_ROUTE_ID = 11;

    private static final int CODE_STOPS = 20;
    private static final int CODE_STOP_ROUTE_ID = 21;

    private static final int CODE_TIME_LIST = 30;
    private static final int CODE_TYPE_LIST = 31;

    private static final int CODE_DETAIL_LIST = 40;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(BaseContract.AUTHORITY, NewsContract.PATH, CODE_NEWS);
        sURIMatcher.addURI(BaseContract.AUTHORITY, RouteContract.PATH, CODE_ROUTES);
        sURIMatcher.addURI(BaseContract.AUTHORITY, RouteContract.PATH + "/#", CODE_ROUTE_ID);
        sURIMatcher.addURI(BaseContract.AUTHORITY, StopContract.PATH, CODE_STOPS);
        sURIMatcher.addURI(BaseContract.AUTHORITY, RouteListContract.PATH + "/#", CODE_STOP_ROUTE_ID);
        sURIMatcher.addURI(BaseContract.AUTHORITY, TimeListContract.PATH + "/#", CODE_TIME_LIST);
        sURIMatcher.addURI(BaseContract.AUTHORITY, TimeListContract.PATH + "/type/#", CODE_TYPE_LIST);
        sURIMatcher.addURI(BaseContract.AUTHORITY, StopDetailContract.PATH, CODE_DETAIL_LIST);
    }


    private DBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new DBHelper(getContext());
        return false;
    }

    public BusProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String limit = null;
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CODE_NEWS:
                queryBuilder.setTables(NewsContract.PATH);
                NewsContract.checkColumns(projection);

                break;
            case CODE_ROUTES:
                queryBuilder.setTables(RouteContract.JOIN_PATH);
                queryBuilder.setProjectionMap(RouteContract.PROJECTION_MAP);
                RouteContract.checkColumns(projection);
                break;

            case CODE_ROUTE_ID:
                queryBuilder.setTables(RouteContract.JOIN_PATH);
                queryBuilder.setProjectionMap(RouteContract.PROJECTION_MAP);
                RouteContract.checkColumns(projection);

                queryBuilder.appendWhere(RouteContract.PATH + "." + RouteContract.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;

            case CODE_STOPS:
                queryBuilder.setTables(StopContract.PATH);
                StopContract.checkColumns(projection);
                break;

            case CODE_STOP_ROUTE_ID:
                queryBuilder.setTables(RouteListContract.JOIN_PATH);
                queryBuilder.setProjectionMap(RouteListContract.PROJECTION_MAP);
                queryBuilder.appendWhere(RouteListContract.PATH + "." +
                        RouteListContract.COLUMN_ROUTE_ID + "=" + uri.getLastPathSegment());
                RouteListContract.checkColumns(projection);
                break;

            case CODE_TIME_LIST:
                queryBuilder.setTables(TimeListContract.PATH);
                queryBuilder.setProjectionMap(TimeListContract.PROJECTION_MAP);
                queryBuilder.appendWhere(TimeListContract.PATH + "." +
                        TimeListContract.COLUMN_ROUTE_LIST_ID + "=" + uri.getLastPathSegment());
                TimeListContract.checkColumns(projection);
                break;

            case CODE_TYPE_LIST:
                queryBuilder.setTables(TimeListContract.PATH);
                queryBuilder.setProjectionMap(TimeListContract.PROJECTION_MAP);
                queryBuilder.appendWhere(TimeListContract.PATH + "." +
                        TimeListContract.COLUMN_ROUTE_LIST_ID + "=" + uri.getLastPathSegment());
                TimeListContract.checkColumns(projection);
                limit = "1";
                break;

            case CODE_DETAIL_LIST:
                queryBuilder.setTables(StopDetailContract.JOIN_PATH);
                queryBuilder.setProjectionMap(StopDetailContract.PROJECTION_MAP);
                StopDetailContract.checkColumns(projection);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = queryBuilder.query(db, projection, selection,
                    selectionArgs, null, null, sortOrder, limit);

            // make sure that potential listeners are getting notified
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        } catch (Exception e) {
            Log.e(LOG_TAG, "" + e);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
