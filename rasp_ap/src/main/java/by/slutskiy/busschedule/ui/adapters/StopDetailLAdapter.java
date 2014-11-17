package by.slutskiy.busschedule.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.providers.contracts.StopDetailContract;
import by.slutskiy.busschedule.services.UpdateService;

/**
 * RouteAdapter
 * Version 1.0
 * 13.11.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopDetailLAdapter extends BaseAdapter<StopDetailLAdapter.ViewHolder> {

    private final String NO_BUS;
    private onItemClickListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mRouteName;
        public TextView mTime;
        private onItemClickListener mHolderClickListener;

        public ViewHolder(View v, onItemClickListener listener) {
            super(v);
            mHolderClickListener = listener;
            mTime = (TextView) v.findViewById(R.id.text_view_next_time);
            mRouteName = (TextView) v.findViewById(R.id.text_view_route_name);
            v.setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    if (mHolderClickListener != null) {
                        mHolderClickListener.onClick(mRouteName.getText().toString(), (Long) mTime.getTag());
                    }
                }
            });
        }
    }

    public StopDetailLAdapter(Cursor cursor, Context context, onItemClickListener listener) {
        super(cursor, R.layout.list_item_stop_detail);
        NO_BUS = context.getResources().getString(R.string.text_view_no_bus);
        mListener = listener;
    }

    @Override
    public StopDetailLAdapter.ViewHolder getHolder(View v) {
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        holder.mRouteName.setText(getFieldValue(StopDetailContract.COLUMN_FULL_ROUTE));

        String minutes = getFieldValue(StopDetailContract.COLUMN_MINUTES);
        String types = getFieldValue(StopDetailContract.COLUMN_TYPES);

        String[] splitMinutes = TextUtils.split(minutes, UpdateService.TYPE_DELIMITER);
        String[] splitTypes = TextUtils.split(types, UpdateService.TYPE_DELIMITER);
        String resultMinutes = "";

        for (int i = 0; i < splitMinutes.length && i < splitTypes.length; i++) {
            resultMinutes += splitTypes[i] + " " +
                    (splitMinutes[i].equals("") ? NO_BUS : splitMinutes[i]) + " ";
        }
        holder.mTime.setText(resultMinutes);
        holder.mTime.setTag(getItemId(position));
    }

    public interface onItemClickListener {
        void onClick(String routeName, long id);
    }
}
