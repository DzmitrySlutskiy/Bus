package by.slutskiy.busschedule.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.DBReader;

/**
 * StopDetailAdapter
 * Version 1.0
 * 18.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopDetailAdapter extends CursorAdapter {

    /*  private fields  */

    private final LayoutInflater mInflater;
//    private final List<StopDetail> mStopDetailList;
//    /*  public constructors */
//
//    public StopDetailAdapter(Context context, List<StopDetail> stopDetailList) {
//        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mStopDetailList = stopDetailList;
//    }
//
//    /**
//     * How many items are in the data set represented by this Adapter.
//     *
//     * @return Count of items.
//     */
//    @Override
//    public int getCount() {
//        return mStopDetailList == null ? 0 : mStopDetailList.size();
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
//        return mStopDetailList == null ? null : mStopDetailList.get(position);
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
//        return mStopDetailList == null ? 0 : mStopDetailList.get(position).getRouteListId();
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
//
//        StopDetail stopDetail = mStopDetailList.get(position);
//        ViewHolder holder;
//
//        if (convertView == null) {
//            v = mInflater.inflate(R.layout.list_item_stop_detail, parent, false);
//
//            holder = new ViewHolder();
//
//            holder.mRouteName = (TextView) v.findViewById(R.id.text_view_route_name);
//            holder.mTime = (TextView) v.findViewById(R.id.text_view_next_time);
//
//            v.setTag(holder);
//        } else {
//            v = convertView;
//            holder = (ViewHolder) v.getTag();
//        }
//
//        String minutes = "";
//
//        List<String> minList = stopDetail.getMinuteList();
//        for (String aMinList : minList) {
//            minutes = minutes + aMinList + "   ";
//        }
//
//        holder.mRouteName.setText(stopDetail.getRouteName());
//        holder.mTime.setText(minutes);
//
//        return v;
//    }

    public StopDetailAdapter(Context context, Cursor c) {
        super(context, c, false);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.list_item_stop_detail, viewGroup, false);

        ViewHolder holder = new ViewHolder();

        holder.mRouteName = (TextView) v.findViewById(R.id.text_view_route_name);
        holder.mTime = (TextView) v.findViewById(R.id.text_view_next_time);

        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.mRouteName.setText(cursor.getString(cursor.getColumnIndex(DBReader.FULL_ROUTE)));

        holder.mTime.setText(cursor.getString(cursor.getColumnIndex(DBReader.KEY_MINUTES)));
    }

    private static class ViewHolder {
        public TextView mRouteName;
        public TextView mTime;
    }
}
