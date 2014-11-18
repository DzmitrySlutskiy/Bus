package by.slutskiy.busschedule.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
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
 * RouteAdapter
 * Version 1.0
 * 13.11.2014
 * Created by Dzmitry Slutskiy.
 */
public class TimeAdapter extends BaseAdapter<TimeAdapter.ViewHolder> {

    private final int COLOR_CURRENT_HOUR;
    private final int COLOR_ANY_HOUR;

    private final int mHour;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mHour;
        public final TimeView mTimeView;
        public final LinearLayout mLayout;

        public ViewHolder(View v) {
            super(v);

            mHour = (TextView) v.findViewById(R.id.text_view_hour);
            mTimeView = (TimeView) v.findViewById(R.id.time_view);
            mLayout = (LinearLayout) v;
        }
    }

    public TimeAdapter(Cursor cursor, Context context) {
        super(cursor, R.layout.list_item_time);

        Calendar mRightNow = Calendar.getInstance();
        mHour = mRightNow.get(Calendar.HOUR_OF_DAY);

        Resources resources = context.getResources();
        COLOR_CURRENT_HOUR = resources.getColor(R.color.text_view_current_hour);
        COLOR_ANY_HOUR = resources.getColor(R.color.text_view_any_hour);
    }

    @Override
    public TimeAdapter.ViewHolder getHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        int hour = getFieldValueAsInt(TimeListContract.COLUMN_HOUR);
        holder.mHour.setText(String.valueOf(hour));

        List<String> minutes = new ArrayList<String>();

        String type = getFieldValue(TimeListContract.COLUMN_MINUTES);

        String[] result = TextUtils.split(type, UpdateService.TYPE_DELIMITER);
        Collections.addAll(minutes, result);

        holder.mTimeView.setMinList(minutes);
        holder.mLayout.setBackgroundColor((hour == mHour) ? COLOR_CURRENT_HOUR : COLOR_ANY_HOUR);
    }
}
