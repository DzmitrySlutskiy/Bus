package by.slutskiy.busschedule.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.DBStructure;

/**
 * BusRouteAdapter
 * Version 1.0
 * 18.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class BusRouteAdapter extends CursorAdapter {

    private final LayoutInflater mInflater;

    public BusRouteAdapter(Context context, Cursor c) {
        super(context, c, false);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context   Interface to application's global information
     * @param cursor    The cursor from which to get the data. The cursor is already
     *                  moved to the correct position.
     * @param viewGroup The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.list_item_route, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.mBus = (TextView) v.findViewById(R.id.text_view_bus_number);
        viewHolder.mBegin = (TextView) v.findViewById(R.id.text_view_begin_stop);
        viewHolder.mEnd = (TextView) v.findViewById(R.id.text_view_end_stop);

        v.setTag(viewHolder);

        return v;
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        int fieldIndex = cursor.getColumnIndex(DBStructure.KEY_BUS_NUMBER);//1
        if (fieldIndex >= 0) {
            holder.mBus.setText(cursor.getString(fieldIndex));
        }

        fieldIndex = cursor.getColumnIndex(DBStructure.KEY_BEGIN_STOP);//2
        if (fieldIndex >= 0) {
            holder.mBegin.setText(cursor.getString(fieldIndex));
        }

        fieldIndex = cursor.getColumnIndex(DBStructure.KEY_END_STOP);//3
        if (fieldIndex >= 0) {
            holder.mEnd.setText(cursor.getString(fieldIndex));
        }
    }

    private static class ViewHolder {
        public TextView mBus;
        public TextView mBegin;
        public TextView mEnd;
    }
}
