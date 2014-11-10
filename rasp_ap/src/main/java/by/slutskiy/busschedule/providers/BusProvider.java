package by.slutskiy.busschedule.providers;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import by.slutskiy.busschedule.providers.contracts.BaseContract;
import by.slutskiy.busschedule.providers.contracts.RouteListContract;
import by.slutskiy.busschedule.providers.contracts.StopDetailContract;
import by.slutskiy.busschedule.providers.contracts.TimeListContract;

/**
 * BusProvider
 * Version 1.0
 * 22.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class BusProvider extends ContentProvider {
//    private static final String LOG_TAG = BusProvider.class.getSimpleName();

    private static final int CODE_TABLE = 1;
    private static final int CODE_ID = 2;
    private static final int CODE_TYPE = 3;

    private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/";
    private static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(BaseContract.AUTHORITY, "*", CODE_TABLE);
        sURIMatcher.addURI(BaseContract.AUTHORITY, "*/#", CODE_ID);
        sURIMatcher.addURI(BaseContract.AUTHORITY, "*/type", CODE_TYPE);
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
    public String getType(Uri uri) {
        int matchCode = matchUriOrThrow(uri);
        String tableName = getTableNameByUriCode(matchCode, uri);

        if (matchCode == CODE_TABLE) {
            return CONTENT_DIR_TYPE + tableName;
        } else {
            return CONTENT_ITEM_TYPE + tableName;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = matchUriOrThrow(uri);

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        String tableName = getTableNameByUriCode(uriType, uri);
        long id = db.insert(tableName, null, values);

        getContext().getContentResolver().notifyChange(uri, null);
        return buildResultUri(id, tableName);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = matchUriOrThrow(uri);
        String limits = null;
        String tableName;

        if (uriType == CODE_TYPE) {
            limits = "1";
            tableName = TimeListContract.PATH;
        } else {
            tableName = getTableNameByUriCode(uriType, uri);
        }

        if (tableName.equals(RouteListContract.PATH)) {
            queryBuilder.setTables(RouteListContract.JOIN_PATH);
            queryBuilder.setProjectionMap(RouteListContract.PROJECTION_MAP);

        } else if (tableName.equals(StopDetailContract.PATH)) {
            queryBuilder.setTables(StopDetailContract.JOIN_PATH);
            queryBuilder.setProjectionMap(StopDetailContract.PROJECTION_MAP);

        } else if (tableName.equals(TimeListContract.PATH)) {
            queryBuilder.setTables(TimeListContract.JOIN_PATH);
            queryBuilder.setProjectionMap(TimeListContract.PROJECTION_MAP);

        } else {
            queryBuilder.setTables(tableName);
        }

        if (uriType == CODE_ID) {
            queryBuilder.appendWhere(tableName + "." + BaseContract.COLUMN_ID + "="
                    + uri.getLastPathSegment());
        }


        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder, limits);

        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = matchUriOrThrow(uri);

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        String tableName = getTableNameByUriCode(uriType, uri);
        long id = db.update(tableName, values,
                buildSelection(uriType, tableName, selection),
                buildSelectionArgs(uriType, uri, selectionArgs));

        getContext().getContentResolver().notifyChange(uri, null);

        return (int) id;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = matchUriOrThrow(uri);

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        String tableName = getTableNameByUriCode(uriType, uri);
        long id = db.delete(tableName,
                buildSelection(uriType, tableName, selection),
                buildSelectionArgs(uriType, uri, selectionArgs));

        getContext().getContentResolver().notifyChange(uri, null);

        return (int) id;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        int uriType = matchUriOrThrow(uri);

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        String tableName = getTableNameByUriCode(uriType, uri);
        DBHelper.beginTransaction(db);

        try {
            for (ContentValues value : values) {
                db.insert(tableName, null, value);
            }
            DBHelper.setTransactionSuccessful(db);
        } finally {
            DBHelper.endTransaction(db);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return values.length;
    }

    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        DBHelper.beginTransaction(db);
        try {
            ContentProviderResult[] result = super.applyBatch(operations);
            DBHelper.setTransactionSuccessful(db);

            return result;
        } finally {
            DBHelper.endTransaction(db);
        }
    }

    private String getTableNameByUriCode(int matchCode, Uri uri) {
        String tableName;
        if (matchCode == CODE_TABLE) {
            tableName = uri.getLastPathSegment();
        } else {
            List<String> listSegments = uri.getPathSegments();
            tableName = listSegments.get(listSegments.size() - 2);
        }

        return tableName;
    }

    private Uri buildResultUri(long id, String tableName) {
        return Uri.withAppendedPath(BaseContract.AUTHORITY_URI, "/" + tableName + "/" + Long.toString(id));
    }

    /**
     * Match uri code by uri params or throw IllegalArgumentException when URI has unknown type
     *
     * @param uri uri for matching
     * @return uri code
     */
    private int matchUriOrThrow(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        if (uriType == UriMatcher.NO_MATCH) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return uriType;
    }

    /**
     * if current uri use ID - add this ID to selection as COLUMN_ID = ?
     * need add this ID as selectionArgs
     *
     * @param matchedUriCode uri code
     * @param tableName      table name
     * @param selection      default selection
     * @return rebuilt selection string
     */
    private String buildSelection(int matchedUriCode, String tableName, String selection) {

        if (matchedUriCode == CODE_TABLE) {
            return selection;
        }

        if (selection == null) {
            selection = tableName + "." + BaseContract.COLUMN_ID + " = ?";
        } else {
            selection += " AND " + tableName + "." + BaseContract.COLUMN_ID + " = ?";
        }

        return selection;
    }

    /**
     * if current uri use ID - add this id to selectionArgs
     *
     * @param matchedUriCode uri code
     * @param selectionArgs  selectionArgs
     * @return selectionArgs with added ID
     */
    private String[] buildSelectionArgs(int matchedUriCode, Uri uri, String[] selectionArgs) {
        if (matchedUriCode == CODE_TABLE) {
            return selectionArgs;
        }

        if (selectionArgs != null) {
            selectionArgs = Arrays.copyOf(selectionArgs, selectionArgs.length + 1);
        } else {
            selectionArgs = new String[1];
        }
        selectionArgs[selectionArgs.length - 1] = uri.getLastPathSegment();

        return selectionArgs;
    }

}
