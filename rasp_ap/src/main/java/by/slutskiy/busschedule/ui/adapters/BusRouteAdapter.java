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
//    private final List<BusRoute> mBusRouteList;

//    public BusRouteAdapter(Context context, List<BusRoute> busRouteList) {
//        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mBusRouteList = busRouteList;
//    }

//    /**
//     * How many items are in the data set represented by this Adapter.
//     *
//     * @return Count of items.
//     */
//    @Override
//    public int getCount() {
//        return mBusRouteList == null ? 0 : mBusRouteList.size();
//    }
//
//    /**
//     * Get the data item associated with the specified position in the data set.
//     *
//     * @param position Position of the item whose data we want within the adapter's
//     *                 data set.
//     * @return The data at the specified position.
//     */
//    @Override
//    public Object getItem(int position) {
//        return mBusRouteList == null ? null : mBusRouteList.get(position);
//    }
//
//    /**
//     * Get the row id associated with the specified position in the list.
//     *
//     * @param position The position of the item within the adapter's data set whose row id we want.
//     * @return The id of the item at the specified position.
//     */
//    @Override
//    public long getItemId(int position) {
//        return mBusRouteList == null ? 0 : mBusRouteList.get(position).getRouteId();
//    }
//
//    /**
//     * Get a View that displays the data at the specified position in the data set. You can either
//     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
//     * parent View (GridView, ListView...) will apply default layout parameters unless you use
//     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
//     * to specify a root view and to prevent attachment to the root.
//     *
//     * @param position    The position of the item within the adapter's data set of the item whose view
//     *                    we want.
//     * @param convertView The old view to reuse, if possible. Note: You should check that this view
//     *                    is non-null and of an appropriate type before using. If it is not possible to convert
//     *                    this view to display the correct data, this method can create a new view.
//     *                    Heterogeneous lists can specify their number of view types, so that this View is
//     *                    always of the right type (see {@link #getViewTypeCount()} and
//     *                    {@link #getItemViewType(int)}).
//     * @param parent      The parent that this view will eventually be attached to
//     * @return A View corresponding to the data at the specified position.
//     */
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View v;
//        if (convertView == null) {
//            v = mInflater.inflate(R.layout.list_item_route, parent, false);
//
//            ViewHolder viewHolder = new ViewHolder();
//
//            viewHolder.mBus = (TextView) v.findViewById(R.id.text_view_bus_number);
//            viewHolder.mBegin = (TextView) v.findViewById(R.id.text_view_begin_stop);
//            viewHolder.mEnd = (TextView) v.findViewById(R.id.text_view_end_stop);
//
//            v.setTag(viewHolder);
//        } else {
//            v = convertView;
//        }
//
//        ViewHolder holder = (ViewHolder) v.getTag();
//
//        BusRoute busRoute = mBusRouteList.get(position);
//
//        holder.mBus.setText(busRoute.getBusNumber());
//        holder.mBegin.setText(busRoute.getBeginStop());
//        holder.mEnd.setText(busRoute.getEndStop());
//
//        return v;
//    }

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
