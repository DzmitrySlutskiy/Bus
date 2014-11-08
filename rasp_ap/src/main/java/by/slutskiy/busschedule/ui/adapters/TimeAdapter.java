package by.slutskiy.busschedule.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.providers.contracts.TimeListContract;
import by.slutskiy.busschedule.services.UpdateService;
import by.slutskiy.busschedule.ui.views.TimeView;

/**
 * TimeAdapter
 * Version 1.0
 * 18.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class TimeAdapter extends CursorAdapter {

    /*  private fields  */
    private final LayoutInflater mInflater;
    private final int COLOR_CURRENT_HOUR;
    private final int COLOR_ANY_HOUR;

    private int mHour;

    public TimeAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Calendar mRightNow = Calendar.getInstance();
        mHour = mRightNow.get(Calendar.HOUR_OF_DAY);

        Resources resources = context.getResources();
        COLOR_CURRENT_HOUR = resources.getColor(R.color.text_view_current_hour);
        COLOR_ANY_HOUR = resources.getColor(R.color.text_view_any_hour);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.list_item_time, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.mHour = (TextView) v.findViewById(R.id.text_view_hour);
        viewHolder.mView = (TimeView) v.findViewById(R.id.time_view);

        v.setTag(viewHolder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        int hour = cursor.getInt(cursor.getColumnIndex(TimeListContract.COLUMN_HOUR));
        List<String> minutes = new ArrayList<String>();

        int index = cursor.getColumnIndex(TimeListContract.COLUMN_MINUTES);
        String type = cursor.getString(index);

        String[] result = TextUtils.split(type, UpdateService.TYPE_DELIMITER);
        Collections.addAll(minutes, result);

        holder.mHour.setText("" + hour);
        holder.mView.setMinList(minutes);

        view.setBackgroundColor((hour == mHour) ? COLOR_CURRENT_HOUR : COLOR_ANY_HOUR);
    }

    private static class ViewHolder {
        public TextView mHour;
        public TimeView mView;
    }
}
