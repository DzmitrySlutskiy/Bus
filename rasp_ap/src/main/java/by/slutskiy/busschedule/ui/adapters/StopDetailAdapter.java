package by.slutskiy.busschedule.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.j256.ormlite.dao.ForeignCollection;

import java.util.Calendar;
import java.util.List;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.entities.RouteList;
import by.slutskiy.busschedule.data.entities.TimeList;

/**
 * StopDetailAdapter
 * Version 1.0
 * 15.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopDetailAdapter extends BaseAdapter {

    /*  private fields  */
    private final LayoutInflater mInflater;
    private List<RouteList> mRouteList = null;
    private int mCurrentHour;
    private final String mNoBus;

    /*  public constructors */

    public StopDetailAdapter(Context context, List<RouteList> routeList) {
        mRouteList = routeList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mNoBus = context.getString(R.string.text_view_no_bus);

        initCurrentHour();
    }

    private void initCurrentHour() {
        Calendar rightNow = Calendar.getInstance();
        mCurrentHour = rightNow.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mRouteList == null ? 0 : mRouteList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return mRouteList == null ? null : mRouteList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return mRouteList == null ? 0 : mRouteList.get(position).getmId();
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(R.layout.list_item_stop_detail, parent, false);
        } else {
            v = convertView;
        }
        initCurrentHour();

        String route = "";
        String nextTimeRoute = "" + mCurrentHour + ": ";

        if (mRouteList != null) {
            RouteList routeList = mRouteList.get(position);

            route = routeList.getmRoutes().toString();

            ForeignCollection<TimeList> timeLists = routeList.getmTimeList();
            for (TimeList timeListItem : timeLists) {
                if (timeListItem.getmHour() == mCurrentHour) {
                    String minutes = timeListItem.getmMinutes();
                    if (minutes.isEmpty()) {
                        minutes = mNoBus;
                    }
                    nextTimeRoute += timeListItem.getmDayType().getmType() +
                            " " + minutes + "   ";
                }
            }
        }

        setTextViewString(v, R.id.text_view_route_name, route);
        setTextViewString(v, R.id.text_view_next_time, nextTimeRoute);

        return v;
    }

    /**
     * Set text to TextView
     * @param parentView    parent view
     * @param id    TextView id
     * @param text  text for set
     */
    private void setTextViewString(View parentView, int id, String text) {
        TextView textView = (TextView) parentView.findViewById(id);
        if (textView != null) {
            textView.setText(text);
        }
    }
}
