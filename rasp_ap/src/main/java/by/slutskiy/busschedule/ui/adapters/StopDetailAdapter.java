package by.slutskiy.busschedule.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.providers.contracts.StopDetailContract;
import by.slutskiy.busschedule.services.UpdateService;

/**
 * StopDetailAdapter
 * Version 1.0
 * 18.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopDetailAdapter extends CursorAdapter {

    private final LayoutInflater mInflater;
    private final String NO_BUS;

    public StopDetailAdapter(Context context, Cursor c) {
        super(context, c, false);

        NO_BUS = context.getResources().getString(R.string.text_view_no_bus);
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

        holder.mRouteName.setText(cursor.getString(cursor.getColumnIndex(StopDetailContract.COLUMN_FULL_ROUTE)));

        String minutes = cursor.getString(cursor.getColumnIndex(StopDetailContract.COLUMN_MINUTES));
        String types = cursor.getString(cursor.getColumnIndex(StopDetailContract.COLUMN_TYPES));

        String[] splitMinutes = TextUtils.split(minutes, UpdateService.TYPE_DELIMITER);
        String[] splitTypes = TextUtils.split(types, UpdateService.TYPE_DELIMITER);
        String resultMinutes = "";

        for (int i = 0; i < splitMinutes.length && i < splitTypes.length; i++) {
            resultMinutes += splitTypes[i] + " " +
                    (splitMinutes[i].equals("") ? NO_BUS : splitMinutes[i]) + " ";
        }

        holder.mTime.setText(resultMinutes);
    }

    private static class ViewHolder {
        public TextView mRouteName;
        public TextView mTime;
    }
}
