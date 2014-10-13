package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.DBReader;

/**
 * background task loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopDetailLoader extends BaseLoader {

    public static final String ATT_STOP_ID = "stopId";
    public static final String ATT_HOUR = "currentHour";
    private int mStopIdLoader;
    private int mCurrentHourLoader;

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public StopDetailLoader(Context context, Bundle args) {
        super(context);

        if (args != null) {
            mStopIdLoader = args.getInt(ATT_STOP_ID);
            mCurrentHourLoader = args.getInt(ATT_HOUR);
        }
    }

    @Override
    public Cursor loadInBackground() {
        DBReader dbReader = DBReader.getInstance(getContext());

        MatrixCursor matrixCursor = new MatrixCursor(
                new String[]{DBReader.KEY_ID,
                        DBReader.FULL_ROUTE,
                        DBReader.KEY_MINUTES});

        Cursor cursor = dbReader.getStopDetailCursor(mStopIdLoader, mCurrentHourLoader);
        if (cursor.moveToFirst()) {
            MatrixCursor.RowBuilder builder = matrixCursor.newRow();
            int lastRouteId = - 1;

            String type = "";
            String minutes = "";

            do {
                int routeId = 0;

                int fieldIndex = cursor.getColumnIndex(DBReader.KEY_ROUTE_ID);
                if (fieldIndex >= 0) {
                    routeId = cursor.getInt(fieldIndex);
                }

                if (lastRouteId != routeId) {

                    if (lastRouteId != - 1) {
                        builder.add(DBReader.KEY_MINUTES, minutes);
                        minutes = "";
                        builder = matrixCursor.newRow();
                    }
                    lastRouteId = routeId;
                    String routeName = "";

                    fieldIndex = cursor.getColumnIndex(DBReader.KEY_BUS_NUMBER);
                    if (fieldIndex >= 0) {
                        routeName += cursor.getString(fieldIndex) + "   ";
                    }

                    fieldIndex = cursor.getColumnIndex(DBReader.KEY_BEGIN_STOP);
                    if (fieldIndex >= 0) {
                        routeName += cursor.getString(fieldIndex) + " - ";
                    }

                    fieldIndex = cursor.getColumnIndex(DBReader.KEY_END_STOP);
                    if (fieldIndex >= 0) {
                        routeName += cursor.getString(fieldIndex);
                    }

                    int _id = 0;
                    fieldIndex = cursor.getColumnIndex(DBReader.KEY_ID);
                    if (fieldIndex >= 0) {
                        _id = cursor.getInt(fieldIndex);
                    }

                    builder.add(DBReader.KEY_ID, _id);
                    builder.add(DBReader.FULL_ROUTE, routeName);
                }


                fieldIndex = cursor.getColumnIndex(DBReader.KEY_TYPE);
                if (fieldIndex >= 0) {
                    type = cursor.getString(fieldIndex);
                }
                String min = "";
                fieldIndex = cursor.getColumnIndex(DBReader.KEY_MINUTES);
                if (fieldIndex >= 0) {
                    min = cursor.getString(fieldIndex).trim();
                }

                if (min.isEmpty()) {
                    min = getContext().getString(R.string.text_view_no_bus);
                }

                minutes += type + " " + min + " ";
            } while (cursor.moveToNext());
            if (! minutes.equals("")) {
                builder.add(DBReader.KEY_MINUTES, minutes);
            }
        }
        cursor.close();

        return matrixCursor;
    }
}
