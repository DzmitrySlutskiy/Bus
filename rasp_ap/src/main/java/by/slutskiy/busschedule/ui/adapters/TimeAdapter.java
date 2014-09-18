package by.slutskiy.busschedule.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.entities.TimeList;
import by.slutskiy.busschedule.ui.fragments.TimeListFragment;

/**
 * TimeAdapter
 * Version 1.0
 * 18.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class TimeAdapter extends BaseAdapter {

    /*  private fields  */
    private final LayoutInflater mInflater;
    private final List<TimeList> mTimeList;

    /*  public constructors */

    public TimeAdapter(Context context, List<TimeList> timeList) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTimeList = timeList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return (mTimeList == null) ? 0 : mTimeList.size();
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
        return mTimeList == null ? null : mTimeList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
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

        int typeSize = mTimeList.get(0).getMinSize();

        if (convertView == null) {
            v = mInflater.inflate(R.layout.list_item_time, parent, false);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.mHour = (TextView) v.findViewById(R.id.text_view_hour);
            LinearLayout minPanel = (LinearLayout) v.findViewById(R.id.layout_minutes);

            viewHolder.mMinutePanel = minPanel;

            //add TextView to min panel
            for (int i = 0; i < typeSize; i++) {
                TextView textView = TimeListFragment.getTextView(v.getContext(), "");
                viewHolder.mMinutes.add(textView);

                minPanel.addView(textView);
            }

            v.setTag(viewHolder);
        } else {
            v = convertView;

            ViewHolder holder = (ViewHolder) v.getTag();

            //check for view count in minute panel
            if (holder.mMinutePanel.getChildCount() < typeSize) {
                int childNeeded = typeSize - holder.mMinutePanel.getChildCount();
                for (int i = 0; i < childNeeded; i++) {
                    TextView textView = TimeListFragment.getTextView(v.getContext(), "");
                    holder.mMinutePanel.addView(textView);
                    holder.mMinutes.add(textView);
                }
            }
        }

        ViewHolder holder = (ViewHolder) v.getTag();

        TimeList timeList = mTimeList.get(position);

        holder.mHour.setText("" + timeList.getHour());

        for (int i = 0; i < timeList.getMinSize(); i++) {
            holder.mMinutes.get(i).setText(timeList.getMin(i));
        }

        return v;
    }

    private static class ViewHolder {
        public TextView mHour;
        public LinearLayout mMinutePanel;
        public List<TextView> mMinutes = new ArrayList<TextView>();
    }

}
